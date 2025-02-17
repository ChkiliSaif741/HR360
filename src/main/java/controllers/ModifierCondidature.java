package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entities.Candidature;
import services.ServiceCandidature;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ModifierCondidature {

    @FXML
    private Label cvLabel;
    private LocalDateTime dateEntretien;
    @FXML
    private Label lettreLabel;
    @FXML
    private Label cvError;
    @FXML
    private Label lettreError;
    @FXML
    private Label dateError;

    private Candidature candidatureSelectionnee;
    private String nouveauCvPath;  // Variable temporaire pour stocker le nouveau CV
    private String nouvelleLettrePath;  // Variable temporaire pour stocker la nouvelle lettre de motivation

    public void initialize() {
        // Initialisation des champs avec les valeurs de la candidature sélectionnée
        if (candidatureSelectionnee != null) {
            cvLabel.setText(candidatureSelectionnee.getCv());
            lettreLabel.setText(candidatureSelectionnee.getLettreMotivation());
        }
    }

    public void setCandidatureSelectionnee(Candidature candidature) {
        this.candidatureSelectionnee = candidature;

        // Préremplir les champs avec les informations actuelles
        if (candidatureSelectionnee != null) {
            cvLabel.setText(candidatureSelectionnee.getCv() != null ? candidatureSelectionnee.getCv() : "Aucun CV");
            lettreLabel.setText(candidatureSelectionnee.getLettreMotivation() != null ? candidatureSelectionnee.getLettreMotivation() : "Aucune lettre de motivation");
        }
    }

    @FXML
    private void uploadCv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            cvLabel.setText(selectedFile.getName());
            nouveauCvPath = selectedFile.getAbsolutePath();  // Stocker le nouveau chemin du CV temporairement
        } else {
            cvError.setText("Aucun fichier sélectionné");
        }
    }

    @FXML
    private void uploadLettreMotivation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            lettreLabel.setText(selectedFile.getName());
            nouvelleLettrePath = selectedFile.getAbsolutePath();  // Stocker le nouveau chemin de la lettre de motivation temporairement
        } else {
            lettreError.setText("Aucun fichier sélectionné");
        }
    }

    @FXML
    private void choisirDateEntretien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DateEntretien.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            DateEntretien controller = loader.getController();

            // Passer la dateEntretien existante au contrôleur de la fenêtre modale
            if (candidatureSelectionnee != null && candidatureSelectionnee.getDateEntretien() != null) {
                controller.setDateEntretien(candidatureSelectionnee.getDateEntretien());
            }

            stage.showAndWait();

            // Récupérer la date sélectionnée
            dateEntretien = controller.getSelectedDateTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierCandidature() {
        boolean isModified = false;

        // Vérifier si le CV a été modifié
        if (nouveauCvPath != null && !nouveauCvPath.equals(candidatureSelectionnee.getCv())) {
            isModified = true;
        }

        // Vérifier si la lettre de motivation a été modifiée
        if (nouvelleLettrePath != null && !nouvelleLettrePath.equals(candidatureSelectionnee.getLettreMotivation())) {
            isModified = true;
        }

        // Vérifier si la date d'entretien a été modifiée
        if (dateEntretien != null && !dateEntretien.equals(candidatureSelectionnee.getDateEntretien())) {
            isModified = true;
        }

        // Si aucun changement n'a été effectué, ne pas afficher l'alerte
        if (!isModified) {
            System.out.println("Aucune modification effectuée.");
            return; // Sortir de la méthode sans afficher l'alerte
        }

        // Alerte de confirmation si des modifications ont été effectuées
        javafx.scene.control.Alert confirmationAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir modifier cette candidature ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        // Boutons de confirmation
        javafx.scene.control.ButtonType boutonOui = new javafx.scene.control.ButtonType("Oui");
        javafx.scene.control.ButtonType boutonNon = new javafx.scene.control.ButtonType("Non");
        confirmationAlert.getButtonTypes().setAll(boutonOui, boutonNon);

        // Affichage de l'alerte et gestion de la réponse
        java.util.Optional<javafx.scene.control.ButtonType> resultat = confirmationAlert.showAndWait();
        if (resultat.isPresent() && resultat.get() == boutonOui) {
            // Si l'utilisateur confirme, appliquez les modifications
            if (nouveauCvPath != null) {
                candidatureSelectionnee.setCv(nouveauCvPath);
            }
            if (nouvelleLettrePath != null) {
                candidatureSelectionnee.setLettreMotivation(nouvelleLettrePath);
            }
            if (dateEntretien != null) {
                candidatureSelectionnee.setDateEntretien(dateEntretien); // Mettre à jour la date d'entretien
            }

            // Mise à jour dans la base de données en appelant la méthode modifier du service
            try {
                // Appel du service pour modifier la candidature dans la base de données
                ServiceCandidature serviceCandidature = new ServiceCandidature();
                serviceCandidature.modifier(candidatureSelectionnee); // On utilise la méthode modifier

                System.out.println("Modifications enregistrées pour : " + candidatureSelectionnee);

                // Fermer la fenêtre après confirmation
                Stage stage = (Stage) cvLabel.getScene().getWindow(); // Obtenir la fenêtre actuelle
                stage.close(); // Fermer la fenêtre
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la modification dans la base de données.");
            }
        } else {
            // Si l'utilisateur annule
            System.out.println("Modification annulée.");
        }
    }



    @FXML
    private void annuler() {
        // Obtenir la fenêtre actuelle
        Stage stage = (Stage) cvLabel.getScene().getWindow();

        // Fermer la fenêtre
        stage.close();

        System.out.println("Modification annulée.");
    }

}