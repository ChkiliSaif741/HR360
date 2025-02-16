package controllers;

import entities.Personne;
import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServicePersonne;
import services.ServiceProjet;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class AjoutProjetController {

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateStart;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField nomTF;

    @FXML
    void AfficherProjet(ActionEvent event) {

    }

    @FXML
    void AjouterProjet(ActionEvent event) {
        ServiceProjet serviceProjet = new ServiceProjet();
        Projet projet = new Projet(nomTF.getText(),descriptionTF.getText(), Date.valueOf(dateStart.getValue()),Date.valueOf(dateEnd.getValue()));
        try {
            serviceProjet.ajouter(projet);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Projet a ete ajouté avec succes !");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

}
