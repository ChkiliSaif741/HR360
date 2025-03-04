package controllers;

import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import services.ServiceProjet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ItemProjetEMPController {

    private AffichageProjetEMPController parentController;

    public void setParentController(AffichageProjetEMPController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private Button btnDel;

    @FXML
    private Button btnMod;

    @FXML
    private VBox cardContainer;

    @FXML
    private Label dateDebut;

    @FXML
    private Label dateFin;

    @FXML
    private Label descriptionProjet;

    @FXML
    private Label nomProjet;


    private Projet projet;

    public void SetData(Projet projet) {
        this.projet = projet;
        nomProjet.setText(projet.getNom());
        dateDebut.setText(projet.getDateDebut().toString());
        dateFin.setText(projet.getDateFin().toString());
        descriptionProjet.setText(projet.getDescription());
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            DetailsProjetEMPController detailsProjetController = controller.loadPage("/DetailsProjetEMP.fxml").getController();
            detailsProjetController.setIdProjet(projet.getId());
            nomProjet.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
