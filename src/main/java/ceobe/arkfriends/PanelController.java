package ceobe.arkfriends;

//region imports
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

//import java.awt.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;

//import System.out;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

//endregion
public class PanelController
{
    //region 引用模块
    public static PanelController panelController;

    public boolean isLaunched=false;

    public Button starterButton,agentButton,settingsButton,moreButton;
    public Button minimizeButton,closeButton;
    public Button launchButton;
    public AnchorPane starterPanel,agentPanel,settingsPanel,morePanel;
    public Region curPanel;
    //public AnchorPane curPanel;
    public ImageView previewImage;
    public Label nameLabel;


    public CheckBox vanguardCheckBox,guardCheckBox,sniperCheckBox,alchemistCheckBox,
            medicCheckBox,assistCheckBox,reshipperCheckBox,specialistCheckBox;
    public Map<CheckBox,CharacterOccupation> occupationCheckBoxs;
    /*public final Map<CheckBox,CharacterOccupation> occupationCheckBoxs=new HashMap<CheckBox,CharacterOccupation>(){{
        put(vanguardCheckBox,CharacterOccupation.VANGUARD);
        put(guardCheckBox,CharacterOccupation.GUARD);
        put(sniperCheckBox,CharacterOccupation.SNIPER);
        put(alchemistCheckBox,CharacterOccupation.ALCHEMIST);
        put(medicCheckBox,CharacterOccupation.MEDIC);
        put(assistCheckBox,CharacterOccupation.ASSIST);
        put(reshipperCheckBox,CharacterOccupation.RESHIPPER);
        put(specialistCheckBox,CharacterOccupation.SPECIALIST);
    }};*/

    //我想知道是不是把final去掉就可以了

    public CheckBox sixStarCheckBox,fiveStarCheckBox,fourStarCheckBox,
            threeStarCheckBox,twoStarCheckBox,oneStarCheckBox;
    public Map<CheckBox,Integer> starCheckBoxs;
    /*public final Map<CheckBox,Integer> starCheckBoxs=new HashMap<CheckBox,Integer>(){
        {
            put(sixStarCheckBox, 6);
            put(fiveStarCheckBox, 5);
            put(fourStarCheckBox, 4);
            put(threeStarCheckBox, 3);
            put(twoStarCheckBox, 2);
            put(oneStarCheckBox, 1);
        }};*/


    public TextField searchField;
    public ListView characterListView;
    public Label searchCountLabel;

    public String curCharacterName="ceobe";

    //再维护一个名字列表吧
    public List<String> allCharacterNames=new ArrayList<>();
    //public Map<String,CharacterSearchData> allCharacterSearchData=new HashMap<String,CharacterSearchData>();
    public Map<String,CharacterSearchData> allCharacterSearchData=new HashMap<>();//String是name
    public Map<String,CharacterSearchData> curCharacterSearchData;//=new HashMap<>();

    //public ObservableList allListItems,curListItems;
    public ObservableList allListItems;

    public VBox leftColumn;
    public StackPane centerPanel;
    public ImageView picturePosition1,picturePosition2,picturePosition3,picturePosition4,
            picturePosition5,picturePosition6,picturePosition7,picturePosition8;
    public List<ImageView> picturePositions;
    /*public List<ImageView> picturePositions=new ArrayList<>(){
        {
            add(picturePosition1);
            add(picturePosition2);
            add(picturePosition3);
            add(picturePosition4);
            add(picturePosition5);
            add(picturePosition6);
            add(picturePosition7);
            add(picturePosition8);
        }
    };*/
    //又是这个初始化为null

    //endregion

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
                Platform.runLater(() ->{
                    DelayedInitialization();
                });
                //Ok了这样就可以了，必须要在javafx的UI线程里执行
                //DelayedInitialization();

