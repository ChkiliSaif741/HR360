package controllers;

import entities.Formation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceFormation;
import utils.alertMessage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjoutFormationController implements Initializable {


    @FXML
    private Button btnAdd;

    @FXML
    private TextField dateField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField descField;

    @FXML
    private TextField dureeField;

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

    @FXML
    private TextField titreField;
    ServiceFormation serviceFormation;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceFormation = new ServiceFormation();

        // Remplir les ComboBox avec les valeurs des énumérations

    }

    // Méthode pour réinitialiser les champs après l'ajout
    private void reinitialiserChamps() {
        //datePicker.setValue(null);
        dureeField.clear();
        titreField.clear();
        descField.clear();
        dateField.clear();
    }

    @FXML
    public void AddFormation(ActionEvent event) {
        alertMessage alert = new alertMessage();

        // Vérifier que tous les champs sont remplis
        if (titreField.getText().isEmpty() || descField.getText().isEmpty() || dureeField.getText().isEmpty() || dateField.getText().isEmpty()) {
            alert.errorMessage("Veuillez remplir tous les champs !");
            return;
        }

        // Récupérer les valeurs des champs
        String titre = titreField.getText();
        String description = descField.getText();
        int duree = Integer.parseInt(dureeField.getText());
        String date = dateField.getText();

        // Créer l'objet Formation
        Formation formation = new Formation(titre, description, duree, date);

        // Ajouter la formation dans la base de données
        try {
            serviceFormation.ajouter(formation);
            alert.successMessage("Formation ajoutée avec succès !");

            // Réinitialiser les champs après l'ajout
            reinitialiserChamps();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/FormationsRH.fxml");
            titreField.getScene().setRoot(parent);



        } catch (SQLException | IOException e) {
            alert.errorMessage("Erreur lors de l'ajout de la formation : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
