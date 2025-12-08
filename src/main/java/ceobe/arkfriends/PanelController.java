package ceobe.arkfriends;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javafx.util.Callback;
import javafx.util.Duration;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class PanelController
{
    public static PanelController panelController;

    public Button starterButton,agentButton,settingsButton,moreButton;
    public Button minimizeButton,closeButton;
    public Button launchButton;
    public AnchorPane starterPanel,agentPanel,settingsPanel,morePanel;
    public Region curPanel;
    //public AnchorPane curPanel;
    public ImageView previewImage;


    public CheckBox vanguardCheckBox,guardCheckBox,sniperCheckBox,alchemistCheckBox,
            medicCheckBox,assistCheckBox,reshipperCheckBox,specialistCheckBox;
    //public Map<CheckBox,CharacterOccupation> occupationCheckBoxs;
    public final Map<CheckBox,CharacterOccupation> occupationCheckBoxs=new HashMap<CheckBox,CharacterOccupation>(){{
        put(vanguardCheckBox,CharacterOccupation.VANGUARD);
        put(guardCheckBox,CharacterOccupation.GUARD);
        put(sniperCheckBox,CharacterOccupation.SNIPER);
        put(alchemistCheckBox,CharacterOccupation.ALCHEMIST);
        put(medicCheckBox,CharacterOccupation.MEDIC);
        put(assistCheckBox,CharacterOccupation.ASSIST);
        put(reshipperCheckBox,CharacterOccupation.RESHIPPER);
        put(specialistCheckBox,CharacterOccupation.SPECIALIST);
    }};

    public CheckBox sixStarCheckBox,fiveStarCheckBox,fourStarCheckBox,
            threeStarCheckBox,twoStarCheckBox,oneStarCheckBox;
    //public Map<CheckBox,Integer> starCheckBoxs;
    public final Map<CheckBox,Integer> starCheckBoxs=new HashMap<CheckBox,Integer>(){
        {
            put(sixStarCheckBox, 6);
            put(fiveStarCheckBox, 5);
            put(fourStarCheckBox, 4);
            put(threeStarCheckBox, 3);
            put(twoStarCheckBox, 2);
            put(oneStarCheckBox, 1);
        }};


    public TextField searchField;
    public ListView characterListView;
    public Label searchCountLabel;

    public String curCharacterName="ceobe";

    //public Map<String,CharacterSearchData> allCharacterSearchData=new HashMap<String,CharacterSearchData>();
    public Map<String,CharacterSearchData> allCharacterSearchData=new HashMap<>();//String是name
    public Map<String,CharacterSearchData> curCharacterSearchData;//=new HashMap<>();

    public PanelController()
    {
        if(panelController==null)
            panelController=this;
        //AddWindowsEffect();
        Timer timer=new Timer();
        timer.schedule(new java.util.TimerTask(){
            @Override
            public void run(){
                /*//AddWindowsEffect();
                curPanel=starterPanel;
                if(curPanel==null)
                    System.out.println("curPanel is null");

                System.out.println(allCharacterSearchData.size()+"  allCharacterSearchData.size()");
                //curCharacterSearchData=allCharacterSearchData;
                curCharacterSearchData=new HashMap<>(allCharacterSearchData);
                PopulateCharacterInListView();*/
                DelayedInitialization();

                timer.cancel();
            }
        },1000);//100毫秒还不行，还需要更久，不知道线程执行的怎么样

        LoadCharacterSearchData();


        //curPanel=starterPanel;//应该还是线程的问题，这两句执行时间比较靠后
        //System.out.println(curPanel.getClass().toString());
//        if(curPanel==null)
//            System.out.println("curPanel is null");
//        if(starterPanel==null)
//            System.out.println("starterPanel is null");//应该还是线程的问题，这两句执行时间比较靠后
    }
    private void DelayedInitialization()
    {
        curPanel=starterPanel;
        if(curPanel==null)
            System.out.println("curPanel is null");

        //AddWindowsEffect();
        //这个还必须放在Launcher里面

        System.out.println(allCharacterSearchData.size()+"  allCharacterSearchData.size()");
        //curCharacterSearchData=allCharacterSearchData;
        curCharacterSearchData=new HashMap<>(allCharacterSearchData);
        PopulateCharacterInListView();

        //延迟初始化
        /*occupationCheckBoxs=new HashMap<CheckBox,CharacterOccupation>(){{
            put(vanguardCheckBox,CharacterOccupation.VANGUARD);
            put(guardCheckBox,CharacterOccupation.GUARD);
            put(sniperCheckBox,CharacterOccupation.SNIPER);
            put(alchemistCheckBox,CharacterOccupation.ALCHEMIST);
            put(medicCheckBox,CharacterOccupation.MEDIC);
            put(assistCheckBox,CharacterOccupation.ASSIST);
            put(reshipperCheckBox,CharacterOccupation.RESHIPPER);
            put(specialistCheckBox,CharacterOccupation.SPECIALIST);
        }};
        starCheckBoxs=new HashMap<CheckBox,Integer>(){
            {
                put(sixStarCheckBox, 6);
                put(fiveStarCheckBox, 5);
                put(fourStarCheckBox, 4);
                put(threeStarCheckBox, 3);
                put(twoStarCheckBox, 2);
                put(oneStarCheckBox, 1);
            }};*/
    }

    public void LoadCharacterSearchData()
    {
        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            //System.out.println(1);
            // 读取 JSON 文件并将其映射到 Map
            File jsonFile = new File("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\characterSearchData.json");
            //File jsonFile = new File("src/main/resources/jsons/characterSearchData.json");
            //System.out.println(2);
            if (!jsonFile.exists())
            {
                System.out.println("JSON 文件未找到: " + jsonFile.getAbsolutePath());
                return;
            }
            //System.out.println(3);
            Map<String, CharacterSearchData> data = objectMapper.readValue(jsonFile,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, CharacterSearchData.class));
            //我真草了，搞了半天检查好久发现是变量名拼错了我草

            //System.out.println(4);
            // 将数据存入 allCharacterSearchData
            //allCharacterSearchData.putAll(data);
            allCharacterSearchData=data;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load character search data.");
        }
        //curCharacterSearchData=allCharacterSearchData;
    }


    //region面板切换
    //其实下面这四个函数没必要，我想着可能会有一些特殊效果之类的
    //可以直接lambda表达式传递参数调用ChangeRegion
    public void OnChangeStarterPanelClick()
    {
        ChangeRegion(curPanel,starterPanel);
    }
    public void OnChangeAgentPanelClick()
    {
        ChangeRegion(curPanel,agentPanel);
    }
    public void OnChangeSettingsPanelClick()
    {
        ChangeRegion(curPanel,settingsPanel);
    }
    public void OnChangeMorePanelClick()
    {
        ChangeRegion(curPanel,morePanel);
    }

    public void ChangeRegion(Region regionClose, Region regionOpen)
    {
        if(regionOpen.isVisible())
        {
            System.out.println("Region already open");
            return;//如果已经打开就不操作了
        }
        if(curPanel==null)
            System.out.println("curPanel is null when changing region");
        if (regionClose != null)
        {
            //FadeTransition fadeOut = new FadeTransition(Duration.millis(300), regionClose);
            //fadeOut.setFromValue(1.0);
            //fadeOut.setToValue(0.0);
            //fadeOut.setOnFinished(e -> regionClose.setVisible(false));
            //fadeOut.play();
            regionClose.setVisible(false);
            System.out.println("Region Close");
        }
        if (regionOpen != null)
        {
            curPanel=regionOpen;

            //regionOpen.setOpacity(0.0);
            regionOpen.setVisible(true);
            //FadeTransition fadeIn = new FadeTransition(Duration.millis(300), regionOpen);
            //fadeIn.setFromValue(0.0);
            //fadeIn.setToValue(1.0);
            //fadeIn.play();
            regionOpen.toFront();
            System.out.println("Region Open");
        }
    }
    public void LaunchRunning() throws IOException
    {
        //Application.launch(Running.class);
        //Launcher.launcher.runningScene=new Scene(Launcher.launcher.mainReader.load());
        //Launcher.launcher.
        Launcher.launcher.StartRunning();

        AnimationController.animationController.ChangeCharacter(curCharacterName);
        AnimationController.animationController.PlayAnimation();
    }
    //endregion

    public void SelectCharacterInListView(String name)
    {
        curCharacterName=name;
        previewImage.setImage(new Image(
            //allCharacterSearchData.get(name).defaultImagePath
            new File(allCharacterSearchData.get(name).defaultImagePath).toURI().toString()
        ));
        /*for (Object item : characterListView.getItems())
        {
            if (item instanceof String && item.equals(name))
            {
                characterListView.getSelectionModel().select(item);
                characterListView.scrollTo(item);
                break;
            }
        }*/
    }

    public void SiftCharacterSearchData()
    {
        //还得再写一版
        //其实不用，是我这个笨蛋忘记更新UI了呜呜呜
        //又浪费了马原课上的十五分钟

        // 收集被选中的职业
        Set<CharacterOccupation> selectedOccupations = new HashSet<>();
        for (Map.Entry<CheckBox, CharacterOccupation> e : occupationCheckBoxs.entrySet())
        {
            CheckBox cb = e.getKey();
            if (cb != null && cb.isSelected() && e.getValue() != null)
                selectedOccupations.add(e.getValue());
            //这里getValue一直为空，我在想为什么
            //不会又是线程延迟导致十四个checkBox还没初始化吧
            //加到timer里延迟一下试一试
//            if (e.getKey().isSelected())
//                selectedOccupations.add(e.getValue());
        }

        // 收集被选中的星级
        Set<Integer> selectedStars = new HashSet<>();
        for (Map.Entry<CheckBox, Integer> e : starCheckBoxs.entrySet())
        {
            CheckBox cb = e.getKey();
            if (cb != null && cb.isSelected() && e.getValue() != null)
                selectedStars.add(e.getValue());
//            if (e.getKey().isSelected())
//                selectedStars.add(e.getValue());
        }

        // 如果都未选中，则恢复为全部（使用副本以避免修改原始数据）
        if (selectedOccupations.isEmpty() && selectedStars.isEmpty())
        {
            curCharacterSearchData = new HashMap<>(allCharacterSearchData);
        }
        else
        {
            Map<String, CharacterSearchData> filtered = new HashMap<>();
            for (Map.Entry<String, CharacterSearchData> entry : allCharacterSearchData.entrySet())
            {
                CharacterSearchData data = entry.getValue();
                if (data == null) continue;

                boolean matchOccupation = data.occupationEnum != null && selectedOccupations.contains(data.occupationEnum);
                boolean matchStar = selectedStars.contains(data.stars);

                if (matchOccupation || matchStar)
                    filtered.put(entry.getKey(), data);
            }
            curCharacterSearchData = filtered;
        }

        PopulateCharacterInListView();
    }
    public void ResetSiftCheckBox()
    {
        for (CheckBox cb : occupationCheckBoxs.keySet())
        {
            if (cb != null)
                cb.setSelected(false);
        }
        for (CheckBox cb : starCheckBoxs.keySet())
        {
            if (cb != null)
                cb.setSelected(false);
        }
        //curCharacterSearchData = new HashMap<>(allCharacterSearchData);
        //PopulateCharacterInListView();
    }
    //这个是copilot自己补的，不过我觉得随时改变还是有点耗性能
    /*public void OnSearchFieldChanged()
    {
        String keyword = searchField.getText().toLowerCase();

        if (keyword.isEmpty())
        {
            curCharacterSearchData = new HashMap<>(allCharacterSearchData);
        }
        else
        {
            Map<String, CharacterSearchData> filtered = new HashMap<>();
            for (Map.Entry<String, CharacterSearchData> entry : allCharacterSearchData.entrySet())
            {
                CharacterSearchData data = entry.getValue();
                if (data == null) continue;

                if (data.EnglishName.toLowerCase().contains(keyword) ||
                    data.ChineseName.toLowerCase().contains(keyword) ||
                    data.description.toLowerCase().contains(keyword))
                {
                    filtered.put(entry.getKey(), data);
                }
            }
            curCharacterSearchData = filtered;
        }

        PopulateCharacterInListView();
    }*/

    public void SearchCharacter()
    {
        String keyword = searchField.getText().toLowerCase();

        if (keyword.isEmpty())
            return;

        Map<String, CharacterSearchData> filtered = new HashMap<>();
        for (Map.Entry<String, CharacterSearchData> entry : allCharacterSearchData.entrySet())
        {
            CharacterSearchData data = entry.getValue();
            if (data == null) continue;

            if (data.EnglishName.contains(keyword) ||
                    data.ChineseName.contains(keyword))
            {
                filtered.put(entry.getKey(), data);
            }
        }
        curCharacterSearchData = filtered;

        PopulateCharacterInListView();
    }
    public void OnSearchFieldClearClick()
    {
//        if(curCharacterSearchData==allCharacterSearchData)
//            return;
        if(curCharacterSearchData.equals(allCharacterSearchData))
            return;
        searchField.clear();
        curCharacterSearchData = new HashMap<>(allCharacterSearchData);
        PopulateCharacterInListView();
    }

    public void PopulateCharacterInListView()
    {
        // 清空当前列表项
        System.out.println(characterListView.getItems().size()+"  before characterListView.getItems().size()");
        characterListView.getItems().clear();
        ObservableList list=characterListView.getItems();

        // 遍历 curCharacterSearchData，动态创建项
        for (Map.Entry<String, CharacterSearchData> entry : curCharacterSearchData.entrySet())
        {
            CharacterSearchData data = entry.getValue();
            String listItem = String.format("%s (%s)\n%s", data.EnglishName, data.ChineseName, data.description);

            //characterListView.getItems().add(listItem);
            list.add(listItem);
        }

        //设置自定义单元格工厂以显示多行文本
        /*characterListView.setCellFactory(new Callback<ListView<String>,
                ListCell<String>>(){
            @Override
            public ListCell<String> call(ListView<String> param)
            {
                return new ListCell<String>()
                {
                    @Override
                    protected void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                    }
                };
            }
        });*/
        //为每一项订阅事件
        //好像也只能这么写
        characterListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                // 提取角色英文名（假设英文名在第一行）
                //String temp=(String)newValue;
                String selectedName = ((String)newValue).split(" ")[0];

                System.out.println("Selected character: " + selectedName);
                //这个性能不咋地
                SelectCharacterInListView(selectedName);
            }
        });
        searchCountLabel.setText("筛选结果："+curCharacterSearchData.size()+"个角色");
        System.out.println(characterListView.getItems().size()+"  after characterListView.getItems().size()");
    }

    //region窗口拖动

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

    //@FXML
    public void MinimizeWindow(MouseEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);

        // 添加点击效果
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #4a6572; -fx-background-radius: 0;");
    }
    //要么用public要么加注解@FXML
    //@FXML
    public void CloseWindow(MouseEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void WhenButtonHover(MouseEvent event)
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
    private void WhenButtonExit(MouseEvent event)
    {
        Button button = (Button)event.getSource();
        //button.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-border-color: #4a6572; -fx-border-width: 1;");
        button.setStyle("-fx-background-color: #00BFFF; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-border-color: #4a6572; -fx-border-width: 1;");
        button.setEffect(null);
    }
    public void ButtonTest()
    {
        System.out.println("Button clicked!");
    }
    //endregion
}

class CharacterSearchData
{
    public String EnglishName;
    public String ChineseName;
    public String description;
    public int stars;
    //职业string改成enum更节省性能
    public CharacterOccupation occupationEnum;//我草
    //public String occupation;
    public String defaultImagePath;
}