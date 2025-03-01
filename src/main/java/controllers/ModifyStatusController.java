package controllers;

import entities.Tache;
import entities.TacheStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import services.ServiceTache;

import java.sql.SQLException;

public class ModifyStatusController {
    @FXML private RadioButton radioAFaire, radioEnCours, radioTerminee;
    @FXML private ToggleGroup statusGroup;

    private Tache task; // Will be set when opening the window

    public void setTask(Tache task) {
        this.task = task;
    }

    @FXML
    public void initialize() {
        // Manually assign ToggleGroup
        statusGroup = new ToggleGroup();
        radioAFaire.setToggleGroup(statusGroup);
        radioEnCours.setToggleGroup(statusGroup);
        radioTerminee.setToggleGroup(statusGroup);

        // Set default selection
        statusGroup.selectToggle(radioAFaire);
    }

    @FXML
    private void saveStatus() {
        String newStatus = ((RadioButton) statusGroup.getSelectedToggle()).getText();
        ServiceTache serviceTache = new ServiceTache();

        try {
            task.setStatut(TacheStatus.fromValue(newStatus));
            serviceTache.updateTaskStatus(task);
            showAlert("Success", "Task status updated successfully!");

            // Close the window after saving
            Stage stage = (Stage) radioAFaire.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update task status!");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
