package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entities.Candidature;
import services.ServiceCandidature;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.net.URL;
import java.util.ResourceBundle;

public class AjouterCondidature implements Initializable {


    @FXML
    private javafx.scene.control.TextArea descriptionField;
    @FXML
    private Label descriptionError;

    @FXML
    private Label cvLabel;
    @FXML
    private Label lettreLabel;

    @FXML
    private Label cvError;
    @FXML
    private Label lettreError;
    @FXML
    private ImageView cvIcon;
    @FXML
    private ImageView lettreIcon;

    private File cvFile;
    private File lettreFile;
    private int idOffre;

    private final Image pdfIconImage = new Image(getClass().getResourceAsStream("/icons/pdf-file-format.png"));

    private final ServiceCandidature serviceCandidature = new ServiceCandidature();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Plus besoin de gérer le statut ici
        cvIcon.setVisible(false);
        lettreIcon.setVisible(false);
        // Ajout d'un événement de clic sur l'icône du CV
        cvIcon.setOnMouseClicked(event -> {
            if (cvFile != null) {
                ouvrirFichier(cvFile);
            }
        });

        // Ajout d'un événement de clic sur l'icône de la lettre de motivation
        lettreIcon.setOnMouseClicked(event -> {
            if (lettreFile != null) {
                ouvrirFichier(lettreFile);
            }
        });
    }
    public void initData(int idOffre) {
        this.idOffre = idOffre;
        System.out.println("ID Offre reçu : " + idOffre);
    }
    @FXML
    public void uploadCv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        cvFile = fileChooser.showOpenDialog(new Stage());

        if (cvFile != null) {
            cvIcon.setImage(pdfIconImage);
            cvIcon.setVisible(true);
            cvError.setText("");
        }
    }

    public void uploadLettreMotivation() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        lettreFile = fileChooser.showOpenDialog(new Stage());

        if (lettreFile != null) {
            lettreIcon.setImage(pdfIconImage);
            lettreIcon.setVisible(true);
            lettreError.setText("");
        }
    }
    @FXML
    private void ouvrirFichierCv() {
        if (cvFile != null) {
            ouvrirFichier(cvFile);
        }
    }

    @FXML
    private void ouvrirFichierLettre() {
        if (lettreFile != null) {
            ouvrirFichier(lettreFile);
        }
    }


    private void ouvrirFichier(File fichier) {
        try {
            Desktop.getDesktop().open(fichier);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le fichier.");
        }
    }

    @FXML
    public void enregistrerCandidature() {
        boolean isValid = true;
        // Validation de la description
        String description = descriptionField.getText().trim();
        if (description.isEmpty() || description.length() < 10 || description.length() > 500) {
            descriptionError.setText("La description doit comporter entre 10 et 500 caractères.");
            isValid = false;
            showAlert(AlertType.ERROR, "Erreur de description", "Attention!!!!");
        } else {
            descriptionError.setText("");
        }

        // Validation du CV
        if (cvFile == null || !cvFile.getName().endsWith(".pdf")) {
            cvError.setText("Veuillez sélectionner un fichier PDF pour le CV.");
            isValid = false;
            showAlert(AlertType.ERROR, "Erreur de fichier", "Le fichier CV doit être un fichier PDF.");
        } else {
            cvError.setText("");
        }

        // Validation de la lettre de motivation
        if (lettreFile == null || !lettreFile.getName().endsWith(".pdf")) {
            lettreError.setText("Veuillez sélectionner un fichier PDF pour la lettre.");
            isValid = false;
            showAlert(AlertType.ERROR, "Erreur de fichier", "Le fichier de lettre de motivation doit être un fichier PDF.");
        } else {
            lettreError.setText("");
        }

        // Si tout est valide, on enregistre la candidature
        if (isValid) {
            File uploadsDir = new File("uploads");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }

            try {
                // Sauvegarder le CV
                File cvDest = new File(uploadsDir, cvFile.getName());
                Files.copy(cvFile.toPath(), cvDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Sauvegarder la lettre de motivation
                File lettreDest = new File(uploadsDir, lettreFile.getName());
                Files.copy(lettreFile.toPath(), lettreDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Initialiser le statut à "En attente"
                String statut = "En attente";

                // Création de l'objet Candidature
                Candidature candidature = new Candidature(
                        LocalDateTime.now(),          // Date de candidature
                        statut,                       // Statut
                        cvDest.getAbsolutePath(),     // Chemin du CV
                        lettreDest.getAbsolutePath(), // Chemin de la lettre
                        idOffre,                            // ID de l'offre
                        descriptionField.getText(),   // Description (depuis le champ TextArea)
                        LocalDateTime.now() ,// Date de modification (initialisée à maintenant)
                        1
                );

                // Enregistrement dans la base de données
                try {
                    serviceCandidature.ajouter(candidature);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                showAlert(AlertType.INFORMATION, "Succès", "Candidature enregistrée avec succès.");

                // Fermer l'interface actuelle (fenêtre de l'ajout)
                Stage currentStage = (Stage) descriptionField.getScene().getWindow();

                currentStage.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la sauvegarde des fichiers : " + e.getMessage());
                showAlert(AlertType.ERROR, "Erreur de fichier", "Une erreur est survenue lors de la sauvegarde des fichiers.");
            }
        }
    }


    @FXML
    private void annuler(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}