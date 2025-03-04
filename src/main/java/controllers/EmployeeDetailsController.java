package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceUtilisateur;

public class EmployeeDetailsController {

    @FXML
    private TextField posteField;

    @FXML
    private TextField salaireField;

    @FXML
    private TextField idFormationField; // Nouveau champ

    private Utilisateur employe;
    private final ServiceUtilisateur serviceEmploye = new ServiceUtilisateur();

    public void setEmploye(Utilisateur employe) {
        this.employe = employe;
        // Remplir les champs avec les informations de l'employé (si elles existent)
        if (employe != null) {
            posteField.setText(employe.getPoste());
            salaireField.setText(String.valueOf(employe.getSalaire()));
        }
    }

    @FXML
    private void saveEmployeeDetails(ActionEvent event) {
        String poste = posteField.getText();
        String salaireText = salaireField.getText();
        String idFormationText = idFormationField.getText();

        // Vérifier que les champs ne sont pas vides
        if (poste.isEmpty() || salaireText.isEmpty() || idFormationText.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs !");
            return;
        }

        // Convertir le salaire en double et l'ID de formation en int
        double salaire = Double.parseDouble(salaireText);
        int idFormation = Integer.parseInt(idFormationText);

        // Mettre à jour l'employé avec les nouvelles informations
        employe.setPoste(poste);
        employe.setSalaire((float) salaire);

        try {
            // Vérifier si l'employé existe déjà dans la base de données
            if (employe.getId() == 0) { // Si l'ID est 0, l'employé n'existe pas encore
                serviceEmploye.ajouter(employe); // Ajouter l'employé à la base de données
                System.out.println("Employé ajouté avec succès !");
            } else {
                serviceEmploye.modifier(employe); // Modifier l'employé existant
                System.out.println("Employé modifié avec succès !");
            }

            // Fermer la fenêtre actuelle après l'enregistrement
            Stage stage = (Stage) posteField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la sauvegarde des détails de l'employé : " + e.getMessage());
        }
    }
}