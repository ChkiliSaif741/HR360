package controllers;

import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.ServiceReservation;
import services.ServiceRessource;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class FormulaireAjoutReservationEMPController implements Initializable {

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;



    @FXML
    private Button btnRetour;

    private int idRessource;

    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final ServiceRessource serviceRessource = new ServiceRessource();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void ajouterReservation(ActionEvent event) {
        try {
            if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate dateDebutLocal = dateDebutPicker.getValue();
            LocalDate dateFinLocal = dateFinPicker.getValue();

            if (dateDebutLocal.isBefore(today) || dateFinLocal.isBefore(today)) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur de date", "Les dates doivent être égales ou postérieures à aujourd'hui.");
                return;
            }

            Date dateDebut = Date.valueOf(dateDebutLocal);
            Date dateFin = Date.valueOf(dateFinLocal);


            if (!dateDebut.before(dateFin)) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur de date", "La date de début doit être antérieure à la date de fin.");
                return;
            }

            Reservation reservation = new Reservation(idRessource, dateDebut, dateFin, 1);

            if (!serviceReservation.estDisponible(reservation)) {
                afficherAlerte(Alert.AlertType.ERROR, "Ressource indisponible", "Cette ressource est déjà réservée pour la période sélectionnée.");
                return;
            }

            serviceReservation.ajouter(reservation);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès !");

        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réservation.");
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerAjout() {
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
    }

    @FXML
    private void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
            Parent parent=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessourceEMP.fxml");
            dateDebutPicker.getScene().setRoot(parent);

        } catch (IOException e) {
            //showErrorAlert("Erreur", "Une erreur est survenue lors du chargement de la page.");
        }
    }

    private boolean isValidUser(String input) {
        return Pattern.matches("^[A-Za-z0-9\\s]+$", input);
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }

    public void setIdRessource(int idRessource) {
        this.idRessource = idRessource;
    }
}
