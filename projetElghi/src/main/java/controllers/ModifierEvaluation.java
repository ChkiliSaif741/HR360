package controllers;

import entities.Evaluation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import services.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierEvaluation implements Initializable {
    @javafx.fxml.FXML
    private ComboBox<Integer> idMod;
    @javafx.fxml.FXML
    private DatePicker dateEvaluationMod;
    @javafx.fxml.FXML
    private TextField noteSoftSkillsMod;
    //@javafx.fxml.FXML
    //private ComboBox<Integer> idEntretienMod;
    @javafx.fxml.FXML
    private TextField noteTechniqueMod;
    @javafx.fxml.FXML
    private TextField commentaireMod;
    @javafx.fxml.FXML
    private Button ModEvaluation;

    private int idEntretien;

    private int idEvaluation;



    private ServiceEvaluation serviceEvaluation; // Service pour gérer les évaluations
    private Evaluation evaluation;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceEvaluation = new ServiceEvaluation();
        //chargerIdEntretien(); // Charger les ID des entretiens dans la ComboBox
        chargerIdEvaluateur(); // Charger les ID des évaluateurs dans la ComboBox

        try {
            //idEntretienMod.getItems().addAll(new ServiceEvaluation().getIdsEntretien());
            idMod.getItems().addAll(new ServiceEvaluation().getIdsEvaluateur());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Méthode pour charger les ID des entretiens dans la ComboBox
    /*private void chargerIdEntretien() {
        try {
            List<Integer> idEntretiens = serviceEvaluation.getIdsEntretien();
            idEntretienMod.setItems(FXCollections.observableArrayList(idEntretiens));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les ID des entretiens : " + e.getMessage());
        }
    }*/

    // Méthode pour charger les ID des évaluateurs dans la ComboBox
    private void chargerIdEvaluateur() {
        try {
            List<Integer> idEvaluateurs = serviceEvaluation.getIdsEvaluateur();
            idMod.setItems(FXCollections.observableArrayList(idEvaluateurs));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les ID des évaluateurs : " + e.getMessage());
        }
    }

    // Méthode pour modifier l'évaluation
    @FXML
    public void ModifierEvaluation(ActionEvent actionEvent) {
        try {
            // Récupérer les valeurs des champs
            float noteTech = Float.parseFloat(noteTechniqueMod.getText());
            float noteSS = Float.parseFloat(noteSoftSkillsMod.getText());
            LocalDate dateEva = dateEvaluationMod.getValue();
            String comment = commentaireMod.getText();
            int idEva = idEntretien; // Utiliser l'idEntretien déjà défini
            int idE = idMod.getValue(); // Récupérer l'ID de l'évaluateur sélectionné

            // Créer l'objet Evaluation avec l'ID de l'évaluation à modifier
            Evaluation evaluation = new Evaluation(idEvaluation, noteTech, noteSS, comment, dateEva, idEva, idE);

            // Modifier l'évaluation dans la base de données
            serviceEvaluation.modifier(evaluation);

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Évaluation modifiée avec succès !");

            // Rediriger vers l'affichage des évaluations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheevaluation.fxml"));
            Parent parent = loader.load();
            AfficheEvaluation controller = loader.getController();
            controller.setIdEntretien(idEntretien); // Transmettre l'idEntretien à AfficheEvaluation
            commentaireMod.getScene().setRoot(parent);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les notes doivent être des nombres valides !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la modification de l'évaluation : " + e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // Méthodes pour initialiser les champs avec les données de l'évaluation sélectionnée
    public void setNoteTechniqueMod(float noteTechnique) {
        this.noteTechniqueMod.setText(String.valueOf(noteTechnique));
    }

    public void setNoteSoftSkillsMod(float noteSoftSkills) {
        this.noteSoftSkillsMod.setText(String.valueOf(noteSoftSkills));
    }

    public void setCommentaireMod(String commentaire) {
        this.commentaireMod.setText(commentaire);
    }

    public void setDateEvaluationMod(LocalDate dateEvaluation) {
        this.dateEvaluationMod.setValue(dateEvaluation);
    }

    /*public void setIdEntretienMod(int idEntretien) {
        this.idEntretienMod.setValue(idEntretien);
    }*/

    public void setIdMod(int id) {
        this.idMod.setValue(id);
    }

    public void setIdEvaluation(int idEvaluation) {
        this.idEvaluation = idEvaluation;
    }
    AfficheEvaluation afficheEvaluation;
    public void setIdEntretien(int idEntretien) {
        this.idEntretien = idEntretien;
    }

    public int getIdEntretien(int idEntretien) {
        return this.idEntretien;
    }

    public int getIdEvaluation(int idEvaluation) {
        return this.idEvaluation;
    }

    // Méthode pour charger les données de l'évaluation à modifier
    public void setEvaluationData(Evaluation evaluation) {
        this.evaluation = evaluation;
        setIdEvaluation(evaluation.getIdEvaluation());
        setNoteTechniqueMod(evaluation.getNoteTechnique());
        setNoteSoftSkillsMod(evaluation.getNoteSoftSkills());
        setCommentaireMod(evaluation.getCommentaire());
        setDateEvaluationMod(evaluation.getDateEvaluation());
        setIdEntretien(evaluation.getIdEntretien());
        setIdMod(evaluation.getId());
        this.idEntretien = evaluation.getIdEntretien();

    }






    /*public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
        idEvaluation = evaluation.getIdEvaluation();
        idEntretien=evaluation.getIdEntretien();
        dateEvaluationMod.setValue(evaluation.getDateEvaluation());
        noteTechniqueMod.setText(String.valueOf(evaluation.getNoteTechnique()));
        noteSoftSkillsMod.setText(String.valueOf(evaluation.getNoteSoftSkills()));
        commentaireMod.setText(evaluation.getCommentaire());
        idMod.setValue(evaluation.getId());

    }*/


}
