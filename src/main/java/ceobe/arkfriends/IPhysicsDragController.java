package ceobe.arkfriends;

import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public interface IPhysicsDragController
{
    //public boolean isDragging = false;
    //先把函数填上，变量之后再加
    void OnMousePressed(double mx, double my);

    void OnMouseDragged(double mx, double my);

    void OnMouseReleased();

    void Update(double dt);

    void Apply(Stage stage, ImageView view);

    //double Clamp(double v, double min, double max);
    void SetIsDragging(boolean dragging);

    DoublePoint GetVelocity();
}
