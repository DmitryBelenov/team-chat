package chat.windows.group;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateGroupWindow extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("create_group.fxml"));

        Scene scene = new Scene(root, 349, 426);

        stage.setTitle("Team Chat - Create group");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
