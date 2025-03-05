package controllers;

import entities.Ressource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import services.QRCodeService;
import services.ServiceRessource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class FormulaireAjoutRessourceController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField prixField;

    @FXML
    private ComboBox<String> etatComboBox;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnAnnuler;
    @FXML
    private Button btnRetour;
    @FXML
    private Button btnAfficherQR;

    private ServiceRessource serviceRessource = new ServiceRessource();
    private Ressource lastAddedRessource;

    @FXML
    private void ajouterRessource(ActionEvent event) {
        String nom = nomField.getText().trim();
        String type = typeField.getText().trim();
        String etat = etatComboBox.getValue();
        System.out.println("Nom saisi : " + nom);  // DEBUG




        if (nom.isEmpty() || type.isEmpty() || etat == null || etat.trim().isEmpty()) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }


        if (!isValidName(nom) || !isValidName(type)) {
            showErrorAlert("Erreur de saisie", "Le nom et le type ne doivent contenir que des lettres !");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixField.getText().trim());
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Veuillez entrer un prix valide !");
            return;
        }

        if (prix <= 0) {
            showErrorAlert("Erreur", "Le prix doit être un nombre positif !");
            return;
        }


        Ressource ressource = new Ressource(nom, type, etat, prix);
        try {
            serviceRessource.ajouter(ressource);
            lastAddedRessource = ressource; // Stocke la dernière ressource ajoutée

            // Génération du QR Code
            String qrPath = QRCodeService.generateQRCodeForRessource(ressource);
            if (qrPath != null) {
                showConfirmationAlert("Succès", "Ressource ajoutée avec succès ! QR Code généré.");
            } else {
                showErrorAlert("Erreur", "QR Code non généré.");
            }

            annulerAjout(null);
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors de l'ajout de la ressource : " + e.getMessage());
        }
    }

    @FXML
    private void afficherQRCode() {
        if (lastAddedRessource != null) {
            File file = new File("qrcodes/ressource_" + lastAddedRessource.getId() + ".png");
            if (file.exists()) {
                try {
                    java.awt.Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    showErrorAlert("Erreur", "Impossible d'ouvrir le QR Code.");
                }
            } else {
                showErrorAlert("Erreur", "QR Code introuvable.");
            }
        } else {
            showErrorAlert("Erreur", "Aucune ressource ajoutée récemment.");
        }
    }

    @FXML
    private void annulerAjout(ActionEvent event) {
        nomField.clear();
        typeField.clear();
        etatComboBox.getSelectionModel().clearSelection();
        prixField.clear();
    }

    private boolean isValidName(String input) {
        return Pattern.matches("^[A-Za-zÀ-ÿ\\s]+$", input);
    }

    private void showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void retour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessource.fxml");
            typeField.getScene().setRoot(parent);
        } catch (IOException e) {
            showErrorAlert("Erreur", "Une erreur est survenue lors du chargement de la page.");
        }
    }
}
