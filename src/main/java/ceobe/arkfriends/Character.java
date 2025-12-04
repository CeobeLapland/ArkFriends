package ceobe.arkfriends;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Character
{
    public String name;
    public Map<String, AnimationState> states;
    public AnimationState defaultState;

    public Character()
    {
        this("ceobe");
    }

    public Character(String name)
    {
        this.name = name;
        this.states = new HashMap<>();
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\characterAnimation.json")));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Object>> characters = objectMapper.readValue(jsonContent, new TypeReference<Map<String, Map<String, Object>>>() {});

            // 获取指定角色数据
            if (characters.containsKey(name)) {
                Map<String, Object> characterData = characters.get(name);
                String defaultStateName = (String) characterData.get("defaultState");
                Map<String, Map<String, Object>> actions = (Map<String, Map<String, Object>>) characterData.get("actions");

                // 初始化动画状态
                for (Map.Entry<String, Map<String, Object>> entry : actions.entrySet()) {
                    String actionName = entry.getKey();
                    Map<String, Object> actionData = entry.getValue();

                    String path = (String) actionData.get("path");
                    int fps = (int) actionData.get("fps");
                    boolean loop = (boolean) actionData.get("loop");

                    // 加载路径下的所有 PNG 文件
                    List<Image> images = loadImagesFromPath(path);

                    // 创建 AnimationState 并添加到 states
                    AnimationState state = new AnimationState(path, images, loop, fps);
                    states.put(actionName, state);

                    // 设置默认状态
                    if (actionName.equals(defaultStateName)) {
                        defaultState = state;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Image> loadImagesFromPath(String path) throws IOException
    {
        List<Image> images = new ArrayList<>();
//        try {
//            System.out.println("Loading images from path: " + path);
//            Files.list(Paths.get(path))
//                .filter(p -> p.toString().endsWith(".png"))
//                .sorted()//确保按文件名排序
//                .forEach(
//                        p -> images.add(new Image(p.toUri().toString()))
//                );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println("Loading images from path: " + path);
        Files.list(Paths.get(path))
                .filter(p -> p.toString().endsWith(".png"))
                .sorted()//确保按文件名排序
                .forEach(
                        p -> images.add(new Image(p.toUri().toString()))
                );
        return images;
    }
}

class AnimationState
{
    String assertsPath;
    public List<Image> animations;//路径里的所有png
    boolean isLoop;
    int fps;

    public AnimationState(String assertsPath, List<Image> animations, boolean isLoop, int fps)
    {
        this.assertsPath = assertsPath;
        this.animations = animations;
        this.isLoop = isLoop;
        this.fps = fps;
    }
}
