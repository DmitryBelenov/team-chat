package chat.controllers;

import chat.database.entity.UserEntity;
import chat.objects.Group;
import chat.objects.User;
import chat.socket.client.users.list.UserListClient;
import chat.windows.chat.ChatWindow;
import chat.windows.group.CreateGroupWindow;
import chat.windows.main.MainWindow;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatController {

    private List<String> openedTabs = Collections.synchronizedList(new ArrayList<>());

    @FXML
    private ScrollPane usersPane;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TabPane chatsPane;

    @FXML
    private Tab currentChat;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextArea chatMainField;

    @FXML
    private TextField inputLine;

    @FXML
    private Button send;

    @FXML
    private Button fileChooser;

    @FXML
    private MenuButton menu;

    @FXML
    private MenuItem logOut;

    @FXML
    private MenuItem createGroup;

    @FXML
    private ScrollPane groupPane;

    @FXML
    private TableView<Group> groupTable;

    @FXML
    void initialize() {
        usersPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        groupPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        usersListInitialization("all");
        groupsListInitialization();

        logOut.setOnAction(event -> {
            MainWindow mainWindow = new MainWindow();
            try {
                ChatWindow.stage.close();
                mainWindow.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        createGroup.setOnAction(event -> {
            UserListClient userListClient = new UserListClient(MainWindow.connectedHost, "all");
            List<UserEntity> userEntities = userListClient.get();
            if (userEntities.size() > 0) {
                CreateGroupWindow createGroupWindow = new CreateGroupWindow();
                try {
                    createGroupWindow.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showAlertNoUsersForGroupCreating();
            }
        });

        send.setOnAction(event -> {
            inputLine.clear();
        });

        inputLine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                inputLine.clear();
                if (inputLine.isFocused()) {
                    chatMainField.requestFocus();
                }
            }
        });

        final FileChooser chooser = new FileChooser();
        fileChooser.setOnAction(event -> {
            inputLine.clear();
            List<File> files = chooser.showOpenMultipleDialog(ChatWindow.getPrimaryStage());
            if (files != null && files.size() > 0) {
                System.out.println(files.size());
            }
        });

        chatMainField.setText("Example main chat");

        chatsPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        List<Tab> chats = fillTabs();
        if (chats.size() > 0)
            chatsPane.getTabs().addAll(chats);
    }

    private List<Tab> fillTabs() {
        //groups
        List<Tab> tabs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            tabs.add(fillTab("text area content - " + i, ("new - " + i)));
        }
        return tabs;
    }

    private Tab fillTab(String textAreaContent, String tabName) {
        Tab tab = new Tab(tabName);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(529);
        textArea.setPrefWidth(583);
        textArea.setText(textAreaContent);

        AnchorPane scrollContent = new AnchorPane(textArea);
        scrollContent.setPrefWidth(582);
        scrollContent.setPrefHeight(528);

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(530);
        scrollContent.setPrefWidth(583);

        Font font = new Font("Verdana", 12);

        TextField textField = new TextField();
        textField.setLayoutY(529);
        textField.setPrefHeight(30);
        textField.setPrefWidth(468);
        textField.setFont(font);
        textField.setPromptText("Введите текст");
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                textField.clear();
                if (textField.isFocused()) {
                    textArea.requestFocus();
                }
            }
        });

        Button send = new Button("^");
        send.setLayoutX(468);
        send.setLayoutY(530);
        send.setMnemonicParsing(false);
        send.setPrefHeight(29);
        send.setPrefWidth(50);
        send.setFont(font);
        send.setOnAction(event -> {
            textField.clear();
        });

        Button file = new Button("Файл");
        file.setLayoutX(518);
        file.setLayoutY(530);
        file.setMnemonicParsing(false);
        file.setPrefHeight(29);
        file.setPrefWidth(64);
        file.setFont(font);
        final FileChooser chooser = new FileChooser();
        file.setOnAction(event -> {
            textField.clear();
            List<File> files = chooser.showOpenMultipleDialog(ChatWindow.getPrimaryStage());
            if (files != null && files.size() > 0) {
                System.out.println(files.size());
            }
        });

        AnchorPane tabContent = new AnchorPane(scrollPane, textField, send, file);
        tabContent.setPrefWidth(200);
        tabContent.setPrefHeight(180);

        tab.setContent(tabContent);
        tab.setOnClosed(event -> {
            openedTabs.remove(tabName);
        });

        return tab;
    }

    @SuppressWarnings("unchecked")
    private void usersListInitialization(String alias) {
        UserListClient userListClient = new UserListClient(MainWindow.connectedHost, alias);
        List<UserEntity> users = userListClient.get();

        if (users.size() > 0) {
            List<User> list = new ArrayList<>();

            for (UserEntity user : users) {
                User listUser = new User(user.getNickname(), user.isOnline() ? "on" : "off");
                list.add(listUser);
            }

            ObservableList<User> data = FXCollections.observableArrayList(list);

            TableColumn nickColumn = new TableColumn("nick");
            nickColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nick"));

            TableColumn stateColumn = new TableColumn("state");
            stateColumn.setCellValueFactory(new PropertyValueFactory<User, String>("state"));

            nickColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.63));
            stateColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.3));

            nickColumn.setResizable(false);
            stateColumn.setResizable(false);

            usersTable.setItems(data);
            usersTable.getColumns().clear();
            usersTable.getColumns().addAll(nickColumn, stateColumn);

            usersTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Object>) (observableValue, oldValue, newValue) -> {
                if (usersTable.getSelectionModel().getSelectedItem() != null) {
                    TableView.TableViewSelectionModel<User> selectionModel = usersTable.getSelectionModel();
                    ObservableList<?> selectedCells = selectionModel.getSelectedCells();
                    @SuppressWarnings("unchecked")
                    TablePosition<Object, ?> tablePosition = (TablePosition<Object, ?>) selectedCells.get(0);
                    Object val = tablePosition.getTableColumn().getCellData(newValue);

                    String nickname = val.toString();
                    if (!openedTabs.contains(nickname)) {
                        Tab userTab = fillTab("", nickname);
                        openedTabs.add(nickname);
                        chatsPane.getTabs().add(userTab);
                    } else {
                        List<Tab> allTabs = chatsPane.getTabs();
                        for (Tab tab : allTabs) {
                            if (tab.getText().equals(nickname)) {
                                SingleSelectionModel<Tab> sm = chatsPane.getSelectionModel();
                                sm.select(tab);
                            }
                        }
                    }
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void groupsListInitialization() {
        ObservableList<Group> groupData = FXCollections.observableArrayList(
                new Group("Разработка"),
                new Group("Разное"),
                new Group("Проверка"));

        TableColumn nameColumn = new TableColumn("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        nameColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.9));
        nameColumn.setResizable(false);

        groupTable.setItems(groupData);
        groupTable.getColumns().add(nameColumn);

        groupTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Object>) (observableValue, oldValue, newValue) -> {
            if (groupTable.getSelectionModel().getSelectedItem() != null) {
                TableView.TableViewSelectionModel<Group> selectionModel = groupTable.getSelectionModel();
                ObservableList<?> selectedCells = selectionModel.getSelectedCells();
                @SuppressWarnings("unchecked")
                TablePosition<Object, ?> tablePosition = (TablePosition<Object, ?>) selectedCells.get(0);
                Object val = tablePosition.getTableColumn().getCellData(newValue);

                String name = val.toString();
                if (!openedTabs.contains(name)) {
                    Tab userTab = fillTab("", name);
                    openedTabs.add(name);
                    chatsPane.getTabs().add(userTab);
                } else {
                    List<Tab> allTabs = chatsPane.getTabs();
                    for (Tab tab : allTabs) {
                        if (tab.getText().equals(name)) {
                            SingleSelectionModel<Tab> sm = chatsPane.getSelectionModel();
                            sm.select(tab);
                        }
                    }
                }
            }
        });
    }

    private void showAlertNoUsersForGroupCreating() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Нет зарегистрированных пользователей для добавления в группу");

        alert.showAndWait();
    }
}
