package controllers;

import entities.Reservation;
import entities.Sessions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import services.ServiceReservation;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModifierReservationEMPController {


    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    private Reservation reservation;

    private ServiceReservation serviceReservation = new ServiceReservation();


    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        //utilisateurField.setText(String.valueOf(reservation.getIduser()));
        dateDebutPicker.setValue(reservation.getDateDebut().toLocalDate());
        dateFinPicker.setValue(reservation.getDateFin().toLocalDate());

        configurerDatePickers();
    }

    public void updateReservation() {
        // Vérifier si les champs sont vides
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        Date nouveauDebut = Date.valueOf(dateDebutPicker.getValue());
        Date nouveauFin = Date.valueOf(dateFinPicker.getValue());
        int nouvelIduser = Sessions.getInstance().getIdUtilisateur();


        if (nouveauDebut.after(nouveauFin) || nouveauDebut.equals(nouveauFin)) {
            showErrorAlert("Erreur de saisie", "La date de début doit être antérieure à la date de fin !");
            return;
        }



        try {
            Reservation rs = new Reservation(reservation.getId(), reservation.getIdRessource(), nouveauDebut, nouveauFin,nouvelIduser);
            serviceReservation.modifier(rs);
            showConfirmationAlert("Succès", "Réservation mise à jour avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            AfficherReservationEMPController controller1 = controller.loadPage("/AfficherReservationEMP.fxml").getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
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



    private void configurerDatePickers() {
        try {
            // Récupérer les dates réservées pour la ressource spécifique
            List<Date> datesReservees = serviceReservation.getDatesReservees(reservation.getIdRessource());

            // Convertir en LocalDate
            Set<LocalDate> datesOccupees = datesReservees.stream()
                    .map(Date::toLocalDate)
                    .filter(date -> !(date.isEqual(reservation.getDateDebut().toLocalDate())
                            || date.isEqual(reservation.getDateFin().toLocalDate())
                            || (date.isAfter(reservation.getDateDebut().toLocalDate()) && date.isBefore(reservation.getDateFin().toLocalDate()))))
                    .collect(Collectors.toSet());

            // Désactiver uniquement les autres réservations
            dateDebutPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    if (datesOccupees.contains(date)) {
                        setStyle("-fx-background-color: #ffcccc;");
                        setDisable(true);
                    }
                }
            });

            dateFinPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    if (datesOccupees.contains(date)) {
                        setStyle("-fx-background-color: #ffcccc;");
                        setDisable(true);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