                timer.cancel();
            }
        },1000);//100毫秒还不行，还需要更久，不知道线程执行的怎么样

        LoadCharacterSearchData();
        //Platform.runLater(() ->{
        //    LoadPicturePositions();
        //});
        //LoadPicturePositions();


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


        //延迟初始化
        occupationCheckBoxs=new HashMap<CheckBox,CharacterOccupation>(){{
            put(vanguardCheckBox,CharacterOccupation.VANGUARD);
            put(guardCheckBox,CharacterOccupation.GUARD);
            put(sniperCheckBox,CharacterOccupation.SNIPER);
            put(alchemistCheckBox,CharacterOccupation.ALCHEMIST);
            put(medicCheckBox,CharacterOccupation.MEDIC);
            put(assistCheckBox,CharacterOccupation.ASSIST);
            put(reshipperCheckBox,CharacterOccupation.RESHIPPER);
            put(specialistCheckBox,CharacterOccupation.SPECIALIST);
        }};
        starCheckBoxs=new HashMap<CheckBox,Integer>(){{
                put(sixStarCheckBox, 6);
                put(fiveStarCheckBox, 5);
                put(fourStarCheckBox, 4);
                put(threeStarCheckBox, 3);
                put(twoStarCheckBox, 2);
                put(oneStarCheckBox, 1);
            }};

        picturePositions=new ArrayList<>(){
            {
                add(picturePosition1);
                add(picturePosition2);
                add(picturePosition3);
                add(picturePosition4);
                add(picturePosition5);
                add(picturePosition6);
                add(picturePosition7);
                add(picturePosition8);
            }
        };

        System.out.println(allCharacterSearchData.size()+"  allCharacterSearchData.size()");
        //curCharacterSearchData=allCharacterSearchData;
        curCharacterSearchData=new HashMap<>(allCharacterSearchData);
        PopolateCharacterListView();

        LoadPicturePositions();
        //临时测试用
        /*titleBar.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                // 双击事件处理逻辑
                System.out.println("Title bar double-clicked!");
                //这里可以实现最大化和还原窗口的功能
                Stage stage = Launcher.launcher.launcherStage;
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                } else {
                    stage.setMaximized(true);
                }
            }
        });*/
