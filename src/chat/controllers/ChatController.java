package chat.controllers;

import chat.windows.chat.ChatWindow;
import chat.windows.main.MainWindow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import objects.User;

import javax.swing.text.StyleConstants;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatController {

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
    @SuppressWarnings("unchecked")
    void initialize() {
        usersPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        ObservableList<User> data = FXCollections.observableArrayList(
                new User("dmitry", "on"),
                new User("vasya", "off"),
                new User("dmitryB", "on"));

        TableColumn nickColumn = new TableColumn("nick");
        nickColumn.setCellValueFactory(new PropertyValueFactory<User,String>("nick"));

        TableColumn stateColumn = new TableColumn("state");
        stateColumn.setCellValueFactory(new PropertyValueFactory<User,String>("state"));

        nickColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.63));
        stateColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.3));

        nickColumn.setResizable(false);
        stateColumn.setResizable(false);

        usersTable.setItems(data);
        usersTable.getColumns().addAll(nickColumn, stateColumn);

        usersTable.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Object>) (observableValue, oldValue, newValue) -> {
            if (usersTable.getSelectionModel().getSelectedItem() != null) {
                TableView.TableViewSelectionModel<User> selectionModel = usersTable.getSelectionModel();
                ObservableList<?> selectedCells = selectionModel.getSelectedCells();
                @SuppressWarnings("unchecked")
                TablePosition<Object, ?> tablePosition = (TablePosition<Object, ?>) selectedCells.get(0);
                Object val = tablePosition.getTableColumn().getCellData(newValue);
                System.out.println("Selected Value " + val);
            }
        });

        logOut.setOnAction(event -> {
            MainWindow mainWindow = new MainWindow();
            try {
                ChatWindow.stage.close();
                mainWindow.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        send.setOnAction(event -> {
            inputLine.clear();
        });

        inputLine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                inputLine.clear();
                if (inputLine.isFocused()){
                    chatMainField.requestFocus();
                }
            }
        });

        final FileChooser chooser = new FileChooser();
        fileChooser.setOnAction(event -> {
            inputLine.clear();
            List<File> files = chooser.showOpenMultipleDialog(ChatWindow.getPrimaryStage());
            if (files.size() > 0) {
                System.out.println(files.size());
            }
        });
    }
}
