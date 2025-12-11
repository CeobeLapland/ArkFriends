package ceobe.arkfriends;

//region imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.Scene;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


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

//endregion
public class AnimationController
{
    public static AnimationController animationController;

    //region 引用变量
    public Character curChar;
    public String curCharName;

    @FXML
    public ImageView content;
    @FXML
    public Pane rootPane;
    private Stage stage;
    private Scene scene;
    //private List<List<Image>> curCharAni;

    //这几个都可以当引用用
    //private Map<String,List<Image>> curCharAni;
    private List<Image> curAni;//可以当引用用
    //public int stateLastTime=10;

    int curAniCount,maxAniCount;
    boolean curLoop=true;
    boolean curIsMove=false;
    int curFps=24;
    public int curFpsMultiplier=1;

    public float deltaTime=0.05f;

    //public List<Point> movePath=new ArrayList<>();
    public Point nextPoint;
    public Queue<Point> movePath=new java.util.LinkedList<>();
    public float speed=50f;

    public Timeline animator;
    public Timeline switcher;

    private int switcherCounter=10;

    public Map<String,Character> allChars=new java.util.HashMap<>();

    private Random ran= new Random();
    //endregion

    public AnimationController()
    {
        if(animationController==null)
            animationController=this;
        //一切都是因为这里添了一个if判断
        //然后其实事实是一直都存在两个AnimationController实例的
        //一个是petPanel.fxml自动创建的，一个是我在Launcher里new的

        //delay没什么用了

        //有用
        //至少在我用println调试的时候发现每创建一个右键小窗口就会实例化一个控制器

        /*Timer timer=new Timer();
        timer.schedule(new java.util.TimerTask()
        {
            @Override
            public void run()
            {
                //Platform.runLater(() -> DelayedInitialization());
                //javafx.application.Platform.runLater(() -> DelayedInitialization());
                DelayedInitialization();
                timer.cancel();
            }
        },1000);//延迟一秒执行*/

        /*if(rootPane==null)
            System.out.println("rootPane is null 1");
        if(content==null)
            System.out.println("content is null 1");*/

        //DelayedInitialization();
    }

    private double xOffset = 0;
    private double yOffset = 0;
    public void DelayedInitialization()
    {

        if(rootPane==null)
            System.out.println("rootPane is null 2");
        if(content==null)
            System.out.println("content is null 2");

        rootPane.setStyle("-fx-background-color: transparent;");
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        //stage=(Stage)root.getScene().getWindow();
        stage=Launcher.launcher.petStage;
        scene=Launcher.launcher.petScene;

        rootPane.setOnMouseDragged(event -> {
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
                    //popupStage.close();
                    popupStage.hide();
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
                if (!isClickInPopup(event.getScreenX(), event.getScreenY()))
                {
                    //popupStage.close();
                    popupStage.hide();
                    System.out.println("Closed popup stage");
                }
            }
        });


