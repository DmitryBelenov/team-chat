package chat.controllers;

import chat.database.entity.UserEntity;
import chat.objects.GroupUserTableView;
import chat.objects.User;
import chat.socket.client.users.list.UserListClient;
import chat.windows.main.MainWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateGroupController {
    @FXML
    private TextField groupName;

    @FXML
    private ScrollPane userScrollPane;

    @FXML
    private TableView<GroupUserTableView> usersTableView;

    @FXML
    private Button createButton;

    @FXML
    void initialize() {
        userScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        fillUsersList();
    }

    @SuppressWarnings("unchecked")
    private void fillUsersList() {
        UserListClient userListClient = new UserListClient(MainWindow.connectedHost, "all");
        List<UserEntity> userEntities = userListClient.get();

        List<GroupUserTableView> groupUserTableViews = new ArrayList<>();
        for (UserEntity userEntity : userEntities){
            GroupUserTableView groupUserTableView = new GroupUserTableView(userEntity.getNickname(), userEntity.getUsername(),false);
            groupUserTableViews.add(groupUserTableView);
        }

        ObservableList<TableColumn<GroupUserTableView, ?>> columns = usersTableView.getColumns();

        final TableColumn<GroupUserTableView, Boolean> loadedColumn = new TableColumn<>( "add" );
        loadedColumn.setCellValueFactory( new PropertyValueFactory<>( "add" ));
        loadedColumn.setCellFactory( tc -> new CheckBoxTableCell<>());
        columns.add( loadedColumn );

        final TableColumn<GroupUserTableView, Boolean> nickColumn = new TableColumn<>( "nick" );
        nickColumn.setCellValueFactory( new PropertyValueFactory<>( "nick" ));
        columns.add( nickColumn );

        final TableColumn<GroupUserTableView, Boolean> nameColumn = new TableColumn<>( "name" );
        nameColumn.setCellValueFactory( new PropertyValueFactory<>( "name" ));
        columns.add( nameColumn );

        ObservableList<GroupUserTableView> data =  FXCollections.observableArrayList(groupUserTableViews);

        usersTableView.setItems(data);
        usersTableView.setEditable(true);

        createButton.setOnAction(event -> {
            final Set<GroupUserTableView> chosen = new HashSet<>();
            for (GroupUserTableView user : usersTableView.getItems()){
                if (user.isAdd()){
                    chosen.add(user);
                }
            }
        });

    }
}
