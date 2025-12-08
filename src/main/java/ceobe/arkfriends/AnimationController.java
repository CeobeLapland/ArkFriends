package ceobe.arkfriends;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.Scene;

import java.beans.EventHandler;
import java.util.List;
import java.util.Map;


import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



//import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class AnimationController
{
    public static AnimationController animationController;

    public Character curChar;

    @FXML
    public ImageView content;
    public Pane root;
    private Stage stage;
    private Scene scene;
    //private List<List<Image>> curCharAni;

    //这几个都可以当引用用
    private Map<String,List<Image>> curCharAni;
    private List<Image> curAni;//可以当引用用
    int curAniCount,maxAniCount;
    boolean curLoop=true;
    int curFps=24;

    public Timeline animator;

    public Map<String,Character> allChars=new java.util.HashMap<>();
    public AnimationController()
    {
        animationController=this;

    }

    private double xOffset = 0;
    private double yOffset = 0;
    public void Initialize()
    {
        root.setStyle("-fx-background-color: transparent;");
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        //stage=(Stage)root.getScene().getWindow();
        stage=Launcher.launcher.petStage;
        scene=Launcher.launcher.petScene;

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });


        //content.setOnMouseClicked(mouseEvent -> {
        //    EventHandler()bulabula
        //});
        // 设置主窗口右键事件
        //把scene换成ImageView
        //content.setOnMouseClicked(event -> {
        content.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            System.out.println("Mouse clicked");
            if (event.getButton() == MouseButton.SECONDARY)
            {
                System.out.println("RightMouse clicked");
                // 如果已有弹出窗口，先关闭
                if (popupStage != null && popupStage.isShowing())
                {
                    popupStage.close();
                }

                // 创建新窗口
                CreateSecondaryStage(stage, event.getScreenX(), event.getScreenY());
                event.consume(); // 防止事件继续传播
            }
        });
        System.out.println("setted");
        //这个只能绑定一个事件
        // 主窗口点击事件（用于关闭弹出窗口）
        //content.setOnMouseClicked(event -> {
        content.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            if (event.getButton() == MouseButton.PRIMARY && popupStage != null && popupStage.isShowing())
            {
                // 检查点击是否在弹出窗口内
                if (!isClickInPopup(event.getScreenX(), event.getScreenY())) {
                    popupStage.close();
                }
            }
        });
    }
    public void PlayAnimation()
    {
        //..\..\characters\ceobe\idle\idle_001.png
        animator=new Timeline(new KeyFrame(Duration.seconds(0.05)
                ,event ->
        {
            //content.setImage();
            if(curAniCount>=maxAniCount)
            {
                if(curChar.curState.isLoop==false)
                {
                    System.out.println("Animation ended, switching to default state");
                    ChangeAnimation(curChar.defaultState);
                }
                curAniCount=0;
            }
            content.setImage(curAni.get(curAniCount++));
            //System.out.println(curAniCount);

        }));
        animator.setCycleCount(Timeline.INDEFINITE);
        animator.play();

    }
    public void PauseAnimation()
    {
        animator.pause();
    }
    public void StopAnimation()
    {
        animator.stop();
    }
    public void ChangeAnimation(AnimationState aniState)//改变当前角色的动画
    {
        //未播放完的要么放在这里，要么放在调用该函数的那个地方
        System.out.println("Changed animation to "+aniState.assertsPath);
        curAni=aniState.animations;
        curLoop=aniState.isLoop;
        curFps=aniState.fps;
        maxAniCount=curAni.size();
        curAniCount=0;
    }
    public void ChangeCharacter(String name)//改变角色
    {
        if(allChars.containsKey(name))
        {
            curChar=allChars.get(name);
        }
        else
        {
            curChar=new Character(name);
            allChars.put(name,curChar);
        }
        System.out.println("Changed character to "+name);
        ChangeAnimation(curChar.defaultState);
    }

    // linear-gradient(to right, #87CEFA, #1E90FF) linear-gradient(to right, #2c3e50, #34495e)




    private Stage popupStage = null;
    private double popupWidth = 200; // 根据你的panel.fxml调整
    private double popupHeight = 150; // 根据你的panel.fxml调整


    private void CreateSecondaryStage(Stage ownerStage, double mouseX, double mouseY)
    {
        try {
            // 加载panel.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("petRightKeyPanel.fxml"));
            System.out.println(getClass().getResource("petRightKeyPanel.fxml").toString());
            Parent popupContent = loader.load();

            // 创建弹出窗口
            popupStage = new Stage();
            popupStage.initOwner(ownerStage);
            popupStage.initModality(Modality.NONE); // 非模态窗口
            popupStage.initStyle(StageStyle.UTILITY); // 简洁样式
            popupStage.setAlwaysOnTop(true);

            // 设置场景
            Scene popupScene = new Scene(popupContent);
            popupStage.setScene(popupScene);

            // 计算最佳位置
            double[] position = SelectBestPosition(mouseX, mouseY);

            // 设置窗口位置
            popupStage.setX(position[0]);
            popupStage.setY(position[1]);

            // 窗口关闭时的清理
            popupStage.setOnCloseRequest(event -> {
                popupStage = null;
            });

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

    private boolean isClickInPopup(double clickX, double clickY)
    {
        if (popupStage == null || !popupStage.isShowing()) {
            return false;
        }

        double windowX = popupStage.getX();
        double windowY = popupStage.getY();
        double windowWidth = popupStage.getWidth();
        double windowHeight = popupStage.getHeight();

        return clickX >= windowX &&
                clickX <= windowX + windowWidth &&
                clickY >= windowY &&
                clickY <= windowY + windowHeight;
    }
    public void ExitPet()
    {
        StopAnimation();
        stage.close();
    }
    public void ChangeToTransparent()
    {

    }

    /*public void start(Stage primaryStage) {
        // 1. 创建内容
        Label label = new Label("这是一个完全透明的窗口");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button closeBtn = new Button("关闭窗口");
        closeBtn.setOnAction(e -> primaryStage.close());

        StackPane root = new StackPane();
        root.getChildren().addAll(label, closeBtn);

        // 2. 关键设置：使布局背景透明
        root.setStyle("-fx-background-color: transparent;");

        // 3. 创建场景并设置为透明
        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.TRANSPARENT);  // 这是最重要的设置

        // 4. 设置窗口样式为透明
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // 5. 移除窗口默认阴影
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("透明窗口");

        // 6. 添加窗口拖拽功能（因为移除了标题栏）
        enableDrag(root, primaryStage);

        primaryStage.show();
    }*/

}
