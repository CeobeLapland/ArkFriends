package ceobe.arkfriends;

import jep.Jep;
import jep.JepException;
import jep.JepConfig;

public class VoiceService
{
    public static VoiceService voiceService;

    private Jep jep;
    private JepConfig config;
    public VoiceService()
    {
        if (voiceService==null)
        {
            voiceService=this;
        }

        config = new JepConfig();
        config.addIncludePaths("src//java//ceobe//arkfriends");
        try {
            jep = config.createSubInterpreter();
            jep.eval("import CosyVoiceManager");
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
    public void GetVoiceWithRainfallZeroShot(String inputText, String promptWav,
                                             String promptText, int speed, String outputDir,
                                             String outputFileName, String singleFileSuffix)
    {
        try {
            jep.invoke("GetVoiceWithRainfallZeroShot", inputText, promptWav, promptText,
                    speed, outputDir, outputFileName, singleFileSuffix);
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
    public void GetVoiceWithRainfallZeroShot(String inputText, String promptWav, String outputDir)
    {
        try {
            jep.invoke("GetVoiceWithRainfallZeroShot", inputText, promptWav, "",
                    1, outputDir, "", "wav");
        } catch (JepException e) {
            e.printStackTrace();
        }
    }

    public void GetPromptWavRecognition(String audioPath)
    {
        try {
            jep.invoke("GetPromptWavRecognition", audioPath);
        } catch (JepException e) {
            e.printStackTrace();
        }
    }

    public void GetVoiceWithRainfallSFT(String inputText, String sftDropdown, int speed,
                                        String outputDir, String outputFileName, String singleFileSuffix)
    {
        try {
            jep.invoke("GetVoiceWithRainfallSFT", inputText, sftDropdown, speed, outputDir,
                    outputFileName, singleFileSuffix);
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
    public void GetVoiceWithRainfallSFT(String inputText, String sftDropdown,
                                        String outputDir, String outputFileName)
    {
        try {
            jep.invoke("GetVoiceWithRainfallSFT",
                    inputText, sftDropdown, 1, outputDir, outputFileName, "wav");
        } catch (JepException e) {
            e.printStackTrace();
        }
    }

    public void GetVoiceWithRainfallInstruct(String inputText, String sftDropdown,
                                             String instructText, int speed, String outputDir,
                                             String outputFileName, String singleFileSuffix)
    {
        try {
            jep.invoke("GetVoiceWithRainfallInstruct", inputText, sftDropdown, instructText,
                    speed, outputDir, outputFileName, singleFileSuffix);
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
    public void GetVoiceWithRainfallInstruct(String inputText, String sftDropdown,
                                             String instructText, String outputDir,
                                             String outputFileName)
    {
        try {
            jep.invoke("GetVoiceWithRainfallInstruct", inputText, sftDropdown, instructText,
                    1, outputDir, outputFileName, "wav");
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
    /*public void GetVoiceWithRainfallPromptText(String inputText, String promptText,
                                               int speed, String outputDir,
                                               String outputFileName, String singleFileSuffix) {
        try {
            jep.invoke("GetVoiceWithRainfallPromptText", inputText, promptText,
                    speed, outputDir, outputFileName, singleFileSuffix);
        } catch (JepException e) {
            e.printStackTrace();
        }
    }*/
    /*public void Speak(String text) {
        try {
            jep.invoke("CosyVoiceManager.speak_text", text);
        } catch (JepException e) {
            e.printStackTrace();
        }
    }*/
    /*public void StartService() {
        try (Jep jep = new Jep()) {
            jep.eval("import pyttsx3");
            jep.eval("engine = pyttsx3.init()");
            jep.eval("engine.setProperty('rate', 150)"); // 设置语速
            jep.eval("engine.setProperty('volume', 1.0)"); // 设置音量
        } catch (JepException e) {
            e.printStackTrace();
        }
    }*/
    public void Test()
    {
        // 1. 创建 Jep 配置对象（替代原构造方法的参数）
        JepConfig config = new JepConfig();
        // 设置 Python 脚本路径（对应旧版本的 "src/main/resources" 参数）
        config.addIncludePaths("src/main/resources");
        // 可选：关闭共享模式（对应旧版本的第一个参数 false）
        //config.setSharedModules(false);
        //config.setSharedInterpreter(false);
        //关闭共享模式
        //config.setInteractive(false);


        // 2. 通过配置创建 Jep 实例（关键：不再 new Jep()）
        try (
                //Jep jep = config.createJep()
                // 适配 4.0-4.2 版本的静态创建方法
                //Jep jep = Jep.create(config);
                Jep jep=config.createSubInterpreter();
        ) { // 自动关闭资源（try-with-resources）
            // 3. 后续调用 Python 函数的逻辑和旧版本一致
            jep.eval("import demo"); // 导入 demo.py

            // 调用 add 函数
            int addResult = (int) jep.invoke("demo.add", 10, 20);
            System.out.println("add(10,20) 结果：" + addResult); // 输出 30

            // 调用 say_hello 函数
            String helloResult = (String) jep.invoke("demo.say_hello", "Java");
            System.out.println("say_hello(Java) 结果：" + helloResult); // 输出 "Hello, Java!"

            // 调用带 numpy 的函数
            jep.eval("import numpy as np");
            jep.eval("def calc_sum(arr): return np.sum(arr)");
            double sumResult = (double) jep.invoke("calc_sum", new int[]{1,2,3,4});
            System.out.println("numpy 求和结果：" + sumResult); // 输出 10.0

        } catch (JepException e) {
            e.printStackTrace();
        }
        /*try (Jep jep = new Jep(false, "src/main/resources")) { // 脚本所在目录
            // 2. 导入 Python 模块并调用函数
            jep.eval("import demo"); // 导入 demo.py

            // 调用 add 函数
            int addResult = (int) jep.invoke("demo.add", 10, 20);
            System.out.println("add(10,20) 结果：" + addResult); // 输出 30

            // 调用 say_hello 函数
            String helloResult = (String) jep.invoke("demo.say_hello", "Java");
            System.out.println("say_hello(Java) 结果：" + helloResult); // 输出 "Hello, Java!"

            // 支持调用带 numpy 的函数（示例）
            jep.eval("import numpy as np");
            jep.eval("def calc_sum(arr): return np.sum(arr)");
            double sumResult = (double) jep.invoke("calc_sum", new int[]{1,2,3,4});
            System.out.println("numpy 求和结果：" + sumResult); // 输出 10.0
        } catch (JepException e) {
            e.printStackTrace();
        }*/
    }

}