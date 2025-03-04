package entities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiClient {
    private static final String API_KEY = "AIzaSyDJCHou0OzX0ydCPVVv3my6SXl-hVJULoA"; // Remplace par ta vraie clé API
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static String sendMessage(String message) throws IOException {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IOException("La clé API n'est pas définie. Vérifiez GEMINI_API_KEY.");
        }

        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // Correction de la structure JSON
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("contents", new JSONArray()
                .put(new JSONObject()
                        .put("parts", new JSONArray()
                                .put(new JSONObject().put("text", message)))));

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new IOException("Erreur lors de la requête : " + responseCode);
        }
    }

    public static void main(String[] args) {
        try {
            String response = sendMessage("Quelles formations recommandez-vous pour améliorer mes compétences ?");
            System.out.println("Réponse de l'API : " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
