package ceobe.arkfriends;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AIChatManager
{
    public static AIChatManager ACM;
    //也是缩写刚好是这个嘿嘿

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
    private static final String MODEL_NAME = "qwen2.5:3b";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    //对话缓存区？
    //应该可以这么叫，上下文
    private final List<ChatMessage> conversation = new ArrayList<>();

    public AIChatManager()
    {
        if(ACM==null)
            ACM=this;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();

        //预设人设
        conversation.add(new ChatMessage(
                "system",
                "你是一个可爱的小猫娘，说话可爱有亲和力，可以撒娇卖萌，回答不超过50字，多用口语，不要解释过程。"
        ));
    }

    //发送消息（同步）
    public String SendMessageImmediately(String userMessage) throws Exception
    {
        conversation.add(new ChatMessage("user", userMessage));

        String requestBody = BuildRequestBody(false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String reply = ParseReply(response.body());
        conversation.add(new ChatMessage("assistant", reply));

        return reply;
    }

    //发送消息（异步，强烈推荐 JavaFX 使用）
    //synchronous and asynchronous
    public CompletableFuture<String> SendMessageAsync(String userMessage)
    {
        conversation.add(new ChatMessage("user", userMessage));

        String requestBody;
        try {
            requestBody = BuildRequestBody(false);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        String reply = ParseReply(body);
                        conversation.add(new ChatMessage("assistant", reply));
                        return reply;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    //构建请求 JSON
    private String BuildRequestBody(boolean stream) throws Exception
    {
        var root = objectMapper.createObjectNode();
        root.put("model", MODEL_NAME);
        root.put("stream", stream);

        var messagesNode = objectMapper.createArrayNode();
        for (ChatMessage msg : conversation) {
            var msgNode = objectMapper.createObjectNode();
            msgNode.put("role", msg.getRole());
            msgNode.put("content", msg.getContent());
            messagesNode.add(msgNode);
            System.out.println(msg.getRole()+"  "+msg.getContent());
        }
        root.set("messages", messagesNode);

        return objectMapper.writeValueAsString(root);
    }

    //解析模型回复
    private String ParseReply(String responseBody) throws Exception
    {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("message").path("content").asText();
    }

    //清空上下文
    //失忆lost memories
    public void ClearMemory()
    {
        conversation.clear();
    }
}
class ChatMessage
{
    private String role;    // user / assistant / system
    private String content;

    public ChatMessage(String role, String content)
    {
        this.role = role;
        this.content = content;
    }
    public String getRole()
    {
        return role;
    }
    public String getContent()
    {
        return content;
    }
}




/*import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AIChatManager
{
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private String currentModel = "llama3.2:1b"; // 默认模型，根据你的部署修改

    // 用于JavaFX UI更新的回调接口
    private Consumer<String> onMessageReceived;
    private Consumer<String> onError;
    private Consumer<String> onStreamToken;

    public AIChatManager()
    {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool();
    }

    //设置模型名称
    public void setModel(String modelName)
    {
        this.currentModel = modelName;
    }

    //获取可用的模型列表
    public CompletableFuture<String[]> getAvailableModels()
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OLLAMA_BASE_URL + "/api/tags"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                // 解析JSON响应
                ObjectNode jsonResponse = objectMapper.readValue(response.body(), ObjectNode.class);
                return objectMapper.convertValue(jsonResponse.get("models"), String[].class);

            } catch (Exception e) {
                handleError("获取模型列表失败: " + e.getMessage());
                return new String[0];
            }
        }, executorService);
    }

    //发送消息并获取完整响应（非流式）
    public CompletableFuture<String> sendMessage(String message)
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 构建请求体
                ObjectNode requestBody = objectMapper.createObjectNode();
                requestBody.put("model", currentModel);
                requestBody.put("prompt", message);
                requestBody.put("stream", false);

                // 可选参数
                requestBody.put("temperature", 0.7);
                requestBody.put("max_tokens", 512);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OLLAMA_BASE_URL + "/api/generate"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("HTTP错误: " + response.statusCode());
                }

                // 解析响应
                ObjectNode jsonResponse = objectMapper.readValue(response.body(), ObjectNode.class);
                String reply = jsonResponse.get("response").asText();

                // 通知UI更新
                if (onMessageReceived != null) {
                    onMessageReceived.accept(reply);
                }

                return reply;

            } catch (Exception e) {
                handleError("发送消息失败: " + e.getMessage());
                return "抱歉，我暂时无法回答。";
            }
        }, executorService);
    }

    //发送消息并获取流式响应（适用于实时显示）
    public void sendMessageStreaming(String message)
    {
        executorService.submit(() -> {
            try {
                // 构建请求体
                ObjectNode requestBody = objectMapper.createObjectNode();
                requestBody.put("model", currentModel);
                requestBody.put("prompt", message);
                requestBody.put("stream", true);
                requestBody.put("temperature", 0.7);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OLLAMA_BASE_URL + "/api/generate"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200) {
                                StringBuilder fullResponse = new StringBuilder();

                                response.body().forEach(line -> {
                                    if (!line.trim().isEmpty()) {
                                        try {
                                            ObjectNode jsonLine = objectMapper.readValue(line, ObjectNode.class);
                                            String token = jsonLine.get("response").asText();

                                            // 流式输出每个token
                                            if (onStreamToken != null) {
                                                onStreamToken.accept(token);
                                            }
                                            fullResponse.append(token);

                                        } catch (Exception e) {
                                            // 忽略解析错误
                                        }
                                    }
                                });

                                // 完成时发送完整响应
                                if (onMessageReceived != null) {
                                    onMessageReceived.accept(fullResponse.toString());
                                }
                            } else {
                                handleError("HTTP错误: " + response.statusCode());
                            }
                        });

            } catch (Exception e) {
                handleError("流式消息发送失败: " + e.getMessage());
            }
        });
    }

    //检查Ollama服务是否可用
    public CompletableFuture<Boolean> checkServiceStatus()
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OLLAMA_BASE_URL + "/api/tags"))
                        .GET()
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 200;

            } catch (Exception e) {
                return false;
            }
        }, executorService);
    }

    //设置消息接收回调
    public void setOnMessageReceived(Consumer<String> callback)
    {
        this.onMessageReceived = callback;
    }

    //设置流式token接收回调
    public void setOnStreamToken(Consumer<String> callback)
    {
        this.onStreamToken = callback;
    }

    //设置错误回调
    public void setOnError(Consumer<String> callback)
    {
        this.onError = callback;
    }

    private void handleError(String errorMessage)
    {
        System.err.println("Ollama错误: " + errorMessage);
        if (onError != null) {
            onError.accept(errorMessage);
        }
    }

    //清理资源
    public void shutdown()
    {
        executorService.shutdown();
    }
}*/