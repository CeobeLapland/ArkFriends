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
        //有用//至少在我用println调试的时候发现每创建一个右键小窗口就会实例化一个控制器

        //DelayedInitialization();
        SetPhysicsMode(true);
        //SetPhysicsUpdateMode(false);//SetPhysicsRandomMode(false);
    }
    private boolean physicsModeBoolean=false;
    public void SetPhysicsMode(boolean mode)
    {
        if(mode)
        {
            physicsModeBoolean=true;
            SetPhysicsUpdateMode(true);
            SetPhysicsRandomMode(true);
        } else
        {
            physicsModeBoolean=false;
            SetPhysicsUpdateMode(false);
            SetPhysicsRandomMode(false);
        }
    }

    boolean isDragged=false;

    DoublePoint velocity;

    ScheduledExecutorService leaveDragExecutor;

    public void DelayedInitialization()
    {
        rootPane.setStyle("-fx-background-color: transparent;");
        rootPane.setMouseTransparent(false);

        stage=Launcher.launcher.petStage;


        //Filter和Handler的区别在于Filter会先于Handler执行
        // 设置主窗口右键事件
        //把scene换成ImageView
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


        if(physicsDragController==null) {
            if (physicsModeBoolean) {
                physicsDragController = new PhysicsDragWithGravity(
                        new DoublePoint(stage.getX(), stage.getY()),200,200);
                        //content.getImage().getWidth(),content.getImage().getHeight());
                //ChangeState(curChar.states.get("start"));
            } else {
                physicsDragController = new PhysicsDragWithoutGravity(
                        new DoublePoint(stage.getX(), stage.getY()));
            }
            //physicsDragController = new PhysicsDragWithGravity(new DoublePoint(stage.getX(), stage.getY()),200,200);
        }


        content.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{

            //System.out.println("Mouse clicked for interact");
            System.out.println("Mouse clicked");
            if(isDragged)
                return;
            if(event.getButton()==MouseButton.PRIMARY &&
                    (popupStage==null||!popupStage.isShowing()))
            {
                System.out.println("interact");
                nextState=curChar.states.get("interact");
                return;
            }
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
                //event.consume(); // 防止事件继续传播
                return;
            }
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
        System.out.println("have set");

        //region 鼠标点击事件，已放在一起了
        //这个只能绑定一个事件
        //主窗口点击事件（用于关闭弹出窗口）
        /*content.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
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
        });*/

        //这几个mouse click其实可以放一起

        //鼠标左键单击interact
        /*content.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            System.out.println("Mouse clicked for interact");
            if(isDragged)
                return;
            if(event.getButton()==MouseButton.PRIMARY &&
                    (popupStage==null||!popupStage.isShowing()))
            {
                System.out.println("interact");
                nextState=curChar.states.get("interact");
            }
        });*/
        //endregion

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
        Launcher.launcher.petScene.addEventFilter(MouseEvent.MOUSE_RELEASED,event -> {

            //加了这个
            if(event.getButton()!=MouseButton.PRIMARY)
                return;

            System.out.println("filter Mouse released");

            //改掉了dragController.isDragging=false//physicsDragController.isDragging=false;
            physicsDragController.SetIsDragging(false);
            //我草了这么重要的我居然忘记加上去了

            //ScheduledExecutorService scheduler= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            leaveDragExecutor= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            //scheduler.scheduleAtFixedRate(()->{
            leaveDragExecutor.scheduleAtFixedRate(()->{
                System.out.println(velocity.x+"   x   "+velocity.y+"   y  "+"in scheduled release");
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
                    if (movingToNextMovementTimer != null) {
                        movingToNextMovementTimer.cancel();
                        movingToNextMovementTimer = null;
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
            if(movingToNextMovementTimer !=null)
            {
                System.out.println("Cancel movingToSitTimer");
                movingToNextMovementTimer.cancel();
                movingToNextMovementTimer =null;
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
                }));
        animator.setCycleCount(Timeline.INDEFINITE);
        animator.play();
        //先用TimeLine凑合一下，等功能实现的差不多了再换成线程那些更节省性能的东西

    }
    public void PauseAnimation()
    {
        animator.pause();
    }
    public void ResumeAnimation()
    {
        animator.play();
    }
    public void StopAnimation()
    {
        animator.stop();
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
        timeCount++;
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
            physicsDragController.Update(deltaTime);
            physicsDragController.Apply(stage,content);
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
            else {
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
    }
    private void MovementUpdate()
    {
        if(curState.isMove==false)
            return;
        MoveToPoint();
    }

    public void ChangeEmotion(Emotion emotion)
    {
        if(leaveDragExecutor!=null)
        {
            System.out.println("还在拖拽，无法改变情绪");
            return;
        }
        //关掉当前的动作计时器
        if(movingToNextMovementTimer !=null)
        {
            movingToNextMovementTimer.cancel();
            movingToNextMovementTimer =null;
        }
        switch (emotion)
        {
            //case QUIET -> {} 还能这么写哈哈哈
            case QUIET: {
                nextState=curChar.states.get("quiet");
                break;
            }
            case HAPPY: {
                nextState=curChar.states.get("happy");
                break;
            }
            case SAD: {
                nextState=curChar.states.get("sad");
                break;
            }
            case ANGRY: {
                nextState=curChar.states.get("angry");
                break;
            }
            case SURPRISED:{
                nextState=curChar.states.get("surprised");
                break;
            }
            case DISGUSTED: {
                nextState=curChar.states.get("disgusted");
                break;
            }
            case EXCITED: {
                nextState=curChar.states.get("excited");
                break;
            }
            case SHY:{
                nextState=curChar.states.get("shy");
                break;
            }
            default: {
                System.out.println("Unknown emotion");
                nextState=curChar.defaultState;
                break;
            }
        }
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

            point = null; //移动完成后清空目标点
            ChangeState(curChar.defaultState);
        }
        else
        {
            stage.setX(stage.getX()+moveX);
            stage.setY(stage.getY()+moveY);
        }
    }


    //之前想用Supplier<Void> physicsMode//Runnable physicsMode;
    Supplier<AnimationState> physicsRandomMode;

    Timer movingToNextMovementTimer;
    private Point a=new Point(0,0),b=new Point(1600,0);

    private int xExcursion=-100,yExcursion=-155;

    //public void SetPhysicsMode(Supplier<Boolean> mode)
    public void SetPhysicsRandomMode(boolean mode)
    {
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

        if(r<1){//默认idle
            lastingTime=240;
            return curChar.defaultState;
        }
        else if (r<4){//move
            return MoveState();
        }
        else if(r<7){//sit
            return SitState();
        }
        else if (r<90){//sleep
            return SleepState();
        }
        else {
            return curChar.defaultState;
        }
    }
    private AnimationState MoveState()
    {
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
    private AnimationState SitState()
    {
        WindowsScanner.windowsScanner.GiveHorizontalLine(a,b);
        //从窗口中找到合适的位置
        //WindowsScanner.windowsScanner.FindTargetWindow(200,200);
        //这个回来要乘上角色的放大倍数
        if(a.x==b.x){
            //找不到线，没有找到合适的窗口
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

        if(movingToNextMovementTimer !=null)
        {
            movingToNextMovementTimer.cancel();
        }
        movingToNextMovementTimer =new Timer();
        movingToNextMovementTimer.schedule(new java.util.TimerTask(){
            @Override
            public void run()
            {
                Platform.runLater(()->{
                    movingToNextMovementTimer.cancel();
                    //下面这句话其实可以挪到前面去
                    curWindow=WindowsScanner.windowsScanner.curWindow;

                    curWindowPosition=WindowsScanner.windowsScanner.GetWindowPosition(curWindow.hWnd);
                    curWindowX=curWindowPosition.x;
                    curWindowY=curWindowPosition.y;
                    System.out.println("Current window position: ("+curWindowX+","+curWindowY+")");
                    if (Math.sqrt(Math.pow(saveCurWindowPosition.x - curWindowX, 2)
                            + Math.pow(saveCurWindowPosition.y - curWindowY, 2)) > 50)
                    {
                        System.out.println("不要乱动窗口喵！cancel sitting");
                        nextState = curChar.states.get("interact");
                        movingToNextMovementTimer.cancel();
                        return;
                    }

                    nextState=curChar.states.get("sit");

                    //开始跟随窗口
                    if(followWindowExecutor==null)
                        followWindowExecutor= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
                    followWindowExecutor.scheduleAtFixedRate(()->{
                        if(curWindow==null)
                            return;
                        if(curWindow.minimized)
                        {
                            System.out.println("窗口最小化，取消坐下");
                            nextState=curChar.states.get("interact");
                            followWindowExecutor.shutdownNow();
                            followWindowExecutor=null;
                            return;
                        }
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
    private AnimationState SleepState()
    {
        WindowsScanner.windowsScanner.GiveHorizontalLine(a,b);
        if(a.x==b.x){
            //找不到线，没有找到合适的窗口
            return curChar.defaultState;
        }
        saveCurWindowPosition=new Point(WindowsScanner.windowsScanner.curWindow.x,WindowsScanner.windowsScanner.curWindow.y);
        System.out.println("Saved window position: ("+saveCurWindowPosition.x+","+saveCurWindowPosition.y+")");

        System.out.println("Found window line: ("+a.x+","+a.y+") to ("+b.x+","+b.y+")");
        //在ab中间随机一个点
        point=new Point(ran.nextInt(a.x,b.x)+yExcursion,a.y+yExcursion);
        System.out.println("Target point for sleeping: ("+point.x+","+point.y+")");

        double distance=Math.sqrt(Math.pow(stage.getX()-point.x,2)+Math.pow(stage.getY()-point.y,2));
        dx=(point.x-stage.getX())/distance;
        dy=(point.y-stage.getY())/distance;
        ChangeDirection();

        if(movingToNextMovementTimer !=null)
        {
            movingToNextMovementTimer.cancel();
        }
        movingToNextMovementTimer =new Timer();
        movingToNextMovementTimer.schedule(new java.util.TimerTask(){
            @Override
            public void run()
            {
                Platform.runLater(()->{
                    movingToNextMovementTimer.cancel();
                    //下面这句话其实可以挪到前面去
                    curWindow=WindowsScanner.windowsScanner.curWindow;

                    curWindowPosition=WindowsScanner.windowsScanner.GetWindowPosition(curWindow.hWnd);
                    curWindowX=curWindowPosition.x;
                    curWindowY=curWindowPosition.y;
                    System.out.println("Current window position: ("+curWindowX+","+curWindowY+")");
                    if (Math.sqrt(Math.pow(saveCurWindowPosition.x - curWindowX, 2)
                            + Math.pow(saveCurWindowPosition.y - curWindowY, 2)) > 50)
                    {
                        System.out.println("不要乱动窗口喵！cancel sleeping");
                        nextState = curChar.states.get("attack");
                        movingToNextMovementTimer.cancel();
                        return;
                    }

                    nextState=curChar.states.get("sleep");

                    //开始跟随窗口
                    if(followWindowExecutor==null)
                        followWindowExecutor= java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
                    followWindowExecutor.scheduleAtFixedRate(()->{
                        if(curWindow==null)
                            return;
                        if(curWindow.minimized)
                        {
                            System.out.println("窗口最小化，取消睡觉");
                            nextState=curChar.states.get("interact");
                            followWindowExecutor.shutdownNow();
                            followWindowExecutor=null;
                            return;
                        }
                        Platform.runLater(()->{
                            AdjustStagePosition();
                        });
                    },0,100,java.util.concurrent.TimeUnit.MILLISECONDS);

                });
            }
        },(long)(distance/speed*1000)+2000);//走到坐的位置然后切换sit


        lastingTime=(int)(distance/speed*20)+800;

        return curChar.states.get("move");
    }
    private ScheduledExecutorService followWindowExecutor;
    public WindowsScanner.DesktopWindow curWindow;
    private int curWindowX,curWindowY;
    Point curWindowPosition;
    private void AdjustStagePosition()
    {
        curWindowPosition=WindowsScanner.windowsScanner.GetWindowPosition(curWindow.hWnd);
        int deltaX=curWindowPosition.x-curWindowX;
        int deltaY=curWindowPosition.y-curWindowY;
        if(deltaX==0 && deltaY==0)
            return;

        System.out.println("Adjusting stage position "+deltaX+","+deltaY);
        stage.setX(stage.getX()+deltaX);
        stage.setY(stage.getY()+deltaY);

        curWindowX=curWindowPosition.x;
        curWindowY=curWindowPosition.y;
        //我又草了又忘记更新这个了
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
        if(physicsModeBoolean) {
            ChangeState(curChar.states.get("start"));
        } else {
            ChangeState(curChar.defaultState);
        }
    }
}