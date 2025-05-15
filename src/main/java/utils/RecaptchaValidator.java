package utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RecaptchaValidator {
    private static final String SECRET_KEY = "6LczPDorAAAAAOfcWNyF5TXPkxN1jvb3eh5uIefc"; // Votre clé secrète
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static boolean verifyCaptcha(String response) {
        try {
            String params = "secret=" + SECRET_KEY + "&response=" + response;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERIFY_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Meilleure vérification de la réponse JSON
            String responseBody = httpResponse.body();
            return responseBody.contains("\"success\": true") &&
                    !responseBody.contains("\"error-codes\"");
        } catch (Exception e) {
            System.err.println("Erreur de validation reCAPTCHA: " + e.getMessage());
            return false;
        }
    }
}