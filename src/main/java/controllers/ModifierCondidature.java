package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Candidature;
import services.ServiceCandidature;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ModifierCondidature {

    @FXML
    private TextArea descriptionField;
    @FXML
    private Label cvLabel;
    @FXML
    private Label lettreLabel;
    @FXML
    private Label cvError;
    @FXML
    private Label lettreError;
    @FXML
    private Label dateError;

    @FXML
    private ImageView cvIcon;
    @FXML
    private ImageView lettreIcon;

    private String nouvelleDescription;
    private Candidature candidatureSelectionnee;
    private String nouveauCvPath;  // Variable temporaire pour stocker le nouveau CV
    private String nouvelleLettrePath;  // Variable temporaire pour stocker la nouvelle lettre de motivation

    private final Image pdfIconImage = new Image(getClass().getResourceAsStream("/icons/pdf-file-format.png"));

    public void initialize() {
        // Initialisation des champs avec les valeurs de la candidature sélectionnée
        if (candidatureSelectionnee != null) {
            System.out.println(candidatureSelectionnee);
            cvLabel.setText(candidatureSelectionnee.getCv());
            lettreLabel.setText(candidatureSelectionnee.getLettreMotivation());
            descriptionField.setText(candidatureSelectionnee.getDescription());
            // Set the PDF icon visibility and file name
            if (candidatureSelectionnee.getCv() != null) {
                cvIcon.setImage(pdfIconImage);
                cvIcon.setVisible(true);
            }
            if (candidatureSelectionnee.getLettreMotivation() != null) {
                lettreIcon.setImage(pdfIconImage);
                lettreIcon.setVisible(true);
            }
        }
    }

    public void setCandidatureSelectionnee(Candidature candidature) {
        this.candidatureSelectionnee = candidature;

        // Préremplir les champs avec les informations actuelles
        if (candidatureSelectionnee != null) {
            // Use getName() to extract only the file name
            String cvName = (candidatureSelectionnee.getCv() != null) ? new File(candidatureSelectionnee.getCv()).getName() : "Aucun CV";
            String lettreName = (candidatureSelectionnee.getLettreMotivation() != null) ? new File(candidatureSelectionnee.getLettreMotivation()).getName() : "Aucune lettre de motivation";

            cvLabel.setText(cvName);  // Display only the file name for CV
            lettreLabel.setText(lettreName);  // Display only the file name for lettre de motivation
            descriptionField.setText(candidatureSelectionnee.getDescription());
        }

        // Set the PDF icon visibility and file name
        if (candidatureSelectionnee.getCv() != null) {
            cvIcon.setImage(pdfIconImage);
            cvIcon.setVisible(true);
        }
        if (candidatureSelectionnee.getLettreMotivation() != null) {
            lettreIcon.setImage(pdfIconImage);
            lettreIcon.setVisible(true);
        }
    }

    @FXML
    private void uploadCv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            cvLabel.setText(selectedFile.getName());  // Only set the file name (not the full path)
            cvIcon.setImage(pdfIconImage);  // Set the PDF icon
            cvIcon.setVisible(true);  // Show the icon
            nouveauCvPath = selectedFile.getAbsolutePath();  // Store the full path of the file
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
            lettreLabel.setText(selectedFile.getName());  // Only set the file name (not the full path)
            lettreIcon.setImage(pdfIconImage);  // Set the PDF icon
            lettreIcon.setVisible(true);  // Show the icon
            nouvelleLettrePath = selectedFile.getAbsolutePath();  // Store the full path of the file
        } else {
            lettreError.setText("Aucun fichier sélectionné");
        }
    }

    @FXML
    private void ouvrirFichierCv() {
        // Use the new CV path if a new file was selected, otherwise use the original one
        String filePath = (nouveauCvPath != null) ? nouveauCvPath : (candidatureSelectionnee != null ? candidatureSelectionnee.getCv() : null);

        if (filePath != null) {
            File cvFile = new File(filePath);
            if (cvFile.exists()) {
                ouvrirFichier(cvFile);  // Open the file (CV)
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier du CV n'existe pas.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun fichier de CV disponible.");
        }
    }

    @FXML
    private void ouvrirFichierLettre() {
        // Use the new Lettre file path if a new file was selected, otherwise use the original one
        String filePath = (nouvelleLettrePath != null) ? nouvelleLettrePath : (candidatureSelectionnee != null ? candidatureSelectionnee.getLettreMotivation() : null);

        if (filePath != null) {
            File lettreFile = new File(filePath);
            if (lettreFile.exists()) {
                ouvrirFichier(lettreFile);  // Open the file (Lettre de Motivation)
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier de la lettre de motivation n'existe pas.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun fichier de lettre de motivation disponible.");
        }
    }


    private void ouvrirFichier(File fichier) {
        try {
            Desktop.getDesktop().open(fichier);  // Open the file using the default system application
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le fichier.");
        }
    }

    @FXML
    private void modifierCandidature() {
        boolean isModified = false;

        // Récupérer la nouvelle description depuis le TextArea
        String nouvelleDescription = descriptionField.getText();

        // Vérifier si le CV a été modifié
        if (nouveauCvPath != null && !nouveauCvPath.equals(candidatureSelectionnee.getCv())) {
            isModified = true;
        }

        // Vérifier si la lettre de motivation a été modifiée
        if (nouvelleLettrePath != null && !nouvelleLettrePath.equals(candidatureSelectionnee.getLettreMotivation())) {
            isModified = true;
        }

        // Vérifier si la description a été modifiée
        if (nouvelleDescription != null && !nouvelleDescription.equals(candidatureSelectionnee.getDescription())) {
            isModified = true;
        }

        // Si aucun changement n'a été effectué, ne pas afficher l'alerte et sortir de la méthode
        if (!isModified) {
            System.out.println("Aucune modification effectuée.");
            return;
        }

        // Alerte de confirmation si des modifications ont été effectuées
        javafx.scene.control.Alert confirmationAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir modifier cette candidature ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        javafx.scene.control.ButtonType boutonOui = new javafx.scene.control.ButtonType("Oui");
        javafx.scene.control.ButtonType boutonNon = new javafx.scene.control.ButtonType("Non");
        confirmationAlert.getButtonTypes().setAll(boutonOui, boutonNon);

        java.util.Optional<javafx.scene.control.ButtonType> resultat = confirmationAlert.showAndWait();
        if (resultat.isPresent() && resultat.get() == boutonOui) {
            if (nouveauCvPath != null) {
                candidatureSelectionnee.setCv(nouveauCvPath);
            }
            if (nouvelleLettrePath != null) {
                candidatureSelectionnee.setLettreMotivation(nouvelleLettrePath);
            }
            if (nouvelleDescription != null) {
                candidatureSelectionnee.setDescription(nouvelleDescription);
            }

            try {
                ServiceCandidature serviceCandidature = new ServiceCandidature();
                serviceCandidature.modifier(candidatureSelectionnee);

                System.out.println("Modifications enregistrées pour : " + candidatureSelectionnee);

                Stage stage = (Stage) cvLabel.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la modification dans la base de données.");
            }
        } else {
            System.out.println("Modification annulée.");
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) cvLabel.getScene().getWindow();
        stage.close();
        System.out.println("Modification annulée.");
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
