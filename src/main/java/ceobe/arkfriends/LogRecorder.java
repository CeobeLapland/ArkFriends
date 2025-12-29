package ceobe.arkfriends;

import java.io.File;


public class LogRecorder
{
    public static LogRecorder logRecorder;
    //存放日志的TXT文件路径
    //public String logFilePath = System.getProperty("user.home") + File.separator + "ArkFriendsLog.txt";
    //private File logFile = new File(logFilePath);
    public LogRecorder()
    {
        if (logRecorder == null)
            logRecorder = this;
    }
}

// LogLevel.java
/*
public enum LogLevel {
    DEBUG(0, "DEBUG"),
    INFO(1, "INFO"),
    WARN(2, "WARN"),
    ERROR(3, "ERROR"),
    FATAL(4, "FATAL");

    private final int level;
    private final String name;

    LogLevel(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
}
*/
// LoggerConfig.java
/*
public class LoggerConfig {
    private String logDir = "logs";
    private String logFileName = "application.log";
    private LogLevel logLevel = LogLevel.INFO;
    private int maxFileSizeMB = 10;
    private int maxBackupFiles = 5;
    private boolean consoleOutput = true;
    private boolean fileOutput = true;
    private String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    // 建造者模式，方便配置
    public static class Builder {
        private LoggerConfig config = new LoggerConfig();

        public Builder logDir(String logDir) {
            config.logDir = logDir;
            return this;
        }

        public Builder logFileName(String logFileName) {
            config.logFileName = logFileName;
            return this;
        }

        public Builder logLevel(LogLevel logLevel) {
            config.logLevel = logLevel;
            return this;
        }

        public Builder maxFileSizeMB(int maxFileSizeMB) {
            config.maxFileSizeMB = maxFileSizeMB;
            return this;
        }

        public Builder maxBackupFiles(int maxBackupFiles) {
            config.maxBackupFiles = maxBackupFiles;
            return this;
        }

        public Builder consoleOutput(boolean consoleOutput) {
            config.consoleOutput = consoleOutput;
            return this;
        }

        public Builder fileOutput(boolean fileOutput) {
            config.fileOutput = fileOutput;
            return this;
        }

        public Builder datePattern(String datePattern) {
            config.datePattern = datePattern;
            return this;
        }

        public LoggerConfig build() {
            return config;
        }
    }

    // Getters
    public String getLogDir() { return logDir; }
    public String getLogFileName() { return logFileName; }
    public LogLevel getLogLevel() { return logLevel; }
    public int getMaxFileSizeMB() { return maxFileSizeMB; }
    public int getMaxBackupFiles() { return maxBackupFiles; }
    public boolean isConsoleOutput() { return consoleOutput; }
    public boolean isFileOutput() { return fileOutput; }
    public String getDatePattern() { return datePattern; }
}
*/
// TextFileLogger.java
/*
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class TextFileLogger {
    private static TextFileLogger instance;
    private final LoggerConfig config;
    private final ReentrantLock lock = new ReentrantLock();
    private SimpleDateFormat dateFormatter;
    private long currentFileSize = 0;
    private String currentLogFilePath;

    private TextFileLogger(LoggerConfig config) {
        this.config = config;
        this.dateFormatter = new SimpleDateFormat(config.getDatePattern());
        initLogDirectory();
    }

    public static synchronized TextFileLogger getInstance() {
        if (instance == null) {
            instance = new TextFileLogger(new LoggerConfig());
        }
        return instance;
    }

    public static synchronized TextFileLogger getInstance(LoggerConfig config) {
        if (instance == null) {
            instance = new TextFileLogger(config);
        }
        return instance;
    }

    private void initLogDirectory() {
        try {
            Path logDirPath = Paths.get(config.getLogDir());
            if (!Files.exists(logDirPath)) {
                Files.createDirectories(logDirPath);
            }
            updateCurrentLogFile();
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
        }
    }

    private void updateCurrentLogFile() {
        currentLogFilePath = config.getLogDir() + File.separator + config.getLogFileName();
        File logFile = new File(currentLogFilePath);
        currentFileSize = logFile.exists() ? logFile.length() : 0;
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message, null);
    }

    public void fatal(String message, Throwable throwable) {
        log(LogLevel.FATAL, message, throwable);
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        if (level.getLevel() < config.getLogLevel().getLevel()) {
            return;
        }

        lock.lock();
        try {
            String logEntry = formatLogEntry(level, message, throwable);

            // 控制台输出
            if (config.isConsoleOutput()) {
                writeToConsole(logEntry, level);
            }

            // 文件输出
            if (config.isFileOutput()) {
                writeToFile(logEntry);
            }
        } finally {
            lock.unlock();
        }
    }

    private String formatLogEntry(LogLevel level, String message, Throwable throwable) {
        String timestamp = dateFormatter.format(new Date());
        String threadName = Thread.currentThread().getName();
        StringBuilder sb = new StringBuilder();

        sb.append(timestamp)
                .append(" [").append(level.getName()).append("]")
                .append(" [").append(threadName).append("]")
                .append(" - ").append(message);

        if (throwable != null) {
            sb.append("\n").append(getStackTrace(throwable));
        }

        return sb.toString();
    }

    private void writeToConsole(String logEntry, LogLevel level) {
        switch (level) {
            case ERROR:
            case FATAL:
                System.err.println(logEntry);
                break;
            default:
                System.out.println(logEntry);
        }
    }

    private void writeToFile(String logEntry) {
        try {
            // 检查是否需要轮转文件
            checkFileRotation();

            // 写入文件
            try (FileWriter fw = new FileWriter(currentLogFilePath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {

                out.println(logEntry);
                currentFileSize += logEntry.getBytes().length + System.lineSeparator().getBytes().length;
            }
        } catch (IOException e) {
            System.err.println("Failed to write log to file: " + e.getMessage());
        }
    }

    private void checkFileRotation() {
        long maxFileSizeBytes = config.getMaxFileSizeMB() * 1024L * 1024L;

        if (currentFileSize >= maxFileSizeBytes) {
            rotateLogFiles();
            updateCurrentLogFile();
        }
    }

    private void rotateLogFiles() {
        File currentFile = new File(currentLogFilePath);
        if (!currentFile.exists()) {
            return;
        }

        // 删除最旧的备份文件
        File oldestBackup = new File(config.getLogDir() + File.separator +
                config.getLogFileName() + "." + config.getMaxBackupFiles());
        if (oldestBackup.exists()) {
            oldestBackup.delete();
        }

        // 重命名现有的备份文件
        for (int i = config.getMaxBackupFiles() - 1; i >= 1; i--) {
            File oldFile = new File(config.getLogDir() + File.separator +
                    config.getLogFileName() + "." + i);
            File newFile = new File(config.getLogDir() + File.separator +
                    config.getLogFileName() + "." + (i + 1));

            if (oldFile.exists()) {
                oldFile.renameTo(newFile);
            }
        }

        // 重命名当前日志文件为第一个备份
        File firstBackup = new File(config.getLogDir() + File.separator +
                config.getLogFileName() + ".1");
        currentFile.renameTo(firstBackup);
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    // 清理资源
    public void shutdown() {
        lock.lock();
        try {
            // 这里可以添加清理逻辑
        } finally {
            lock.unlock();
        }
    }
}*/
// LogReader.java
/*

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
public class LogReader {

    public static List<String> readLatestLogs(String logDir, String logFileName, int lines) throws IOException {
        String logFilePath = logDir + File.separator + logFileName;
        File logFile = new File(logFilePath);

        if (!logFile.exists()) {
            return Collections.emptyList();
        }

        // 使用反向读取来获取最后几行
        return readLastLines(logFile, lines);
    }

    public static List<String> readLogsByLevel(String logDir, String logFileName, LogLevel level) throws IOException {
        String logFilePath = logDir + File.separator + logFileName;
        File logFile = new File(logFilePath);

        if (!logFile.exists()) {
            return Collections.emptyList();
        }

        List<String> filteredLogs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("[" + level.getName() + "]")) {
                    filteredLogs.add(line);
                }
            }
        }

        return filteredLogs;
    }

    public static List<String> searchLogs(String logDir, String logFileName, String keyword) throws IOException {
        String logFilePath = logDir + File.separator + logFileName;
        File logFile = new File(logFilePath);

        if (!logFile.exists()) {
            return Collections.emptyList();
        }

        List<String> matchedLogs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    matchedLogs.add(line);
                }
            }
        }

        return matchedLogs;
    }

    public static List<String> getAllLogFiles(String logDir) throws IOException {
        Path dirPath = Paths.get(logDir);
        if (!Files.exists(dirPath)) {
            return Collections.emptyList();
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.log*")) {
            return stream.map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private static List<String> readLastLines(File file, int lines) throws IOException {
        List<String> result = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long pointer = fileLength; pointer >= 0; pointer--) {
                raf.seek(pointer);
                byte c = raf.readByte();

                if (c == '\n') {
                    if (pointer < fileLength) {
                        result.add(0, sb.reverse().toString());
                        sb = new StringBuilder();
                    }
                    if (result.size() == lines) {
                        break;
                    }
                } else {
                    sb.append((char) c);
                }
            }
        }

        return result;
    }
}*/


