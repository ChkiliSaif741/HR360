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

public class ItemProjetController {

    private AffichageProjetController parentController;

    public void setParentController(AffichageProjetController parentController) {
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

    @FXML
    void DeleteProjet(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Voulez vous vraiment supprimer "+projet.getNom()+" ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ServiceProjet serviceProjet = new ServiceProjet();
            try {
                serviceProjet.supprimer(projet.getId());
                parentController.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ModifProjet(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent parent = loader.load();
            Controller controller = loader.getController();
            ModifProjetController ModifController = controller.loadPage("/ModifProjet.fxml").getController();
            ModifController.setDateStart(projet.getDateDebut());
            ModifController.setDateEnd(projet.getDateFin());
            ModifController.setDescriptionTF(descriptionProjet.getText());
            ModifController.setNomTF(nomProjet.getText());
            ModifController.setIdProjet(projet.getId());
            nomProjet.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            DetailsProjetController detailsProjetController = controller.loadPage("/DetailsProjet.fxml").getController();
            detailsProjetController.setIdProjet(projet.getId());
            nomProjet.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
