package controllers;

import entities.Entretien;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import entities.Evaluation;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ServiceEntretien;
import services.ServiceEvaluation;
import controllers.AfficheEvaluation;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ItemEvaluation {

    private AfficheEvaluation afficheEvaluation ;
    @FXML
    private AnchorPane anchorEva;

    public void setEvaluation(AfficheEvaluation afficheEvaluation) {
        this.afficheEvaluation = afficheEvaluation;
    }

    @javafx.fxml.FXML
    private Label dateEvaluation;
    @javafx.fxml.FXML
    private Label noteTechnique;
    @javafx.fxml.FXML
    private Label noteSoftSkills;
    @javafx.fxml.FXML
    private Label commentaire;

    private Evaluation evaluation;

    private int idEntretien;
    private int idEvaluation;



    public void setData(Evaluation evaluation) {
        this.evaluation = evaluation;
        idEntretien = evaluation.getIdEntretien();
        noteTechnique.setText("" + evaluation.getNoteTechnique());
        noteSoftSkills.setText("" + evaluation.getNoteSoftSkills());
        commentaire.setText(evaluation.getCommentaire());
        dateEvaluation.setText("" + evaluation.getDateEvaluation().toString());
    }


    @javafx.fxml.FXML
    public void modifierEva(Event event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvaluation.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
         ModifierEvaluation ModifEvaluation = controller.loadPage("/modifierEvaluation.fxml").getController();
        //controller.getIdEntretien(idEntretien);
        //controller.getIdEvaluation(idEvaluation);
        ModifEvaluation.setIdEntretien(idEntretien);
        ModifEvaluation.setEvaluationData(evaluation); // Passer l'objet Evaluation sélectionné

        // Passer l'entretien sélectionné au contrôleur de ajoute

        //controller.setIdEntretien(idEntretien);
        commentaire.getScene().setRoot(root);



    }

    @javafx.fxml.FXML
    public void deleteEva(Event event) {
        try {
            // Créer une instance du service d'évaluation
            ServiceEvaluation serviceEvaluation = new ServiceEvaluation();

            // Supprimer l'évaluation par son ID
            serviceEvaluation.supprimer(evaluation.getIdEvaluation());
            afficheEvaluation.refresh();




        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
