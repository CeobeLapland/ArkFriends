package ceobe.arkfriends;

//region imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;


import javafx.scene.layout.*;
import javafx.stage.Stage;


//import javafx.application.Application;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

//endregion
public class AnimationController
{
    public static AnimationController animationController;

    //region 引用变量
    public Map<String,Character> allChars=new java.util.HashMap<>();

    public Character curChar;
    public String curCharName;

    @FXML
    public ImageView content;
    @FXML
    public Pane rootPane;
    public Stage stage;
    public Stage popupStage;
    public Stage dialogStage;
    //private Scene scene;
    /*
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

    private int switcherCounter=10;*/

    private RightKeyPanelController rightKeyPanelController;


    //public PhysicsDragWithoutGravity dragController;
    public IPhysicsDragController physicsDragController;
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

        SetPhysicsMode(false);
        //SetPhysicsUpdateMode(false);
        //SetPhysicsRandomMode(false);
    }
    private boolean physicsModeBoolean=false;
    public void SetPhysicsMode(boolean mode)
    {
        if(mode)
        {
            //physicsDragController=dragController;
//            physicsDragController=new PhysicsDragWithGravity(
//                    new DoublePoint(stage.getX(),stage.getY()),
//                    content.getImage().getWidth(),
//                    content.getImage().getHeight());
            physicsModeBoolean=true;
            SetPhysicsUpdateMode(true);
            SetPhysicsRandomMode(true);
        } else
        {
            //physicsDragController=null;
            physicsModeBoolean=false;
            SetPhysicsUpdateMode(false);
            SetPhysicsRandomMode(false);
        }
    }

    boolean isDragged=false;

    DoublePoint velocity;

    ScheduledExecutorService leaveDragExecutor;
    //private double xOffset = 0;
    //private double yOffset = 0;
    public void DelayedInitialization()
    {
//        if(rootPane==null)
//            System.out.println("rootPane is null 2");
//        if(content==null)
//            System.out.println("content is null 2");

        rootPane.setStyle("-fx-background-color: transparent;");
        rootPane.setMouseTransparent(false);
        /*rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });*/
        //stage=(Stage)root.getScene().getWindow();
        stage=Launcher.launcher.petStage;
        //scene=Launcher.launcher.petScene;

        /*rootPane.setOnMouseDragged(event -> {
            //stage.setX(event.getScreenX() - xOffset);
            //stage.setY(event.getScreenY() - yOffset);
        });*/

        //Filter和Handler的区别在于Filter会先于Handler执行

        /*Launcher.launcher.petScene.addEventFilter(MouseEvent.MOUSE_RELEASED,event -> {
            //isDragged=false;
            //dragController.isDragging=false;
            System.out.println("Mouse released in petScene");

            if (movingToSitTimer !=null) {
                movingToSitTimer.cancel();
                movingToSitTimer =null;
            }
            dragController.OnMouseReleased();
        });*/

        /*
        rootPane.addEventHandler(MouseEvent.MOUSE_RELEASED,event -> {
            isDragged=false;
            dragController.isDragging=false;
            System.out.println("Mouse released in rootPane");

            //isDragged=false;
            //后续加上的
            if (movingToSitTimer !=null) {
                movingToSitTimer.cancel();
                movingToSitTimer =null;
            }

            //又加入了这个
            dragController.OnMouseReleased();
        });
        Launcher.launcher.petScene.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            System.out.println("Mouse clicked in petScene");
        });
         */


        //content.setOnMouseClicked(mouseEvent -> {
        //    EventHandler()bulabula
        //});
        // 设置主窗口右键事件
        //把scene换成ImageView
        //content.setOnMouseClicked(event -> {
        if(rightKeyPanelController==null) {
            rightKeyPanelController = new RightKeyPanelController();

            //我真草了
            //他必须要放到UI线程里去更新，早一毫秒都不行
            //我讨厌多线程
            Platform.runLater(() -> {
                rightKeyPanelController.CreateSecondaryStage(stage, stage.getX(), stage.getY());
                //互相交换电话号码
                rightKeyPanelController.petStage = stage;
                popupStage = rightKeyPanelController.popupStage;


//                try{
//                    rightKeyPanelController.InitializeDialogPrinter();
//                }catch (IOException e){
//
//                }
                rightKeyPanelController.HidePopupStage();
                rightKeyPanelController.InitializeDialogPrinter();
                //我感觉这句可能会空报错
                dialogStage=rightKeyPanelController.dialogStage;
                //我最讨厌调顺序了


                //绑定dialog窗口移动情况
                dialogStage.setX(stage.getX()+120);
                dialogStage.setY(stage.getY()+50);
                stage.xProperty().addListener(((observableValue, oldX, newX) -> {
                    dialogStage.setX(newX.doubleValue()+100);
                }));
                stage.yProperty().addListener(((observableValue, oldY, newY) -> {
                    dialogStage.setY(newY.doubleValue()+50);
                }));
            });
        }



        //if(dragController==null)
        //    //dragController=new PhysicsDragWithoutGravity(new DoublePoint(stage.getX()+100,stage.getY()+100));
        //    dragController=new PhysicsDragWithoutGravity(new DoublePoint(stage.getX(),stage.getY()));
        if(physicsDragController==null) {
            if (physicsModeBoolean) {
                physicsDragController = new PhysicsDragWithGravity(
                        new DoublePoint(stage.getX(), stage.getY()),
                        content.getImage().getWidth(),
                        content.getImage().getHeight());
            } else {
                physicsDragController = new PhysicsDragWithoutGravity(
                        new DoublePoint(stage.getX(), stage.getY()));
            }
            //physicsDragController = new PhysicsDragWithGravity(new DoublePoint(stage.getX(), stage.getY()),200,200);
                    //content.getImage().getWidth(),content.getImage().getHeight());
        }


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
                rightKeyPanelController.CreateSecondaryStage(stage, event.getScreenX(), event.getScreenY());
                event.consume(); // 防止事件继续传播
            }
        });
        System.out.println("have set");
        //这个只能绑定一个事件
        //主窗口点击事件（用于关闭弹出窗口）
        //content.setOnMouseClicked(event -> {
        content.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            if (event.getButton() == MouseButton.PRIMARY &&
                    popupStage != null && popupStage.isShowing())
            {
                //检查点击是否在弹出窗口内
                if (!rightKeyPanelController.IsClickInPopup(event.getScreenX(), event.getScreenY()))
                {
                    //popupStage.close();
                    popupStage.hide();
                    System.out.println("Closed popup stage");
                }
            }
        });

        //这几个mouse click其实可以放一起

        //鼠标左键单击interact
        content.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            System.out.println("Mouse clicked for interact");
            if(isDragged)
                return;
            if(event.getButton()==MouseButton.PRIMARY &&
                    (popupStage==null||!popupStage.isShowing()))
            {
                System.out.println("interact");
                nextState=curChar.states.get("interact");
            }
        });

        //鼠标拖拽drag
        content.addEventHandler(MouseEvent.MOUSE_DRAGGED,event -> {
            if(!isDragged)
            {
                //加了一句这个
                if(event.getButton()!=MouseButton.PRIMARY)
                    return;
                isDragged=true;
                nextState=curChar.states.get("drag");
            }

            //dragController.OnMouseDragged(event.getScreenX(),event.getScreenY());
            physicsDragController.OnMouseDragged(event.getScreenX(),event.getScreenY());
        });

        //只读取一遍velocity
        velocity=physicsDragController.GetVelocity();

        //这个还挺重要的
        //content.addEventFilter(MouseEvent.MOUSE_RELEASED,event -> {
        Launcher.launcher.petScene.addEventFilter(MouseEvent.MOUSE_RELEASED,event -> {

            System.out.println("filter Mouse released");

            //dragController.isDragging=false;
            //physicsDragController.isDragging=false;
            physicsDragController.SetIsDragging(false);
            //我草了这么重要的我居然忘记加上去了

            //ScheduledExecutorService scheduler= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            leaveDragExecutor= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            //scheduler.scheduleAtFixedRate(()->{
            leaveDragExecutor.scheduleAtFixedRate(()->{
                //if(Math.abs(dragController.velocity.x)>10 || Math.abs(dragController.velocity.y)>10)
                if(Math.abs(velocity.x)>10 || Math.abs(velocity.y)>10)
                {
                    //System.out.println(dragController.velocity.x+"x"+dragController.velocity.y+"y  "+"return in scheduled release");
                    //System.out.println(dragController.velocity.x+"x"+dragController.velocity.y+"y  "+"isDragged="+isDragged+"  isDragging="+dragController.isDragging);

                    return;
                }else
                {
                    isDragged = false;
                    //后续加上的
                    if (movingToSitTimer != null) {
                        movingToSitTimer.cancel();
                        movingToSitTimer = null;
                    }
                    if(followWindowExecutor!=null)
                    {
                        followWindowExecutor.shutdownNow();
                        followWindowExecutor=null;
                    }

                    //又加入了这个
                    //dragController.OnMouseReleased();
                    physicsDragController.OnMouseReleased();
                    System.out.println("鼠标释放");
                    nextState = curChar.states.get("interact");

                    leaveDragExecutor.shutdown();
                }
            },0,50,java.util.concurrent.TimeUnit.MILLISECONDS);
        });


        content.addEventHandler(MouseEvent.MOUSE_PRESSED,event -> {
            System.out.println("Mouse pressed");
            //加了这个
            if(event.getButton()!=MouseButton.PRIMARY)
                return;

            System.out.println("左键按下");
            if(followWindowExecutor!=null)
            {
                System.out.println("Shut down followWindowExecutor");
                followWindowExecutor.shutdown();
                followWindowExecutor=null;
            }
            if(movingToSitTimer!=null)
            {
                System.out.println("Cancel movingToSitTimer");
                movingToSitTimer.cancel();
                movingToSitTimer=null;
            }
            if(leaveDragExecutor!=null)
            {
                System.out.println("Shut down leaveDragExecutor");
                leaveDragExecutor.shutdownNow();
                leaveDragExecutor=null;
            }
            //dragController.OnMousePressed(event.getScreenX(),event.getScreenY());
            physicsDragController.OnMousePressed(event.getScreenX(),event.getScreenY());
            //dragController.OnMousePressed(event.getScreenX() + 100,event.getScreenY() + 100);
            //加个100试试
        });

        //下面这些是订阅拖拽物理效果的
        //content.addEventHandler();
        //好像放上面整一起也不是不行


        ChangeCharacter(curCharName);
        StartAnimation();
    }


    //region 动画器启动相关
    private Random ran= new Random();

    Timeline animator;
    float deltaTime=0.05f;
    long timeCount=0;
    int nextNullTime=0;
    public int lastingTime=240;//12s

