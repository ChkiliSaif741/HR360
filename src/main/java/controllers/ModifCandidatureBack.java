package controllers;
import entities.Candidature;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import entities.Candidature;
import javafx.stage.Stage;
import services.ServiceCandidature;

import java.sql.SQLException;

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

                    if (nouveauStatut.equalsIgnoreCase("Accepté")) {
                        // Message de félicitations avec une date d'entretien
                        String dateEntretien = "2023-12-15 à 10h00"; // Remplacez par la date réelle
                        message = "Bonjour,\n\n"
                                + "Félicitations ! Votre candidature a été acceptée.\n"
                                + "Vous êtes invité à un entretien le " + dateEntretien + ".\n"
                                + "Merci.\nCordialement.";
                    } else if (nouveauStatut.equalsIgnoreCase("Refusé")) {
                        // Message de refus
                        message = "Bonjour,\n\n"
                                + "Nous regrettons de vous informer que votre candidature a été refusée.\n"
                                + "Nous vous remercions pour l'intérêt que vous avez porté à notre entreprise.\n"
                                + "Cordialement.";
                    } else {
                        // Message par défaut pour d'autres statuts
                        message = "Bonjour,\n\n"
                                + "Le statut de votre candidature a été mis à jour : " + nouveauStatut + "\n"
                                + "Merci.\nCordialement.";
                    }

                    // Envoyer l'e-mail à kousay.najar@esprit.tn
                    serviceMail.envoyerEmail("kousay.najar@esprit.tn", sujet, message);

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
}
