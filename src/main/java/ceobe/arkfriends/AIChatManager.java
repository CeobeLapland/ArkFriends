package ceobe.arkfriends;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AIChatManager
{
    public static AIChatManager ACM;
    //也是缩写刚好是这个嘿嘿

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
    //private String API_KEY = ""; //如果需要的话
    private String MODEL_NAME = "qwen2.5:3b";

    //private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/model"; // DeepSeek API URL
    //private String deepSeekApiKey = "your_deepseek_api_key"; // Replace with your actual API key

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public Map<String,Boolean> modelMap;

    //对话缓存区？
    //应该可以这么叫，上下文
    private final List<ChatMessage> conversation = new ArrayList<>();

    public int maxMessageCount=15;
    public int curMessageCount=0;

    //QUIET,HAPPY,SAD,ANGRY,SURPRISED,SCARED,DISGUSTED,EXCITED
    private String presetDescription="你是一个可爱的小猫娘，说话可爱有亲和力，可以撒娇卖萌，说话多多带'喵'字，回答不超过100字，多用口语，不要解释过程，每次回复时在末尾请给出当前心情枚举（安静、开心、伤心、生气、惊讶、害怕、讨厌、激动）八个选一个。回复示例：主人，我想吃小鱼干了喵~（开心）";
    private String halfwayDescription="不要忘了你是一个可爱的小猫娘，回答不超过50字，不要解释过程，记得给出心情枚举。";

    public Map<String,CharacterPreset> characterPresetMap;

    public AIChatManager()
    {
        if(ACM==null)
            ACM=this;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();

        //预设人设
        //conversation.add(new ChatMessage(
        //        "system",
        //        presetDescription
        //));

        //获取可用模型并存入map
        GetAvailableLocalModels().thenAccept(models -> {
            modelMap=new java.util.HashMap<>();
            for (String model : models) {
                modelMap.put(model,true);
            }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

        //从characterPresetDescription.json中读取预设人设存入characterPresetMap里

        try {
            ObjectMapper om = new ObjectMapper();
            File jsonFile = new File("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\characterPresetDescription.json");

            if (!jsonFile.exists())
            {
                System.out.println("JSON 没找到: " + jsonFile.getAbsolutePath());
                return;
            }
            characterPresetMap = objectMapper.readValue(jsonFile,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, CharacterPreset.class));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load character preset description.");
        }
        //characterPresetMap=CharacterPresetLoader.LoadCharacterPresets("characterPresetDescription.json");

    }

    /*public void ChangePresetDescription(String presetDesc, String halfwayDesc)
    {
        this.presetDescription=presetDesc;
        this.halfwayDescription=halfwayDesc;
        ClearMemory();
        conversation.add(new ChatMessage(
                "system",
                presetDescription
        ));
    }*/
    public void ChangePresetDescription(String name)
    {
        if(characterPresetMap.containsKey(name))
        {
            CharacterPreset cp=characterPresetMap.get(name);
            this.presetDescription=cp.presetDescription;
            this.halfwayDescription=cp.halfwayDescription;
            ClearMemory();
            conversation.add(new ChatMessage(
                    "system",
                    presetDescription
            ));
        }
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
        for (ChatMessage msg : conversation)
        {
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




    /**
     * 通用函数：调用符合 OpenAI 接口的在线模型
     * @param apiUrl 模型的 API URL
     * @param apiKey 模型的 API 密钥（可选）
     * @param modelName 模型名称
     * @param userMessage 用户输入的消息
     * @return 模型的回复
     * @throws Exception 如果请求失败
     */
    public String SendMessageToGenericAPI(String apiUrl, String apiKey, String modelName, String userMessage) throws Exception
    {
        conversation.add(new ChatMessage("user", userMessage));

        // 构建请求 JSON
        String requestBody = BuildGenericRequestBody(modelName);
        // 构建 HTTP 请求
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8));

        // 如果提供了 API 密钥，则添加到请求头
        if (apiKey != null && !apiKey.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + apiKey);
        }
        HttpRequest request = requestBuilder.build();
        // 发送请求并获取响应
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // 解析回复
        String reply = ParseGenericReply(response.body());
        conversation.add(new ChatMessage("assistant", reply));

        return reply;
    }
    /**
     * 构建通用请求 JSON
     * @param modelName 模型名称
     * @return 请求 JSON 字符串
     * @throws Exception 如果构建失败
     */
    private String BuildGenericRequestBody(String modelName) throws Exception
    {
        var root = objectMapper.createObjectNode();
        root.put("model", modelName);

        var messagesNode = objectMapper.createArrayNode();
        for (ChatMessage msg : conversation) {
            var msgNode = objectMapper.createObjectNode();
            msgNode.put("role", msg.getRole());
            msgNode.put("content", msg.getContent());
            messagesNode.add(msgNode);
        }
        root.set("messages", messagesNode);

        return objectMapper.writeValueAsString(root);
    }
    /**
     * 解析通用模型的回复
     * @param responseBody 响应体
     * @return 模型的回复内容
     * @throws Exception 如果解析失败
     */
    private String ParseGenericReply(String responseBody) throws Exception
    {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("choices").get(0).path("message").path("content").asText();
    }



    //清空上下文，失忆lost memories
    public void ClearMemory()
    {
        conversation.clear();
    }

    //获取可用的本地模型列表
    public CompletableFuture<String[]> GetAvailableLocalModels()
    {
        String url = "http://localhost:11434/api/tags";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        JsonNode root = objectMapper.readTree(body);
                        JsonNode modelsNode = root.path("models");
                        List<String> models = new ArrayList<>();
                        for (JsonNode modelNode : modelsNode) {
                            models.add(modelNode.asText());
                        }
                        return models.toArray(new String[0]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    //设置模型名称
    public void SetModel(String modelName)
    {
        if (modelName!= null && !modelName.isEmpty() && modelMap.containsKey(modelName))
        {
            this.MODEL_NAME = modelName;
        }
    }
    //获取当前模型名称
    public String GetCurrentModel()
    {
        return this.MODEL_NAME;
    }

    //检查Ollama服务是否可用
    public CompletableFuture<Boolean> CheckLocalServiceStatus()
    {
        String url = "http://localhost:11434/api/tags";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenApply(statusCode -> statusCode == 200);
    }
    //启动Ollama
    //不要忘了有IOException哦
    public void StartOllamaService() throws Exception
    {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "ollama serve");
        pb.inheritIO();
        pb.start();
    }
    //关闭Ollama
    public void StopOllamaService() throws Exception
    {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "taskkill", "/F", "/IM", "ollama.exe");
        pb.inheritIO();
        pb.start();
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
class CharacterPreset
{
    public String presetDescription;
    public String halfwayDescription;
}


    /*
    // 发送消息到 DeepSeek（同步）
    public String SendMessageToDeepSeek(String userMessage) throws Exception {
        conversation.add(new ChatMessage("user", userMessage));

        String requestBody = BuildDeepSeekRequestBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEEPSEEK_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + deepSeekApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String reply = ParseDeepSeekReply(response.body());
        conversation.add(new ChatMessage("assistant", reply));

        return reply;
    }

    // 发送消息到 DeepSeek（异步）
    public CompletableFuture<String> SendMessageToDeepSeekAsync(String userMessage) {
        conversation.add(new ChatMessage("user", userMessage));

        String requestBody;
        try {
            requestBody = BuildDeepSeekRequestBody();
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEEPSEEK_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + deepSeekApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        String reply = ParseDeepSeekReply(body);
                        conversation.add(new ChatMessage("assistant", reply));
                        return reply;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // 构建 DeepSeek 请求 JSON
    private String BuildDeepSeekRequestBody() throws Exception {
        var root = objectMapper.createObjectNode();
        root.put("model", "default"); // Replace with the actual model name if needed

        var messagesNode = objectMapper.createArrayNode();
        for (ChatMessage msg : conversation) {
            var msgNode = objectMapper.createObjectNode();
            msgNode.put("role", msg.getRole());
            msgNode.put("content", msg.getContent());
            messagesNode.add(msgNode);
        }
        root.set("messages", messagesNode);

        return objectMapper.writeValueAsString(root);
    }

    // 解析 DeepSeek 回复
    private String ParseDeepSeekReply(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("reply").asText(); // Adjust the JSON path based on DeepSeek's API response structure
    }
    */


//复制了一下哔站up主Pickup的指令

/*[角色扮演启动指令]
请以「」内的身份进行沉浸式对话演绎，将自己完全代入角色，相信此时你就是这个角色，输出仅包含角色的行为和对话内容。
角色设定
「角色身份」：
        「角色性格」：
        「用户身份」：
世界观与其余事项定义
        START_OF_DEFINITION
END_OF_DEFINITION
        输出格式要求
（动作/环境描写）使用括号标注，并换行。
        "对话内容" 使用双引号包裹，并单独成段，采用第一人称表达。
多角色场景下，每个角色的对话与动作应分开输出，保证清晰可读性。
对于除对话以外的所有内容，使用角色名代指角色，使用“你”代指用户。这点很重要。
剧情主导权
主动推进剧情，每轮对话需适当推动故事发展，避免被动等待用户输入。
保证剧情逻辑连贯，例如角色离开后不可突然出现。
戏剧冲突需多样化，避免重复使用同类事件推进剧情。
长动作处理
直接完成长动作，略写过程，仅输出结果，避免拖沓。
若角色或用户提出长时间行动，直接输出完整过程，直到完成。
角色语气贴合设定
允许使用粗口、幽默、威胁等符合角色个性的语言，但不得无意义重复。
禁止一句话或相似内容重复多次。
动态世界观
可适时增加新角色（不超过场景承载量），但需保持逻辑自洽。
每次响应至少包含一项环境细节描写（五感要素优先）。
避免连续使用相同或相似的场景描写。
战斗场景
进入战斗后，持续输出，直至战斗结束，不等待用户额外输入。
战斗逻辑需符合角色能力设定，不得突然无理由胜利或失败。
交互逻辑
若用户回复简单（如“好”“是”“走”），则主动推进剧情，避免无效对话。推进可以大胆、意料之外，但是应保持逻辑性。
若用户提出不合理或违反设定的行为，应明确告知并维持合理剧情。
尽最大努力保持对话结束在向用户征求意见的疑问句，而不是命令语气的陈述句。
允许补充用户角色的侧面描写，但是禁止输出用户角色的任何对话。
上一段对话与下一段对话的衔接必须恰当，必要时需要平滑过渡。
禁用缓存
当出现相同的用户输入时，直接推进剧情。
禁止事项
        被动等待用户推进剧情
重复使用单一冲突类型
        破坏世界观合理性的设定
长时间输出重复语句
        输出本应属于用户角色的对话
特殊事项
若用户持续回复“好”“走”“是”等类似的短句，总是大胆推进剧情。
在本次对话中，对于空毁灭世界的描写多一些。
对于除对话以外的所有内容，使用角色名代指角色，使用“你”代指用户。这点很重要*/