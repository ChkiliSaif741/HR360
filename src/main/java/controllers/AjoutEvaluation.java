package controllers;

import entities.Evaluation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import services.ServiceEvaluation;
import utils.statut;
import utils.type;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AjoutEvaluation {


    @javafx.fxml.FXML
    private TextField noteTechnique;
    @javafx.fxml.FXML
    private TextField noteSoftSkills;
    @javafx.fxml.FXML
    private TextField commentaire;

    private ServiceEvaluation serviceEvaluation;

    private int idEntretien;
    @FXML
    private Label noteSoftSkillsC;
    @FXML
    private Label noteTechniqueC;
    @FXML
    private Label commentaireC;


    @FXML
    public void initialize() {
        serviceEvaluation = new ServiceEvaluation();
        //chargerIdEntretien(); // Charger les idEntretien dans la ComboBox
        //chargerIdEvaluateur(); // Charger les id dans la ComboBox
    }



    /*private void chargerIdEntretien() {
        try {
            List<Integer> idEntretiens = serviceEvaluation.getIdsEntretien();
            idEntretien.setItems(FXCollections.observableArrayList(idEntretiens));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les idEntretien : " + e.getMessage());
        }
    }*/

    /*private void chargerIdEvaluateur() {
        try {
            List<Integer> idEvaluateurs = serviceEvaluation.getIdsEvaluateur();
            id.setItems(FXCollections.observableArrayList(idEvaluateurs));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les id : " + e.getMessage());
        }
    }*/

    private boolean validerSaisie() {
        boolean isValid = true;

        // Vérification du champ noteTechnique
        if (noteTechnique.getText().trim().isEmpty()) {
            noteTechniqueC.setText("Ce champ est obligatoire !");
            isValid = false;
        } else {
            try {
                float noteTech = Float.parseFloat(noteTechnique.getText());
                if (noteTech < 0 || noteTech > 20) {
                    noteTechniqueC.setText("La note doit être entre 0 et 20 !");
                    isValid = false;
                } else {
                    noteTechniqueC.setText(""); // Effacer le message d'erreur
                }
            } catch (NumberFormatException e) {
                noteTechniqueC.setText("Veuillez entrer un nombre valide !");
                isValid = false;
            }
        }

        // Vérification du champ noteSoftSkills
        if (noteSoftSkills.getText().trim().isEmpty()) {
            noteSoftSkillsC.setText("Ce champ est obligatoire !");
            isValid = false;
        } else {
            try {
                float noteSS = Float.parseFloat(noteSoftSkills.getText());
                if (noteSS < 0 || noteSS > 20) {
                    noteSoftSkillsC.setText("La note doit être entre 0 et 20 !");
                    isValid = false;
                } else {
                    noteSoftSkillsC.setText("");
                }
            } catch (NumberFormatException e) {
                noteSoftSkillsC.setText("Veuillez entrer un nombre valide !");
                isValid = false;
            }
        }

        // Vérification du champ commentaire
        if (commentaire.getText().trim().isEmpty()) {
            commentaireC.setText("Le commentaire est obligatoire !");
            isValid = false;
        } else {
            commentaireC.setText(""); // Effacer le message d'erreur
        }

        return isValid;
    }






    @FXML
    public void AddEvaluation(ActionEvent actionEvent) {
        if (!validerSaisie()) {
            return;
        }

        try {

            // Vérifier si une évaluation existe déjà pour cet entretien
            if (serviceEvaluation.evaluationExistsForEntretien(idEntretien)) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Une évaluation existe déjà pour cet entretien !");
                return; // Ne pas ajouter une nouvelle évaluation
            }

            // Récupérer les valeurs validées
            float noteTech = Float.parseFloat(noteTechnique.getText());
            float noteSS = Float.parseFloat(noteSoftSkills.getText());
            //LocalDate dateEva = dateEvaluation.getValue();
            String comment = commentaire.getText();
            int idEva = idEntretien;
            //int idE = (int) id.getValue();
            LocalDateTime dateEvaluation = LocalDateTime.now();

            // Créer et ajouter l'évaluation
            Evaluation evaluation = new Evaluation(noteTech, noteSS, comment, dateEvaluation, idEva);
            serviceEvaluation.ajouter(evaluation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Évaluation ajoutée avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root=loader.load();
            Controller controller = loader.getController();
            AfficheEvaluation AffEva = controller.loadPage("/afficheevaluation.fxml").getController();
            AffEva.setIdEntretien(idEntretien);
            commentaire.getScene().setRoot(root);

            // Redirection vers la page d'affichage
            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheevaluation.fxml"));
            Parent parent = loader.load();
            AfficheEvaluation controller = loader.getController();
            controller.setIdEntretien(idEntretien);
            commentaire.getScene().setRoot(parent);*/

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'ajout de l'évaluation : " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void setIdEntretien(int idEntretien) {
        this.idEntretien = idEntretien;
    }
}
