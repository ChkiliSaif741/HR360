package entities;

import controllers.LoginController;

public class JavaAppBridge {
    public void getCaptchaToken(String token) {
        System.out.println("hCaptcha Token reçu : " + token);
        LoginController.hCaptchaToken = token;
    }
}
