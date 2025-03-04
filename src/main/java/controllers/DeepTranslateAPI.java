package controllers;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class DeepTranslateAPI {
    private static final String API_KEY = "f9a37674aemsh1dfa3f25c817e70p173399jsna7ba37200cbd";
    private static final String DETECT_URL = "https://deep-translate1.p.rapidapi.com/language/translate/v2/detect";
    private static final String TRANSLATE_URL = "https://deep-translate1.p.rapidapi.com/language/translate/v2";

    private final OkHttpClient client = new OkHttpClient();

    // ✅ Méthode pour détecter la langue d'un texte
    public String detectLanguage(String text) throws IOException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("q", text);

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(DETECT_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Erreur API: " + response);

            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.toString(4); // Retourne la réponse JSON formatée
        }
    }

    // ✅ Méthode pour traduire un texte du français vers l'anglais
    public String translate(String text) throws IOException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("q", text);
        jsonBody.put("source", "fr"); // Source: Français
        jsonBody.put("target", "en"); // Cible: Anglais

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(TRANSLATE_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Erreur API: " + response);

            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getJSONObject("data").getJSONObject("translations").getString("translatedText");
        }
    }
}
