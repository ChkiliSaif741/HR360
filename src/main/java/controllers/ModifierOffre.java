package controllers;

import entities.Offre;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServiceOffre;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ModifierOffre {

    @FXML
    private TextField titreField;

    @FXML
    private Label titreError;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Label descriptionError;

    @FXML
    private Label datePublicationError;

    @FXML
    private Label dateExpirationError;
    @FXML
    private Label labelDatePublicationModif;
    @FXML
    private Label labelDateExpirationModif;

    private Offre offreSelectionnee;

    private LocalDateTime datePublication;
    private LocalDateTime dateExpiration;

    private String nouveauTitre;
    private String nouvelleDescription;

    public void setOffreSelectionnee(Offre offre) {
        this.offreSelectionnee = offre;

        // Préremplir les champs avec les informations actuelles
        if (offreSelectionnee != null) {
            titreField.setText(offreSelectionnee.getTitre());
            descriptionField.setText(offreSelectionnee.getDescription());
            datePublication = offreSelectionnee.getDatePublication();
            dateExpiration = offreSelectionnee.getDateExpiration();
        }
    }
    public void animateButton(javafx.scene.input.MouseEvent event) {
        Node button = (Node) event.getSource();

        // Appliquer un effet "glow" (luminosité) au bouton
        Glow glow = new Glow();
        glow.setLevel(0.7); // Ajustez la luminosité selon vos préférences
        button.setEffect(glow);

        // Créer une animation de changement de couleur du bouton
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(button.opacityProperty(), 0.8) // Opacité plus faible
                ),
                new KeyFrame(Duration.seconds(0.2),
                        new javafx.animation.KeyValue(button.opacityProperty(), 1.0) // Retour à l'opacité d'origine
                )
        );

        timeline.setOnFinished(e -> {
            button.setEffect(null); // Retirer l'effet glow après l'animation
        });

        timeline.play(); // Joue l'animation
    }

    @FXML
    private void choisirDatePublication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DatePublication.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            // Utiliser le bon type de contrôleur
            DatePublication controller = loader.getController();
            // Passer la dateExpiration existante au contrôleur de la fenêtre modale
            if (offreSelectionnee != null && offreSelectionnee.getDatePublication() != null) {
                controller.setDatePublication(offreSelectionnee.getDatePublication());
            }
            stage.showAndWait();
            if (datePublication != null) {
                labelDatePublicationModif.setText("Date de publication : " + datePublication.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }

            // Récupérer la date et l'heure sélectionnées
            datePublication = controller.getSelectedDateTime();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void choisirDateExpiration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DateExpiration.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            // Utiliser le bon type de contrôleur
            DateExpiration controller = loader.getController();
            // Passer la dateExpiration existante au contrôleur de la fenêtre modale
            if (offreSelectionnee != null && offreSelectionnee.getDateExpiration() != null) {
                controller.setDateExpiration(offreSelectionnee.getDateExpiration());
            }
            stage.showAndWait();
            if (dateExpiration != null) {
                labelDateExpirationModif.setText("Date d'expiration : " + dateExpiration.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }

            // Récupérer la date et l'heure sélectionnées
            dateExpiration = controller.getSelectedDateTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void enregistrerOffre() {
        boolean isValid = true;
        boolean isModified = false; // Indicateur pour savoir si une modification a eu lieu

        // Récupérer les nouvelles valeurs
        String nouveauTitre = titreField.getText().trim();
        String nouvelleDescription = descriptionField.getText().trim();

        // Validation du titre
        if (nouveauTitre.isEmpty() || nouveauTitre.length() < 5 || nouveauTitre.length() > 100) {
            titreError.setText("Le titre doit comporter entre 5 et 100 caractères.");
            isValid = false;
            showAlert(Alert.AlertType.ERROR, "Erreur de titre", "Le titre est invalide.");
        } else {
            titreError.setText("");
        }

        // Validation de la description
        if (nouvelleDescription.isEmpty() || nouvelleDescription.length() < 10 || nouvelleDescription.length() > 500) {
            descriptionError.setText("La description doit comporter entre 10 et 500 caractères.");
            isValid = false;
            showAlert(Alert.AlertType.ERROR, "Erreur de description", "La description est invalide.");
        } else {
            descriptionError.setText("");
        }

        // Vérifier si la date de publication a été modifiée et si elle est valide
        if (datePublication == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "Veuillez sélectionner une date et une heure de publication.");
            datePublicationError.setText("Veuillez sélectionner une date de publication.");
            isValid = false;
        } else {
            datePublicationError.setText("");
        }

        // Vérifier si la date d'expiration a été modifiée et si elle est valide
        if (dateExpiration == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "Veuillez sélectionner une date et une heure d'expiration.");
            dateExpirationError.setText("Veuillez sélectionner une date d'expiration.");
            isValid = false;
        } else {
            dateExpirationError.setText("");
        }

        // Vérifier si la date de publication est avant la date d'expiration
        if (datePublication != null && dateExpiration != null && datePublication.isAfter(dateExpiration)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de dates", "La date de publication ne peut pas être après la date d'expiration.");
            isValid = false;
        }

        // Si tout est valide, on enregistre les modifications dans la base de données
        if (isValid) {
            // Mise à jour de l'objet Offre seulement si les nouvelles valeurs sont différentes
            if (nouveauTitre != null && !nouveauTitre.equals(offreSelectionnee.getTitre())) {
                offreSelectionnee.setTitre(nouveauTitre);
                isModified = true; // Marquer comme modifié
            }
            if (nouvelleDescription != null && !nouvelleDescription.equals(offreSelectionnee.getDescription())) {
                offreSelectionnee.setDescription(nouvelleDescription);
                isModified = true; // Marquer comme modifié
            }
            if (datePublication != null && !datePublication.equals(offreSelectionnee.getDatePublication())) {
                offreSelectionnee.setDatePublication(datePublication);
                isModified = true; // Marquer comme modifié
            }
            if (dateExpiration != null && !dateExpiration.equals(offreSelectionnee.getDateExpiration())) {
                offreSelectionnee.setDateExpiration(dateExpiration);
                isModified = true; // Marquer comme modifié
            }

            // Mettre à jour le statut en fonction de la date d'expiration
            if (dateExpiration != null && dateExpiration.isBefore(LocalDateTime.now())) {
                offreSelectionnee.setStatut("Expirée");
            } else {
                offreSelectionnee.setStatut("Publiée");
            }

            // Enregistrer les modifications dans la base de données si une modification a eu lieu
            if (isModified) {
                try {
                    ServiceOffre serviceOffre = new ServiceOffre();
                    serviceOffre.modifier(offreSelectionnee); // Utilisation de la méthode pour modifier

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre modifiée avec succès.");

                    // Fermer la fenêtre après l'enregistrement
                    Stage stage = (Stage) titreField.getScene().getWindow();
                    stage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification de l'offre.");
                }
            } else {
                // Si aucune modification n'a eu lieu
                showAlert(Alert.AlertType.INFORMATION, "Aucune modification", "Aucune modification n'a été apportée à l'offre.");
            }
        }
    }

    @FXML
    private void annuler() {
        // Obtenir la fenêtre actuelle
        Stage stage = (Stage) titreField.getScene().getWindow();

        // Fermer la fenêtre
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