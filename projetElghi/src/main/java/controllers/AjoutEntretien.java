package controllers;

import entities.Entretien;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceEntretien;
import utils.statut;
import utils.type;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class AjoutEntretien implements Initializable {
    @FXML
    private TextField lienMeetField;
    @FXML
    private ComboBox typeComboBox;
    @FXML
    private TextField heureField;
    @FXML
    private TextField localisationField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox idCandidatureComboBox;
    @FXML
    private ComboBox statutComboBox;

    private ServiceEntretien serviceEntretien;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceEntretien = new ServiceEntretien();

        // Remplir les ComboBox avec les valeurs des énumérations
        typeComboBox.setItems(FXCollections.observableArrayList(type.values()));
        statutComboBox.setItems(FXCollections.observableArrayList(statut.values()));

        // Charger les idCandidature depuis la base de données
        chargerIdCandidature();
    }

    // Méthode pour charger les idCandidature dans la ComboBox
    private void chargerIdCandidature() {
        try {
            List<Integer> idCandidatures = serviceEntretien.getIdsCandidature(); // À implémenter dans ServiceEntretien
            idCandidatureComboBox.setItems(FXCollections.observableArrayList(idCandidatures));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les idCandidature : " + e.getMessage());
        }
    }

    @FXML
    public void AddEntretien(ActionEvent actionEvent) {
        // Récupérer les valeurs des champs
        LocalDate date = datePicker.getValue();
        String heureText = heureField.getText();
        type type = (utils.type) typeComboBox.getValue();
        statut statut = (utils.statut) statutComboBox.getValue();
        String lienMeet = lienMeetField.getText();
        String localisation = localisationField.getText();
        Integer idCandidature = (Integer) idCandidatureComboBox.getValue();

        // Contrôle de saisie
        /*if (date == null || heureText == null || heureText.isEmpty() || type == null || statut == null || idCandidature == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Tous les champs obligatoires doivent être remplis.");
            return;
        }*/

        // Vérifier si le type est "En_ligne" et que le lien Meet est vide
        if (type == type.En_ligne && (lienMeet == null || lienMeet.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le lien Meet est obligatoire pour un entretien en ligne.");
            return;
        }

        // Vérifier si le type est "Presentiel" et que la localisation est vide
        if (type == type.Presentiel && (localisation == null || localisation.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "La localisation est obligatoire pour un entretien présentiel.");
            return;
        }

        // Convertir l'heure en LocalTime
        LocalTime heure;
        try {
            heure = LocalTime.parse(heureText);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le format de l'heure est invalide. Utilisez HH:mm.");
            return;
        }

        // Créer l'objet Entretien
        Entretien entretien = new Entretien(date, heure, type, statut, lienMeet, localisation, idCandidature);

        // Ajouter l'entretien dans la base de données
        try {
            serviceEntretien.ajouter(entretien);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            reinitialiserChamps();


            // Fermer la fenêtre actuelle et ouvrir la fenêtre d'affichage
            Stage currentStage = (Stage) datePicker.getScene().getWindow();

            try {
                // Charger le fichier FXML de la fenêtre d'affichage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheentretien.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Créer une nouvelle fenêtre (Stage)
                Stage newStage = new Stage(); // Utiliser un nom différent pour éviter le conflit
                newStage.setScene(scene);
                newStage.setTitle("Affichage des entretiens");

                // Afficher la fenêtre
                newStage.show();

                // Fermer la fenêtre actuelle
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'affichage.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter l'entretien : " + e.getMessage());
        }
    }

    // Méthode pour réinitialiser les champs après l'ajout
    private void reinitialiserChamps() {
        datePicker.setValue(null);
        heureField.clear();
        typeComboBox.setValue(null);
        statutComboBox.setValue(null);
        lienMeetField.clear();
        localisationField.clear();
        idCandidatureComboBox.setValue(null);
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
