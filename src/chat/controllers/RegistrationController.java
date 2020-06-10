package chat.controllers;

import chat.windows.main.MainWindow;
import chat.windows.registration.RegistrationWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationController {

    @FXML
    private Button register;

    @FXML
    private TextField name;

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
    }
}
