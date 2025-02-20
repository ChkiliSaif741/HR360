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

    @FXML
    public void AddEvaluation(ActionEvent actionEvent) {
        // Validation des champs
        if (noteTechnique.getText().isEmpty() || noteSoftSkills.getText().isEmpty() || commentaire.getText().isEmpty() || dateEvaluation.getValue() == null || id.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            // Récupérer les valeurs des champs
            float noteTech = Float.parseFloat(noteTechnique.getText());
            float noteSS = Float.parseFloat(noteSoftSkills.getText());
            LocalDate dateEva = dateEvaluation.getValue();
            String comment = commentaire.getText();
            int idEva = idEntretien; // Récupérer l'idEntretien sélectionné
            int idE = (int) id.getValue(); // Récupérer l'id sélectionné

            // Créer l'objet Evaluation
            Evaluation evaluation = new Evaluation(noteTech, noteSS, comment, dateEva, idEva, idE);

            // Ajouter l'évaluation
            serviceEvaluation.ajouter(evaluation);

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Évaluation ajoutée avec succès !");

            // Rediriger vers l'affichage des évaluations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheevaluation.fxml")); // Assurez-vous que le chemin est correct
            Parent parent = loader.load();
            AfficheEvaluation controller = loader.getController();
            controller.setIdEntretien(idEntretien);
            commentaire.getScene().setRoot(parent);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les notes doivent être des nombres valides !");
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
