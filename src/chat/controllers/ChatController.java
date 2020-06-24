package chat.controllers;

import chat.database.entity.GroupEntity;
import chat.database.entity.MessageEntity;
import chat.database.entity.UserEntity;
import chat.json.JsonMessageStorage;
import chat.json.chat.Chat;
import chat.json.chat.Message;
import chat.objects.Group;
import chat.objects.User;
import chat.socket.client.group.GroupListClient;
import chat.socket.client.message.GroupMessageDeliveryClient;
import chat.socket.client.message.MessageClient;
import chat.socket.client.message.MessageDeliveryClient;
import chat.socket.client.users.list.UserListClient;
import chat.windows.chat.ChatWindow;
import chat.windows.group.CreateGroupWindow;
import chat.windows.main.MainWindow;
import javafx.application.Platform;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatController {

    private List<String> openedTabs = Collections.synchronizedList(new ArrayList<>());
    private ScheduledExecutorService ses;
    private ScheduledExecutorService ses_groups;
    private Map<String, TextArea> chatFieldMap = Collections.synchronizedMap(new HashMap<>());
    private boolean needUpdateGroupsChats = true;
    private boolean needUpdateChats = true;

    @FXML
    private ScrollPane usersPane;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private volatile TabPane chatsPane;

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
    public void initialize() {
        chatFieldMap.put("Public", chatMainField);
        openedTabs.add("Public");

        JsonMessageStorage.init(ChatWindow.authorizedUserId);
        Chat publicChatContent = JsonMessageStorage.get("Public");

        if (publicChatContent != null) {
            StringBuilder messages = new StringBuilder();
            for (Message message : publicChatContent.getMessages()) {
                messages.append(message.getText()).append("\n");
            }
            chatMainField.setText(messages.toString());
        }

        if (!ChatWindow.chatSchedulerStarted) {
            privateChatsRefreshScheduler();
            ChatWindow.chatSchedulerStarted = true;
        }

        if (!ChatWindow.groupChatSchedulerStarted) {
            groupChatsRefreshScheduler();
            ChatWindow.groupChatSchedulerStarted = true;
        }

        usersPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        groupPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        usersListInitialization("all");
        groupsListInitialization();

        logOut.setOnAction(event -> {
            MainWindow mainWindow = new MainWindow();
            try {
                ChatWindow.chatSchedulerStarted = false;
                ChatWindow.groupChatSchedulerStarted = false;
                ses_groups.shutdown();
                ses.shutdown();

                ChatWindow.groupLastMsgDatesMap = Collections.synchronizedMap(new HashMap<>());

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

                groupsListInitialization();
            } else {
                showAlertNoUsersForGroupCreating();
            }
        });

        send.setOnAction(event -> {
            String msg = inputLine.getText().trim();
            if (msg.length() > 0) {
                sendMessage(msg, "Public", true);
                inputLine.clear();
            }
        });

        inputLine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String msg = inputLine.getText().trim();
                if (msg.length() > 0) {
                    sendMessage(msg, "Public", true);
                    inputLine.clear();
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

        chatsPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    private Tab fillTab(String textAreaContent, String tabName, boolean group) {
        Tab tab = new Tab(tabName);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(529);
        textArea.setPrefWidth(583);
        textArea.setText(textAreaContent);

        chatFieldMap.put(tabName, textArea);

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
                String msg = textField.getText().trim();
                if (msg.length() > 0) {
                    sendMessage(msg, tabName, group);
                    if (group) {
                        needUpdateGroupsChats = true;
                    } else {
                        needUpdateChats = true;
                    }
                    textField.clear();
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
            String msg = textField.getText().trim();
            if (msg.length() > 0) {
                sendMessage(msg, tabName, group);
                if (group) {
                    needUpdateGroupsChats = true;
                } else {
                    needUpdateChats = true;
                }
                textField.clear();
            }
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

        if (users != null && users.size() > 0) {
            List<User> list = new ArrayList<>();

            for (UserEntity user : users) {
                if (!user.getId().equals(ChatWindow.authorizedUserId)) {
                    User listUser = new User(user.getNickname(), user.isOnline() ? "on" : "off");
                    list.add(listUser);
                }
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
                        // todo textAreaContent наполнять из локального хранилища сообщений

                        Chat chatStory = JsonMessageStorage.get(nickname);
                        StringBuilder messages = new StringBuilder();
                        if (chatStory != null) {
                            for (Message message : chatStory.getMessages()) {
                                messages.append(message.getText()).append("\n");
                            }
                        }

                        Tab userTab = fillTab(messages.toString(), nickname, false);
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
        GroupListClient groupListClient = new GroupListClient(MainWindow.connectedHost, ChatWindow.authorizedUserId);
        List<GroupEntity> groups = groupListClient.get();

        if (groups != null && groups.size() > 0) {
            List<Group> groupList = new ArrayList<>();

            for (GroupEntity groupEntity : groups) {
                if (!groupEntity.getGroupName().equals("Public")) {
                    Group groupView = new Group(groupEntity.getGroupName());
                    groupList.add(groupView);
                }
            }

            ObservableList<Group> groupData = FXCollections.observableArrayList(groupList);

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
                        // todo textAreaContent наполнять из локального хранилища сообщений

                        Chat chatStory = JsonMessageStorage.get(name);
                        StringBuilder messages = new StringBuilder();
                        if (chatStory != null) {
                            for (Message message : chatStory.getMessages()) {
                                messages.append(message.getText()).append("\n");
                            }
                        }

                        Tab userTab = fillTab(messages.toString(), name, true);
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

                    usersListInitialization("group_" + name);
                }
            });
        }

        JsonMessageStorage.initializeGroupChatMapping(ChatWindow.authorizedUserId);
    }

    private void showAlertNoUsersForGroupCreating() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Нет зарегистрированных пользователей для добавления в группу");

        alert.showAndWait();
    }

    private void sendMessage(String msg, String nameTo, boolean groupMsg) {
        Date messageDate = new Date();

        MessageEntity message = new MessageEntity();
        message.setId(UUID.randomUUID().toString());
        message.setMsgText(msg);
        message.setFromId(ChatWindow.authorizedUserId);
        message.setToId(nameTo);
        message.setGroupMsg(groupMsg);
        message.setSendDate(messageDate);
        message.setReceived(false);

        MessageClient messageClient = new MessageClient(MainWindow.connectedHost, message);
        messageClient.send();

        Message jsonMsg = new Message();
        jsonMsg.setFrom(ChatWindow.authorizedUserId);
        jsonMsg.setTo(nameTo);
        jsonMsg.setText(msg);
        jsonMsg.setMessageDate(messageDate);
        JsonMessageStorage.addChatMessage(nameTo, jsonMsg);
    }

    private void privateChatsRefreshScheduler() {
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(() -> {
            CompletableFuture<Void> refresh = CompletableFuture.runAsync(() ->
            {
                Platform.runLater(this::refreshHistory);
            });
            try {
                refresh.get();
            } catch (Exception e) {
                System.out.println("Unable to refresh private chats:" + e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void groupChatsRefreshScheduler() {
        ses_groups = Executors.newSingleThreadScheduledExecutor();
        ses_groups.scheduleWithFixedDelay(() -> {
            CompletableFuture<Void> refresh = CompletableFuture.runAsync(() ->
            {
                Platform.runLater(this::refreshGroupsHistory);
            });
            try {
                refresh.get();
            } catch (Exception e) {
                System.out.println("Unable to refresh group chats:" + e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private synchronized void refreshHistory() {
        MessageDeliveryClient messageDeliveryClient = new MessageDeliveryClient(MainWindow.connectedHost, ChatWindow.authorizedUserId);
        List<MessageEntity> messages = messageDeliveryClient.refresh();
        if (messages.size() > 0) {
            if (chatFieldMap.size() > 0) {
                for (MessageEntity me : messages) {
                    String tabName = me.getFromId();
                    if (!openedTabs.contains(tabName)) {
                        // todo textAreaContent наполнять из локального хранилища сообщений

                        Chat chatStory = JsonMessageStorage.get(tabName);
                        StringBuilder content = new StringBuilder();
                        if (chatStory != null) {
                            for (Message message : chatStory.getMessages()) {
                                content.append(message.getText()).append("\n");
                            }
                        }

                        Tab userTab = fillTab(content.toString(), tabName, false);
                        openedTabs.add(tabName);
                        chatsPane.getTabs().add(userTab);
                    } else {
                        // todo не делать фокус а подключить счетчик сообщений
                        List<Tab> allTabs = chatsPane.getTabs();
                        for (Tab tab : allTabs) {
                            if (tab.getText().equals(tabName)) {
                                SingleSelectionModel<Tab> sm = chatsPane.getSelectionModel();
                                sm.select(tab);
                            }
                        }
                    }

                    TextArea chatTextArea = chatFieldMap.get(tabName);

                    Message message = new Message();
                    message.setText(me.getMsgText());
                    message.setMessageDate(me.getSendDate());

                    JsonMessageStorage.addChatMessage(tabName, message);

                    Chat chatStory = JsonMessageStorage.get(tabName);
                    StringBuilder story = new StringBuilder();
                    if (chatStory != null) {
                        for (Message msg : chatStory.getMessages()) {
                            story.append(msg.getText()).append("\n");
                        }
                    }

                    chatTextArea.setText(story.toString());
                }
            }
        } else {
            if (needUpdateChats) {
                JsonMessageStorage.updateFromLocalStorage(chatFieldMap, false);
                needUpdateChats = false;
            }
        }
    }

    private synchronized void refreshGroupsHistory() {
        GroupMessageDeliveryClient groupMessageDeliveryClient = new GroupMessageDeliveryClient(MainWindow.connectedHost, ChatWindow.authorizedUserId);
        List<MessageEntity> messages = groupMessageDeliveryClient.refresh();
        if (messages.size() > 0) {
            if (chatFieldMap.size() > 0) {
                for (MessageEntity me : messages) {
                    Date lustReceivedMsgDate = ChatWindow.groupLastMsgDatesMap.get(me.getToId());
                    if (lustReceivedMsgDate != null) {
                        if (lustReceivedMsgDate.before(me.getSendDate())) {
                            groupChatsMessageRefresh(me);
                        }
                    } else {
                        groupChatsMessageRefresh(me);
                    }
                }
            }
        } else {
            if (needUpdateGroupsChats) {
                JsonMessageStorage.updateFromLocalStorage(chatFieldMap, true);
                needUpdateGroupsChats = false;
            }
        }
    }

    private synchronized void groupChatsMessageRefresh(MessageEntity messageEntity) {
        String groupName = messageEntity.getToId();
        if (!openedTabs.contains(messageEntity.getToId())) {
            // todo textAreaContent наполнять из локального хранилища сообщений

            Chat chatStory = JsonMessageStorage.get(groupName);
            StringBuilder content = new StringBuilder();
            if (chatStory != null) {
                for (Message message : chatStory.getMessages()) {
                    content.append(message.getText()).append("\n");
                }
            }

            Tab userTab = fillTab(content.toString(), groupName, false);
            openedTabs.add(groupName);
            chatsPane.getTabs().add(userTab);
        } else {
            // todo не делать фокус а подключить счетчик сообщений
            List<Tab> allTabs = chatsPane.getTabs();
            for (Tab tab : allTabs) {
                if (tab.getText().equals(groupName)) {
                    SingleSelectionModel<Tab> sm = chatsPane.getSelectionModel();
                    sm.select(tab);
                }
            }
        }

        TextArea chatTextArea = chatFieldMap.get(groupName);

        if (!messageEntity.getFromId().equals(ChatWindow.authorizedUserId)) {
            Message message = new Message();
            message.setText(messageEntity.getMsgText());
            message.setMessageDate(messageEntity.getSendDate());

            JsonMessageStorage.addChatMessage(groupName, message);
        }

        Chat chatStory = JsonMessageStorage.get(groupName);
        StringBuilder story = new StringBuilder();
        if (chatStory != null) {
            for (Message msg : chatStory.getMessages()) {
                story.append(msg.getText()).append("\n");
            }
        }

        chatTextArea.setText(story.toString());

        ChatWindow.groupLastMsgDatesMap.put(groupName, messageEntity.getSendDate());
    }
}