// ExampleUsage.java
/*
public class ExampleUsage {

    public static void main(String[] args) {
        // 1. 使用默认配置
        TextFileLogger logger = TextFileLogger.getInstance();

        // 2. 使用自定义配置
        LoggerConfig config = new LoggerConfig.Builder()
                .logDir("myapp/logs")
                .logFileName("myapp.log")
                .logLevel(LogLevel.DEBUG)
                .maxFileSizeMB(5)
                .maxBackupFiles(10)
                .consoleOutput(true)
                .fileOutput(true)
                .build();

        TextFileLogger customLogger = TextFileLogger.getInstance(config);

        // 3. 记录日志
        logger.debug("这是一条调试信息");
        logger.info("应用启动成功");
        logger.warn("内存使用率过高");
        logger.error("文件读取失败");

        try {
            int result = 10 / 0;
        } catch (Exception e) {
            logger.error("发生除零错误", e);
        }

        // 4. 读取日志
        try {
            System.out.println("\n=== 最新的5条日志 ===");
            List<String> latestLogs = LogReader.readLatestLogs("logs", "application.log", 5);
            latestLogs.forEach(System.out::println);

            System.out.println("\n=== 所有ERROR日志 ===");
            List<String> errorLogs = LogReader.readLogsByLevel("logs", "application.log", LogLevel.ERROR);
            errorLogs.forEach(System.out::println);

            System.out.println("\n=== 搜索包含'启动'的日志 ===");
            List<String> searchedLogs = LogReader.searchLogs("logs", "application.log", "启动");
            searchedLogs.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5. 关闭日志系统
        logger.shutdown();
    }
}
*/

