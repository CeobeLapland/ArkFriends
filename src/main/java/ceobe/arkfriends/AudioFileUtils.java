package ceobe.arkfriends;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 音频文件工具类
 */
public class AudioFileUtils {

    /**
     * 获取音频文件信息
     */
    public static AudioInfo getAudioInfo(File audioFile) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioStream.getFormat();

        long frames = audioStream.getFrameLength();
        double duration = (frames + 0.0) / format.getFrameRate();

        audioStream.close();

        return new AudioInfo(
                audioFile.getPath(),
                audioFile.getName(),
                duration,
                format.getSampleRate(),
                format.getChannels(),
                audioFile.length()
        );
    }

    /**
     * 列出目录中的音频文件
     */
    public static List<File> listAudioFiles(String directory, String... extensions) {
        List<File> audioFiles = new ArrayList<>();
        File dir = new File(directory);

        if (dir.exists() && dir.isDirectory()) {
            String[] exts = extensions.length > 0 ? extensions : new String[]{".wav", ".mp3", ".ogg"};

            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    String fileName = file.getName().toLowerCase();
                    for (String ext : exts) {
                        if (fileName.endsWith(ext.toLowerCase())) {
                            audioFiles.add(file);
                            break;
                        }
                    }
                }
            }
        }

        return audioFiles;
    }

    /**
     * 复制音频文件到指定目录
     */
    public static String copyAudioFile(File sourceFile, String targetDirectory) throws IOException {
        createDirectoryIfNotExists(targetDirectory);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "voice_" + timestamp + getFileExtension(sourceFile.getName());
        Path targetPath = Paths.get(targetDirectory, fileName);

        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    /**
     * 创建目录（如果不存在）
     */
    public static void createDirectoryIfNotExists(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 音频信息类
     */
    public static class AudioInfo {
        private String filePath;
        private String fileName;
        private double duration;
        private float sampleRate;
        private int channels;
        private long fileSize;

        public AudioInfo(String filePath, String fileName, double duration,
                         float sampleRate, int channels, long fileSize) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.duration = duration;
            this.sampleRate = sampleRate;
            this.channels = channels;
            this.fileSize = fileSize;
        }

        // Getters
        public String getFilePath() { return filePath; }
        public String getFileName() { return fileName; }
        public double getDuration() { return duration; }
        public float getSampleRate() { return sampleRate; }
        public int getChannels() { return channels; }
        public long getFileSize() { return fileSize; }

        public String getFormattedDuration() {
            int minutes = (int) (duration / 60);
            int seconds = (int) (duration % 60);
            return String.format("%02d:%02d", minutes, seconds);
        }

        public String getFormattedFileSize() {
            if (fileSize < 1024) {
                return fileSize + " B";
            } else if (fileSize < 1024 * 1024) {
                return String.format("%.1f KB", fileSize / 1024.0);
            } else {
                return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
            }
        }
    }
}