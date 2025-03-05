package controllers;

import com.jfoenix.controls.JFXTextArea;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Callback;
import services.ServiceProjet;
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
    private JFXTextArea descriptionTF;

    @FXML
    private TextField nomTF;

    private int idTache;

    private int idProjet;

    @FXML
    void ModifTache(ActionEvent event) {
        LocalDate DateDebut = dateStart.getValue();
        LocalDate DateFin = dateEnd.getValue();
        ServiceProjet serviceProjet = new ServiceProjet();
        try {
            Projet projet=serviceProjet.getById(idProjet);
            LocalDate maxDate=projet.getDateFin().toLocalDate();
            LocalDate minDate=projet.getDateDebut().toLocalDate();
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
        Tache tache = new Tache(idTache,nomTF.getText(), descriptionTF.getText(), Date.valueOf(DateDebut), Date.valueOf(DateFin),TacheStatus.fromValue(StatutDD.getValue()),idProjet);
        try {
            serviceTache.modifier(tache);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Tache a ete Mis a jour avec succes !");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    public void setDatesLimits(int idProjet){
        ServiceProjet serviceProjet = new ServiceProjet();
        try {
            Projet projet=serviceProjet.getById(idProjet);
            LocalDate startDate = projet.getDateDebut().toLocalDate();
            LocalDate endDate = projet.getDateFin().toLocalDate();
            Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isAfter(endDate) || item.isBefore(startDate)) {
                                setDisable(true);
                                setStyle("-fx-text-fill: #3a3a3a;");
                            }
                        }
                    };
                }
            };
            dateStart.setDayCellFactory(dayCellFactory);
            dateEnd.setDayCellFactory(dayCellFactory);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        setDatesLimits(idProjet);
    }

    public void setIdTache(int idTache) {
        this.idTache = idTache;
    }
}
