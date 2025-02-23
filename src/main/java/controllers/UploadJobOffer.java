package controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class UploadJobOffer {
    @FXML
    private Button uploadButton;
    @FXML
    private Label fileNameLabel;

    private File cvFile;

    public void setCvFile(File cvFile) {
        this.cvFile = cvFile;
        fileNameLabel.setText("CV sélectionné: " + cvFile.getName());
    }

    @FXML
    private void handleUploadButtonClick() {
        // Ouvrir une boîte de dialogue pour sélectionner le fichier de l'offre d'emploi
        FileChooser fileChooser = new FileChooser();
        // Modifier l'extension pour accepter les fichiers .txt
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers TXT", "*.txt"));
        File jobOfferFile = fileChooser.showOpenDialog(null);

        if (jobOfferFile != null) {
            // Utiliser l'API pour analyser le CV et l'offre d'emploi
            try {
                analyseCvWithJobOffer(cvFile, jobOfferFile);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'analyse : " + e.getMessage());
            }
        } else {
            showAlert("Fichier manquant", "Veuillez sélectionner une offre d'emploi.");
        }
    }

    @FXML
    private Label resultMessageLabel; // Ajoutez ce Label pour afficher le message de succès ou de refus

    public void setResultMessageLabel(Label resultMessageLabel) {
        this.resultMessageLabel = resultMessageLabel;
    }

    private void analyseCvWithJobOffer(File cvFile, File jobOfferFile) throws IOException, InterruptedException {
        String url = "https://cv-resume-to-job-match-analysis-api.p.rapidapi.com/api:QQ6fvSXH/good_fit_external_API";
        String apiKey = "45ceacae6bmshcdb32db2a45a369p150a62jsn97789d9d3d28"; // Clé API

        HttpClient client = HttpClient.newHttpClient();
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";

        // Lire les fichiers
        byte[] cvFileBytes = Files.readAllBytes(cvFile.toPath());
        byte[] jobOfferFileBytes = Files.readAllBytes(jobOfferFile.toPath());

        // Construire la requête multipart/form-data
        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
        String boundaryLine = "--" + boundary + "\r\n";
        String cvHeader = "Content-Disposition: form-data; name=\"cv_file\"; filename=\"" + cvFile.getName() + "\"\r\n" +
                "Content-Type: application/pdf\r\n\r\n";
        String jobOfferHeader = "Content-Disposition: form-data; name=\"job_offer_file\"; filename=\"" + jobOfferFile.getName() + "\"\r\n" +
                "Content-Type: text/plain\r\n\r\n";
        String endBoundary = "--" + boundary + "--\r\n";

        bodyStream.write(boundaryLine.getBytes(StandardCharsets.UTF_8));
        bodyStream.write(cvHeader.getBytes(StandardCharsets.UTF_8));
        bodyStream.write(cvFileBytes);
        bodyStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        bodyStream.write(boundaryLine.getBytes(StandardCharsets.UTF_8));
        bodyStream.write(jobOfferHeader.getBytes(StandardCharsets.UTF_8));
        bodyStream.write(jobOfferFileBytes);
        bodyStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        bodyStream.write(endBoundary.getBytes(StandardCharsets.UTF_8));

        byte[] bodyBytes = bodyStream.toByteArray();

        // Créer la requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("x-rapidapi-host", "cv-resume-to-job-match-analysis-api.p.rapidapi.com")
                .header("x-rapidapi-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                .build();

        // Envoyer la requête et récupérer la réponse
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Debug: afficher le corps de la réponse
        String responseBody = response.body().trim();
        System.out.println("Response Body: " + responseBody); // Afficher la réponse pour le débogage

        String suitabilityScore = "Non disponible"; // Valeur par défaut

        if (responseBody.startsWith("{")) {
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                suitabilityScore = jsonResponse.optString("match_score", "Non disponible");
            } catch (JSONException e) {
                System.out.println("Erreur: Impossible de lire la réponse JSON : " + response.body());
            }
        } else {
            suitabilityScore = extractSuitabilityScore(responseBody);
        }

        // Affichage du message basé sur le score de correspondance
        if ("Non disponible".equals(suitabilityScore)) {
            resultMessageLabel.setText("Le score de correspondance est indisponible.");
        } else if (suitabilityScore.contains("Score faible") || suitabilityScore.equalsIgnoreCase("APPLICATION LITTLE CORRESPONDING")) {
            resultMessageLabel.setText("Score faible : peu de correspondance.");
        } else if (suitabilityScore.contains("Score élevé") || suitabilityScore.equalsIgnoreCase("APPLICATION STRONGLY MATCHED")) {
            resultMessageLabel.setText("Score élevé : correspondance forte.");
        } else {
            resultMessageLabel.setText("Résultat inconnu.");
        }

        // Gestion des erreurs HTTP
        if (response.statusCode() == 403) {
            resultMessageLabel.setText("Erreur: Accès refusé. Vérifiez votre clé API.");
        } else if (response.statusCode() >= 400) {
            resultMessageLabel.setText("Erreur lors de l'analyse : " + response.statusCode());
        }
    }

    private String extractSuitabilityScore(String responseBody) {
        String suitabilityScore = "Non disponible";

        // Afficher la réponse pour le débogage
        System.out.println("Processing response body for score extraction: " + responseBody);

        // Recherche de la phrase contenant "Fit Score" ou "Suitability Score"
        for (String line : responseBody.split("\n")) {
            if (line.toLowerCase().contains("fit score") || line.toLowerCase().contains("suitability score")) {
                suitabilityScore = line.replace("Fit Score", "").replace("Suitability Score", "").replace("➔", "").trim();
                break; // Stop once the score is found
            }
        }

        // Vérification si l'API renvoie un texte au lieu d'un chiffre
        if (suitabilityScore.equalsIgnoreCase("APPLICATION LITTLE CORRESPONDING")) {
            suitabilityScore = "Score faible : peu de correspondance";
        } else if (suitabilityScore.equalsIgnoreCase("APPLICATION STRONGLY MATCHED")) {
            suitabilityScore = "Score élevé : correspondance forte";
        }

        return suitabilityScore;
    }





    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
