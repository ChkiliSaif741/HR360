package controllers;

import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceProjet;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class ModifProjetController {

    private int idProjet;
    @FXML
    private Button ModifProjet;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateStart;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField nomTF;

    @FXML
    void ModifProjet(ActionEvent event) {
        LocalDate DateDebut = dateStart.getValue();
        LocalDate DateFin = dateEnd.getValue();
        ServiceProjet serviceProjet = new ServiceProjet();
        Projet projet = new Projet(idProjet,nomTF.getText(), descriptionTF.getText(), Date.valueOf(DateDebut), Date.valueOf(DateFin));
        try {
            serviceProjet.modifier(projet);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Projet a ete Mis a jour avec succes !");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
            Parent parent=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AffichageProjet.fxml");
            nomTF.getScene().setRoot(parent);
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd.setValue(LocalDate.parse(dateEnd.toString()));
    }

    public void setDateStart(Date dateStart) {
        this.dateStart.setValue(LocalDate.parse(dateStart.toString()));
    }

    public void setDescriptionTF(String descriptionTF) {
        this.descriptionTF.setText(descriptionTF);
    }

    public void setNomTF(String nomTF) {
        this.nomTF.setText(nomTF);
    }
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }
}
