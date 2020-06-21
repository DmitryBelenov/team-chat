package chat.windows.group;

import chat.windows.Window;
import chat.windows.main.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateGroupWindow extends Application implements Window {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("create_group.fxml"));

        Scene scene = new Scene(root, 349, 426);
        setPrimaryStage(stage);
        stage.setTitle("Team Chat - Create group");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void setPrimaryStage(Stage pStage) {
        CreateGroupWindow.stage = pStage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
