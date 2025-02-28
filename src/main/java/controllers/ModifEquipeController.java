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

public class ModifEquipeController {

    @FXML
    private TextField nomTF;

    private int idEquipe;

    @FXML
    void ModifEquipe(ActionEvent event) {
        if (nomTF.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.show();
        }
        else {
            EquipeService equipeService = new EquipeService();
            Equipe equipe = new Equipe(idEquipe, nomTF.getText());
            try {
                equipeService.updateEquipe(equipe);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Equipe a ete Mis a jour avec succes !");
                alert.show();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                Parent parent = loader.load();
                Controller controller = loader.getController();
                controller.loadPage("/AffichageEquipe.fxml");
                nomTF.getScene().setRoot(parent);
            } catch (SQLException | IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }
    public void setIdEquipe(int idEquipe) {
        this.idEquipe = idEquipe;
    }
    public void setNomTF(String nomTF) {
        this.nomTF.setText(nomTF);
    }
}
