package ceobe.arkfriends;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.Scene;
import java.util.List;
import java.util.Map;


import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class AnimationController
{
    public static AnimationController animationController;

    public Character curChar;

    public ImageView content;
    public Pane root;
    private Stage stage;
    //private List<List<Image>> curCharAni;

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

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
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
                curAniCount=0;
            }
            content.setImage(curAni.get(curAniCount++));
            System.out.println(curAniCount);
        }));
        animator.setCycleCount(Timeline.INDEFINITE);
        animator.play();

    }
    public void StopAnimation()
    {

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
