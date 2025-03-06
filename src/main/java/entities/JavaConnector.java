package entities;

import controllers.LoginController;
import javafx.application.Platform;

public class JavaConnector {
    private LoginController controller;

    public JavaConnector(LoginController controller) {
        this.controller = controller;
    }

    public void submitCaptcha(String response) {
        Platform.runLater(() -> controller.handleCaptchaResponse(response));
    }
}
