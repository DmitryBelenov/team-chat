package chat.socket.server;

import chat.socket.server.registration.RegistrationServer;
import chat.socket.server.service.OnDutyServer;
import chat.windows.main.MainWindow;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamChatServerManager {

    private RegistrationServer registrationServer;
    private OnDutyServer onDutyServer;

    public static List<Thread> threadList = Collections.synchronizedList(new ArrayList<>());

    public TeamChatServerManager() {
        registrationServer = new RegistrationServer();
        onDutyServer = new OnDutyServer();
    }

    public void startCommonServer(){
        registrationServer.startMultiThreadHandler();
        onDutyServer.startMultiThreadHandler();

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
