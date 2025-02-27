package controllers;

import com.jfoenix.controls.JFXTextArea;
import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Callback;
import services.ServiceProjet;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ModifProjetController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDatesLimits();
    }

    private int idProjet;
    @FXML
    private Button ModifProjet;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateStart;

    @FXML
    private JFXTextArea descriptionTF;

    @FXML
    private TextField nomTF;

    @FXML
    void ModifProjet(ActionEvent event) {
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
        }
        else {
            ServiceProjet serviceProjet = new ServiceProjet();
            Projet projet = new Projet(idProjet, nomTF.getText(), descriptionTF.getText(), Date.valueOf(DateDebut), Date.valueOf(DateFin));
            try {
                serviceProjet.modifier(projet);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Projet a ete Mis a jour avec succes !");
                alert.show();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
                Parent parent = loader.load();
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
    }
    public void setDatesLimits()
    {
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-text-fill: #3a3a3a;");
                        }
                    }
                };
            }
        };
        dateStart.setDayCellFactory(dayCellFactory);
        dateEnd.setDayCellFactory(dayCellFactory);
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
