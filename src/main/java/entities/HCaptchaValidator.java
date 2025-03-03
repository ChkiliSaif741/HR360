package entities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HCaptchaValidator {

    private static final String SECRET_KEY = "ES_3d64b43a43ab4577923ae5a431b11306"; // Remplacez par votre clé secrète hCaptcha
    private static final String VERIFY_URL = "https://hcaptcha.com/siteverify";

    public static boolean verifyCaptcha(String hCaptchaResponse) {
        if (hCaptchaResponse == null || hCaptchaResponse.isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(VERIFY_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String postData = "secret=" + SECRET_KEY + "&response=" + hCaptchaResponse;
            System.out.println("POST Data: " + postData);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                String jsonResponse = response.toString();
                System.out.println("Response from hCaptcha: " + jsonResponse);

                if (jsonResponse.contains("\"success\": true")) {
                    return true;
                } else {
                    System.out.println("Error details: " + jsonResponse);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}