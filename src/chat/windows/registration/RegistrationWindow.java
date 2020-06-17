package chat.windows.registration;

import chat.windows.Window;
import chat.windows.main.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationWindow extends Application implements Window {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("registration.fxml"));
        Scene scene = new Scene(root, 790, 490);
        setPrimaryStage(stage);
        stage.setTitle("Team Chat");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
        MainWindow.stage.close();
    }

    public static Stage getPrimaryStage() {
        return stage;
    }

    @Override
    public void setPrimaryStage(Stage pStage) {
        RegistrationWindow.stage = pStage;
    }
}
