package chat.controllers;

import chat.windows.chat.ChatWindow;
import chat.windows.registration.RegistrationWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button registration;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    private Button authorization;

    @FXML
    void initialize() {
        registration.setOnAction(event -> {
            RegistrationWindow registration = new RegistrationWindow();
            try {
                registration.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        authorization.setOnAction(event -> {
            ChatWindow chatWindow = new ChatWindow();
            try {
                chatWindow.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
