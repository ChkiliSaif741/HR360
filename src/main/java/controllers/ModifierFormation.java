package controllers;

import entities.Formation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceFormation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ModifierFormation{
    public void loadFormationdata(){
        titreFieldModif.setText(formation.getTitre());
        descFieldModif.setText(formation.getDescription());
        dureeFieldModif.setText(String.valueOf(formation.getDuree()));
        dateFieldModif.setText(formation.getDateFormation().toString());
    }

    @FXML
    private Button btnModif;

    @FXML
    private TextField dateFieldModif;

    @FXML
    private DatePicker datePickerModif;

    @FXML
    private TextField descFieldModif;

    @FXML
    private TextField dureeFieldModif;

    @FXML
    private Label labeldate;

    @FXML
    private Label labeldate1;

    @FXML
    private Label labelheure;

    @FXML
    private Label labellienmeet;

    @FXML
    private Label labellocalisation;

    private Formation formation;

    @FXML
    private TextField titreFieldModif;

    @FXML
    void ModifierFormation(ActionEvent event) {
        try {
            // Mettre à jour les informations de l'formation
            //formation.setDate(datePickerMod.getValue());
            formation.setTitre(titreFieldModif.getText());
            formation.setDescription(descFieldModif.getText());
            formation.setDuree(Integer.parseInt(dureeFieldModif.getText()));
            formation.setDateFormation(dateFieldModif.getText());


            // Enregistrer les modifications dans la base de données
            ServiceFormation serviceFormation = new ServiceFormation();
            serviceFormation.modifier(formation);

            // Fermer la fenêtre de modification
            Stage currentStage = (Stage) titreFieldModif.getScene().getWindow();
            currentStage.close();

            // Ouvrir la fenêtre afficheFormation.fxml
            ouvrirAfficheFormation();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        loadFormationdata();
    }







    private void ouvrirAfficheFormation() throws IOException {
        // Charger le fichier FXML de la fenêtre afficheFormation.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formations.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (Stage)
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Liste des formations");

        // Afficher la fenêtre
        stage.show();
    }



}




