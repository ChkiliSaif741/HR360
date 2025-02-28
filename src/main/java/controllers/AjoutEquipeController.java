package controllers;

import entities.Equipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.EquipeService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjoutEquipeController {

    @FXML
    private TextField nomTF;

    @FXML
    void AjouterEquipe(ActionEvent event) {
        if (nomTF.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.show();
        }
        else {
            EquipeService Equipeservice = new EquipeService();
            Equipe equipe = new Equipe(nomTF.getText());
            try {
                Equipeservice.addEquipe(equipe);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Equipe a ete ajouté avec succes !");
                alert.show();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                Parent parent=loader.load();
                Controller con=loader.getController();
                con.loadPage("/AffichageEquipe.fxml");
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


