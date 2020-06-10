package chat.windows.chat;

import chat.windows.Window;
import chat.windows.main.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatWindow extends Application implements Window {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));
        Scene scene = new Scene(root, 790, 669);
        setPrimaryStage(stage);
        stage.setTitle("Team Chat");
        stage.setResizable(false);
        stage.setScene(scene);
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
