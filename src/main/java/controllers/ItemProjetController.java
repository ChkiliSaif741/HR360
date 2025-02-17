package controllers;

import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.ServiceProjet;

import java.sql.SQLException;

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
    private Text descriptionProjet;

    @FXML
    private Label nomProjet;

    @FXML
    void DeleteProjet(ActionEvent event) {
        ServiceProjet serviceProjet = new ServiceProjet();
        try {
            serviceProjet.supprimer(projet.getId());
            parentController.refresh(); // Rafraîchir la vue après la suppression
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ModifProjet(ActionEvent event) {

    }

    private Projet projet;

    public void SetData(Projet projet) {
        this.projet = projet;
        nomProjet.setText(projet.getNom());
        dateDebut.setText("Date début: "+projet.getDateDebut().toString());
        dateFin.setText("Date fin: "+projet.getDateFin().toString());
        descriptionProjet.setText(projet.getDescription());
    }

}
