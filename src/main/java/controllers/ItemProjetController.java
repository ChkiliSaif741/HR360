package controllers;

import entities.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ItemProjetController {

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

    private Projet projet;

    public void SetData(Projet projet) {
        this.projet = projet;
        nomProjet.setText(projet.getNom());
        dateDebut.setText("Date début: "+projet.getDateDebut().toString());
        dateFin.setText("Date fin: "+projet.getDateFin().toString());
        descriptionProjet.setText(projet.getDescription());
    }

}
