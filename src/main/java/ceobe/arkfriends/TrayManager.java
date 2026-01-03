package ceobe.arkfriends;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;

import javafx.application.Platform;
import javafx.stage.Stage;

public class TrayManager
{
    public static TrayManager trayManager;
    private static TrayIcon trayIcon;

    private Stage stage;
    //又要滥用单例模式了
    public TrayManager()
    {
        if (trayManager==null)
            trayManager=this;
        // 在启动JavaFX前，初始化AWT字体配置

        // 设置AWT默认字体为中文字体
        /*Font chineseFont = getSystemChineseFont();
        System.setProperty("awt.font.desktophints", "true");
        java.awt.Toolkit.getDefaultToolkit().getFontList(); // 触发字体加载
        Font defaultFont = Font.getFont("Default", chineseFont);
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");*/
        // Windows适配

    }

    public void init(Stage stage)
    {
        if (!SystemTray.isSupported())
        {
            System.out.println("系统不支持托盘");
            LogRecorder.logRecorder.RecordLog("系统不支持托盘");
            return;
        }
        this.stage=stage;

        Platform.setImplicitExit(false);

        SystemTray tray = SystemTray.getSystemTray();

        /*Image image = Toolkit.getDefaultToolkit().getImage(
                //TrayManager.class.getResource("/icon.png")
                TrayManager.class.getResource("D:\\ArkFriends\\ArkFriends\\src\\main\\resources\\icon\\syntheticJade.png")
        );*/
        //用类路径只能加载相对路径里的资源，绝对路径不行
        /*try {
            Image image = Toolkit.getDefaultToolkit().getImage(
                new File("D:\\ArkFriends\\ArkFriends\\src\\main\\resources\\icon\\syntheticJade.png").toURI().toString()
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }*/
        Image image = Toolkit.getDefaultToolkit().getImage(
                new File("D:\\ArkFriends\\ArkFriends\\src\\main\\resources\\icon\\syntheticJade.png").toURI().toString()
        );

        //System.setProperty("file.encoding", "UTF-8");
        //System.setProperty("sun.jnu.encoding", "UTF-8");

        //Font selectedFont = new Font("微软雅黑", Font.PLAIN, 12);

        PopupMenu menu = new PopupMenu();

        //先拿英文凑合吧
        MenuItem showItem = new MenuItem("opaque");
        MenuItem hideItem = new MenuItem("transparent");
        //showItem.setFont(selectedFont);
        //hideItem.setFont(selectedFont);

        Menu modeMenu = new Menu("mode");
        CheckboxMenuItem autoMode = new CheckboxMenuItem("automatic", true);
        CheckboxMenuItem manualMode = new CheckboxMenuItem("manaul");
        //autoMode.setFont(selectedFont);
        //manualMode.setFont(selectedFont);
        //modeMenu.setFont(selectedFont);

        MenuItem exitItem = new MenuItem("exit");
        //exitItem.setFont(selectedFont);
        //好像没啥用还是乱码

        // 菜单结构
        menu.add(showItem);
        menu.add(hideItem);
        menu.addSeparator();
        menu.add(modeMenu);
        modeMenu.add(autoMode);
        modeMenu.add(manualMode);
        menu.addSeparator();
        menu.add(exitItem);

        trayIcon = new TrayIcon(image, "ArkFriends", menu);
        trayIcon.setImageAutoSize(true);

        // 行为绑定
        showItem.addActionListener(e -> showPet());
        hideItem.addActionListener(e -> hidePet());

        autoMode.addItemListener(e -> {
            autoMode.setState(true);
            manualMode.setState(false);
            //switchMode(PetMode.AUTO);
        });

        manualMode.addItemListener(e -> {
            manualMode.setState(true);
            autoMode.setState(false);
            //switchMode(PetMode.MANUAL);
        });

        trayIcon.addActionListener(e -> showPet());

        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            exitApp();
        });

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(
                    "ArkFriends",
                    "启动成功",
                    TrayIcon.MessageType.INFO
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    //private static PetMode currentMode = PetMode.AUTO;

    public void showPet() {
        Platform.runLater(() -> {
            stage.show();
            stage.toFront();
            //PanelController.panelController.
            //AnimationController.animationController.
            System.out.println("显示桌宠");
            LogRecorder.logRecorder.RecordLog("显示桌宠");
        });
    }

    public void hidePet() {
        //Platform.runLater(stage::hide);
        Platform.runLater(() -> {
            System.out.println("隐藏桌宠");
            LogRecorder.logRecorder.RecordLog("隐藏桌宠");
        });
    }

    /*public static void switchMode(PetMode mode) {
        currentMode = mode;
        System.out.println("切换模式：" + mode);

        // TODO：这里以后直接联动你的状态机 / 行为树
    }*/

    public static void exitApp() {
        Platform.exit();
        System.exit(0);
    }

    /*public static PetMode getCurrentMode() {
        return currentMode;
    }*/
}