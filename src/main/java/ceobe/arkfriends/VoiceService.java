package ceobe.arkfriends;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javafx.scene.media.AudioClip;

public class VoiceService
{
    public static VoiceService voice;

    private final AudioRecorder recorder = new AudioRecorder();
    private final WhisperRecognizer asr = new WhisperRecognizer();
    private final PiperSynthesizer tts = new PiperSynthesizer();
    private final AudioPlayer player = new AudioPlayer();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private File inputWav = new File(VoiceConfig.INPUT_WAV);


    public VoiceService()
    {
        if(voice==null)
            voice = this;
    }
    public void StartService()
    {
        new File("temp").mkdirs();
        System.out.println("[Voice] Service started");
    }

    public void StopService() {
        executor.shutdownNow();
        player.Stop();
        System.out.println("[Voice] Service stopped");
    }


    public void StartListening() {
        try {
            recorder.Start(inputWav);
            System.out.println("[Voice] Recording...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void StopListeningAndProcess(java.util.function.Consumer<String> onText)
    {
        recorder.Stop();

        executor.submit(() -> {
            String text = asr.Recognize(inputWav);
            onText.accept(text);
        });
    }

    public void Speak(String text) {
        executor.submit(() -> {
            File wav = tts.Synthesize(text);
            if (wav != null) {
                player.Play(wav);
            }
        });
    }
}
class VoiceConfig
{
    // Whisper
    //public static String WHISPER_EXEC = "whisper/main";
    //public static String WHISPER_MODEL = "models/ggml-base.bin";

    public static String WHISPER_EXEC = "whisper/main";
    public static String WHISPER_MODEL = "models/ggml-base.bin";
    // Piper
    //public static String PIPER_EXEC = "piper/piper.exe";
    //public static String PIPER_MODEL = "models/zh_CN-huayan-medium.onnx";

    public static String PIPER_EXEC = "D:\\ArkFriends\\ArkFriends\\src\\main\\resources\\piper\\piper.exe";
    public static String PIPER_MODEL = "D:\\ArkFriends\\ArkFriends\\src\\main\\resources\\piper\\models\\zh_CN-huayan-medium.onnx";

    // 临时音频文件
    public static String INPUT_WAV = "temp/input.wav";
    public static String OUTPUT_WAV = "temp/output.wav";

    // 录音参数
    public static float SAMPLE_RATE = 16000f;
}

class AudioRecorder
{
    private TargetDataLine line;
    private Thread recordingThread;
    private boolean recording = false;

    public void Start(File wavFile) throws Exception
    {
        AudioFormat format = new AudioFormat(
                VoiceConfig.SAMPLE_RATE,
                16,
                1,
                true,
                false
        );
        line = AudioSystem.getTargetDataLine(format);
        line.open(format);
        line.start();

        recording = true;

        recordingThread = new Thread(() -> {
            try (AudioInputStream ais = new AudioInputStream(line)) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        recordingThread.start();
    }

    public void Stop()
    {
        if (line != null) {
            recording = false;
            line.stop();
            line.close();
        }
    }
}
class AudioPlayer
{
    private AudioClip current;

    public void Play(File wavFile)
    {
        if (current != null) {
            current.stop();
        }
        current = new AudioClip(wavFile.toURI().toString());
        current.play();
    }
    public void Stop()
    {
        if (current != null) {
            current.stop();
        }
    }
}
class WhisperRecognizer
{
    public String Recognize(File wavFile)
    {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    VoiceConfig.WHISPER_EXEC,
                    "-m", VoiceConfig.WHISPER_MODEL,
                    "-f", wavFile.getAbsolutePath(),
                    "-l", "zh"
            );

            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );

            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (!line.startsWith("[")) {
                    text.append(line).append(" ");
                }
            }
            p.waitFor();
            return text.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
class PiperSynthesizer
{
    public File Synthesize(String text)
    {
        File out = new File(VoiceConfig.OUTPUT_WAV);
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    VoiceConfig.PIPER_EXEC,
                    "--model", VoiceConfig.PIPER_MODEL,
                    "--output_file", out.getAbsolutePath()
            );
            Process p = pb.start();

            try (BufferedWriter writer =
                         new BufferedWriter(new OutputStreamWriter(p.getOutputStream()))) {
                writer.write(text);
            }

            p.waitFor();
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}