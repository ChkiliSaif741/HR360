package controllers;

import entities.Entretien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceEntretien;
import utils.type;
import utils.statut;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ModifierEntretien {

    @FXML
    private TextField lienMeetFieldMod;
    @FXML
    private ComboBox<type> typeComboBoxMod;
    @FXML
    private ComboBox<Integer> idCandidatureComboBoxMod;
    @FXML
    private TextField heureFieldMod;
    @FXML
    private ComboBox<statut> statutComboBoxMod;
    @FXML
    private DatePicker datePickerMod;
    @FXML
    private TextField localisationFieldMod;

    private Entretien entretien;

    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
        datePickerMod.setValue(entretien.getDate());
        heureFieldMod.setText(entretien.getHeure().toString());
        typeComboBoxMod.setValue(entretien.getType());
        statutComboBoxMod.setValue(entretien.getStatut());
        lienMeetFieldMod.setText(entretien.getLien_meet());
        localisationFieldMod.setText(entretien.getLocalisation());
        idCandidatureComboBoxMod.setValue(entretien.getIdCandidature());
    }

    @FXML
    public void initialize() {
        typeComboBoxMod.getItems().setAll(type.values());
        statutComboBoxMod.getItems().setAll(statut.values());
        try {
            idCandidatureComboBoxMod.getItems().addAll(new ServiceEntretien().getIdsCandidature());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ModifierEntretien(ActionEvent actionEvent) {
        try {
            // Mettre à jour les informations de l'entretien
            entretien.setDate(datePickerMod.getValue());
            entretien.setHeure(LocalTime.parse(heureFieldMod.getText()));
            entretien.setType(typeComboBoxMod.getValue());
            entretien.setStatut(statutComboBoxMod.getValue());
            entretien.setLien_meet(lienMeetFieldMod.getText());
            entretien.setLocalisation(localisationFieldMod.getText());
            entretien.setIdCandidature(idCandidatureComboBoxMod.getValue());



            // Enregistrer les modifications dans la base de données
            ServiceEntretien serviceEntretien = new ServiceEntretien();
            serviceEntretien.modifier(entretien);

            // Fermer la fenêtre de modification
            Stage currentStage = (Stage) datePickerMod.getScene().getWindow();
            currentStage.close();

            // Ouvrir la fenêtre afficheEntretien.fxml
            ouvrirAfficheEntretien();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirAfficheEntretien() throws IOException {
        // Charger le fichier FXML de la fenêtre afficheEntretien.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheEntretien.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène
        Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (Stage)
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Liste des entretiens");

        // Afficher la fenêtre
        stage.show();
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