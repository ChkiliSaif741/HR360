package controllers;
import entities.Notification;
import entities.Sessions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.*;

import services.ServiceNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherNotificationController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private VBox vbox;
    
    private ServiceNotification serviceNotification=new ServiceNotification();

    private List<Notification> notifications=new ArrayList<>();

    private List<Notification> getData() {
        try {
            notifications = serviceNotification.getNotificationsByUserId(Sessions.getInstance().getIdUtilisateur());
            System.out.println("Nombre de réservations trouvées: " + notifications.size());
            return notifications;
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Méthode pour afficher une notification
    public void afficherNotification() {

        grid.getChildren().clear();
        notifications = getData(); // Met à jour la liste

        int column = 0;
        int row = 1;

        try {
            for (Notification notification : notifications) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/notification.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                notificationController NotificationController = fxmlLoader.getController();
                NotificationController.setParentController(this);
                NotificationController.setData(notification);

                if (column == 1) {
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


    public void notifierSuppression(int idRessource) {
        ServiceNotification serviceNotification = new ServiceNotification();


        Notification notification = new Notification(
                idRessource,
                1,
                "Votre réservation pour la ressource a été annulée ! ", // Message
                Timestamp.valueOf(LocalDate.now().atStartOfDay()) // Timestamp
        );


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getData();
        afficherNotification();
    }
}
