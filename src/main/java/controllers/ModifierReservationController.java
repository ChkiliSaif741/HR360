package controllers;

import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceReservation;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ModifierReservationController {



    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    private Reservation reservation;

    private ServiceReservation serviceReservation = new ServiceReservation();


    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        dateDebutPicker.setValue(reservation.getDateDebut().toLocalDate());
        dateFinPicker.setValue(reservation.getDateFin().toLocalDate());
    }

    public void updateReservation() {
        // Vérifier si les champs sont vides
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null ) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        Date nouveauDebut = Date.valueOf(dateDebutPicker.getValue());
        Date nouveauFin = Date.valueOf(dateFinPicker.getValue());
        int nouvelIduser=1;


        if (nouveauDebut.after(nouveauFin) || nouveauDebut.equals(nouveauFin)) {
            showErrorAlert("Erreur de saisie", "La date de début doit être antérieure à la date de fin !");
            return;
        }



        try {
            Reservation rs = new Reservation(reservation.getId(), reservation.getIdRessource(), nouveauDebut, nouveauFin, nouvelIduser);
            serviceReservation.modifier(rs);
            showConfirmationAlert("Succès", "Réservation mise à jour avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            AfficherReservationController controller1 = controller.loadPage("/AfficherReservation.fxml").getController();
            controller1.setIdRessource(reservation.getIdRessource());
            dateDebutPicker.getScene().setRoot(parent);

        } catch (SQLException | IOException e) {
            showErrorAlert("Erreur", "Erreur lors de la modification de la réservation : " + e.getMessage());
        }
    }

    public void annulerModification(ActionEvent actionEvent) {
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
    }

    public void retour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            AfficherReservationEMPController controller1=controller.loadPage("/AfficherReservationEMP.fxml").getController();
            controller1.setIdRessource(reservation.getIdRessource());
            dateDebutPicker.getScene().setRoot(parent);
        } catch (IOException e) {
            showErrorAlert("Erreur", "Une erreur est survenue lors du chargement de la page.");
        }
    }

    private boolean isValidUser(String input) {
        return Pattern.matches("^[A-Za-z0-9\\s]+$", input);
    }

    private void showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
