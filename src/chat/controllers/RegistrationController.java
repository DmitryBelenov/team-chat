package chat.controllers;

import chat.database.entity.UserEntity;
import chat.socket.client.registration.RegistrationClient;
import chat.utils.*;
import chat.windows.main.MainWindow;
import chat.windows.registration.RegistrationWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

public class RegistrationController {

    @FXML
    private Button register;

    @FXML
    private TextField name;

    @FXML
    private TextField nickName;

    @FXML
    private TextField password;

    @FXML
    private Button back;

    @FXML
    private TextField mail;

    @FXML
    private TextField passwordConfirm;

    @FXML
    void initialize() {
        back.setOnAction(event -> {
            MainWindow mainWindow = new MainWindow();
            try {
                RegistrationWindow.stage.close();
                mainWindow.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        register.setOnAction(event -> {
            if (name.getText().trim().isEmpty()
                    || nickName.getText().trim().isEmpty()
                    || mail.getText().trim().isEmpty()
                    || password.getText().trim().isEmpty()
                    || passwordConfirm.getText().trim().isEmpty()) {
                showAlertNoEmptyFieldsAccepted();
            } else {
                if (!password.getText().trim().equals(passwordConfirm.getText().trim())) {
                        showAlertPasswordsAreDifferent();
                } else {
                    registration(name.getText().trim(),
                            nickName.getText().trim(),
                            mail.getText().trim(),
                            password.getText().trim());
                }
            }
        });
    }

    private void showAlertNoEmptyFieldsAccepted() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Поля формы регистрации не могут быть пустыми");

        alert.showAndWait();
    }

    private void showAlertPasswordsAreDifferent() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Пароли не совпадают");

        alert.showAndWait();
    }

    private void registration(String... args){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setUsername(args[0]);
        userEntity.setNickname(args[1]);
        userEntity.setUserRole("user");
        userEntity.setEmail(args[2]);

        String encodedPass = null;
        try {
            encodedPass = CryptoUtils.getHexBase64(args[3]);
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();
        }

        userEntity.setPasswordHex(encodedPass != null ? encodedPass : args[3]);
        userEntity.setOnline(true);
        userEntity.setBlocked(false);
        userEntity.setCreateDate(new Date());

        RegistrationClient rc = new RegistrationClient(MainWindow.connectedHost, userEntity);
        rc.register();

        name.setText("");
        nickName.setText("");
        mail.setText("");
        password.setText("");
        passwordConfirm.setText("");
    }
}
