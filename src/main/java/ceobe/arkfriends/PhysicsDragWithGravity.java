package ceobe.arkfriends;

import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class PhysicsDragWithGravity implements IPhysicsDragController
{
    //基础状态
    public DoublePoint position;
    public DoublePoint velocity = new DoublePoint(0, 0);

    public double rotation = 0;
    public double angularVelocity = 0;

    public boolean isDragging = false;

    /* ================== 鼠标 ================== */

    private DoublePoint mouseTarget = new DoublePoint(0, 0);
    private DoublePoint lastMouse = new DoublePoint(0, 0);

    /* ================== 物理参数 ================== */

    // 重力
    private double gravity = 2000; // px / s^2

    // 弹簧（拖拽）
    private double stiffness = 30;
    private double damping = 10;

    // 角度
    private double angularStiffness = 30;
    private double angularDamping = 5;
    private double maxAngle = 60;

    // 反弹 & 摩擦
    private double bounce = 0.55;
    private double friction = 0.9;

    // 地面（任务栏）
    private double groundY;

    // 屏幕边界
    private double leftBound = 0;
    private double rightBound;

    public PhysicsDragWithGravity(DoublePoint startPos, double petWidth, double petHeight)
    {
        this.position = startPos;

        var bounds = Screen.getPrimary().getVisualBounds();
        this.rightBound = bounds.getWidth() - petWidth+200;
        //this.groundY = bounds.getHeight() - petHeight;
        this.groundY = bounds.getHeight() - petHeight + 150; //预留任务栏高度
    }

    public void SetIsDragging(boolean dragging)
    {
        this.isDragging = dragging;
    }
    public DoublePoint GetVelocity()
    {
        return this.velocity;
    }
    /* ================== 鼠标事件 ================== */

    public void OnMousePressed(double mx, double my)
    {
        isDragging = true;

        mouseTarget.x = mx;
        mouseTarget.y = my;

        lastMouse.x = mx;
        lastMouse.y = my;

        velocity.x = velocity.y = 0;
    }

    public void OnMouseDragged(double mx, double my)
    {
        mouseTarget.x = mx;
        mouseTarget.y = my;

        double dx = mx - lastMouse.x;
        double dy = my - lastMouse.y;

        // 拖拽速度直接叠加 → 用于“扔飞”
        velocity.x += dx * 20;
        velocity.y += dy * 20;

        // 摆动角度
        double targetAngle = Clamp(dx * 2.5, -maxAngle, maxAngle);
        angularVelocity += (targetAngle - rotation) * 0.6;

        lastMouse.x = mx;
        lastMouse.y = my;
    }

    public void OnMouseReleased() {
        isDragging = false;
    }

    //主更新
    public void Update(double dt)
    {

        if (isDragging) {
            // 拖拽弹簧（弱重力）
            double ax = (mouseTarget.x - position.x) * stiffness - velocity.x * damping;
            double ay = (mouseTarget.y - position.y) * stiffness - velocity.y * damping;

            velocity.x += ax * dt;
            velocity.y += ay * dt * 0.6; // 拖拽时重力减弱

        } else {
            // 自由落体
            if(Math.abs(position.y-groundY)<=5)
                velocity.y=0;
            else
                velocity.y += gravity * dt;
        }
        if(velocity.x<0.1 && velocity.x>-0.1)
            velocity.x=0;


        velocity.x *= friction;

        // 更新位置
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // 边界反弹
        HandleBounds();

        // 角度回正
        double angleAcc = -rotation * angularStiffness - angularVelocity * angularDamping;
        angularVelocity += angleAcc * dt;
        rotation += angularVelocity * dt;
    }

    //边界 & 地面
    private void HandleBounds()
    {
        // 地面
        if (position.y >= groundY)
        //if(Math.abs(position.y-groundY)<=5)
        {
            position.y = groundY;

            if (Math.abs(velocity.y) > 50) {
                velocity.y = -velocity.y * bounce;
                velocity.x *= friction;
            } else {
                velocity.y = 0;
            }
        }

        // 左右
        if (position.x <= leftBound) {
            position.x = leftBound;
            velocity.x = -velocity.x * bounce;
        }

        if (position.x >= rightBound) {
            position.x = rightBound;
            velocity.x = -velocity.x * bounce;
        }

        //上方
        if (position.y <= 0) {
            position.y = 0;
            velocity.y = -velocity.y * bounce;
        }
    }

    //应用到视图

    /*public void Apply(ImageView view) {
        view.setLayoutX(position.x);
        view.setLayoutY(position.y);
        view.setRotate(rotation);
    }*/
    public void Apply(Stage stage, ImageView view)
    {
        //100还得改成可变变量
        stage.setX(position.x-100);
        stage.setY(position.y-100);
        view.setRotate(rotation);
    }

    private double Clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
