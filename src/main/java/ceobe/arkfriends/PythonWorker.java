package ceobe.arkfriends;

import jep.Jep;
import jep.JepConfig;
import jep.JepException;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PythonWorker extends Thread
{

    private Jep jep;
    private JepConfig jepConfig = new JepConfig().addIncludePaths("src//java//ceobe//arkfriends");

    private final BlockingQueue<PythonTask> taskQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public PythonWorker()
    {
        setName("Python-Jep-Worker");
        setDaemon(true); // 桌宠类程序建议 daemon
    }

    /*@Override
    public void run()
    {
        //try (Jep jep = new Jep()) {
        try {
            //this.jep = jep;
            this.jep=jepConfig.createSubInterpreter();

            System.out.println("[PythonWorker] Jep initialized.");

            // 🔹 你可以在这里 import / exec 初始化 Python 环境
            // jep.eval("import sys");
            // jep.eval("sys.path.append('xxx')");
            // jep.eval("from tts import GetVoiceWithRainfallZeroShot");
            String pythonFilePath = "D:\\ArkFriends\\ArkFriends\\src\\main\\py\\CosyVoiceManager.py";
            File file = new File(pythonFilePath);
            String parentDir = file.getParent();


            jep.eval("import CosyVoiceManager");

            System.out.println("成功加载CosyVoiceManager.py");
            LogRecorder.logRecorder.RecordLog("成功加载CosyVoiceManager.py");

            jep.eval("import sys");
            jep.eval("sys.path.append(r'" + parentDir + "')");
            jep.eval("from CosyVoiceManager import GetVoiceWithRainfallZeroShot");
            jep.eval("print('Functions loaded:', dir())");

            while (running) {
                PythonTask task = taskQueue.take(); // 阻塞等待任务
                try {
                    task.run(jep);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("[PythonWorker] Jep encountered an error:");
            LogRecorder.logRecorder.RecordLog("[PythonWorker] Jep encountered an error:");
            e.printStackTrace();
        }

        System.out.println("[PythonWorker] Worker stopped.");
    }*/
    @Override
    public void run() {
        try {
            this.jep = jepConfig.createSubInterpreter();
            System.out.println("[PythonWorker] Jep initialized.");

            // 获取 Python 文件路径
            //String pythonFilePath = "D:\\ArkFriends\\ArkFriends\\src\\main\\py\\CosyVoiceManager.py";
            String pythonFilePath = "src//main//py//CosyVoiceManager.py";

            File file = new File(pythonFilePath);
            String parentDir = file.getParent();

            // 添加路径到 sys.path
            jep.eval("import sys");

            jep.eval("print(sys.executable)");
            jep.eval("print(sys.path)");

            jep.eval("sys.path.append(r'" + parentDir + "')");

            // 导入模块
            jep.eval("import CosyVoiceManager");
            System.out.println("成功加载CosyVoiceManager.py");
            LogRecorder.logRecorder.RecordLog("成功加载CosyVoiceManager.py");

            jep.eval("from CosyVoiceManager import GetVoiceWithRainfallZeroShot");
            jep.eval("print('Functions loaded:', dir())");

            while (running) {
                PythonTask task = taskQueue.take();
                try {
                    task.run(jep);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("[PythonWorker] Jep encountered an error:");
            LogRecorder.logRecorder.RecordLog("[PythonWorker] Jep encountered an error:");
            e.printStackTrace();
        }

        System.out.println("[PythonWorker] Worker stopped.");
    }

    public void submit(PythonTask task)
    {
        if (!running) {
            throw new IllegalStateException("PythonWorker already stopped");
        }
        taskQueue.offer(task);
    }

    public void shutdown()
    {
        running = false;
        this.interrupt();
    }
}