//    AnimationState manualRequest = null;
//    AnimationState interactRequest = null;
//    AnimationState randomRequest = null;
    public boolean isManual=false;

    AnimationState curState = null;
    AnimationState nextState=null;

    int frameCount, totalFrame;
    public Point point=null;

    private double dx,dy;
    public int speed=100;

    public void StartAnimation()
    {
        animator=new Timeline(
                new KeyFrame(Duration.seconds(deltaTime),actionEvent -> {
                    StateUpdate();
                    MovementUpdate();
                    //有一点担心会内存泄漏，结果发现是我想多了
                    //System.out.println(animator.getKeyFrames().size());
                    //ImageUpdate();
                }));
        animator.setCycleCount(Timeline.INDEFINITE);
        animator.play();
        //..\..\characters\ceobe\idle\idle_001.png
        /*animator=new Timeline(new KeyFrame(Duration.seconds(deltaTime)
                ,event ->
        {
            AnimatorUpdate();
//            //content.setImage();
//            if(curAniCount>=maxAniCount)
//            {
//                //if(curChar.curState.isLoop==false)
//                if(curLoop==false)
//                {
//                    System.out.println("Animation ended, switching to default state");
//                    ChangeAnimationImmediately(curChar.defaultState);
//                }
//                curAniCount=0;
//            }
//            content.setImage(curAni.get(curAniCount++));
//            //System.out.println(curAniCount);
//
//            //if(curChar.curState.isMove)
//            if(curIsMove)
//            {
//
//            }
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
        */
    }
    public void PauseAnimation()
    {
        animator.pause();
//        animator.pause();
//        switcher.pause();
    }
    public void ResumeAnimation()
    {
        animator.play();
//        animator.play();
//        switcher.play();
    }
    public void StopAnimation()
    {
        animator.stop();
//        animator.stop();
//        switcher.stop();
    }
    //endregion


    //我觉得应该从根本上就区分物理模式和非物理模式
    //然后把委托直接改成StateUpdate里调用不同的函数
    private Runnable physicsUpdateMode;
    public void SetPhysicsUpdateMode(boolean mode)
    {
        if(mode){
            physicsUpdateMode=this::StateUpdateWithPhysics;
        }
        else{
            physicsUpdateMode=this::StateUpdateWithoutPhysics;
        }
    }

    private void StateUpdate()
    {
        //把原始内容全部塞到Without里去了
        physicsUpdateMode.run();
    }
    private void StateUpdateWithPhysics()
    {
        //physicsUpdateMode.run();
    }
    private void StateUpdateWithoutPhysics()
    {
        //原本打算放在这里面的
        timeCount++;
        //这个版本的逻辑是nextState有最高优先级
        //只要nextStat不为空就直接更换
        if(nextState!=null)
        {
            ChangeState(nextState);
            nextNullTime=0;
            nextState=null;
        } else {
            if(!isDragged) {
                nextNullTime++;

                CheckNextState();
            }
            //这俩玩意联动的属实有点远，也算是动态平衡了
            //指的是nextNullTime
            else {
                //dragController.Update(deltaTime);
                //dragController.Apply(stage,content);
                physicsDragController.Update(deltaTime);
                physicsDragController.Apply(stage,content);
            }
        }
        if(frameCount>=totalFrame)
        {
            if(curState.isLoop) {
                frameCount=0;
            } else {
                ChangeState(curChar.defaultState);
            }
        }
        content.setImage(curState.imageList.get(frameCount++));
        /*if (manualRequest != null)
        {
            ChangeState(manualRequest);
            manualRequest = null;
            return;
        }
        if (interactRequest != null)
        {
            ChangeState(interactRequest);
            interactRequest = null;
            return;
        }
        if (randomRequest != null)
        {
            ChangeState(randomRequest);
            randomRequest = null;
            return;
        }*/
    }
    private void MovementUpdate()
    {
        if(curState.isMove==false)
            return;
        MoveToPoint();
        /*if (!curState.isMove) return;
        if (input.hasInput()) {
            // WASD 方向移动
            double speed = 130;

            if (input.w) position.y -= speed * dt;
            if (input.s) position.y += speed * dt;
            if (input.a) position.x -= speed * dt;
            if (input.d) position.x += speed * dt;

            return;
        }

        if (randomMoveTarget == null) return;

        double dx = randomMoveTarget.x - position.x;
        double dy = randomMoveTarget.y - position.y;

        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist < 2) {
            randomMoveTarget = null;
            return;
        }

        double speed = currentState.name.equals("run") ? 200 : 120;

        position.x += dx/dist * speed * dt;
        position.y += dy/dist * speed * dt;*/
    }
