package ceobe.arkfriends;

import javafx.application.Application;

import javafx.fxml.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.Paint;
import javafx.scene.control.*;

import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Timer;

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

        launcherFxml = new FXMLLoader(Launcher.class.getResource("mainPanel.fxml"));
        launcherScene=new Scene(launcherFxml.load());
        stage.setScene(launcherScene);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        PanelController.panelController.AddWindowsEffect();//当时这个顺序放错位置了
        //一直报错
        //草了，反而这里对了，线程顺序怎么搞的
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
        petStage=new Stage();
        petStage.setScene(petScene);//这个有问题
        petScene.setFill(Color.TRANSPARENT);

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
                timer.cancel();
            }
        },200);//延迟一秒执行
        //AnimationController.animationController.DelayedInitialization();
        //放到DelayedInitialization里去了

        //new AIChatManager();
    }
}