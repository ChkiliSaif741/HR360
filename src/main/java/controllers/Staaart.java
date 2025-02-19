package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class Staaart {

    @FXML
    private AnchorPane anchorPane;

    // Method to navigate to the ListeOffres scene
    @FXML
    private void goToListeOffres() {
        try {
            // Load the FXML for the new scene (e.g., ListeOffres.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeOffresFront.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the current stage
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(scene);  // Set the new scene
            stage.show();  // Show the new scene

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du changement de scène.");
        }
    }

    // Helper method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
