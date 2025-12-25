package ceobe.arkfriends;

import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class PhysicsDragWithoutGravity implements IPhysicsDragController
{

    // 位置 & 速度
    public DoublePoint position;

    public DoublePoint velocity = new DoublePoint(0, 0);

    // 旋转（角度制）
    public double rotation = 0;
    public double angularVelocity = 0;

    // 拖拽状态
    public boolean isDragging = false;

    // 鼠标目标
    private DoublePoint mouseTarget = new DoublePoint(0, 0);
    private DoublePoint lastMouse = new DoublePoint(0, 0);

    // 参数（可调）
    //我把所有double全改成int了
    private double stiffness = 20;     // 弹性
    private double damping = 5;        // 阻尼
    private double angularStiffness = 30;
    private double angularDamping = 1;
    private double maxAngle = 50;      // 最大晃动角度

    public PhysicsDragWithoutGravity(DoublePoint startPos)
    {
        this.position = startPos;
    }

    public void SetIsDragging(boolean dragging)
    {
        this.isDragging = dragging;
    }
    public DoublePoint GetVelocity()
    {
        return this.velocity;
    }
    /* 鼠标按下 */
    public void OnMousePressed(double mx, double my)
    {
        isDragging = true;
        mouseTarget.x = mx;
        mouseTarget.y = my;
        lastMouse.x = mx;
        lastMouse.y = my;
        velocity.x = velocity.y = 0;

        //加一个
        position.x=mx;
        position.y=my;
    }

    /* 鼠标拖拽 */
    public void OnMouseDragged(double mx, double my)
    {
        mouseTarget.x = mx;
        mouseTarget.y = my;

        // 计算鼠标速度
        double dx = mx - lastMouse.x;
        //double dy = my - lastMouse.y;

        // 用水平速度制造摆动
        double targetAngle = Clamp(dx * 2, -maxAngle, maxAngle);
        angularVelocity += (targetAngle - rotation) * 0.5;

        lastMouse.x = mx;
        lastMouse.y = my;
    }

    /* 鼠标释放 */
    public void OnMouseReleased()
    {
        isDragging = false;
    }

    //int tempVelocityX;
    /* 每帧更新 */
    public void Update(double dt) {

        if (isDragging) {
            // 弹簧跟随鼠标
            double ax = (mouseTarget.x - position.x) * stiffness - velocity.x * damping;
            double ay = (mouseTarget.y - position.y) * stiffness - velocity.y * damping;

            velocity.x += ax * dt;
            velocity.y += ay * dt;

        } else {
            // 松手后的惯性衰减
            //tempVelocityX= velocity.x;
            //velocity.x *= 0.92;
            //if(tempVelocityX==velocity.x)
            //{
            //    velocity.x=0;
            //    velocity.y=0;
            //}

            //其实这个没啥用，因为时间太短了，负向速度只能抵消很小一部分
            //之后还没等移出去就又变号了
            //所以还得加上position的改变
            if(position.x<0) {
                position.x = 0;
                velocity.x = -velocity.x;
                System.out.println("碰到左边");
            } else if (position.x>1700) {
                position.x = 1700;
                velocity.x = -velocity.x;
                System.out.println("碰到右边");
            }
            if(position.y<0) {
                position.y = 0;
                velocity.y = -velocity.y;
                System.out.println("碰到上边");
            } else if (position.y>900) {
                position.y= 900;
                velocity.y = -velocity.y;
                System.out.println("碰到下边");
            }

            velocity.x *= 0.95;
            velocity.y *= 0.95;
        }

        // 更新位置
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // 角度回正
        double angleAcc = -rotation * angularStiffness - angularVelocity * angularDamping;
        angularVelocity += angleAcc * dt;
        rotation += angularVelocity * dt;
        //System.out.println("Position: (" + position.x + ", " + position.y + ") Velocity: (" + velocity.x + ", " + velocity.y + ") Rotation: " + rotation + " Angular Velocity: " + angularVelocity);
    }

    /* 应用到 ImageView */
    public void Apply(Stage stage, ImageView view)
    {
        //view.setLayoutX(position.x);
        //view.setLayoutY(position.y);
        //100还得改成可变变量
        stage.setX(position.x-100);
        stage.setY(position.y-100);
        view.setRotate(rotation);
    }

    private double Clamp(double v, double min, double max)
    {
        return Math.max(min, Math.min(max, v));
    }
}
class DoublePoint
{
    //我是真没法了
    //之前以为有int就够用了，现在如果要把之前的point全变成带泛型的point就太麻烦了
    public double x;
    public double y;

    public DoublePoint(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
}