//        titleBar.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            System.out.println(Launcher.launcher.launcherStage.getX()+" , "+Launcher.launcher.launcherStage.getY());
//        });
        InitializeSettings();
        LoadPreferredSettings();

        SetupHyperlinkActions();
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
            allCharacterNames=new ArrayList<>(data.keySet());

            allCharacterSearchData=data;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load character search data.");
        }
        //curCharacterSearchData=allCharacterSearchData;
    }

    private void LoadPicturePositions()
    {
        //非常重要的
        //禁用自动调节
        centerPanel.setManaged(false);
        //从八个位置里随便挑三到五个
        for(ImageView iv : picturePositions)
        {
            if(Math.random()<0.6)
            {
                //加载图片
                //iv.setImage(new Image(
                //    new File("D:\\ArkFriends\\ArkFriends\\src\\main\\resources\\images\\launcher\\picturePositionSample.png").toURI().toString()
                //));
                //从allCharacterNames里随机挑一个角色
                String randomCharName=allCharacterNames.get(
                        (int)(Math.random()*allCharacterNames.size())
                );
                iv.setImage(new Image(
                        new File(allCharacterSearchData.get(randomCharName).defaultImagePath).toURI().toString()
                ));
                //随机旋转+-15度
                iv.setRotate((Math.random()-0.5)*30+iv.getRotate());
                //随机偏移上下左右+-20像素
                iv.setLayoutX(iv.getLayoutX()+(Math.random()-0.5)*40);
                iv.setLayoutY(iv.getLayoutY()+(Math.random()-0.5)*40);
            }
            else
                iv.setImage(null);
        }
        leftColumn.toFront();
        titleBar.toFront();
        //System.out.println(starterPanel.getLayoutX()+" , "+starterPanel.getLayoutY()+"LayoutX and Y" );
        //System.out.println(starterPanel.getTranslateX()+" , "+starterPanel.getTranslateY()+"TranslateX and Y" );
        //System.out.println(starterPanel+" , "+starterPanel.getLayoutY()+"LayoutX and Y" );
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
            FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), regionClose);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> regionClose.setVisible(false));
            fadeOut.play();
            regionClose.setVisible(false);
            System.out.println("Region Close");
        }
        if (regionOpen != null)
        {
            curPanel=regionOpen;

            regionOpen.setOpacity(0.0);
            regionOpen.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), regionOpen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            regionOpen.toFront();
            System.out.println("Region Open");
        }
    }

    //endregion

    public void LaunchRunning() throws IOException
    {
        launchButton.setDisable(true);
        //Application.launch(Running.class);
        //Launcher.launcher.runningScene=new Scene(Launcher.launcher.mainReader.load());
        //Launcher.launcher.
        Launcher.launcher.StartRunning();

        //AnimationController.animationController.ChangeCharacter(curCharacterName);
        //AnimationController.animationController.StartAnimation();
        AnimationController.animationController.curCharName=curCharacterName;
    }


    //region 角色搜索与列表刷新相关
    public void SelectCharacterInListView(String name)
    {
        curCharacterName=name;
        previewImage.setImage(new Image(
            //allCharacterSearchData.get(name).defaultImagePath
            new File(allCharacterSearchData.get(name).defaultImagePath).toURI().toString()
        ));
        nameLabel.setText(allCharacterSearchData.get(name).ChineseName+" ("+allCharacterSearchData.get(name).EnglishName+")");
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
        //System.out.println(occupationCheckBoxs.size()+"  occupationCheckBoxs.size()");
        //System.out.println(starCheckBoxs.size()+"  starCheckBoxs.size()");

        // 收集被选中的职业
        Set<CharacterOccupation> selectedOccupations = new HashSet<>();
        for (Map.Entry<CheckBox, CharacterOccupation> e : occupationCheckBoxs.entrySet())
        {
            CheckBox cb = e.getKey();
            if (cb != null && cb.isSelected() && e.getValue() != null)
            {
                selectedOccupations.add(e.getValue());
                System.out.println(e.getValue());
            }
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
            {
                selectedStars.add(e.getValue());
                System.out.println(e.getValue());
            }
//            if (e.getKey().isSelected())
//                selectedStars.add(e.getValue());
        }

        // 如果都未选中，则恢复为全部（使用副本以避免修改原始数据）
        if (selectedOccupations.isEmpty() && selectedStars.isEmpty())
        {
            /*curCharacterSearchData = new HashMap<>(allCharacterSearchData);
            //characterListView.setItems(allListItems);
            characterListView.setItems(
                    FXCollections.observableArrayList(
                            new ArrayList<>(allListItems)
                    ));
            System.out.println(allListItems.size()+"  allListItems.size()");*/
            PopolateBack();
            return;
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
                System.out.println(data.EnglishName+"  "+data.stars+" stars"+data.occupationEnum+" occupation");
                //System.out.println();
                if (matchOccupation || matchStar)
                    filtered.put(entry.getKey(), data);
            }
            curCharacterSearchData = filtered;
        }

        RefreshCharacterListView();
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
        //characterListView.setItems(allListItems);
        /*characterListView.setItems(
                FXCollections.observableArrayList(
                        new ArrayList<>(allListItems)
                ));
        System.out.println(allListItems.size()+"  allListItems.size()");*/
        PopolateBack();
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

        RefreshCharacterListView();
    }
    public void OnSearchFieldClearClick()
    {
//        if(curCharacterSearchData==allCharacterSearchData)
//            return;
        if(curCharacterSearchData.equals(allCharacterSearchData))
            return;
        searchField.clear();

        //curCharacterSearchData = new HashMap<>(allCharacterSearchData);
        //RefreshCharacterListView();
        //这两句替换为下面这句
        /*curCharacterSearchData=new HashMap<>(allCharacterSearchData);
        //characterListView.setItems(allListItems);
        characterListView.setItems(
                FXCollections.observableArrayList(
                        new ArrayList<>(allListItems)
                ));
        System.out.println(allListItems.size()+"  allListItems.size()");*/
        PopolateBack();
    }


    public void PopolateBack()//回归最初所有角色都在的样子
    {
        if(curCharacterSearchData.equals(allCharacterSearchData)
            ||characterListView.getItems().size()==allListItems.size())
            return;
        curCharacterSearchData=new HashMap<>(allCharacterSearchData);
        searchCountLabel.setText("筛选结果："+allListItems.size()+"个角色");
        characterListView.setItems(
                FXCollections.observableArrayList(
                        new ArrayList<>(allListItems)
                ));
        System.out.println(allListItems.size()+"  allListItems.size()");
    }
    public void PopolateCharacterListView()
    {
        //allListItems=characterListView.getItems();
        allListItems=FXCollections.observableArrayList(new ArrayList<>(characterListView.getItems()));
        //very important

        //遍历curCharacterSearchData
        for (Map.Entry<String, CharacterSearchData> entry : allCharacterSearchData.entrySet())
        {
            CharacterSearchData data = entry.getValue();
            String itemString = String.format("%s (%s)\n%s", data.EnglishName, data.ChineseName, data.description);
            allListItems.add(itemString);
        }
        //为每一项订阅事件//好像也只能这么写
        characterListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null)
                    {
                        // 提取角色英文名（假设英文名在第一行）
                        String selectedName = ((String)newValue).split(" ")[0];
                        System.out.println("Selected character: " + selectedName);
                        //这个性能不咋地
                        SelectCharacterInListView(selectedName);
                        //完蛋了，订阅多次呜呜呜//重新搞一版
                    }
                });
        searchCountLabel.setText("筛选结果："+allListItems.size()+"个角色");
        characterListView.setItems(FXCollections.observableArrayList(
                        new ArrayList<>(allListItems)));
        System.out.println(allListItems.size()+"  all and cur ListItems.size()");
        System.out.println(allListItems.size()+"  allListItems.size()");

    }
    public void RefreshCharacterListView()
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
        /*//为每一项订阅事件
        //好像也只能这么写
        characterListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null)
                    {
                        // 提取角色英文名（假设英文名在第一行）
                        //String temp=(String)newValue;
                        String selectedName = ((String)newValue).split(" ")[0];

                        System.out.println("Selected character: " + selectedName);
                        //这个性能不咋地
                        SelectCharacterInListView(selectedName);
                        //完蛋了，订阅多次呜呜呜
                        //重新搞一版
                    }
                });*/
        searchCountLabel.setText("筛选结果："+curCharacterSearchData.size()+"个角色");
        System.out.println(characterListView.getItems().size()+"  after characterListView.getItems().size()");
    }
    /*public void PopulateCharacterInListView()
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
//        characterListView.setCellFactory(new Callback<ListView<String>,
//                ListCell<String>>(){
//            @Override
//            public ListCell<String> call(ListView<String> param)
//            {
//                return new ListCell<String>()
//                {
//                    @Override
//                    protected void updateItem(String item, boolean empty)
//                    {
//                        super.updateItem(item, empty);
//                        if (empty || item == null) {
//                            setText(null);
//                        } else {
//                            setText(item);
//                        }
//                    }
//                };
//            }
//        });
        //为每一项订阅事件
        //好像也只能这么写
        characterListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                // 提取角色英文名（假设英文名在第一行）
                //String temp=(String)newValue;
                String selectedName = ((String)newValue).split(" ")[0];

                System.out.println("Selected character: " + selectedName);
                //这个性能不咋地
                SelectCharacterInListView(selectedName);
                //完蛋了，订阅多次呜呜呜
                //重新搞一版
            }
        });
        searchCountLabel.setText("筛选结果："+curCharacterSearchData.size()+"个角色");
        System.out.println(characterListView.getItems().size()+"  after characterListView.getItems().size()");
    }*/

    //endregion

    //region窗口拖动相关

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
            //Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage stage = Launcher.launcher.launcherStage;
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
        //Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //stage.close();

        Launcher.launcher.launcherStage.close();
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

    //region 设置相关
    @FXML public Button restoreDefaultsButton;
    @FXML public Button saveSettingsButton;
    @FXML public Label settingsSavedLabel;

    // 启动器设置
    @FXML public CheckBox allowBackgroundCheckBox;
    @FXML public CheckBox isCloseLauncherCheckBox;
    @FXML public CheckBox transparentModeCheckBox;
    @FXML public CheckBox outputLogCheckBox;
    @FXML public TextField initialXField;
    @FXML public TextField initialYField;
    @FXML public TextField downloadPathField;

    // 声音设置
    @FXML public CheckBox voiceInteractionCheckBox;
    @FXML public CheckBox aiDialogueCheckBox;

    public TextField voiceAPITextField;

    @FXML public Slider characterVoiceSlider;
    @FXML public Label characterVoiceValue;
    @FXML public Slider soundVolumeSlider;
    @FXML public Label soundVolumeValue;
    public Slider noiseFilterSlider;
    public Label noiseFilterValue;
    public Slider soundPlaceholderSlider;
    public Label soundPlaceholderValue;

    // 动画设置
    @FXML public Button fps20Button,fps24Button,fps30Button;
    @FXML public Slider scaleSlider;
    @FXML public Label scaleValue;
    @FXML public Slider opacitySlider;
    @FXML public Label opacityValue;
    @FXML public CheckBox flipAxisCheckBox;
    public CheckBox boomCheckBox;

    @FXML public Button frameAnimationButton,spineAnimationButton;

    // 行为设置
    @FXML public CheckBox idleCheckBox;
    @FXML public CheckBox ignoreGravityCheckBox;
    @FXML public CheckBox sleepCheckBox;
    @FXML public CheckBox sitCheckBox;
    public CheckBox climbCheckBox;
    public CheckBox hideAndSeekCheckBox;
    public CheckBox interactableCheckBox;
    public CheckBox specialActionCheckBox;

    public Slider actionFrequencySlider;
    public Label actionFrequencyValue;
    @FXML public Slider movementSpeedSlider;
    @FXML public Label movementSpeedValue;

    // AI设置
    @FXML public CheckBox activeReplyCheckBox;
    public CheckBox outputEmotionCheckBox;
    public CheckBox printerEffectCheckBox;
    public CheckBox voiceStreamCheckBox;

    @FXML public Slider activeReplyFrequencySlider;
    @FXML public Label activeReplyFrequencyValue;

    @FXML public Slider coralationDegreeSlider;
    @FXML public Label coralationDegreeValue;

    @FXML
    public void InitializeSettings()
    {
        // 绑定滑动条和标签
        bindSliderToLabel(characterVoiceSlider, characterVoiceValue, "%");
        bindSliderToLabel(soundVolumeSlider, soundVolumeValue, "%");
        bindSliderToLabel(noiseFilterSlider, noiseFilterValue, "%");
        bindSliderToLabel(soundPlaceholderSlider, soundPlaceholderValue, "%");

        bindSliderToLabel(scaleSlider, scaleValue, "x", 1.0);
        bindSliderToLabel(opacitySlider, opacityValue, "%");
        bindSliderToLabel(movementSpeedSlider, movementSpeedValue, "%");
        bindSliderToLabel(actionFrequencySlider, actionFrequencyValue, "%");

        bindSliderToLabel(activeReplyFrequencySlider, activeReplyFrequencyValue, "%");
        bindSliderToLabel(coralationDegreeSlider, coralationDegreeValue, "%");


        //给几个按钮添加效果
        fps20Button.setOnMouseEntered(this::WhenButtonHover);
        fps20Button.setOnMouseExited(this::WhenButtonExit);
        fps24Button.setOnMouseEntered(this::WhenButtonHover);
        fps24Button.setOnMouseExited(this::WhenButtonExit);
        fps30Button.setOnMouseEntered(this::WhenButtonHover);
        fps30Button.setOnMouseExited(this::WhenButtonExit);

        frameAnimationButton.setOnMouseEntered(this::WhenButtonHover);
        frameAnimationButton.setOnMouseExited(this::WhenButtonExit);
        spineAnimationButton.setOnMouseEntered(this::WhenButtonHover);
        spineAnimationButton.setOnMouseExited(this::WhenButtonExit);

        //style="-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-background-radius: 4;"

        // 设置默认值
        //fps30Button.setSelected(true);
        //frameAnimationButton.setSelected(true);

        // 按钮事件
        //saveSettingsButton.setOnAction(e -> saveSettings());
        //restoreDefaultsButton.setOnAction(e -> restoreDefaults());
    }

    private void bindSliderToLabel(Slider slider, Label label, String suffix) {
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(String.format("%.0f%s", newVal.doubleValue(), suffix));
        });
    }

    private void bindSliderToLabel(Slider slider, Label label, String suffix, double multiplier) {
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(String.format("%.1f%s", newVal.doubleValue() * multiplier, suffix));
        });
    }

    @FXML
    private void SaveSettings()
    {
        Map<String, Object> settings = new HashMap<>();
        settings.put("allowBackground", allowBackgroundCheckBox.isSelected());
        settings.put("isCloseLauncher", isCloseLauncherCheckBox.isSelected());
        settings.put("transparentMode", transparentModeCheckBox.isSelected());
        settings.put("outputLog", outputLogCheckBox.isSelected());
        settings.put("initialX", initialXField.getText());
        settings.put("initialY", initialYField.getText());
        settings.put("downloadPath", downloadPathField.getText());

        settings.put("voiceInteraction", voiceInteractionCheckBox.isSelected());
        settings.put("aiDialogue", aiDialogueCheckBox.isSelected());
        settings.put("voiceAPI", voiceAPITextField.getText());
        settings.put("characterVoiceVolume", characterVoiceSlider.getValue());
        settings.put("soundVolume", soundVolumeSlider.getValue());
        settings.put("noiseFilter", noiseFilterSlider.getValue());
        settings.put("soundPlaceholder", soundPlaceholderSlider.getValue());

        settings.put("scale", scaleSlider.getValue());
        settings.put("opacity", opacitySlider.getValue());
        settings.put("flipAxis", flipAxisCheckBox.isSelected());
        settings.put("boom", boomCheckBox.isSelected());

        settings.put("idle", idleCheckBox.isSelected());
        settings.put("ignoreGravity", ignoreGravityCheckBox.isSelected());
        settings.put("sleep", sleepCheckBox.isSelected());
        settings.put("sit", sitCheckBox.isSelected());
        settings.put("climb", climbCheckBox.isSelected());
        settings.put("hideAndSeek", hideAndSeekCheckBox.isSelected());
        settings.put("interactable", interactableCheckBox.isSelected());
        settings.put("specialAction", specialActionCheckBox.isSelected());
        settings.put("actionFrequency", actionFrequencySlider.getValue());
        settings.put("movementSpeed", movementSpeedSlider.getValue());

        settings.put("activeReply", activeReplyCheckBox.isSelected());
        settings.put("outputEmotion", outputEmotionCheckBox.isSelected());
        settings.put("printerEffect", printerEffectCheckBox.isSelected());
        settings.put("voiceStream", voiceStreamCheckBox.isSelected());
        settings.put("activeReplyFrequency", activeReplyFrequencySlider.getValue());
        settings.put("coralationDegree", coralationDegreeSlider.getValue());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\preferenceSettings.json"), settings);
            settingsSavedLabel.setText("设置已保存！");
            settingsSavedLabel.setVisible(true);
            System.out.println("Settings saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save settings.");
        }
        // 3秒后隐藏提示
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() ->
                {
                    settingsSavedLabel.setVisible(false);

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void RestoreDefaults()
    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> defaultSettings = objectMapper.readValue(
                new File("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\defaultSettings.json"),
                new TypeReference<Map<String, Object>>() {}
            );

            allowBackgroundCheckBox.setSelected((boolean) defaultSettings.get("allowBackground"));
            isCloseLauncherCheckBox.setSelected((boolean) defaultSettings.get("isCloseLauncher"));
            transparentModeCheckBox.setSelected((boolean) defaultSettings.get("transparentMode"));
            outputLogCheckBox.setSelected((boolean) defaultSettings.get("outputLog"));
            initialXField.setText((String) defaultSettings.get("initialX"));
            initialYField.setText((String) defaultSettings.get("initialY"));
            downloadPathField.setText((String) defaultSettings.get("downloadPath"));

            voiceInteractionCheckBox.setSelected((boolean) defaultSettings.get("voiceInteraction"));
            aiDialogueCheckBox.setSelected((boolean) defaultSettings.get("aiDialogue"));
            voiceAPITextField.setText((String) defaultSettings.get("voiceAPI"));
            characterVoiceSlider.setValue(((Number) defaultSettings.get("characterVoiceVolume")).floatValue());
            soundVolumeSlider.setValue(((Number) defaultSettings.get("soundVolume")).floatValue());
            noiseFilterSlider.setValue(((Number) defaultSettings.get("noiseFilter")).floatValue());
            soundPlaceholderSlider.setValue(((Number) defaultSettings.get("soundPlaceholder")).floatValue());

            scaleSlider.setValue(((Number) defaultSettings.get("scale")).floatValue());
            opacitySlider.setValue(((Number) defaultSettings.get("opacity")).floatValue());
            flipAxisCheckBox.setSelected((boolean) defaultSettings.get("flipAxis"));
            boomCheckBox.setSelected((boolean) defaultSettings.get("boom"));

            idleCheckBox.setSelected((boolean) defaultSettings.get("idle"));
            ignoreGravityCheckBox.setSelected((boolean) defaultSettings.get("ignoreGravity"));
            sleepCheckBox.setSelected((boolean) defaultSettings.get("sleep"));
            sitCheckBox.setSelected((boolean) defaultSettings.get("sit"));
            climbCheckBox.setSelected((boolean) defaultSettings.get("climb"));
            hideAndSeekCheckBox.setSelected((boolean) defaultSettings.get("hideAndSeek"));
            interactableCheckBox.setSelected((boolean) defaultSettings.get("interactable"));
            specialActionCheckBox.setSelected((boolean) defaultSettings.get("specialAction"));
            actionFrequencySlider.setValue(((Number) defaultSettings.get("actionFrequency")).floatValue());
            movementSpeedSlider.setValue(((Number) defaultSettings.get("movementSpeed")).floatValue());

            activeReplyCheckBox.setSelected((boolean) defaultSettings.get("activeReply"));
            outputEmotionCheckBox.setSelected((boolean) defaultSettings.get("outputEmotion"));
            printerEffectCheckBox.setSelected((boolean) defaultSettings.get("printerEffect"));
            voiceStreamCheckBox.setSelected((boolean) defaultSettings.get("voiceStream"));
            activeReplyFrequencySlider.setValue(((Number) defaultSettings.get("activeReplyFrequency")).floatValue());
            coralationDegreeSlider.setValue(((Number) defaultSettings.get("coralationDegree")).floatValue());

            settingsSavedLabel.setText("已恢复默认设置！");
            settingsSavedLabel.setVisible(true);
            System.out.println("Settings restored to default.");
        } catch (IOException e) {
            System.out.println("Failed to restore default settings.");
            e.printStackTrace();
        }
        // 3秒后隐藏提示
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() ->
                {
                    settingsSavedLabel.setVisible(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void LoadPreferredSettings()
    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> preferredSettings = objectMapper.readValue(
                new File("D:\\ArkFriends\\ArkFriends\\src\\main\\java\\ceobe\\jsons\\preferenceSettings.json"),
                new TypeReference<Map<String, Object>>() {}
            );

            allowBackgroundCheckBox.setSelected((boolean) preferredSettings.get("allowBackground"));
            isCloseLauncherCheckBox.setSelected((boolean) preferredSettings.get("isCloseLauncher"));
            transparentModeCheckBox.setSelected((boolean) preferredSettings.get("transparentMode"));
            outputLogCheckBox.setSelected((boolean) preferredSettings.get("outputLog"));
            initialXField.setText((String) preferredSettings.get("initialX"));
            initialYField.setText((String) preferredSettings.get("initialY"));
            downloadPathField.setText((String) preferredSettings.get("downloadPath"));

            voiceInteractionCheckBox.setSelected((boolean) preferredSettings.get("voiceInteraction"));
            aiDialogueCheckBox.setSelected((boolean) preferredSettings.get("aiDialogue"));
            voiceAPITextField.setText((String) preferredSettings.get("voiceAPI"));
            characterVoiceSlider.setValue(((Number) preferredSettings.get("characterVoiceVolume")).floatValue());
            soundVolumeSlider.setValue(((Number) preferredSettings.get("soundVolume")).floatValue());
            noiseFilterSlider.setValue(((Number) preferredSettings.get("noiseFilter")).floatValue());
            soundPlaceholderSlider.setValue(((Number) preferredSettings.get("soundPlaceholder")).floatValue());

            scaleSlider.setValue(((Number) preferredSettings.get("scale")).floatValue());
            opacitySlider.setValue(((Number) preferredSettings.get("opacity")).floatValue());
            flipAxisCheckBox.setSelected((boolean) preferredSettings.get("flipAxis"));
            boomCheckBox.setSelected((boolean) preferredSettings.get("boom"));

            idleCheckBox.setSelected((boolean) preferredSettings.get("idle"));
            ignoreGravityCheckBox.setSelected((boolean) preferredSettings.get("ignoreGravity"));
            sleepCheckBox.setSelected((boolean) preferredSettings.get("sleep"));
            sitCheckBox.setSelected((boolean) preferredSettings.get("sit"));
            climbCheckBox.setSelected((boolean) preferredSettings.get("climb"));
            hideAndSeekCheckBox.setSelected((boolean) preferredSettings.get("hideAndSeek"));
            interactableCheckBox.setSelected((boolean) preferredSettings.get("interactable"));
            specialActionCheckBox.setSelected((boolean) preferredSettings.get("specialAction"));
            actionFrequencySlider.setValue(((Number) preferredSettings.get("actionFrequency")).floatValue());
            movementSpeedSlider.setValue(((Number) preferredSettings.get("movementSpeed")).floatValue());

            activeReplyCheckBox.setSelected((boolean) preferredSettings.get("activeReply"));
            outputEmotionCheckBox.setSelected((boolean) preferredSettings.get("outputEmotion"));
            printerEffectCheckBox.setSelected((boolean) preferredSettings.get("printerEffect"));
            voiceStreamCheckBox.setSelected((boolean) preferredSettings.get("voiceStream"));
            activeReplyFrequencySlider.setValue(((Number) preferredSettings.get("activeReplyFrequency")).floatValue());
            coralationDegreeSlider.setValue(((Number) preferredSettings.get("coralationDegree")).floatValue());

            System.out.println("Preferred settings loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load preferred settings.");
        }
    }
    //endregion

    //region 更多面板相关

    @FXML public Hyperlink quickStartLink;
    @FXML public Hyperlink faqLink;
    @FXML public Hyperlink tutorialLink;
    @FXML public Hyperlink troubleshootingLink;
    @FXML public Hyperlink advancedUsageLink;

    @FXML public Hyperlink githubLink;
    @FXML public Hyperlink bilibiliLink;
    @FXML public Hyperlink catTalkLink;
    @FXML public Hyperlink safetyStatementLink;

    @FXML public Hyperlink PRTSLink;
    @FXML public Hyperlink moreLink2;
    @FXML public Hyperlink moreLink3;
    @FXML public Hyperlink moreLink4;
    @FXML public Hyperlink moreLink5;
    @FXML public Hyperlink moreLink6;
    @FXML public Hyperlink moreLink7;
    @FXML public Hyperlink moreLink8;

    @FXML public Hyperlink emailLink;
    @FXML public Hyperlink forumLink;
    @FXML public Hyperlink documentationLink;

    //@FXML
    //public void initialize() {
        // 绑定超链接事件
    //    setupHyperlinkActions();
    //}

    private void SetupHyperlinkActions() {
        // GitHub链接
        githubLink.setOnAction(e -> OpenURL("https://github.com/CeobeLapland/ArkFriends"));

        // B站链接
        bilibiliLink.setOnAction(e -> OpenURL("https://space.bilibili.com/490290276?spm_id_from=333.788.0.0"));

        // 邮箱链接
        //emailLink.setOnAction(e -> OpenURL("mailto:support@example.com"));

        // 论坛链接
        //forumLink.setOnAction(e -> OpenURL("https://forum.example.com"));

        // 文档链接
        //documentationLink.setOnAction(e -> OpenURL("https://docs.example.com"));

        // 宇宙安全声明（示例：显示对话框）
        //safetyStatementLink.setOnAction(e -> showSafetyStatement());

        // 喵言喵语（示例：显示趣味对话框）
        //catTalkLink.setOnAction(e -> showCatTalk());

        // 更多链接（占位符）
        /*moreLink1.setOnAction(e -> showPlaceholderAlert("更多链接 1"));
        moreLink2.setOnAction(e -> showPlaceholderAlert("更多链接 2"));
        moreLink3.setOnAction(e -> showPlaceholderAlert("更多链接 3"));
        moreLink4.setOnAction(e -> showPlaceholderAlert("更多链接 4"));
        moreLink5.setOnAction(e -> showPlaceholderAlert("更多链接 5"));
        moreLink6.setOnAction(e -> showPlaceholderAlert("更多链接 6"));
        moreLink7.setOnAction(e -> showPlaceholderAlert("更多链接 7"));
        moreLink8.setOnAction(e -> showPlaceholderAlert("更多链接 8"));*/

        // 帮助链接
        //quickStartLink.setOnAction(e -> OpenURL("https://docs.example.com/quick-start"));
        //faqLink.setOnAction(e -> OpenURL("https://docs.example.com/faq"));
        //tutorialLink.setOnAction(e -> OpenURL("https://docs.example.com/tutorials"));
        //troubleshootingLink.setOnAction(e -> OpenURL("https://docs.example.com/troubleshooting"));
        //advancedUsageLink.setOnAction(e -> OpenURL("https://docs.example.com/advanced"));
    }

    private void OpenURL(String url)
    {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("Opened URL: " + url);
            } else {
                //showAlert("无法打开链接", "请手动访问: " + url, Alert.AlertType.INFORMATION);
                System.out.println("Desktop browsing not supported.");
            }
        } catch (Exception ex) {
            //showAlert("错误", "无法打开链接: " + ex.getMessage(), Alert.AlertType.ERROR);
            System.out.println("Failed to open URL: " + ex.getMessage());
        }
    }

    /*private void showSafetyStatement() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("宇宙安全声明");
        alert.setHeaderText("宇宙安全声明");
        alert.setContentText("本软件在设计和实现过程中严格遵守宇宙安全法则。\n\n" +
                "1. 不会产生任何形式的宇宙射线\n" +
                "2. 不会导致时空扭曲\n" +
                "3. 不会影响平行宇宙的稳定性\n" +
                "4. 已通过银河系安全委员会认证");
        alert.showAndWait();
    }

    private void showCatTalk() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("喵言喵语");
        alert.setHeaderText("来自开发者的喵喵消息");
        alert.setContentText("喵~ 感谢使用我们的软件！\n\n" +
                "开发者正在努力学习猫语中...\n" +
                "目前只会说：喵喵喵，喵！\n\n" +
                "翻译：祝你使用愉快！");
        alert.showAndWait();
    }

    private void showPlaceholderAlert(String linkName) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("占位符链接");
        alert.setHeaderText(linkName);
        alert.setContentText("这是一个占位符链接。\n\n" +
                "你可以在控制器中将其替换为实际的功能。");
        alert.showAndWait();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 获取版本信息（示例）
    public String getVersionInfo() {
        return "v2.1.0";
    }

    public String getBuildDate() {
        return "2024-01-15";
    }*/

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