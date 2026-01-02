package ceobe.arkfriends;

//import com.sun.util.*;
import jep.Jep;
import jep.JepException;
import jep.JepConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class VoiceService
{
    public static VoiceService voiceService;

    private Jep jep;
    private JepConfig config;

    public String outputDir="D:\\ArkFriends\\ArkFriends\\temp\\outputs";
    public String voiceServerDir="D:\\cosyvoice3-rainfall-v2\\cosyvoice-rainfall-v2\\cosyvoice-rainfall\\rainfall_starter.exe";

    public Map<String, CharacterVoicePresets> characterVoicePresetsMap;
    public String chineseVoicePreset;
    public String japaneseVoicePreset;
    public String englishVoicePreset;

    public VoiceService()
    {
        System.out.println("初始化VoiceService");
        if (voiceService==null)
        {
            voiceService=this;
        }
        //System.out.println(System.getProperty("java.library.path"));

        config = new JepConfig();
        config.addIncludePaths("src//java//ceobe//arkfriends");
        try {
            jep = config.createSubInterpreter();

            jep.eval("import os");
            jep.eval("print(os.getcwd())");



            String pythonFilePath = "D:\\ArkFriends\\ArkFriends\\src\\main\\py\\CosyVoiceManager.py";
            File file = new File(pythonFilePath);
            String parentDir = file.getParent();

            jep.eval("import sys");
            jep.eval("sys.path.append(r'" + parentDir + "')");
            jep.eval("from CosyVoiceManager import GetVoiceWithRainfallZeroShot");
            jep.eval("print('Functions loaded:', dir())");

            //jep.eval("import CosyVoiceManager");
            // 获取 Python 文件的绝对路径
            //String pythonFilePath = "D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\arkfriends\\CosyVoiceManager.py";
            /*String pythonFilePath = "D:\\ArkFriends\\ArkFriends\\src\\main\\py\\CosyVoiceManager.py";
            File file = new File(pythonFilePath);
            String parentDir = file.getParent();

            jep.eval("import sys");
            jep.eval("sys.path.append(r'" + parentDir + "')");
            jep.eval("import CosyVoiceManager");*/

            System.out.println("成功加载CosyVoiceManager.py");

            jep.eval("import sys");
            jep.eval("print(sys.path)");
            jep.eval("print('CosyVoiceManager loaded successfully')");

        } catch (JepException e) {
            e.printStackTrace();
            System.out.println("加载CosyVoiceManager.py失败");
        }

        /*System.out.println("启动语音服务程序");
        //启动voiceServerDir下的exe文件
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(voiceServerDir);
            processBuilder.start();
            System.out.println("成功启动exe文件：" + voiceServerDir);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("启动exe文件失败：" + voiceServerDir);
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("D:\\cosyvoice3-rainfall-v2\\cosyvoice-rainfall-v2\\cosyvoice-rainfall\\CosyVoice3雨落版启动器.exe");
            processBuilder.start();
            System.out.println("成功启动exe文件：" + "D:\\cosyvoice3-rainfall-v2\\cosyvoice-rainfall-v2\\cosyvoice-rainfall\\CosyVoice3雨落版启动器.exe");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("启动exe文件失败：" + "D:\\cosyvoice3-rainfall-v2\\cosyvoice-rainfall-v2\\cosyvoice-rainfall\\CosyVoice3雨落版启动器.exe");
        }*/


        LoadVoicePresets();
        chineseVoicePreset=characterVoicePresetsMap.get(AnimationController.animationController.curCharName).chinesePreset;
        japaneseVoicePreset=characterVoicePresetsMap.get(AnimationController.animationController.curCharName).japanesePreset;
    }
    //从characterVoice.json读取json文件存入characterVoicePresetsMap里
    public void LoadVoicePresets()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 读取 JSON 文件并解析为 Map
            Map<String, Map<String, String>> data = objectMapper.readValue(
                Paths.get("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\characterVoice.json").toFile(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Map.class)
            );

            // 初始化 characterVoicePresetsMap
            characterVoicePresetsMap = new HashMap<>();

            // 遍历 JSON 数据并填充 characterVoicePresetsMap
            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                String characterName = entry.getKey();
                Map<String, String> presets = entry.getValue();
                String chinesePreset = presets.get("cn");
                String japanesePreset = presets.get("jp");

                // 创建 CharacterVoicePresets 对象并存入 Map
                characterVoicePresetsMap.put(characterName, new CharacterVoicePresets(chinesePreset, japanesePreset));
            }

            System.out.println("成功加载角色语音预设！");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("加载角色语音预设失败！");
        }
    }
    public void GetVoiceWithRainfallZeroShot(String inputText, String promptWav,
                                             String promptText, int speed, String outputDir,
                                             String outputFileName, String singleFileSuffix)
    {
        try {
            //jep.invoke("GetVoiceWithRainfallZeroShot", inputText, promptWav, promptText, speed, outputDir, outputFileName, singleFileSuffix);
            jep.eval("GetVoiceWithRainfallZeroShot(r'" + inputText + "', r'" + promptWav + "', r'" +promptText + "', " + speed + ", r'" + outputDir + "', r'" + outputFileName + "', r'" + singleFileSuffix + "')");
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
    public void GetVoiceWithRainfallZeroShot(String inputText)
    {
        try {
            jep.invoke("GetVoiceWithRainfallZeroShot", inputText, chineseVoicePreset, "",
                    1, outputDir, "", "wav");
            //jep.eval("GetVoiceWithRainfallZeroShot(r'" + inputText + "', r'" + chineseVoicePreset + "', r'', 1, r'" + outputDir + "', r'', r'wav')");
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
    public void GetVoiceWithRainfallSFT(String inputText, String sftDropdown)
                                        //String outputDir, String outputFileName)
    {
        try {
            jep.invoke("GetVoiceWithRainfallSFT",
                    inputText, sftDropdown, 1, outputDir, "", "wav");
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
                                             String instructText)
    {
        try {
            jep.invoke("GetVoiceWithRainfallInstruct", inputText, sftDropdown, instructText,
                    1, outputDir, "", "wav");
        } catch (JepException e) {
            e.printStackTrace();
        }
    }
}
class CharacterVoicePresets
{
    //public String characterName;
    public String chinesePreset;
    public String japanesePreset;
    //public String englishPreset;

    public CharacterVoicePresets(String chinesePreset, String japanesePreset)
    {
        this.chinesePreset = chinesePreset;
        this.japanesePreset = japanesePreset;
    }
}