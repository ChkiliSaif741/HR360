package controllers;
import entities.Candidature;
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
            // Vérifier si le statut a changé
            String nouveauStatut = statutComboBox.getValue();
            if (!nouveauStatut.equals(candidatureSelectionnee.getStatut())) {
                candidatureSelectionnee.setStatut(nouveauStatut);
                try {
                    // Appel du service pour modifier la candidature dans la base de données
                    ServiceCandidature serviceCandidature = new ServiceCandidature();
                    serviceCandidature.modifier(candidatureSelectionnee); // On utilise la méthode modifier

                    System.out.println("Modifications enregistrées pour : " + candidatureSelectionnee);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors de la modification dans la base de données.");
                }
            } else {
                // Aucun changement effectué, ne rien faire
                System.out.println("Aucun changement effectué.");
            }
        }
    }
    @FXML
    private void annuler() {
        statutComboBox.getScene().getWindow().hide();
    }
}
