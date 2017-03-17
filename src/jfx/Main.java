package jfx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {


    private Controller controller;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("window.fxml"));
        Parent root = loader.load();
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sokoban");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        controller = loader.getController();

        controller.loadDefaultSaveFile(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
