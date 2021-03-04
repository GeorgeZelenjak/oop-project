/**package nl.tudelft.oopp.livechat.views;

import com.sun.glass.ui.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UserChatPageDisplay extends Application {


    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/userChatPage.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

*/