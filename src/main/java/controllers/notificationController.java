package controllers;

import entities.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import services.ServiceNotification;

public class notificationController {

    @FXML
    private Label date;

    @FXML
    private Button deleteBtn;

    @FXML
    private Label message;

    private Notification notification;

    public void setData(Notification notification) {

        this.notification=notification;
        message.setText(notification.getMessage());
        date.setText("La date d'annulation : " + notification.getDate().toString());
    }


    private AfficherNotificationController parentController;

    public void setParentController(AfficherNotificationController parentController) {
        this.parentController = parentController;
    }


    @FXML
    void deleteBtn(ActionEvent event) {
        ServiceNotification serviceNotification = new ServiceNotification();
        try {
            serviceNotification.supprimerNotification(notification.getId());
            System.out.println("Notification supprimée avec succès !");

            // Supprimer la notification du parent (VBox ou GridPane)
            ((Button) event.getSource()).getParent().getParent().setVisible(false);

            // Optionnel : Rafraîchir la liste des notifications
            ((Button) event.getSource()).getScene().lookup("#grid").applyCss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        parentController.afficherNotification();
    }


}
