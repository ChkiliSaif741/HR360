package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entities.QuizQuestion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class QuizApiService {
    private static final String API_KEY = "vSI5WEVie4xRm1b0UH4EgsvBMWGqUU3ZRNi05I9v";
    private static final String API_URL = "https://quizapi.io/api/v1/questions";

    public List<QuizQuestion> fetchQuizQuestions(String category, String difficulty, int limit) throws Exception {
        // Construire l'URL de l'API avec les paramètres
        String urlString = String.format("%s?apiKey=%s&limit=%d&category=%s&difficulty=%s",
                API_URL, API_KEY, limit, category, difficulty);

        // Créer une connexion HTTP
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Vérifier le code de réponse
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new RuntimeException("Unauthorized: Check your API key.");
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        // Lire la réponse
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parser la réponse JSON en une liste de QuizQuestion
            Gson gson = new Gson();
            return gson.fromJson(response.toString(), new TypeToken<List<QuizQuestion>>() {}.getType());
        } finally {
            connection.disconnect();
        }
    }
}