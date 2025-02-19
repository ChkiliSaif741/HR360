package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import services.ServiceCandidature;
import entities.Candidature;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

public class DateEntretien {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timePicker;

    private LocalDateTime dateEntretien;

    @FXML
    public void initialize() {
        // Ajouter des heures prédéfinies au ComboBox
        timePicker.getItems().addAll(
                "08:00","12:00", "23:59","00:00"

        );
    }

    @FXML
    public void handleConfirm() {
        boolean isValid = true;

        // Validation de la date et de l'heure
        if (datePicker.getValue() == null || timePicker.getValue() == null) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner la date et l'heure de l'offre.");
            isValid = false;
        } else {
            LocalDate dateEntretien = datePicker.getValue();
            String time = timePicker.getValue();
            try {
                // Format de l'heure
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTime = LocalTime.parse(time, formatter);
                this.dateEntretien = LocalDateTime.of(dateEntretien, localTime);
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur", "L'heure entrée est invalide. Veuillez entrer l'heure dans le format correct.");
                isValid = false;
            }
        }
    }

    // Ajoutez cette méthode pour obtenir la date et l'heure sélectionnées
    public LocalDateTime getSelectedDateTime() {
        return dateEntretien;
    }
    // Ajoutez cette méthode dans votre contrôleur pour recevoir la dateEntretien existante.
    public void setDateEntretien(LocalDateTime dateEntretien) {
        this.dateEntretien = dateEntretien;

        // Si la dateEntretien n'est pas nulle, préremplir les champs
        if (dateEntretien != null) {
            // Initialiser le DatePicker avec la date existante
            datePicker.setValue(dateEntretien.toLocalDate());

            // Initialiser le ComboBox avec l'heure existante
            timePicker.setValue(dateEntretien.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    @FXML
    private void annuler() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
