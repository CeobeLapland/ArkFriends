/*package ceobe.arkfriends;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;

//JavaFX中的CosyVoice管理器
 
public class CosyVoiceManager
{
    private CosyVoiceClient client;
    private MediaPlayer currentPlayer;

    public CosyVoiceManager() {
        this.client = new CosyVoiceClient();
    }

    public CosyVoiceManager(String apiUrl, String outputDir) {
        this.client = new CosyVoiceClient(apiUrl, outputDir);
    }

//异步生成语音
     
    public CompletableFuture<String> generateSpeechAsync(String text, String voiceFile,
                                                         String promptText, float speed) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 生成文件名
                String timestamp = String.valueOf(System.currentTimeMillis());
                String outputName = "speech_" + timestamp;

                // 调用生成接口
                return client.generateSpeech(text, voiceFile, promptText, speed, outputName);
            } catch (Exception e) {
                throw new RuntimeException("语音生成失败", e);
            }
        });
    }

//播放音频文件
     
    public void playAudio(String audioFilePath) {
        stopCurrentAudio(); // 停止当前播放

        try {
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                throw new FileNotFoundException("音频文件不存在: " + audioFilePath);
            }

            // 创建Media对象
            String mediaUrl = audioFile.toURI().toString();
            Media media = new Media(mediaUrl);

            // 创建播放器
            currentPlayer = new MediaPlayer(media);
            currentPlayer.setOnEndOfMedia(this::stopCurrentAudio);
            currentPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//停止当前播放
     
    public void stopCurrentAudio() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }

//检查服务状态
     
    public boolean isServiceAvailable() {
        return client.checkService();
    }

//生成并立即播放
     
    public void generateAndPlay(String text, String voiceFile, String promptText, float speed) {
        Task<String> generateTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateMessage("正在生成语音...");
                String timestamp = String.valueOf(System.currentTimeMillis());
                return client.generateSpeech(text, voiceFile, promptText, speed, "temp_" + timestamp);
            }
        };

        generateTask.setOnSucceeded(event -> {
            String audioPath = generateTask.getValue();
            if (audioPath != null) {
                updateMessage("播放语音...");
                playAudio(audioPath);
            }
        });

        generateTask.setOnFailed(event -> {
            updateMessage("语音生成失败: " + generateTask.getException().getMessage());
        });

        new Thread(generateTask).start();
    }

    private void updateMessage(String message) {
        // 这里可以更新UI状态，例如：
        // statusLabel.setText(message);
        System.out.println(message);
    }
}*/



/*Python


'''from gradio_client import Client, handle_file

client = Client("http://127.0.0.1:7860/")
result = client.predict(
    input_text="",
    prompt_wav=handle_file(''),
    prompt_text="",
    speed=1,
    output_dir="",
    output_file_name="",
    single_file_suffix="wav",
    api_name="/rainfall_gen_zero_shot"
)
print(result)


API name: /rainfall_gen_zero_shot
from gradio_client import Client, handle_file

client = Client("http://127.0.0.1:7860/")
result = client.predict(
    input_text="",
    prompt_wav=handle_file(),
    prompt_text="",
    speed=1,
    output_dir="D:\cosyvoice3-rainfall-v1\cosyvoice-rainfall-v1\cosyvoice-rainfall/outputs",
    output_file_name="",
    single_file_suffix="wav",
    api_name="/rainfall_gen_zero_shot"
)
print(result)
'''
'''
Accepts 7 parameters:
input_text str Default: ""

The input value that is provided in the "待处理文本" Textbox component.

prompt_wav filepath Required

The input value that is provided in the "音色文件(最长不要超过30秒)" Audio component. The FileData class is a subclass of the GradioModel class that represents a file object within a Gradio interface. It is used to store file data and metadata when a file is uploaded. Attributes: path: The server file path where the file is stored. url: The normalized server URL pointing to the file. size: The size of the file in bytes. orig_name: The original filename before upload. mime_type: The MIME type of the file. is_stream: Indicates whether the file is a stream. meta: Additional metadata used internally (should not be changed).

prompt_text str Default: ""

The input value that is provided in the "提示语音对应的文本(支持自动识别，支持手动修改)" Textbox component.

speed float Default: 1

The input value that is provided in the "语速" Slider component.

output_dir str Default: ""

The input value that is provided in the "保存路径" Textbox component.

output_file_name str Default: ""

The input value that is provided in the "保存文件名[为空则根据时间戳自动生成,不需要后缀]" Textbox component.

single_file_suffix Literal['wav'] Default: "wav"

The input value that is provided in the "文件格式[后缀名]" Dropdown component.

Returns 1 element
filepath

The output value that appears in the "生成结果预览" Audio component.'''




'''
helloGPT，我在用javafx做项目，现在准备把cosyvoice加到我的项目里，找到了一个大佬做的整合包，他提供的API调用实例我放在下面了（描述有点长），现在就是想请你帮忙写java和Python的音频管理和调用脚本，因为我没学过Python），谢谢你啦！
API name: /rainfall_gen_zero_shot
from gradio_client import Client, handle_file

client = Client("http://127.0.0.1:7860/")
result = client.predict(
    input_text="",#待处理文本
    prompt_wav=handle_file(''),#音色文件(最长不要超过30秒)
    prompt_text="",#提示语音对应的文本(支持自动识别，支持手动修改)
    speed=1,#语速
    output_dir="",#保存路径
    output_file_name="",#保存文件名[为空则根据时间戳自动生成,不需要后缀]
    single_file_suffix="wav",#文件格式[后缀名]
    api_name="/rainfall_gen_zero_shot"#生成结果
)
print(result)


Accepts 7 parameters:
input_text str Default: ""

prompt_wav filepath Required
The FileData class is a subclass of the GradioModel class that represents a file object within a Gradio interface.It is used to store file data and metadata when a file is uploaded.Attributes: path: The server file path where the file is stored.url: The normalized server URL pointing to the file.size: The size of the file in bytes. orig_name: The original filename before upload. mime_type: The MIME type of the file. is_stream: Indicates whether the file is a stream. meta: Additional metadata used internally (should not be changed).

prompt_text str Default: ""
speed float Default: 1
output_dir str Default: ""
output_file_name str Default: ""
single_file_suffix Literal['wav'] Default: "wav"

Returns 1 element
filepath
'''
 */