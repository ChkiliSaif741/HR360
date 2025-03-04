package controllers;
import entities.Candidature;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import services.ServiceCandidature;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ModifCandidatureBack {
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Label statutError, descriptionError;

    private Candidature candidatureSelectionnee;

    public void setCandidatureSelectionnee(Candidature candidature) {
        this.candidatureSelectionnee = candidature;
        if (candidature != null) {
            statutComboBox.setValue(candidature.getStatut());
        }
    }

    @FXML
    private void modifierCandidature() {
        if (candidatureSelectionnee != null) {
            // Liste des statuts qui ne peuvent pas être modifiés une fois définis
            List<String> statutsFinaux = Arrays.asList("Accepté", "Refusé");

            // Vérifier si le statut actuel est non modifiable
            if (statutsFinaux.contains(candidatureSelectionnee.getStatut())) {
                System.out.println("Le statut ne peut pas être modifié car il est déjà défini comme : " + candidatureSelectionnee.getStatut());
                showAlert("Erreur", "Le statut ne peut pas être modifié car il est déjà défini comme : " + candidatureSelectionnee.getStatut());
                return; // Arrêter l'exécution de la méthode
            }

            String nouveauStatut = statutComboBox.getValue();

            if (!nouveauStatut.equals(candidatureSelectionnee.getStatut())) {
                candidatureSelectionnee.setStatut(nouveauStatut);

                try {
                    ServiceCandidature serviceCandidature = new ServiceCandidature();
                    serviceCandidature.modifier(candidatureSelectionnee);

                    System.out.println("Modifications enregistrées pour : " + candidatureSelectionnee);

                    // Envoi de l'email en fonction du nouveau statut
                    ServiceMail serviceMail = new ServiceMail();
                    String sujet = "Mise à jour de la candidature";
                    String message = "";

                    // Utiliser du HTML pour personnaliser l'email
                    String entrepriseNom = "RH Entreprise 360"; // Nom de l'entreprise
                    String logoPath = getClass().getClassLoader().getResource("images/logoRH360.png").getPath();

                    // Structure HTML avec le logo à gauche du nom de l'entreprise
                    message = "<html><body>"
                            + "<div style='display: flex; align-items: center;'>"
                            + "<img src='cid:logo' width='100' height='100' style='margin-right: 10px;' />" // Logo à gauche
                            + "<h2>" + entrepriseNom + "</h2>" // Nom de l'entreprise à droite
                            + "</div>"
                            + "<p>Bonjour,</p>";

                    if (nouveauStatut.equalsIgnoreCase("Accepté")) {
                        // Message de félicitations avec une date d'entretien
                        String dateEntretien = "2023-12-15 à 10h00"; // Remplacez par la date réelle
                        message += "<p>Felicitations ! Votre candidature a ete acceptee.</p>"
                                + "<p>Vous etes invite à un entretien le " + dateEntretien + ".</p>";
                    } else{
                        // Message de refus
                        message += "<p>Nous regrettons de vous informer que votre candidature a ete refusee.</p>"
                                + "<p>Nous vous remercions pour l'interet que vous avez porte à notre entreprise.</p>";
                    }

                    message += "<p>Merci.</p>"
                            + "<p>Cordialement.</p>"
                            + "</body></html>";

                    // Envoyer l'e-mail à kousay.najar@esprit.tn avec le logo et le contenu HTML
                    serviceMail.envoyerEmailAvecLogo("kousay.najar@esprit.tn", sujet, message, logoPath);

                    System.out.println("Email envoyé avec succès à kousay.najar@esprit.tn");

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors de la modification dans la base de données.");
                } catch (MessagingException e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors de l'envoi de l'email.");
                }
            } else {
                System.out.println("Aucun changement effectué.");
            }
        }
    }
    @FXML
    private void annuler() {
        statutComboBox.getScene().getWindow().hide();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
