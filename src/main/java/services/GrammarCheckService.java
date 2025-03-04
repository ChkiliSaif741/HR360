package services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GrammarCheckService {

    private static final String API_URL = "https://grammer-checker1.p.rapidapi.com/v1/grammer-checker";
    private static final String API_KEY = "f9a37674aemsh1dfa3f25c817e70p173399jsna7ba37200cbd";
    private static final String API_HOST = "grammer-checker1.p.rapidapi.com";

    public static String checkGrammar(String text) {
        try {
            System.out.println("Envoi de la requête à l'API Grammar Checker...");
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-rapidapi-host", API_HOST);
            conn.setRequestProperty("x-rapidapi-key", API_KEY);
            conn.setDoOutput(true);

            // Préparer le corps de la requête
            String jsonInputString = "{\"text\":\"" + text + "\"}";
            System.out.println("Corps de la requête : " + jsonInputString);

            // Envoyer la requête
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Réponse de l'API : " + response.toString());
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'appel à l'API : " + e.getMessage());
            return "{\"status\":\"error\", \"message\":\"An error occurred while checking grammar.\"}";
        }
    }
}