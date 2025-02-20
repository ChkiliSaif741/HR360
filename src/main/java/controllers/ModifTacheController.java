package controllers;

import entities.Tache;
import entities.TacheStatus;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceTache;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class ModifTacheController {

    @FXML
    private ComboBox<String> StatutDD;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateStart;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField nomTF;

    private int idTache;

    private int idProjet;

    @FXML
    void ModifTache(ActionEvent event) {
        LocalDate DateDebut = dateStart.getValue();
        LocalDate DateFin = dateEnd.getValue();
        ServiceTache serviceTache = new ServiceTache();
        Tache tache = new Tache(idTache,nomTF.getText(), descriptionTF.getText(), Date.valueOf(DateDebut), Date.valueOf(DateFin),TacheStatus.fromValue(StatutDD.getValue()),idProjet);
        try {
            serviceTache.modifier(tache);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Tache a ete Mis a jour avec succes !");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
            Parent parent=loader.load();
            Controller controller0 = loader.getController();
            AffichageTacheController controller = controller0.loadPage("/AffichageTache.fxml").getController();
            controller.setIdProjet(idProjet);
            nomTF.getScene().setRoot(parent);
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void setStatutDD(TacheStatus statutDD) {
        StatutDD.setItems(FXCollections.observableArrayList("A faire", "En cours", "Terminée"));
        StatutDD.setValue(statutDD.getValue());
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

    public void setIdTache(int idTache) {
        this.idTache = idTache;
    }
}