        ChangeCharacter(curCharName);
        StartAnimation();
    }
    public void StartAnimation()
    {
        //..\..\characters\ceobe\idle\idle_001.png
        animator=new Timeline(new KeyFrame(Duration.seconds(deltaTime)
                ,event ->
        {
            AnimatorUpdate();
            /*//content.setImage();
            if(curAniCount>=maxAniCount)
            {
                //if(curChar.curState.isLoop==false)
                if(curLoop==false)
                {
                    System.out.println("Animation ended, switching to default state");
                    ChangeAnimationImmediately(curChar.defaultState);
                }
                curAniCount=0;
            }
            content.setImage(curAni.get(curAniCount++));
            //System.out.println(curAniCount);

            //if(curChar.curState.isMove)
            if(curIsMove)
            {

            }*/
        }));
        animator.setCycleCount(Timeline.INDEFINITE);
        animator.play();

        //先用TimeLine凑合一下，等功能实现的差不多了再换成线程那些更节省性能的东西
        //改成一秒更新一次
        switcher=new Timeline(new KeyFrame(Duration.seconds(1d),
                event ->{
            SwitcherUpdate();
        }));
        switcher.setCycleCount(Timeline.INDEFINITE);
        switcher.play();


        movePath.add(new Point(ran.nextInt(200,1200),ran.nextInt(200,800)));
        movePath.add(new Point(ran.nextInt(200,1200),ran.nextInt(200,800)));
        movePath.add(new Point(ran.nextInt(200,1200),ran.nextInt(200,800)));
    }
    public void PauseAnimation()
    {
        animator.pause();
        switcher.pause();
    }
    public void ResumeAnimation()
    {
        animator.play();
        switcher.play();
    }
    public void StopAnimation()
    {
        animator.stop();
        switcher.stop();
    }

    private void AnimatorUpdate()
    {
        if(curAniCount>=maxAniCount)
        {
            if(curLoop==false)
            {
                System.out.println("Animation ended, switching to default state");
                ChangeAnimationImmediately(curChar.defaultState);
            }
            curAniCount=0;
        }
        content.setImage(curAni.get(curAniCount++));
        if(curIsMove)
        {
            MoveToPoint();

        }
    }
    private void SwitcherUpdate()
    {
        if(switcherCounter<=0)
        {
            ChangeAnimationImmediately(curChar.nextState);
            curChar.curState=curChar.nextState;
            //int randomTime=5+(int)(Math.random()*10);
            //switcherCounter=randomTime;

            //int randomTime=(int)(Math.random()*10);
            int randomTime=5+ran.nextInt(10);
            int randomType= ran.nextInt(100);
            switcherCounter=randomTime;
            if(randomType<5)
            {
                System.out.println("Change to default state");
                curChar.nextState=curChar.defaultState;
            }
            else if(randomType<95)
            {
                System.out.println("Change to move state");
                curChar.nextState=curChar.states.get("move");
                System.out.println(movePath.size());
                nextPoint=movePath.poll();
                System.out.println(movePath.size());

                movePath.add(new Point(ran.nextInt(200,1200),ran.nextInt(200,800)));
                System.out.println("New target point from queue: ("+nextPoint.x+","+nextPoint.y+")");
            }
            else
            {

            }
//            if(randomType<50)
//            {
//                //50%的概率不变
//                //nextState不变
//            }
//            else if(randomType<80)
//            {
//                //30%的概率变成默认状态
//                curChar.nextState=curChar.defaultState;
//            }
//            else
//            {
//                //20%的概率随机一个状态
//                List<AnimationState> stateList=new java.util.ArrayList<>(curChar.states.values());
//                int randomIndex=ran.nextInt(stateList.size());
//                curChar.nextState=stateList.get(randomIndex);
//            }
            System.out.println("Switched to next state, next switch in "+randomTime+" seconds");
            return;
        }
        switcherCounter--;
        /*if(curIsMove)
        {
            switcherCounter--;
            if(switcherCounter<=0)
            {
                //switcherCounter=10;
                //每十秒换一个点
                //先随便定一个点吧
                //nextPoint=new Point((int)(Math.random()*800),(int)(Math.random()*600),0);
                //改成在角色周围随机一个点
                int range=100;
                int centerX=(int)stage.getX();
                int centerY=(int)stage.getY();
                int targetX=centerX+(int)(Math.random()*(range*2))-range;
                int targetY=centerY+(int)(Math.random()*(range*2))-range;
                nextPoint=new Point(targetX,targetY,0);
                System.out.println("New target point: ("+targetX+","+targetY+")");
                switcherCounter=10;
            }
        }*/
    }

    public void ChangeDirection()
    {
        if(stage.getX()<nextPoint.x)
        {
            //向右
            content.setScaleX(1);
        }
        else
        {
            //向左
            content.setScaleX(-1);
        }
    }

    private void MoveToPoint()
    {
//        if (nextPoint == null)
//            return;

        double currentX = stage.getX(), currentY = stage.getY();

        //System.out.println(currentX+"  "+currentY);
        double targetX = nextPoint.x, targetY = nextPoint.y;

        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        //if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) < speed * deltaTime)
        if(distance < speed * deltaTime)
        {
            //如果距离小于一步的移动距离，直接移动到目标点
            stage.setX(targetX);
            stage.setY(targetY);
            nextPoint = null; //移动完成后清空目标点
            curIsMove=false;

            ChangeAnimationImmediately(curChar.defaultState);
        }
        else
        {
//            //按比例移动
//            double ratio = (speed * deltaTime) / distance;
//            stage.setX(currentX + deltaX * ratio);
//            stage.setY(currentY + deltaY * ratio);
            stage.setX(currentX+speed*deltaTime*(deltaX/distance));
            stage.setY(currentY+speed*deltaTime*(deltaY/distance));
        }
    }
    //还是加两个不一样的改变函数吧
    //我想着搞一个animationState的队列，然后按照顺序调用
    public void ChangeAnimationImmediately(AnimationState aniState)//改变当前角色的动画
    {
        //未播放完的要么放在这里，要么放在调用该函数的那个地方
        //改成immediately了
        System.out.println("Changed animation immediately to "+aniState.assertsPath);
        curAni=aniState.imageList;
        curLoop=aniState.isLoop;
        curIsMove=aniState.isMove;
        if(curIsMove)
            ChangeDirection();


        curFps=aniState.fps*curFpsMultiplier;
        maxAniCount=curAni.size();
        curAniCount=0;
    }
    //主要是改变nextState的（队列）
    //怎么感觉用处不大
    /*public  void ChangeAnimation(AnimationState aniState)
    {
        curChar.nextState=aniState;

        System.out.println("Changed animation to "+aniState.assertsPath);
        curAni=aniState.animations;
        curLoop=aniState.isLoop;
        curIsMove=aniState.isMove;

        curFps=aniState.fps*curFpsMultiplier;
        maxAniCount=curAni.size();
        curAniCount=0;
    }*/



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
        ChangeAnimationImmediately(curChar.defaultState);
    }

    // linear-gradient(to right, #87CEFA, #1E90FF) linear-gradient(to right, #2c3e50, #34495e)



    //按理来说下面这些不该出现在动画控制器里，但懒了，就这样叭
    //region 右键角色窗口相关

    private Stage popupStage = null;
    private double popupWidth = 200; //根据panel.fxml调整
    private double popupHeight = 100; //根据panel.fxml调整

    private double popupX = 0;
    private double popupY = 0;
    public HBox popupTitleBar;

    private void CreateSecondaryStage(Stage ownerStage, double mouseX, double mouseY)
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
                Scene popupScene = new Scene(popupContent);
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

    private boolean isClickInPopup(double clickX, double clickY)
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
        StopAnimation();
        stage.close();

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
        stage.close();
        //Launcher.launcher.petStage.close();
    }

    public void HidePopupStage()
    {
        popupStage.hide();
    }

    //endregion
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
class Point
{
    public int x,y;
    //public float waitTime;
    public Point(int x,int y)//,float waitTime)
    {
        this.x=x;
        this.y=y;
        //this.waitTime=waitTime;
    }
}
