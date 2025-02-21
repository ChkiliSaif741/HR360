package controllers;

import entities.Reservation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import services.ServiceReservation;
import java.io.IOException;
import java.sql.SQLException;

public class ReservationController {

    @FXML
    private Label labelId;

    @FXML
    private Label labelRessource;

    @FXML
    private Label labelDateDebut;

    @FXML
    private Label labelDateFin;

    @FXML private Label resourceIdLabel;

    private Reservation reservation;

    @FXML
    private Button deleteBtn;

    private AfficherReservationController parentController;

    private ServiceReservation serviceReservation = new ServiceReservation();

    public void setData(Reservation reservation) {

        this.reservation=reservation;
        labelRessource.setText(reservation.getUtilisateur().toString());
        labelDateDebut.setText("Début: " + reservation.getDateDebut().toString());
        labelDateFin.setText("Fin: " + reservation.getDateFin().toString());
        resourceIdLabel.setText(String.valueOf(reservation.getId()));
    }

    public void modifierReservation(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent parent=loader.load();
        Controller controller0 = loader.getController();
        ModifierReservationController controller =controller0.loadPage("/ModifierReservation.fxml").getController();
        controller.setReservation(reservation);
        labelRessource.getScene().setRoot(parent);
    }

    public void setParentController(AfficherReservationController parentController) {
        this.parentController = parentController;
    }

    public void deleteBtn(ActionEvent actionEvent) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer Ressource");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer cette ressource ?");
        ///confirmation.setContentText("Ressource: " + nameL.getText());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(resourceIdLabel.getText());
                    serviceReservation.supprimer(id);


                    Platform.runLater(() -> {
                        Node cardToRemove = deleteBtn.getParent().getParent();
                        if (cardToRemove != null) {
                            ((GridPane) cardToRemove.getParent()).getChildren().remove(cardToRemove);
                        }
                    });

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Erreur lors de la suppression !");
                    alert.show();
                    e.printStackTrace();
                }
            }
        });
        parentController.afficherRessource();
    }


}
