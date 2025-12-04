package ceobe.arkfriends;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.*;
import javafx.scene.transform.*;
import javafx.stage.*;
import javafx.scene.image.*;

import java.io.IOException;
import javafx.util.Duration;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;


public class PanelController
{
    public static PanelController panelController;

    public Button starterButton,agentButton,settingsButton,moreButton;
    public Button minimizeButton,closeButton;
    public Region starterPanel,agentPanel,settingsPanel,morePanel;

    public PanelController()
    {
        if(panelController==null)
            panelController=this;
        //AddWindowsEffect();
    }

    public void OnChangeStarterPanelClick()
    {

    }
    public void OnChangeAgentPanelClick()
    {

    }
    public void OnChangeSettingsPanelClick()
    {

    }
    public void OnChangeMorePanelClick()
    {

    }

    public void ChangeRegion(Region regionClose, Region regionOpen)
    {
        if (regionClose != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), regionClose);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> regionClose.setVisible(false));
            fadeOut.play();
        }
        if (regionOpen != null) {
            regionOpen.setOpacity(0.0);
            regionOpen.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), regionOpen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }
    public void LaunchRunning() throws IOException
    {
        //Application.launch(Running.class);
        //Launcher.launcher.runningScene=new Scene(Launcher.launcher.mainReader.load());
        //Launcher.launcher.
        Launcher.launcher.StartRunning();

        AnimationController.animationController.ChangeCharacter("ceobe");
        AnimationController.animationController.PlayAnimation();
    }



    private double xOffset = 0;
    private double yOffset = 0;


    public AnchorPane titleBar;
    //private AnchorPane contentPane;


    public void AddWindowsEffect()
    {
        // 添加窗口拖动支持
        if(titleBar==null)
        {
            System.out.println("Title bar is null!");
            return;
        }
        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged((MouseEvent event) -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // 设置标题栏悬停效果
//        titleBar.setOnMouseEntered(event -> {
//            titleBar.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e);");
//        });
    }

    @FXML
    private void MinimizeWindow(MouseEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);

        // 添加点击效果
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #4a6572; -fx-background-radius: 0;");
    }

    @FXML
    private void CloseWindow(MouseEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleButtonHover(MouseEvent event)
    {
        Button button = (Button)event.getSource();
        //button.setStyle("-fx-background-color: #4a6572; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-color: #5d6d7e; -fx-border-width: 1;");
        button.setStyle("-fx-background-color: #4a6572; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-border-color: #5d6d7e; -fx-border-width: 1;");
        // 添加阴影效果
        //DropShadow shadow = new DropShadow();
        //shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        //shadow.setRadius(5);
        //button.setEffect(shadow);
    }

    @FXML
    private void handleButtonExit(MouseEvent event)
    {
        Button button = (Button)event.getSource();
        //button.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-border-color: #4a6572; -fx-border-width: 1;");
        button.setStyle("-fx-background-color: #00BFFF; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-border-color: #4a6572; -fx-border-width: 1;");
        button.setEffect(null);
    }
}
