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

public class DatePublication {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timePicker;

    private LocalDateTime datePublication;

    @FXML
    public void initialize() {
        // Add predefined hours to the ComboBox
        timePicker.getItems().addAll(
                "08:00", "12:00", "23:59", "00:00"
        );
    }

    @FXML
    public void handleConfirm() {
        boolean isValid = true;

        // Validate the date and time selection
        if (datePicker.getValue() == null || timePicker.getValue() == null) {
            showAlert(AlertType.ERROR, "Error", "Please select both date and time.");
            isValid = false;
        } else {
            LocalDate date = datePicker.getValue();
            String time = timePicker.getValue();
            try {
                // Time format parsing
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTime = LocalTime.parse(time, formatter);
                this.datePublication = LocalDateTime.of(date, localTime);
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "The selected time is invalid. Please enter the correct time format.");
                isValid = false;
            }
        }

        // If the data is valid, close the window
        if (isValid) {
            Stage stage = (Stage) datePicker.getScene().getWindow();
            stage.close();
        }
    }

    // Getter for the selected date and time
    public LocalDateTime getSelectedDateTime() {
        return datePublication;
    }

    // Set a previously selected date and time
    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;

        if (datePublication != null) {
            // Pre-fill DatePicker and ComboBox with the existing date and time
            datePicker.setValue(datePublication.toLocalDate());
            timePicker.setValue(datePublication.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
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
