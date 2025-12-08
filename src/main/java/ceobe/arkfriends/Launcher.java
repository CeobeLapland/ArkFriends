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
        if(AnimationController.animationController==null)
            AnimationController.animationController=new AnimationController();
        petFxml= new FXMLLoader(Launcher.class.getResource("petPanel.fxml"));
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
        AnimationController.animationController.Initialize();
        //放到DelayedInitialization里去了
    }
}