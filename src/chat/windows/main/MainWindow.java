package chat.windows.main;

import chat.windows.Window;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application implements Window {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 790, 490);
        setPrimaryStage(stage);
        stage.setTitle("Team Chat");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void setPrimaryStage(Stage pStage) {
        MainWindow.stage = pStage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
