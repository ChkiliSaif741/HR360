package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.ServiceTache;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ItemTacheController {

    private AffichageTacheController parentController;

    public void setParentController(AffichageTacheController parentController) {
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
    void DeleteTache(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Voulez vous vraiment supprimer "+tache.getNom()+" ?");
        Optional<ButtonType> result = alert.showAndWait();
        ServiceTache serviceTache = new ServiceTache();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceTache.supprimer(tache.getId());
                parentController.setIndiceTacheSelected(0);
                parentController.loadTasks();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ModifTache(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        try {
            Parent parent = loader.load();
            Controller controller = loader.getController();
            ModifTacheController ModifController = controller.loadPage("/ModifTache.fxml").getController();
            ModifController.setDateStart(tache.getDateDebut());
            ModifController.setDateEnd(tache.getDateFin());
            ModifController.setDescriptionTF(tache.getDescription());
            ModifController.setNomTF(nomTache.getText());
            ModifController.setStatutDD(tache.getStatut());
            ModifController.setIdProjet(tache.getIdProjet());
            ModifController.setIdTache(tache.getId());
            nomTache.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
