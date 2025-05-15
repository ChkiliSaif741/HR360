package controllers;

import entities.Reservation;
import entities.Sessions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import services.ServiceReservation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherReservationEMPController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private BorderPane borderpane;

    @FXML
    private Button btnSort;


    @FXML
    private BorderPane borderpaneheader;
    private int idRessource;
    private ServiceReservation serviceReservation = new ServiceReservation();

    public List<Reservation> getData() {
        try {
            int iduser= Sessions.getInstance().getIdUtilisateur();
            return serviceReservation.afficherEMP().stream().filter(r->r.getIduser()==iduser).toList();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int iduser= Sessions.getInstance().getIdUtilisateur();
        afficherRessource(getData().stream()
                .filter(t -> t.getIduser() == iduser)
                .toList());
    }


    public void afficherRessource(List<Reservation> reservations) {
        grid.getChildren().clear();



        int column = 0;
        int row = 1;

        try {
            for (Reservation reservation : reservations) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reservationEMP.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                ReservationEMPController reservationController = fxmlLoader.getController();
                reservationController.setParentController(this);
                reservationController.setData(reservation);

                if (column == 3) {
                    column = 0;
                    row++;
                }
                grid.add(anchorPane, column++, row);
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10, 10, 10, 10));
                GridPane.setValignment(anchorPane, VPos.CENTER);
                GridPane.setHalignment(anchorPane, HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setIdRessource(int idRessource) {
        this.idRessource = idRessource;
        afficherRessource(getData());
    }

    @FXML
    private void sortReservations() {
        List<Reservation> sortedReservations = getData().stream()
                .filter(t -> t.getIduser() == 1) // Filtrage si nécessaire
                .sorted(Comparator.comparing(Reservation::getDateDebut)) // Change selon ton critère
                .toList();

        afficherRessource(sortedReservations);
    }

}
