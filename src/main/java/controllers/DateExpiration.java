package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateExpiration {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timePicker;

    private LocalDateTime dateExpiration;

    @FXML
    public void initialize() {
        // Ajouter des heures prédéfinies dans le ComboBox
        timePicker.getItems().addAll("08:00", "12:00", "23:59", "00:00");

        // Si une date et une heure sont déjà sélectionnées, les remplir dans les champs
        if (dateExpiration != null) {
            datePicker.setValue(dateExpiration.toLocalDate());
            timePicker.setValue(dateExpiration.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    @FXML
    public void handleConfirm() {
        boolean isValid = true;

        // Valider la sélection de la date et de l'heure
        if (datePicker.getValue() == null || timePicker.getValue() == null) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner une date et une heure.");
            isValid = false;
        } else {
            LocalDate date = datePicker.getValue();
            String time = timePicker.getValue();
            try {
                // Analyse du format de l'heure
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTime = LocalTime.parse(time, formatter);
                this.dateExpiration = LocalDateTime.of(date, localTime);  // Enregistrer la date et l'heure sélectionnées
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur", "Le format de l'heure sélectionnée est invalide. Veuillez entrer un format d'heure correct.");
                isValid = false;
            }
        }

        // Si les données sont valides, fermer la fenêtre
        if (isValid) {
            System.out.println("Les données sont valides. Fermeture de la fenêtre...");
            Stage stage = (Stage) datePicker.getScene().getWindow();
            if (stage != null) {
                stage.close();  // Fermer le stage
            } else {
                System.out.println("Erreur : Stage est null.");
            }
        } else {
            System.out.println("Les données sont invalides. La fenêtre ne se fermera pas.");
        }
    }

    // Getter pour récupérer la date et l'heure sélectionnées
    public LocalDateTime getSelectedDateTime() {
        return dateExpiration;
    }

    // Définir la date et l'heure d'expiration précédemment sélectionnées
    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;

        // Si une date d'expiration a été définie, la pré-remplir dans les champs
        if (dateExpiration != null) {
            datePicker.setValue(dateExpiration.toLocalDate());
            timePicker.setValue(dateExpiration.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
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
