package ceobe.arkfriends;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class RightKeyPanelController
{
    public Stage petStage;

    public Stage popupStage = null;
    private Scene popupScene;


    //region 位置相关
    public double popupWidth = 200; //根据panel.fxml调整
    public double popupHeight = 100; //根据panel.fxml调整

    private double popupX = 0;
    private double popupY = 0;
    public HBox popupTitleBar;

    public void CreateSecondaryStage(Stage ownerStage, double mouseX, double mouseY)
    {
        try {
            if(popupStage==null)
            {
                // 加载panel.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("petRightKeyPanel.fxml"));
                //原来只用加上这一句
                loader.setController(this);

                System.out.println(getClass().getResource("petRightKeyPanel.fxml").toString());
                Parent popupContent = loader.load();

                System.out.println("Loaded petRightKeyPanel.fxml successfully");
                // 创建弹出窗口
                popupStage = new Stage();
//                if(popupStage==null)
//                    System.out.println("popupStage is still null");

                popupStage.initOwner(ownerStage);
                popupStage.initModality(Modality.NONE); // 非模态窗口
                popupStage.initStyle(StageStyle.UNDECORATED);
                //popupStage.initStyle(StageStyle.UTILITY); // 简洁样式
                popupStage.setAlwaysOnTop(true);


                if (popupTitleBar == null) {
                    System.out.println("popupTitleBar is null!");
                    //return;
                }
                popupTitleBar.setOnMousePressed((MouseEvent event) -> {
                    popupX = event.getSceneX();
                    popupY = event.getSceneY();
                });

                popupTitleBar.setOnMouseDragged((MouseEvent event) -> {
                    //Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    popupStage.setX(event.getScreenX() - popupX);
                    popupStage.setY(event.getScreenY() - popupY);
                });

//                popupTitleBar.setOnMouseEntered(event -> {
//                    popupTitleBar.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e);");
//                });

                // 设置场景
                popupScene = new Scene(popupContent);
                popupStage.setScene(popupScene);

            }


            //本来是想在这里把新窗口的controller设置为this的
            //还是改一改代码让窗口能够复用，这样感觉更节约资源，而且也不会多实例了

            // 计算最佳位置
            double[] position = SelectBestPosition(mouseX, mouseY);

            // 设置窗口位置
            popupStage.setX(position[0]);
            popupStage.setY(position[1]);

            // 窗口关闭时的清理
//            popupStage.setOnCloseRequest(event -> {
//                popupStage = null;
//            });
            //这个去掉系统自带的关闭按钮
            //设置成我自己的然后把close也改成hide

            // 显示窗口
            popupStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed Exception in CreateSecondaryStage");
        }
    }

    private double[] SelectBestPosition(double mouseX, double mouseY)
    {
        // 获取屏幕边界
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double x = mouseX;
        double y = mouseY;

        //优先尝试右下角
        if (mouseX + popupWidth <= screenBounds.getMaxX())
        {
            x = mouseX;
        }
        else if (mouseX - popupWidth >= screenBounds.getMinX())
        {//右边不够位置，尝试左边
            x = mouseX - popupWidth;
        }
        else
        {//左右都不够，居中显示
            x = Math.max(screenBounds.getMinX(),
                    Math.min(screenBounds.getMaxX() - popupWidth, mouseX - popupWidth / 2));
        }
        if (mouseY + popupHeight <= screenBounds.getMaxY())
        {
            y = mouseY;
        }
        else if (mouseY - popupHeight >= screenBounds.getMinY())
        {//下边不够位置，尝试上边
            y = mouseY - popupHeight;
        }
        else
        {//上下都不够，居中显示
            y = Math.max(screenBounds.getMinY(),
                    Math.min(screenBounds.getMaxY() - popupHeight, mouseY - popupHeight / 2));
        }

        return new double[]{x, y};
    }

    public boolean isClickInPopup(double clickX, double clickY)
    {
        if (popupStage == null || !popupStage.isShowing()) {
            return false;
        }

        /*double windowX = popupStage.getX();
        double windowY = popupStage.getY();
        double windowWidth = popupStage.getWidth();
        double windowHeight = popupStage.getHeight();

        return clickX >= windowX &&
                clickX <= windowX + windowWidth &&
                clickY >= windowY &&
                clickY <= windowY + windowHeight;*/
        return clickX >= popupX && clickX <= popupX + popupWidth &&
                clickY >= popupY && clickY <= popupY + popupHeight;
    }
    public void ExitPet()
    {
        AnimationController.animationController.StopAnimation();
        AnimationController.animationController.stage.close();

        //按理来说这样写绝对的违反设计流程
        //跨脚本乱调用函数
        PanelController.panelController.launchButton.setDisable(false);
    }
    //屏蔽掉所有鼠标点击事件
    public void ChangeToTransparent()
    {

    }
    public void ChangeToNormal()
    {

    }

    public void MinimizePetStage()
    {
        AnimationController.animationController.stage.close();
        //Launcher.launcher.petStage.close();
    }

    public void HidePopupStage()
    {
        popupStage.hide();
    }
    //endregion

    public TextField inputField;
    public Button sendButton;

    public void SendMessage()
    {
        String userText = inputField.getText();
        if(Objects.equals(userText, ""))
            return;
        inputField.clear();

        Platform.runLater(()->{
            AIChatManager.ACM.sendMessageAsync(userText)
                    .thenAccept(reply -> {
                        Platform.runLater(() -> {
                            //chatArea.appendText("桌宠：" + reply + "\n");
                            PrinterFlow("桌宠：" + reply + "\n");
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        });
    }



    //region 打字机相关
    //public DialogPrinter dialogPrinter;
    //public Label dialogLabel;
    public Text dialog;

    public Stage dialogStage;
    private Scene dialogScene;

    public Timeline printerLine;
    private String curText;
    private int textSize;
    private int curTextIndex;

    public void InitializeDialogPrinter()// throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogPrinter.fxml"));
        loader.setController(this);

        //System.out.println(getClass().getResource("dialogPrinter.fxml").toString());
        //System.out.println("dialogPrinter.fxml");
        //Parent tempContent = loader.load();
        System.out.println("Loaded dialogPrinter.fxml successfully");
        dialogStage=new Stage();
        try{
            dialogScene = new Scene(loader.load());
        }catch (IOException e){
            System.out.println("未加载");
            URL url = getClass().getResource("dialogPrinter.fxml");
            if (url == null) {
                System.out.println("找不到FXML文件！");
                System.out.println("当前类路径: " + getClass().getPackage().getName());
                System.out.println("应该在: " + getClass().getResource("."));
            }
        }

        //System.out.println("Scene"+dialogScene==null);
        //dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setScene(dialogScene);

        //dialogStage.show();

        /*System.out.println(dialogStage.isShowing());
        Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                PrinterFlow("FXMLLoader loader = new FXMLLoader(getClass().getResource(\"dialogPrinter.fxml\"));\nloader.setController(this);");
                t.cancel();
            }
        }, 500);*/
        //PrinterFlow("FXMLLoader loader = new FXMLLoader(getClass().getResource(\"dialogPrinter.fxml\"));\nloader.setController(this);");

//        VBox root = new VBox(20);
//        root.setPadding(new Insets(20));
//        root.setStyle("-fx-background-color: #f0f0f0;");
        /*dialogPrinter=new DialogPrinter();
        //popupScene.getRoot().getChildren

        dialogPrinter.SetDialogStyle("#ffffff", "#333333", "#4a90e2", 14);
        dialogPrinter.setMaxWidth(400);
        dialogPrinter.setPrefWidth(400);
        dialogPrinter.setMinWidth(400);
        //dialogPrinter.setMaxHeight(1000);
        //dialogPrinter.setPrefHeight(300);
        //dialogPrinter.setMinHeight(50);

        dialogPrinter.SetTypingSpeed(5); // 30字符/秒
//        dialogPrinter.setLayoutX(500);
//        dialogPrinter.setLayoutY(500);
        Scene dialogScene=new Scene(dialogPrinter);
        Stage dialogStage=new Stage();
        dialogStage.setScene(dialogScene);

        //dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.show();


        // 示例文本
        String[] sampleTexts = {
                "你好！我是DeepSeek，很高兴为你服务。这是一个具有打字机效果的对话输出框。",
                "这个对话框会根据文本内容自动调整大小，支持自动换行和动态高度调整。",
                "你可以自定义打字速度、颜色、字体大小等属性。这个功能非常适合游戏对话、教程引导等场景。",
                "让我们看一个长文本的例子：JavaFX是一个用于构建富客户端应用程序的框架，它提供了丰富的UI控件和强大的图形功能。使用JavaFX可以创建跨平台的桌面应用程序。"
        };

        // 创建控制按钮
//        HBox controls = new HBox(10);
//        controls.setPadding(new Insets(10));
//
//        javafx.scene.control.Button startButton = new javafx.scene.control.Button("开始打字");
//        javafx.scene.control.Button skipButton = new javafx.scene.control.Button("跳过动画");
//        javafx.scene.control.ComboBox<Integer> speedCombo = new javafx.scene.control.ComboBox<>();
//        speedCombo.getItems().addAll(10, 20, 30, 40, 50);
//        speedCombo.setValue(30);
//        speedCombo.setPromptText("打字速度");
//
//        // 按钮事件处理
//        startButton.setOnAction(e -> {
//            String text = sampleTexts[(int)(Math.random() * sampleTexts.length)];
//            dialogPrinter.TypeText(text);
//        });
//
//        skipButton.setOnAction(e -> dialogPrinter.ShowFullText());
//        speedCombo.setOnAction(e -> {
//            dialogPrinter.SetTypingSpeed(speedCombo.getValue());
//        });
//        controls.getChildren().addAll(startButton, skipButton, speedCombo);

        // 添加到根布局
        //root.getChildren().addAll(dialogPrinter, controls);

        // 初始显示一个示例
        dialogPrinter.TypeText("欢迎使用打字机效果对话系统！点击'开始打字'按钮查看效果。让我们看一个长文本的例子：JavaFX是一个用于构建富客户端应用程序的框架，它提供了丰富的UI控件和强大的图形功能。使用JavaFX可以创建跨平台的桌面应用程序。");*/
    }

    private double w1 = 0,w2 = 0;
    public void PrinterFlow(String text)
    {
        curText=text;
        textSize=text.length();
        curTextIndex=0;
        //dialogLabel.setText("开始了");
        dialog.setText("");

        printerLine=new Timeline(
                new KeyFrame(Duration.seconds(0.1),event->{
                    //dialogLabel.getText()+=text.indexOf(curTextIndex++);
                    if(curTextIndex>=textSize)
                    {
                        printerLine.stop();
                        return;
                    }
                    dialog.setText(text.substring(0,curTextIndex++));
                    //double w=dialogLabel.getLayoutBounds().getWidth();
                    //double w=new Text()
                    //double w=dialog.getLayoutBounds().getWidth();
                    w1=dialog.getLayoutBounds().getHeight();
                    //System.out.println(w);
                    if(w1!=w2) {
                        w2=w1;
                        dialogStage.setHeight(w2 + 35);
                        System.out.println(w1);
                    }
                    //if(w>=200)
                    //{
                        //dialogLabel.setPrefHeight(20*(w/200+1));
                        //dialogStage.setHeight(20*(w/200+1));
                    //}
                    //if(curTextIndex%14==0)
                    //{
                        //dialogLabel.setPrefHeight(15*(curTextIndex/14+2));
                        //dialogStage.setHeight(15*(curTextIndex/14+2));
                    //}
                    //dialogLabel.setPrefHeight(dialogLabel.getLayoutBounds().getWidth());
                    //System.out.println(curTextIndex);
                }));
        //我以为这一句话只对循环动画起作用
        //没想到还必须得加
        printerLine.setCycleCount(Timeline.INDEFINITE);
        printerLine.play();
    }
    public void PrinterImmediately(String text)
    {
        curText=text;
        dialog.setText(text);
        dialogStage.setHeight(dialog.getLayoutBounds().getHeight() + 35);
    }
    public void OpenDialog()
    {
        dialogStage.show();
    }
    public void HideDialog()
    {
        dialogStage.hide();
    }
    public void CloseDialog()
    {
        dialogStage.close();
    }
    //endregion

}
//打字机效果对话框
/*class DialogPrinter extends Pane
{
    private final Label textLabel;
    private final Rectangle background;
    private final Timeline timeline;
    private String fullText = "";
    private double maxWidth = 400;
    private double padding = 15;
    private double lineSpacing = 5;

    public DialogPrinter()
    {
        //创建背景矩形
        background = new Rectangle();
        background.setArcWidth(20);
        background.setArcHeight(20);
        //background.setFill(Color.WHITE);
        background.setFill(Color.RED);
        background.setStroke(Color.LIGHTGRAY);
        background.setStrokeWidth(1);

        //创建文本标签
        textLabel = new Label();
        textLabel.setWrapText(true);
        textLabel.setFont(Font.font(14));
        textLabel.setPadding(new Insets(padding));

        textLabel.setWrapText(true);
        textLabel.setMaxWidth(400);

        //设置布局
        this.getChildren().addAll(background, textLabel);
        //动画时间线
        timeline = new Timeline();
        //监听文本变化，动态调整大小
        textLabel.textProperty().addListener((obs, oldText, newText) -> {
            AdjustSize();
        });
    }

    // 设置打字速度（字符/秒）
    public void SetTypingSpeed(double charsPerSecond)
    {
        timeline.setRate(charsPerSecond / 10);
    }

    // 设置最大宽度
//    public void SetMaxWidth(double width)
//    {
//        this.maxWidth = width;
//        textLabel.setMaxWidth(width - 2 * padding);
//        AdjustSize();
//    }

    //显示文本（打字机效果）
    public void TypeText(String text)
    {
        fullText = text;
        textLabel.setText("");

        //清除之前的动画
        timeline.stop();
        timeline.getKeyFrames().clear();

        //创建打字动画
        final int textLength = text.length();
        for (int i = 1; i <= textLength; i++)
        {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * 30), // 每个字符间隔30ms
                    e -> textLabel.setText(text.substring(0, index))
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        // 开始动画
        timeline.play();
    }
    // 立即显示完整文本（跳过动画）
    public void ShowFullText()
    {
        timeline.stop();
        textLabel.setText(fullText);
    }

    // 调整对话框大小
    private void AdjustSize()
    {
        // 计算文本所需的高度
        //textLabel.applyCss();
        //double textHeight = textLabel.prefHeight(textLabel.getMaxWidth());
        //double textHeight=400.0/14/fullText.length();
        int textHeight=textLabel.getText().length()/2+1;

        // 设置对话框大小
//        double dialogWidth = Math.min(
//                textLabel.prefWidth(-1) + 2 * padding,
//                maxWidth
//        );
        double dialogHeight = textHeight + 2 * padding;

        // 更新背景和标签大小
        //background.setWidth(dialogWidth);
        background.setHeight(dialogHeight);
        //textLabel.setPrefWidth(dialogWidth - 2 * padding);
        textLabel.setPrefHeight(textHeight);
        // 更新对话框大小
        //this.setPrefSize(dialogWidth, dialogHeight);
        this.setPrefSize(maxWidth,dialogHeight);
    }

    //设置样式
    public void SetDialogStyle(String backgroundColor, String textColor,
                               String borderColor, double fontSize)
    {
        background.setFill(Color.valueOf(backgroundColor));
        background.setStroke(Color.valueOf(borderColor));
        textLabel.setTextFill(Color.valueOf(textColor));
        textLabel.setFont(Font.font(fontSize));
    }
}*/