package entities;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class RecaptchaExample extends Application {

    public   static final String SITE_KEY = "6LclYegqAAAAAM8WaQYFGMOlbIk0yKLlibF-7EzW"; // Remplacez par votre clé de site

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        webView.getEngine().loadContent(
                "<html>" +
                        "<head>" +
                        "<script src='https://www.google.com/recaptcha/api.js' async defer></script>" +
                        "</head>" +
                        "<body>" +
                        "<form action='?' method='POST'>" +
                        "<div class='g-recaptcha' data-sitekey='" + SITE_KEY + "'></div>" +
                        "<br/>" +
                        "<input type='submit' value='Submit'>" +
                        "</form>" +
                        "</body>" +
                        "</html>"
        );

        VBox root = new VBox(webView);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("reCAPTCHA Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}