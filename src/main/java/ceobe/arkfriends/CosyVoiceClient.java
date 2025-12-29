/*package ceobe.arkfriends;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * CosyVoice TTS Java客户端
 */
/*public class CosyVoiceClient
{
    private String apiUrl;
    private String outputDir;

    public CosyVoiceClient() {
        //this("http://127.0.0.1:5000", "output");
        this("http://127.0.0.1:7860/","D:\\ArkFriends\\ArkFriends\\temp\\outputs");
    }

    public CosyVoiceClient(String apiUrl, String outputDir) {
        this.apiUrl = apiUrl;
        this.outputDir = outputDir;
        createDirectoryIfNotExists(outputDir);
    }

    /**
     * 生成语音
     * @param text 要合成的文本
     * @param voiceFilePath 音色文件路径
     * @param promptText 提示文本
     * @param speed 语速
     * @param outputFileName 输出文件名
     * @return 生成的音频文件路径
     * @throws IOException
     */
/*    public String generateSpeech(String text, String voiceFilePath,
                                 String promptText, float speed,
                                 String outputFileName) throws IOException {

        // 验证音色文件存在
        File voiceFile = new File(voiceFilePath);
        if (!voiceFile.exists()) {
            throw new FileNotFoundException("音色文件不存在: " + voiceFilePath);
        }

        // 构建JSON请求
        JSONObject requestJson = new JSONObject();
        requestJson.put("input_text", text);
        requestJson.put("prompt_wav_path", voiceFilePath);
        requestJson.put("prompt_text", promptText != null ? promptText : "");
        requestJson.put("speed", speed);
        requestJson.put("output_file_name", outputFileName != null ? outputFileName : "");

        // 发送HTTP请求
        URL url = new URL(apiUrl + "/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 读取响应
        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode == 200 ?
                        conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {

            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == 200) {
            JSONObject responseJson = new JSONObject(response.toString());
            if (responseJson.has("filepath")) {
                String filepath = responseJson.getString("filepath");

                // 如果API返回的是本地文件路径，复制到输出目录
                if (filepath.startsWith("/") || filepath.contains(":\\")) {
                    return copyToOutputDirectory(filepath, outputFileName);
                }

                return filepath;
            } else if (responseJson.has("error")) {
                throw new IOException("语音生成失败: " + responseJson.getString("error"));
            }
        }

        throw new IOException("HTTP请求失败: " + responseCode + " - " + response.toString());
    }

    /**
     * 检查服务状态
     * @return 服务是否可用
     */
/*    public boolean checkService() {
        try {
            URL url = new URL(apiUrl + "/api/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 下载音频文件
     * @param audioUrl 音频URL
     * @param localPath 本地保存路径
     * @throws IOException
     */
/*    public void downloadAudio(String audioUrl, String localPath) throws IOException {
        URL url = new URL(audioUrl);
        try (InputStream in = url.openStream()) {
            Path targetPath = Paths.get(localPath);
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 复制文件到输出目录
     */
/*    private String copyToOutputDirectory(String sourcePath, String targetName) throws IOException {
        Path source = Paths.get(sourcePath);
        String fileName = targetName != null && !targetName.isEmpty() ?
                targetName : source.getFileName().toString();

        if (!fileName.toLowerCase().endsWith(".wav")) {
            fileName += ".wav";
        }

        Path target = Paths.get(outputDir, fileName);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    /**
     * 创建目录（如果不存在）
     */
/*    private void createDirectoryIfNotExists(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}*/