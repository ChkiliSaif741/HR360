package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceTache;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ItemTacheEMPController {

    private AffichageTacheEMPController parentController;

    public void setParentController(AffichageTacheEMPController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private HBox IconContainerStatus;

    @FXML
    private Label nomTache;

    @FXML
    private Label statutTache;

    @FXML
    private FontAwesomeIcon IconStatus;

    @FXML
    private AnchorPane taskCard;

    @FXML
    private HBox listItemConatianer;


    private Tache tache;

    public void setTaskData(Tache tache) {
        this.tache = tache;
        nomTache.setText(tache.getNom());
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
    void ModifTache(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyStatusTache.fxml"));
            Parent root = loader.load();

            // Get controller and pass task ID
            ModifyStatusController controller = loader.getController();
            controller.setTask(tache);

            // Open a new window
            Stage stage = new Stage();
            stage.setTitle("Modify Task Status");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            parentController.loadTasks();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void ResetTacheColor()
    {
        listItemConatianer.getStyleClass().add("list-item");
    }

    void SetSelectedTacheColor()
    {
        listItemConatianer.getStyleClass().add("list-item-selected");
    }


    public Tache getTache() {
        return tache;
    }
}
