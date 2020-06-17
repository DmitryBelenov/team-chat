package chat.socket.client.registration;

import chat.database.entity.UserEntity;
import chat.socket.server.ServerPortManager;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegistrationClient {

    private String host;
    private UserEntity userEntity;

    public RegistrationClient(String host, UserEntity userEntity) {
        this.host = host;
        this.userEntity = userEntity;
    }

    public void register() {
        try {
            if (host != null) {
                Socket socket = new Socket(host, ServerPortManager.REGISTRATION_PORT);
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                os.writeObject(userEntity);

                socket.close();

                showAlertRegistered();
            } else {
                showAlertNoHostDetected();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void showAlertNoHostDetected() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("IP адрес сервера Team Chat не определен");
        alert.setContentText("Укажите IP адрес в пункте 'Подключиться' меню 'Сервер'");

        alert.showAndWait();
    }

    private void showAlertRegistered() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Регистрация");
        alert.setContentText("Данные для авторизации отправлены на сервер");

        alert.showAndWait();
    }
}
