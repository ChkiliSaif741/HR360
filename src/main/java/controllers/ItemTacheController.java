package controllers;

import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.ServiceProjet;
import services.ServiceTache;

import java.sql.SQLException;

public class ItemTacheController {

    private AffichageTacheController parentController;

    public void setParentController(AffichageTacheController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private VBox contentBox;

    @FXML
    private Label dateDebut;

    @FXML
    private Label dateFin;

    @FXML
    private Text descriptionTache;

    @FXML
    private Label nomTache;

    @FXML
    private Label statutTache;

    @FXML
    private AnchorPane taskCard;

    private Tache tache;

    public void setTaskData(Tache tache) {
        this.tache = tache;
        nomTache.setText(tache.getNom());
        descriptionTache.setText(tache.getDescription());
        this.dateDebut.setText("Début: " + tache.getDateDebut());
        this.dateFin.setText("Fin: " + tache.getDateFin());
        statutTache.setText(tache.getStatut().getValue());

        switch (tache.getStatut().getValue()) {
            case "En cours":
                statutTache.getStyleClass().add("en-cours");
                break;
            case "Terminée":
                statutTache.getStyleClass().add("termine");
                break;
            case "A faire":
                statutTache.getStyleClass().add("en-retard");
                break;
            default:
                statutTache.getStyleClass().add("termine"); // Par défaut
        }
    }
    @FXML
    void DeleteTache(ActionEvent event) {
        ServiceTache serviceTache = new ServiceTache();
        try {
            serviceTache.supprimer(tache.getId());
            parentController.loadTasks(); // Rafraîchir la vue après la suppression
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ModifTache(ActionEvent event) {

    }
}
