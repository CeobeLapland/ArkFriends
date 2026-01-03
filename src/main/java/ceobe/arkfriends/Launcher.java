package ceobe.arkfriends;

import javafx.application.Application;

import javafx.fxml.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.*;

import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Timer;

import javafx.fxml.FXML;

import javafx.stage.FileChooser;

public class Launcher extends Application
{
    public static Launcher launcher;
    public Stage launcherStage,petStage;
    FXMLLoader launcherFxml,petFxml;
    public Scene launcherScene,petScene;
    public void start(Stage stage) throws IOException
    {
        launcher=this;
        launcherStage=stage;

        //if(PanelController.panelController==null)
        //    PanelController.panelController=new PanelController();
        //是不是这两个不一个，那边stage创建时自动创建了一个controller

        //new VoiceService();

        launcherFxml = new FXMLLoader(Launcher.class.getResource("mainPanel.fxml"));
        System.out.println("加载FXML");
        launcherScene=new Scene(launcherFxml.load());
        stage.setScene(launcherScene);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        PanelController.panelController.AddWindowsEffect();//当时这个顺序放错位置了
        //一直报错
        //草了，反而这里对了，线程顺序怎么搞的

        //new VoiceServiceWP();
        //VoiceServiceWP.voice.StartService();
        //VoiceServiceWP.voice.Speak("博士、もう遅いです。もう休みましょう\n");
        //程序启动成功，博士，已经很晚了，该休息啦

        /*new TrayManager();
        TrayManager.trayManager.init(stage);
        stage.setOnCloseRequest(event->{
            event.consume();
            //阻止事件传播
            stage.hide();
        });*/

        //new VoiceService();//放下面了

        //System.out.println("初始化前");
        //initializeTTS();
        //generateSpeech();
        //System.out.println("初始化后");
    }

    public void StartRunning() throws IOException
    {
        //if(AnimationController.animationController==null)
        //    AnimationController.animationController=new AnimationController();

        //我想破脑袋都没想到是这里出的问题
        //详细说明在AnimationController的构造函数里
        petFxml= new FXMLLoader(Launcher.class.getResource("petPanel.fxml"));

        //加了一句比较重要的
        //还是不太对，之后还需要提前实例化AnimationController然后手动分配
        //把builder里的默认选项去掉
        if(AnimationController.animationController!=null)
            petFxml.setController(AnimationController.animationController);

        petScene=new Scene(petFxml.load());
        //launcherStage.setScene(petScene);
        //launcherStage.close();
        //launcherStage.hide();

        petScene.setFill(Color.TRANSPARENT);

        petStage=new Stage();
        petStage.setScene(petScene);//这个有问题
        petStage.initStyle(StageStyle.TRANSPARENT);

        petStage.setAlwaysOnTop(true);
        petStage.show();



        /*if(AnimationController.animationController.rootPane==null)
            System.out.println("rootPane is null 3");
        if(AnimationController.animationController.content==null)
            System.out.println("content is null 3");*/

        //AnimationController.animationController.Initialize();
        Timer timer=new Timer();
        timer.schedule(new java.util.TimerTask()
        {
            @Override
            public void run()
            {
                AnimationController.animationController.DelayedInitialization();

                new AIChatManager();
                AIChatManager.ACM.ChangePresetDescription(AnimationController.animationController.curCharName);

                //new VoiceService();

                //VoiceService.voiceService.GetVoiceWithRainfallZeroShot("博士博士晚上好呀，今天也要天天开心呀");
                //这个也必须放在这里面
                //我也不知道为什么，明明ACM里面不涉及到UI更新，但他还是会导致timeline线程爆炸
                new WindowsScanner();
                AnimationController.animationController.petWindowHandle = WindowsScanner.windowsScanner.GetStageHwnd(petStage);
                PanelController.panelController.mainWindowHandle= WindowsScanner.windowsScanner.GetStageHwnd(launcherStage);


                timer.cancel();
            }
        },500);//延迟一秒执行
        //AnimationController.animationController.DelayedInitialization();
        //放到DelayedInitialization里去了

        //new AIChatManager();//放里面了
    }


    /*@FXML private TextArea inputText;
    @FXML private TextField voiceFileField;
    @FXML private TextField promptText;
    @FXML private Slider speedSlider;
    @FXML private Label statusLabel;
    @FXML private Button playButton;
    @FXML private Button stopButton;*/

    /*private CosyVoiceManager ttsManager;
    private String currentAudioPath;

    public void initializeTTS()
    {
        ttsManager = new CosyVoiceManager();

        // 检查服务状态
        if (ttsManager.isServiceAvailable()) {
            //statusLabel.setText("服务已连接");
            //statusLabel.setStyle("-fx-text-fill: green;");
            System.out.println("CosyVoice TTS服务已连接");
        } else {
            //statusLabel.setText("服务未连接");
            //statusLabel.setStyle("-fx-text-fill: red;");
            System.out.println("无法连接到CosyVoice TTS服务，请确保服务正在运行");
        }

        // 设置默认值
        //speedSlider.setValue(1.0);
    }

    @FXML
    private void selectVoiceFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择音色文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("音频文件", "*.wav", "*.mp3")
        );

        java.io.File selectedFile = fileChooser.showOpenDialog(null);
        //if (selectedFile != null) {
        //    voiceFileField.setText(selectedFile.getAbsolutePath());
        //}
    }

    @FXML
    private void generateSpeech() {
        //String text = inputText.getText();
        //String voiceFile = voiceFileField.getText();

        if (text.isEmpty()) {
            showAlert("错误", "请输入要合成的文本");
            return;
        }

        if (voiceFile.isEmpty()) {
            showAlert("错误", "请选择音色文件");
            return;
        }

        statusLabel.setText("正在生成语音...");

        ttsManager.generateSpeechAsync(
                text,
                voiceFile,
                promptText.getText(),
                (float) speedSlider.getValue()
        ).thenAccept(audioPath -> {
            currentAudioPath = audioPath;
            javafx.application.Platform.runLater(() -> {
                statusLabel.setText("语音生成完成");
                playButton.setDisable(false);
            });
        }).exceptionally(e -> {
            javafx.application.Platform.runLater(() -> {
                statusLabel.setText("生成失败: " + e.getCause().getMessage());
            });
            return null;
        });
        ttsManager.generateSpeechAsync(
                //text,
                //voiceFile,
                //promptText.getText(),
                //(float) speedSlider.getValue()
                "这是一条测试文本",
                "D:\\ArkFriends\\ArkFriends\\temp\\output.wav",
                "",
                1.0f
        ).thenAccept(audioPath -> {
            currentAudioPath = audioPath;
            //javafx.application.Platform.runLater(() -> {
            //    statusLabel.setText("语音生成完成");
            //    playButton.setDisable(false);
            //});
        }).exceptionally(e -> {
            javafx.application.Platform.runLater(() -> {
                //statusLabel.setText("生成失败: " + e.getCause().getMessage());
            });
            return null;
        });
    }*/

    /*@FXML
    private void playAudio() {
        if (currentAudioPath != null) {
            ttsManager.playAudio(currentAudioPath);
            playButton.setDisable(true);
            stopButton.setDisable(false);
        }
    }

    @FXML
    private void stopAudio() {
        ttsManager.stopCurrentAudio();
        playButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }*/
}