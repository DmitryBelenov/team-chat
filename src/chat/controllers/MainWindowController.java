package chat.controllers;

import chat.database.entity.UserEntity;
import chat.socket.client.auth.AuthorizationClient;
import chat.socket.client.connection.ConnectionClient;
import chat.socket.server.TeamChatServerManager;
import chat.windows.chat.ChatWindow;
import chat.windows.main.MainWindow;
import chat.windows.registration.RegistrationWindow;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public class MainWindowController {

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    private Button authorization;

    @FXML
    private Button registration;

    @FXML
    private Label connectionLabel;

    @FXML
    private ImageView connectionIcon;

    @FXML
    private MenuButton serverMenu;

    @FXML
    private MenuItem connect;

    @FXML
    private MenuItem start;

    @FXML
    private volatile Label waitSign;

    @FXML
    void initialize() {
        start.setDisable(MainWindow.serverStartButton);
        if (MainWindow.serverStartButton) {
            start.setText("Сервер запущен");
        }

        if (MainWindow.connectedHost != null) {
            setConnectionIcon();
        }

        connect.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("127.0.0.1");

            dialog.setTitle("Server");
            dialog.setHeaderText("Enter server IP address");
            dialog.setContentText("IP:");

            waitSign.setText("Попытка подключения...");
            Optional<String> result = dialog.showAndWait();
            waitSign.setText("");

            result.ifPresent(name -> {
                MainWindow.connectedHost = name;
                setConnectionIcon();
            });
        });

        registration.setOnAction(event -> {
            ConnectionClient connectionClient = new ConnectionClient(MainWindow.connectedHost);
            boolean onDuty = connectionClient.check();
            if (MainWindow.connectedHost == null || !onDuty) {
                showAlertNoHostDetected();
            } else {
                RegistrationWindow registration = new RegistrationWindow();
                try {
                    registration.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        authorization.setOnAction(event -> {
            ConnectionClient connectionClient = new ConnectionClient(MainWindow.connectedHost);
            boolean onDuty = connectionClient.check();
            if (MainWindow.connectedHost != null || onDuty) {
                if (userAuthorization()){
                    ChatWindow chatWindow = new ChatWindow();
                    try {
                        chatWindow.start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
            } else {
                showAlertNoHostDetected();
            }
        });

        start.setOnAction(event -> {
            serverStartHandler();
        });
    }

    private void showAlertNoHostDetected() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("IP адрес сервера Team Chat не определен");
        alert.setContentText("Укажите IP адрес в пункте 'Подключиться' меню 'Сервер'");

        alert.showAndWait();
    }

    private void serverStartHandler() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Сервер Team Chat");
        alert.setHeaderText("На вашем компьютере будет запущен сервер Team Chat.\nДля его остановки потребуется закрыть приложение");
        alert.setContentText("Вы уверены?");

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                TeamChatServerManager tsm = new TeamChatServerManager();
                tsm.startCommonServer();
                start.setText("Сервер запущен");
                start.setDisable(MainWindow.serverStartButton);
            }
        }
    }

    private void setConnectionIcon() {
        ConnectionClient connectionClient = new ConnectionClient(MainWindow.connectedHost);
        boolean onDuty = connectionClient.check();
        if (onDuty) {
            connectionLabel.setText(MainWindow.connectedHost.trim());
            Image image = new Image(getClass().getResourceAsStream("/chat/assets/connection.png"));
            connectionIcon.setImage(image);
        } else {
            connectionLabel.setText("No connection");
            Image image = new Image(getClass().getResourceAsStream("/chat/assets/no_connection.png"));
            connectionIcon.setImage(image);
            MainWindow.connectedHost = null;
        }
    }

    private boolean userAuthorization(){
        String nickName = login.getText().trim();
        String pass = password.getText().trim();
        if (nickName.isEmpty() || pass.isEmpty()){
            showAlertNoEmptyAuthFields();
            return false;
        }
        String[] authData = new String[2];
        authData[0] = nickName;
        authData[1] = pass;
        AuthorizationClient authorizationClient = new AuthorizationClient(MainWindow.connectedHost, authData);
        UserEntity userEntity = authorizationClient.auth();

        if (userEntity == null) {
            showAlertNoUserFound();
            return false;
        }

        if (userEntity.isBlocked()){
            showAlertUserBlocked(userEntity.getNickname());
            return false;
        }

        ChatWindow.authorizedUserId = userEntity.getId();
        ChatWindow.authorizedNickName = userEntity.getNickname();
        return true;
    }

    private void showAlertNoEmptyAuthFields() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Поля логин/пароль не могут быть пустыми");

        alert.showAndWait();
    }

    private void showAlertNoUserFound() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Пользователя c такой комбинацией логин/пароль не существует");

        alert.showAndWait();
    }

    private void showAlertUserBlocked(String nickName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Пользователь '"+nickName+"' заблокирован");

        alert.showAndWait();
    }
}
