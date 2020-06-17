package chat.controllers;

import chat.database.entity.UserEntity;
import chat.socket.client.users.list.UserListClient;
import chat.windows.main.MainWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupController {
    @FXML
    private TextField groupName;

    @FXML
    private AnchorPane usersPane;

    @FXML
    private Button createButton;

    @FXML
    void initialize() {
//        fillUsersList();
    }

    private void fillUsersList() {
        UserListClient userListClient = new UserListClient(MainWindow.connectedHost, "all");
        List<UserEntity> userEntities = userListClient.get();
        List<CheckBox> checkBoxes = new ArrayList<>();
        int y = 0;
        for (UserEntity user : userEntities) {
            y+=20;
            CheckBox checkBox = new CheckBox(user.getNickname());
            checkBox.setSelected(false);
            checkBox.setLayoutY(y);
            checkBoxes.add(checkBox);
        }
        usersPane.getChildren().addAll(checkBoxes);
    }
}
