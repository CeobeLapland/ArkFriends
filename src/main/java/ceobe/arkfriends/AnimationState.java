package ceobe.arkfriends;

import javafx.scene.image.Image;
import java.util.List;

public class AnimationState
{
    String assertsPath;
    public List<Image> imageList;//路径里的所有png
    boolean isLoop;
    int fps;
    boolean isMove;

    public AnimationState(String assertsPath, List<Image> imageList, boolean isLoop, int fps, boolean isMove)
    {
        this.assertsPath = assertsPath;
        this.imageList = imageList;
        this.isLoop = isLoop;
        this.fps = fps;
        this.isMove = isMove;
    }
}