package controllers;

import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceProjet;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

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
    void AjouterProjet(ActionEvent event) {
            LocalDate DateDebut = dateStart.getValue();
            LocalDate DateFin = dateEnd.getValue();
            if (nomTF.getText().isEmpty() || descriptionTF.getText().isEmpty() || DateDebut == null || DateFin == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Veuillez remplir tous les champs !");
                alert.show();
            } else if (DateDebut.isAfter(DateFin)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("La date de début est avant la date fin !");
                alert.show();
            } else {
                ServiceProjet serviceProjet = new ServiceProjet();
                Projet projet = new Projet(nomTF.getText(), descriptionTF.getText(), Date.valueOf(DateDebut), Date.valueOf(DateFin));
                try {
                    serviceProjet.ajouter(projet);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setContentText("Projet a ete ajouté avec succes !");
                    alert.show();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
                    Parent parent=loader.load();
                    Controller con=loader.getController();
                    con.loadPage("/AffichageProjet.fxml");
                    nomTF.getScene().setRoot(parent);
                } catch (SQLException | IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Information");
                    alert.setContentText(e.getMessage());
                    alert.show();
                }
            }
    }
}
