package controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.control.Alert;

public class Webviewmeet {
    @FXML
    private WebView webview;

    private String meetLink;

    // Méthode pour initialiser la WebView avec le lien Google Meet
    public void initialize() {
        if (meetLink != null && !meetLink.isEmpty()) {
            try {
                // Activer JavaScript (nécessaire pour Google Meet)
                webview.getEngine().setJavaScriptEnabled(true);

                // Charger le lien Google Meet
                webview.getEngine().load(meetLink);

                // Ajouter un écouteur pour les erreurs de chargement
                webview.getEngine().locationProperty().addListener((obs, oldUrl, newUrl) -> {
                    if (newUrl.contains("error") || newUrl.contains("unsupported")) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Google Meet ne peut pas être chargé dans WebView.");
                    }
                });
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de Google Meet : " + e.getMessage());
            }
        } else {
            webview.getEngine().loadContent("<h1>Lien Google Meet non disponible</h1>");
        }
    }

    // Méthode pour définir le lien Google Meet
    public void setMeetLink(String meetLink) {
        this.meetLink = meetLink;
        // Recharger la WebView si elle est déjà initialisée
        if (webview != null) {
            initialize();
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}