//    private void ImageUpdate()
//    {
//        content.setImage(curState.imageList.get(frameCount++));
//    }

//    public void SwitchState(AnimationState newState, AnimationSource source)
//    {
//        /*if (currentState == newState) return;
//
//        currentState = newState;
//        frameIndex = 0;
//        frameTimer = 0;
//
//        if (newState.isMove) {
//            startMovementIfNeeded();
//        }*/
//    }

    public void ChangeEmotion(Emotion emotion)
    {
        /*switch (emotion)
        {
            //case QUIET -> {} 还能这么写哈哈哈
            case QUIET: {
                ChangeState(curChar.states.get("idle"));
                break;
            }
            case HAPPY: {
                ChangeState(curChar.states.get("happy"));
                break;
            }
            case SAD: {
                ChangeState(curChar.states.get("sad"));
                break;
            }
            case ANGRY: {
                ChangeState(curChar.states.get("angry"));
                break;
            }
            case SURPRISED:{
                ChangeState(curChar.states.get("surprised"));
                break;
            }
            case SCARED: {
                ChangeState(curChar.states.get("scared"));
                break;
            }
            case DISGUSTED: {
                ChangeState(curChar.states.get("disgusted"));
                break;
            }
            case EXCITED: {
                ChangeState(curChar.states.get("excited"));
                break;
            }
        }*/
    }




    public void ChangeState(AnimationState newState)
    {
        if (newState==null)
            return;
        curState=newState;
        frameCount=0;
        totalFrame =curState.imageList.size();
        System.out.println("Change to state"+curState.assertsPath);
    }
    public AnimationState RandomState()
    {
        return physicsRandomMode.get();
        /*int r=ran.nextInt(0,100);
        if(r<10) {
            return curChar.defaultState;
        } else if (r<100) {
            point=new Point(ran.nextInt(200,1200),ran.nextInt(200,800));
            System.out.println(point.x+"  "+point.y);
            double distance=Math.sqrt(Math.pow(stage.getX()-point.x,2)+Math.pow(stage.getY()-point.y,2));
            dx=(point.x-stage.getX())/distance;
            dy=(point.y-stage.getY())/distance;
            ChangeDirection();
            return curChar.states.get("move");
        } else {
            return curChar.defaultState;
        }*/
    }
    //先这样用动态平衡的写法
    //之后再换成独立线程
    public void CheckNextState()//如果检查长时间为空就随机一个
    {
        if(nextNullTime>lastingTime)//10s
        {
            nextState=RandomState();
        }
    }
    public void ChangeDirection()
    {
        if(dx > 0) {//向右
            content.setScaleX(1);
        } else {//向左
            content.setScaleX(-1);
        }
    }
    //无奈之下加了个这个
    //Point backupPoint;
    private void MoveToPoint()
    {
        if (point == null) {
            System.out.println("next point is null");
            return;
        }
        double moveX=speed*deltaTime*dx,moveY=speed*deltaTime*dy;
        if(Math.abs(stage.getX()-point.x)<Math.abs(moveX*2))
        {
            //如果距离小于一步的移动距离，直接移动到目标点
            stage.setX(point.x);
            stage.setY(point.y);

            //backupPoint=point;
            //backupPoint=new Point(point.x,point.y);
            point = null; //移动完成后清空目标点
            ChangeState(curChar.defaultState);
        }
        else
        {
            stage.setX(stage.getX()+moveX);
            stage.setY(stage.getY()+moveY);
        }
    }


    //Supplier<Void> physicsMode;
    //Runnable physicsMode;
    Supplier<AnimationState> physicsRandomMode;

    Timer movingToSitTimer;
    //WindowsScanner.DesktopWindow
    private Point a=new Point(0,0),b=new Point(1600,0);

    private int xExcursion=-100,yExcursion=-155;

    //public void SetPhysicsMode(Supplier<Boolean> mode)
    public void SetPhysicsRandomMode(boolean mode)
    {
        //physicsMode=mode;mode
        if(mode) {
            physicsRandomMode = this::RandomStateWithPhysics;
            //physicsMode=()->RandomStateWithPhysics();
        }
        else {
            physicsRandomMode = this::RandomStateWithoutPhysics;
        }
    }
    private AnimationState RandomStateWithPhysics()
    {
        return null;
    }

    private Point saveCurWindowPosition;
    private AnimationState RandomStateWithoutPhysics()
    {
        int r=ran.nextInt(0,100);

        if(followWindowExecutor!=null)
            followWindowExecutor.shutdown();

        if(r<10)
        {//默认idle
            lastingTime=240;
            return curChar.defaultState;
        }
        else if (r<40)
        {//move
            point=new Point(ran.nextInt(150+xExcursion,1500+xExcursion),ran.nextInt(50+yExcursion,700+yExcursion));
            //在原来基础上减去了20
            System.out.println(point.x+"  "+point.y);
            double distance=Math.sqrt(Math.pow(stage.getX()-point.x,2)+Math.pow(stage.getY()-point.y,2));
            dx=(point.x-stage.getX())/distance;
            dy=(point.y-stage.getY())/distance;
            ChangeDirection();

            lastingTime=(int)(distance/speed*20)+200;
            return curChar.states.get("move");
        }
        else if(r<80)
        {//sit
            WindowsScanner.windowsScanner.GiveHorizontalLine(a,b);
            //从窗口中找到合适的位置
            //WindowsScanner.windowsScanner.FindTargetWindow(200,200);
            //这个回来要乘上角色的放大倍数
            if(a.x==b.x){
                //没有找到合适的窗口
                return curChar.defaultState;
            }
            //saveCurWindowPosition=WindowsScanner.windowsScanner.GetWindowPosition(WindowsScanner.windowsScanner.curWindow.hWnd);
            saveCurWindowPosition=new Point(WindowsScanner.windowsScanner.curWindow.x,WindowsScanner.windowsScanner.curWindow.y);
            System.out.println("Saved window position: ("+saveCurWindowPosition.x+","+saveCurWindowPosition.y+")");

            System.out.println("Found window line: ("+a.x+","+a.y+") to ("+b.x+","+b.y+")");
            //在ab中间随机一个点
            point=new Point(ran.nextInt(a.x,b.x)+yExcursion,a.y+yExcursion);
            System.out.println("Target point for sitting: ("+point.x+","+point.y+")");

            double distance=Math.sqrt(Math.pow(stage.getX()-point.x,2)+Math.pow(stage.getY()-point.y,2));
            dx=(point.x-stage.getX())/distance;
            dy=(point.y-stage.getY())/distance;
            ChangeDirection();

            if(movingToSitTimer !=null)
            {
                movingToSitTimer.cancel();
            }
            movingToSitTimer =new Timer();
            movingToSitTimer.schedule(new java.util.TimerTask(){
                @Override
                public void run()
                {
                    Platform.runLater(()->{
                        //如果发现窗口位置发生变化就不让坐了
                        //Point tempPosition=WindowsScanner.windowsScanner.GetWindowPosition(WindowsScanner.windowsScanner.curWindow.hWnd);
                        //如果直线距离大于50就取消坐下
                        //if(Math.sqrt(Math.pow(tempPosition.x - backupPoint.x, 2) + Math.pow(tempPosition.y - backupPoint.y, 2)) > 50)
                        //{
                        //    System.out.println("不要乱动窗口喵！cancel sitting");
                        //    nextState=curChar.states.get("interact");
                        //    movingToSitTimer.cancel();
                        //    return;
                        //}
                        movingToSitTimer.cancel();

                        //下面这句话其实可以挪到前面去
                        curWindow=WindowsScanner.windowsScanner.curWindow;
                        //curWindowX=curWindow.x;
                        //curWindowY=curWindow.y;
                        curWindowPosition=WindowsScanner.windowsScanner.GetWindowPosition(curWindow.hWnd);
                        curWindowX=curWindowPosition.x;
                        curWindowY=curWindowPosition.y;
                        System.out.println("Current window position: ("+curWindowX+","+curWindowY+")");
                        if (Math.sqrt(Math.pow(saveCurWindowPosition.x - curWindowX, 2)
                                + Math.pow(saveCurWindowPosition.y - curWindowY, 2)) > 50)
                        {
                            System.out.println("不要乱动窗口喵！cancel sitting");
                            nextState = curChar.states.get("interact");
                            movingToSitTimer.cancel();
                            return;
                        }

                        nextState=curChar.states.get("sit");

                        //开始跟随窗口
                        if(followWindowExecutor==null)
                            followWindowExecutor= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
                        followWindowExecutor.scheduleAtFixedRate(()->{
                            if(curWindow==null)
                                return;
                            Platform.runLater(()->{
                                AdjustStagePosition();
                            });
                        },0,100,java.util.concurrent.TimeUnit.MILLISECONDS);


                    });
                }
            },(long)(distance/speed*1000)+2000);//走到坐的位置然后切换sit


            lastingTime=(int)(distance/speed*20)+400;

            return curChar.states.get("move");
        }
        else {
            return curChar.defaultState;
        }
    }
    private ScheduledExecutorService followWindowExecutor;
    public WindowsScanner.DesktopWindow curWindow;
    private int curWindowX,curWindowY;
    Point curWindowPosition;
    private void AdjustStagePosition()
    {
        //if(curWindow==null)
        //    return;
        //int deltaX=curWindow.x-curWindowX;
        //int deltaY=curWindow.y-curWindowY;
        curWindowPosition=WindowsScanner.windowsScanner.GetWindowPosition(curWindow.hWnd);
        int deltaX=curWindowPosition.x-curWindowX;
        int deltaY=curWindowPosition.y-curWindowY;
        if(deltaX==0 && deltaY==0)
            return;

        //System.out.println();
        System.out.println("Adjusting stage position "+deltaX+","+deltaY);
        stage.setX(stage.getX()+deltaX);
        stage.setY(stage.getY()+deltaY);
        //curWindowX=curWindow.x;
        //curWindowY=curWindow.y;
        curWindowX=curWindowPosition.x;
        curWindowY=curWindowPosition.y;
        //我又草了又忘记更新这个了
    }

    //这个是动态调整目标位置点的协程
    //我还是设置成走到地方发现窗口位置变化了就不让坐了吧，这样省点事
    /*private ScheduledExecutorService adjustPointExecutor;
    private void AdjustTargetPoint()
    {
        if(adjustPointExecutor!=null)
            adjustPointExecutor.shutdown();
        adjustPointExecutor= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        adjustPointExecutor.scheduleAtFixedRate(()->{
            if(point==null)
                return;
            double distance=Math.sqrt(Math.pow(stage.getX()-point.x,2)+Math.pow(stage.getY()-point.y,2));
            dx=(point.x-stage.getX())/distance;
            dy=(point.y-stage.getY())/distance;
        },0,500,java.util.concurrent.TimeUnit.MILLISECONDS);
    }*/
    //region 临时储存
    /*
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
//        if(curIsMove)
//        {
//            switcherCounter--;
//            if(switcherCounter<=0)
//            {
//                //switcherCounter=10;
//                //每十秒换一个点
//                //先随便定一个点吧
//                //nextPoint=new Point((int)(Math.random()*800),(int)(Math.random()*600),0);
//                //改成在角色周围随机一个点
//                int range=100;
//                int centerX=(int)stage.getX();
//                int centerY=(int)stage.getY();
//                int targetX=centerX+(int)(Math.random()*(range*2))-range;
//                int targetY=centerY+(int)(Math.random()*(range*2))-range;
//                nextPoint=new Point(targetX,targetY,0);
//                System.out.println("New target point: ("+targetX+","+targetY+")");
//                switcherCounter=10;
//            }
//        }
    }
    */

    /*
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
    }*/
    //还是加两个不一样的改变函数吧
    //我想着搞一个animationState的队列，然后按照顺序调用
    /*
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
    */
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
        //ChangeAnimationImmediately(curChar.defaultState);
        ChangeState(curChar.defaultState);
    }

    // linear-gradient(to right, #87CEFA, #1E90FF) linear-gradient(to right, #2c3e50, #34495e)


    //endregion
    //按理来说下面这些不该出现在动画控制器里，但懒了，就这样叭
    //已经挪走了
    //region 右键角色窗口相关

    /*
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

//        double windowX = popupStage.getX();
//        double windowY = popupStage.getY();
//        double windowWidth = popupStage.getWidth();
//        double windowHeight = popupStage.getHeight();
//
//        return clickX >= windowX &&
//                clickX <= windowX + windowWidth &&
//                clickY >= windowY &&
//                clickY <= windowY + windowHeight;
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
    }*/

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