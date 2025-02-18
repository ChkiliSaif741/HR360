package controllers;

import entities.Tache;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

    public void setTaskData(Tache tache) {
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
}
