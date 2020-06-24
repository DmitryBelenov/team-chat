package chat.windows.chat;

import chat.windows.Window;
import chat.windows.main.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatWindow extends Application implements Window {

    public static Stage stage;
    public static String authorizedUserId;
    public static String authorizedNickName;

    public static boolean chatSchedulerStarted = false;
    public static boolean groupChatSchedulerStarted = false;

    // брать актуальную версию маппинга из локального хранилища при авторизации
    public static Map<String, Date> groupLastMsgDatesMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));
        Scene scene = new Scene(root, 790, 669);
        setPrimaryStage(stage);
        stage.setTitle("Team Chat");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
        MainWindow.stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void setPrimaryStage(Stage pStage) {
        ChatWindow.stage = pStage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
