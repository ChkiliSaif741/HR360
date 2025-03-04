package controllers;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import java.io.FileOutputStream;


public class UploadJobOffer {
    @FXML
    private Button uploadButton;
    @FXML
    private Label fileNameLabel;

    private File cvFile;
    private File jobOfferFile; // Fichier d'offre d'emploi
    private File analysisPdfFile; // Variable pour stocker le fichier PDF

    public void setCvFile(File cvFile) {
        this.cvFile = cvFile;
        fileNameLabel.setText("CV sélectionné: " + cvFile.getName());
    }

    @FXML
    private void handleUploadButtonClick() {
        // Ouvrir une boîte de dialogue pour sélectionner le fichier de l'offre d'emploi
        FileChooser fileChooser = new FileChooser();
        // Accepter les fichiers .pdf et .txt pour l'offre d'emploi
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF et Texte", "*.pdf", "*.txt"));
        jobOfferFile = fileChooser.showOpenDialog(null);

        if (jobOfferFile != null) {
            // Afficher le nom du fichier sélectionné
            fileNameLabel.setText("Offre d'emploi sélectionnée: " + jobOfferFile.getName());

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

    private void analyseCvWithJobOffer(File cvFile, File jobOfferFile) throws IOException, InterruptedException {
        String url = "https://cv-resume-to-job-match-analysis-api.p.rapidapi.com/api:QQ6fvSXH/good_fit_external_API";
        String apiKey = "3adc0af0d6msh43f660216ea4668p1e247djsn42fce2b3f563"; // Clé API

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
        // Générer le PDF avec l’analyse complète une fois responseBody défini
        generateAnalysisPdf(responseBody);

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
        } else if (suitabilityScore.contains("APPLICATION LITTLE CORRESPONDING") || suitabilityScore.equalsIgnoreCase("APPLICATION LITTLE CORRESPONDING")) {
            resultMessageLabel.setText("Score faible : peu de correspondance.");
        } else if (suitabilityScore.contains("APPLICATION STRONGLY MATCHED") || suitabilityScore.equalsIgnoreCase("APPLICATION STRONGLY MATCHED")) {
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
    private void generateAnalysisPdf(String analysisContent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport d'analyse");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File saveFile = fileChooser.showSaveDialog(uploadButton.getScene().getWindow());

        if (saveFile != null) {
            try {
                PdfWriter writer = new PdfWriter(new FileOutputStream(saveFile));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Chemin du logo
                String logoPath = "C:\\Users\\Dell\\Desktop\\piDevJava\\HR360\\src\\main\\resources\\Images\\logoRH360.png";
                com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(ImageDataFactory.create(logoPath));
                logo.scaleToFit(100, 100); // Redimensionner le logo

                // Ajouter une table pour aligner le logo et le nom de l'entreprise
                Table headerTable = new Table(2);
                headerTable.setWidth(UnitValue.createPercentValue(100));

                // Ajouter le logo à gauche
                Cell logoCell = new Cell().add(logo);
                logoCell.setBorder(Border.NO_BORDER);
                logoCell.setPadding(10);
                headerTable.addCell(logoCell);

                // Ajouter le nom de l'entreprise à droite
                Cell titleCell = new Cell().add(new Paragraph("RH Entreprise 360")
                        .setBold()
                        .setFontSize(20)
                        .setTextAlignment(TextAlignment.RIGHT));
                titleCell.setBorder(Border.NO_BORDER);
                titleCell.setPadding(15);
                headerTable.addCell(titleCell);

                // Ajouter la table au document
                document.add(headerTable);
                document.add(new Paragraph("\n"));

                // Ajouter le titre du rapport
                document.add(new Paragraph("Résultat d'Analyse de l'Offre et du CV")
                        .setBold()
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setUnderline());

                document.add(new Paragraph("\n"));

                // Nettoyer le contenu pour supprimer les caractères spéciaux
                analysisContent = analysisContent.replace("\\n", "\n").replace("\\u0027", "'").replace("\\u0022", "\"");

                // Séparer les sections dynamiquement
                String[] sections = analysisContent.split("\n\n");
                for (String section : sections) {
                    // Détecter si la section est un titre (commence par "•" ou contient "**")
                    if (section.startsWith("•") || section.contains("**")) {
                        // Extraire le titre et le formater
                        String title = section.replace("•", "").replace("**", "").trim();
                        document.add(new Paragraph(title)
                                .setBold()
                                .setFontSize(16)
                                .setPaddingBottom(5));
                    } else {
                        // Ajouter le contenu normal
                        document.add(new Paragraph(section.trim())
                                .setFontSize(12)
                                .setPaddingBottom(10));
                    }
                    document.add(new Paragraph("\n")); // Ajouter un espace entre les sections
                }

                // Ajout d'un message de fin de rapport
                document.add(new Paragraph("--- Fin du rapport ---")
                        .setItalic()
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER));

                // Fermer le document
                document.close();

                // Stocker le fichier PDF généré dans la variable analysisPdfFile
                analysisPdfFile = saveFile;

                showAlert("Succès", "Le PDF a été généré avec succès !");
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de générer le PDF : " + e.getMessage());
            }
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
    @FXML
    private void handleOpenPdf() {
        if (analysisPdfFile != null && analysisPdfFile.exists()) {
            try {
                Desktop.getDesktop().open(analysisPdfFile);
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir le fichier PDF : " + e.getMessage());
            }
        } else {
            showAlert("Fichier introuvable", "Aucun rapport généré. Veuillez d'abord analyser un CV.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
