package controllers;

import entities.Evaluation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceEvaluation;
import utils.statut;
import utils.type;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AjoutEvaluation {


    //@javafx.fxml.FXML
    //private ComboBox idEntretien;
    @javafx.fxml.FXML
    private DatePicker dateEvaluation;
    @javafx.fxml.FXML
    private TextField noteTechnique;
    @javafx.fxml.FXML
    private ComboBox id;
    @javafx.fxml.FXML
    private TextField noteSoftSkills;
    @javafx.fxml.FXML
    private TextField commentaire;

    private ServiceEvaluation serviceEvaluation;

    private int idEntretien;


    @FXML
    public void initialize() {
        serviceEvaluation = new ServiceEvaluation();
        //chargerIdEntretien(); // Charger les idEntretien dans la ComboBox
        chargerIdEvaluateur(); // Charger les id dans la ComboBox
    }



    /*private void chargerIdEntretien() {
        try {
            List<Integer> idEntretiens = serviceEvaluation.getIdsEntretien();
            idEntretien.setItems(FXCollections.observableArrayList(idEntretiens));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les idEntretien : " + e.getMessage());
        }
    }*/

    private void chargerIdEvaluateur() {
        try {
            List<Integer> idEvaluateurs = serviceEvaluation.getIdsEvaluateur();
            id.setItems(FXCollections.observableArrayList(idEvaluateurs));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les id : " + e.getMessage());
        }
    }

    private boolean validerSaisie() {
        // Vérification de la date
        if (dateEvaluation.getValue() == null || dateEvaluation.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de l'évaluation doit être aujourd'hui ou dans le passé !");
            return false;
        }

        // Vérification des notes
        try {
            float noteTech = Float.parseFloat(noteTechnique.getText());
            float noteSS = Float.parseFloat(noteSoftSkills.getText());

            if (noteTech < 0 || noteTech > 20) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La note technique doit être entre 0 et 20 !");
                return false;
            }

            if (noteSS < 0 || noteSS > 20) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La note Soft Skills doit être entre 0 et 20 !");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les notes doivent être des nombres valides !");
            return false;
        }

        // Vérification du commentaire
        if (commentaire.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le champ commentaire ne peut pas être vide !");
            return false;
        }

        // Vérification de l'ID évaluateur
        if (id.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un évaluateur !");
            return false;
        }

        return true;
    }




    @FXML
    public void AddEvaluation(ActionEvent actionEvent) {
        if (!validerSaisie()) {
            return;
        }

        try {
            // Récupérer les valeurs validées
            float noteTech = Float.parseFloat(noteTechnique.getText());
            float noteSS = Float.parseFloat(noteSoftSkills.getText());
            LocalDate dateEva = dateEvaluation.getValue();
            String comment = commentaire.getText();
            int idEva = idEntretien;
            int idE = (int) id.getValue();

            // Créer et ajouter l'évaluation
            Evaluation evaluation = new Evaluation(noteTech, noteSS, comment, dateEva, idEva, idE);
            serviceEvaluation.ajouter(evaluation);
            //showAlert(Alert.AlertType.INFORMATION, "Succès", "Évaluation ajoutée avec succès !");

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
