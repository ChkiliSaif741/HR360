package controllers;

import entities.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import services.ServiceReservation;
import services.ServiceRessource;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class FormulaireAjoutReservationController implements Initializable {


    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField utilisateurField;

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
            if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null || utilisateurField.getText().isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            Date dateDebut = Date.valueOf(dateDebutPicker.getValue());
            Date dateFin = Date.valueOf(dateFinPicker.getValue());
            String utilisateur = utilisateurField.getText().trim();


            if (!dateDebut.before(dateFin)) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur de date", "La date de début doit être antérieure à la date de fin.");
                return;
            }


            if (!isValidUser(utilisateur)) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur de saisie", "L'utilisateur ne doit pas contenir de caractères spéciaux !");
                return;
            }

            Reservation reservation = new Reservation(idRessource, dateDebut, dateFin, utilisateur);


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
        utilisateurField.clear();
    }

    @FXML
    private void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessource.fxml");
            utilisateurField.getScene().setRoot(parent);

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
