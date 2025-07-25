package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }
//SideBarCAN
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().
                getResource("/Login.fxml"));

        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            //scene.getStylesheets().add(getClass().getResource("/design.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("HR360");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
