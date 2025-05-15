package controllers;

import javafx.application.Platform;

public class JavaBridge {
    private final LoginController controller;

    public JavaBridge(LoginController controller) {
        this.controller = controller;
    }

    public void handleCaptchaResponse(String response) {
        Platform.runLater(() -> controller.handleCaptchaResponse(response));
    }
}
