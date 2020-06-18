package chat.socket.server;

import chat.socket.server.message.MessageDeliveryServer;
import chat.socket.server.message.MessageServer;
import chat.socket.server.registration.RegistrationServer;
import chat.socket.server.service.OnDutyServer;
import chat.socket.server.users.list.UserListServer;
import chat.windows.main.MainWindow;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamChatServerManager {

    private RegistrationServer registrationServer;
    private OnDutyServer onDutyServer;
    private UserListServer userListServer;
    private MessageServer messageServer;
    private MessageDeliveryServer messageDeliveryServer;

    public static List<Thread> threadList = Collections.synchronizedList(new ArrayList<>());

    public TeamChatServerManager() {
        registrationServer = new RegistrationServer();
        onDutyServer = new OnDutyServer();
        userListServer = new UserListServer();
        messageServer = new MessageServer();
        messageDeliveryServer = new MessageDeliveryServer();
    }

    public void startCommonServer(){
        registrationServer.startMultiThreadHandler();
        onDutyServer.startMultiThreadHandler();
        userListServer.startMultiThreadHandler();
        messageServer.startMultiThreadHandler();
        messageDeliveryServer.startMultiThreadHandler();

        showServerInfo();
        MainWindow.serverStartButton = true;
    }

    private void showServerInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Локальный сервер");
        alert.setHeaderText("Локальный сервер запущен\nДля его остановки перезапустите Team Chat");

        alert.showAndWait();
    }
}
