package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceUtilisateur;

public class CandidatDetailsController {

    @FXML
    private TextField cvField; // Champ pour le CV

    @FXML
    private Button saveBtn;

    private Utilisateur candidat;
    private final ServiceUtilisateur serviceCandidat = new ServiceUtilisateur();

    public void setCandidat(Utilisateur candidat) {
        this.candidat = candidat;
        // Remplir les champs avec les informations du candidat (si elles existent)
        if (candidat != null) {
            cvField.setText(candidat.getCompetence());
        }
    }

    @FXML
     public void saveCandidatDetails(ActionEvent event) {
        String cv = cvField.getText();

        // Vérifier que le champ n'est pas vide
        if (cv.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs !");
            return;
        }

        // Mettre à jour le candidat avec les nouvelles informations
        candidat.setCompetence(cv);

        try {
            // Vérifier si le candidat existe déjà dans la base de données
            if (candidat.getId() == 0) { // Si l'ID est 0, le candidat n'existe pas encore
                serviceCandidat.ajouter(candidat); // Ajouter le candidat à la base de données
                System.out.println("Candidat ajouté avec succès !");
            } else {
                serviceCandidat.modifier(candidat); // Modifier le candidat existant
                System.out.println("Candidat modifié avec succès !");
            }

            // Fermer la fenêtre actuelle après l'enregistrement
            Stage stage = (Stage) cvField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la sauvegarde des détails du candidat : " + e.getMessage());
        }
    }

}