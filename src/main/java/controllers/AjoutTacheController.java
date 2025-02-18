package controllers;

import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjoutTacheController {

    private int idProjet;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateStart;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField nomTF;

    @FXML
    void AjouterTache(ActionEvent event) {
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
            ServiceTache serviceTache = new ServiceTache();
            Tache tache = new Tache(nomTF.getText(), descriptionTF.getText(), Date.valueOf(DateDebut), Date.valueOf(DateFin), TacheStatus.A_FAIRE, idProjet);
            try {
                serviceTache.ajouter(tache);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Tache a ete ajouté avec succes !");
                alert.show();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageTache.fxml"));
                Parent parent=loader.load();
                AffichageTacheController controller = loader.getController();
                controller.setIdProjet(idProjet);
                nomTF.getScene().setRoot(parent);
            } catch (SQLException | IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }
}
