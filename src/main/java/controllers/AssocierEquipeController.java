package controllers;

import com.dlsc.gemsfx.AutoscrollListView;
import entities.Equipe;
import entities.Projet;
import entities.ProjetEquipe;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import services.EquipeService;
import services.ProjetEquipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.ServiceChargeTravail;
import services.ServiceProjet;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssocierEquipeController {
    @FXML
    private AutoscrollListView<String> equipeListView;
    @FXML
    private Button associerButton;

    private List<Equipe> equipes;

    private EquipeService equipeService= new EquipeService();
    private ProjetEquipeService projetEquipeService = new ProjetEquipeService();
    private int idProjet; // ID du projet à associer


    @FXML
    public void loadlistView() {
        try {
            equipes = equipeService.getAllEquipes();
            List<String> teamNames = new ArrayList<>();
            for (Equipe equipe : equipes) {
                teamNames.add(equipe.getNom());
            }
            ObservableList<String> equipeObservableList = FXCollections.observableArrayList(teamNames);
            equipeListView.setItems(equipeObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        associerButton.setOnAction(event -> associerEquipe());
    }

    private void associerEquipe() {
        int selectedEquipe = equipeListView.getSelectionModel().getSelectedIndex();
        if (selectedEquipe == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez choisir une option");
            alert.show();
            return;
        }
            try {
                ServiceChargeTravail serviceChargeTravail = new ServiceChargeTravail();
                ServiceProjet serviceProjet = new ServiceProjet();
                Projet projet = serviceProjet.getById(idProjet);
                EquipeService equipeService = new EquipeService();
                Equipe equipe=equipeService.getEquipe(equipes.get(selectedEquipe).getId());
                double charge=serviceChargeTravail.calculerChargeMaximale(projet.getDateDebut().toLocalDate(),projet.getDateFin().toLocalDate(),equipe);
                if (charge!=0)
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(null);
                    int c=(int)charge;
                    alert.setContentText("Cette equipe a "+c+" projet(s) dans cette durees!Vous etes sur de continuer?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        projetEquipeService.assignProjetToEquipe(idProjet, equipes.get(selectedEquipe).getId());
                        ((Stage) associerButton.getScene().getWindow()).close();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        loadlistView();
    }

}
