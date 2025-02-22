package controllers;

import entities.Entretien;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import services.ServiceEntretien;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotifEnt {
    @FXML
    private Label statusLabel;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur NotifEnt...");
        startScheduler();
    }

    public void startScheduler() {
        System.out.println("Démarrage du planificateur...");
        scheduler.scheduleAtFixedRate(this::checkUpcomingEntretiens, 0, 1, TimeUnit.HOURS);
        statusLabel.setText("Statut : Planificateur démarré");
    }

    public void stopScheduler() {
        scheduler.shutdown();
        statusLabel.setText("Statut : Planificateur arrêté");
    }

    private void checkUpcomingEntretiens() {
        try {
            List<Entretien> entretiens = serviceEntretien.afficher(); // Récupérer tous les entretiens
            System.out.println("Nombre total d'entretiens : " + entretiens.size());

            for (Entretien entretien : entretiens) {
                LocalDateTime maintenant = LocalDateTime.now();
                LocalDateTime dateHeureEntretien = LocalDateTime.of(entretien.getDate(), entretien.getHeure());
                long delai = ChronoUnit.MILLIS.between(maintenant, dateHeureEntretien.minusHours(24)); // 24 heures avant l'entretien

                if (delai > 0) {
                    System.out.println("Planification de la notification pour : " + dateHeureEntretien.minusHours(24));
                    scheduleNotification(entretien);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des entretiens : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void scheduleNotification(Entretien entretien) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dateHeureEntretien = LocalDateTime.of(entretien.getDate(), entretien.getHeure());
        long delai = ChronoUnit.MILLIS.between(maintenant, dateHeureEntretien.minusHours(24)); // 24 heures avant l'entretien

        if (delai > 0) {
            scheduler.schedule(() -> showNotification(entretien), delai, TimeUnit.MILLISECONDS);
            System.out.println("Notification planifiée pour : " + dateHeureEntretien.minusHours(24));
        }
    }

    private void showNotification(Entretien entretien) {
        javafx.application.Platform.runLater(() -> {
            // Charger l'image depuis les ressources
            Image image = new Image("/image/error.png"); // Chemin de l'image dans le dossier des ressources

            // Créer la notification
            Notifications notifications = Notifications.create()
                    .title("Rappel d'entretien") // Titre de la notification
                    .text("Un entretien est prévu dans 24 heures : " + entretien.getDate() + " à " + entretien.getHeure()) // Texte de la notification
                    .graphic(new ImageView(image)) // Ajouter l'image à la notification
                    .hideAfter(Duration.seconds(4)); // Durée d'affichage de la notification (4 secondes)

            // Afficher la notification
            notifications.show();
        });
    }
}