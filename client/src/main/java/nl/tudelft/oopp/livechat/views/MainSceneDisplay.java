package nl.tudelft.oopp.livechat.views;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.tudelft.oopp.livechat.controllers.NavigationController;


/**
 * Loads the MainScene.fxml file
 * And initializes the NavigationController
 */
public class MainSceneDisplay extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/mainScene.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        NavigationController.setCurrentController(
                new NavigationController(primaryStage.getScene()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
