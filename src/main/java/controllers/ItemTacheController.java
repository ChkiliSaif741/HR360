package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.sql.SQLException;

public class ItemTacheController {

    private AffichageTacheController parentController;

    public void setParentController(AffichageTacheController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private VBox contentBox;

    @FXML
    private VBox IconContainerStatus;

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
    private FontAwesomeIcon IconStatus;

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
                IconStatus.setGlyphName("SPINNER");
                IconContainerStatus.getStyleClass().add("en-cours");
                break;
            case "Terminée":
                statutTache.getStyleClass().add("termine");
                IconStatus.setGlyphName("CHECK");
                IconContainerStatus.getStyleClass().add("termine");
                break;
            case "A faire":
                statutTache.getStyleClass().add("en-retard");
                IconStatus.setGlyphName("TASKS");
                IconContainerStatus.getStyleClass().add("en-retard");
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
            parentController.loadTasks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ModifTache(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        try {
            Parent parent = loader.load();
            Controller controller = loader.getController();
            ModifTacheController ModifController = controller.loadPage("/ModifTache.fxml").getController();
            ModifController.setDateStart(tache.getDateDebut());
            ModifController.setDateEnd(tache.getDateFin());
            ModifController.setDescriptionTF(descriptionTache.getText());
            ModifController.setNomTF(nomTache.getText());
            ModifController.setStatutDD(tache.getStatut());
            ModifController.setIdProjet(tache.getIdProjet());
            ModifController.setIdTache(tache.getId());
            nomTache.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