// AsyncLogger.java
/*import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncLogger {
    private final TextFileLogger logger;
    private final BlockingQueue<LogTask> logQueue;
    private final ExecutorService executor;
    private final AtomicBoolean isRunning;

    public AsyncLogger(TextFileLogger logger) {
        this.logger = logger;
        this.logQueue = new LinkedBlockingQueue<>(1000);
        this.executor = Executors.newSingleThreadExecutor();
        this.isRunning = new AtomicBoolean(true);

        // 启动日志消费线程
        executor.submit(this::processLogs);
    }

    public void log(LogLevel level, String message, Throwable throwable) {
        if (isRunning.get()) {
            logQueue.offer(new LogTask(level, message, throwable));
        }
    }

    private void processLogs() {
        while (isRunning.get() || !logQueue.isEmpty()) {
            try {
                LogTask task = logQueue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    switch (task.level) {
                        case DEBUG:
                            logger.debug(task.message);
                            break;
                        case INFO:
                            logger.info(task.message);
                            break;
                        case WARN:
                            logger.warn(task.message);
                            break;
                        case ERROR:
                            if (task.throwable != null) {
                                logger.error(task.message, task.throwable);
                            } else {
                                logger.error(task.message);
                            }
                            break;
                        case FATAL:
                            if (task.throwable != null) {
                                logger.fatal(task.message, task.throwable);
                            } else {
                                logger.fatal(task.message);
                            }
                            break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        isRunning.set(false);
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.shutdown();
    }

    private static class LogTask {
        final LogLevel level;
        final String message;
        final Throwable throwable;

        LogTask(LogLevel level, String message, Throwable throwable) {
            this.level = level;
            this.message = message;
            this.throwable = throwable;
        }
    }
}*/