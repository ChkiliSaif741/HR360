package controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import services.ServiceReservation;
import services.ServiceRessource;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FormulaireAjoutReservationEMPController implements Initializable {

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private Button btnRetour;

    private int idRessource;

    private final ServiceRessource serviceRessource = new ServiceRessource();
    private ServiceReservation serviceReservation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stripe.apiKey = "sk_test_51QxdufRgQdrgt057uQn1WmyphpNcg0zjIjUg1UyImEndumS9brmFWHgRfojujeQesMd0Kj0VSyzihyH7wcPXPVT8009tY48RwV";

        serviceReservation = new ServiceReservation();

        // Configurer les DatePicker pour afficher les jours réservés en rouge
        if (idRessource != 0) {
            configurerDatePickers();
        }
    }

    @FXML
    private void ajouterReservation(ActionEvent event) {
        System.out.println("Méthode ajouterReservation appelée");
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


            double montantReservation = serviceRessource.getPrixRessource(idRessource);
            reservation = new Reservation(idRessource, dateDebut, dateFin, 1);


            // Créer une session de paiement Stripe
            String paymentUrl = creerSessionDePaiement(montantReservation);



            if (paymentUrl == null) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur de paiement", "Impossible de générer l'URL de paiement.");
                return;
            }

            // Rediriger vers Stripe Checkout
            if(redirigerVersPaiement(paymentUrl, reservation)==true){
                serviceReservation.ajouter(reservation);
            }



        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réservation.");
            e.printStackTrace();
        }
    }

    private double getPrixRessource(int idRessource) throws SQLException {
        return serviceRessource.getPrixRessource(idRessource);
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
            Parent parent = loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessourceEMP.fxml");
            dateDebutPicker.getScene().setRoot(parent);
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement de la page.");
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }

    public void setIdRessource(int idRessource) {
        this.idRessource = idRessource;
        if (serviceReservation != null) {
            configurerDatePickers();
        }
    }

    /**
     * Crée une session de paiement Stripe et retourne l'URL pour rediriger l'utilisateur.
     */
    private String creerSessionDePaiement(double montant) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://votre-site.com/success") // Remplacez par votre vraie URL de succès
                    .setCancelUrl("https://votre-site.com/cancel")   // Remplacez par votre vraie URL d'annulation
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount((long) (montant * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Réservation")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            System.out.println("Stripe Payment URL: " + session.getUrl()); // Log pour le debug
            return session.getUrl();

        } catch (StripeException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur de paiement", "Une erreur est survenue lors de la création de la session de paiement.");
            return null;
        }
    }

    /**
     * Ouvre un WebView pour rediriger vers Stripe Checkout.
     */
    private boolean redirigerVersPaiement(String paymentUrl, Reservation reservation) {
        AtomicBoolean succes= new AtomicBoolean(false);
        Stage stage = new Stage();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        webEngine.load(paymentUrl);

        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("success")) { // Vérifie si l'URL de succès est atteinte
                try {
                    serviceReservation.ajouter(reservation); // Ajouter la réservation après paiement réussi
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Paiement réussi, réservation confirmée !");
                    succes.set(true);
                } catch (SQLException e) {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réservation après paiement.");
                    e.printStackTrace();
                }
                stage.close(); // Fermer la fenêtre de paiement après succès
            } else if (newValue.contains("cancel")) {
                afficherAlerte(Alert.AlertType.WARNING, "Paiement annulé", "Le paiement a été annulé.");
                stage.close();
            }
        });

        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.show();
        return succes.get();
    }


    private void configurerDatePickers() {
        try {
            // Récupérer les dates réservées pour la ressource spécifique
            List<Date> datesReservees = serviceReservation.getDatesReservees(idRessource);

            // Convertir les dates réservées en un ensemble de LocalDate
            Set<LocalDate> datesOccupees = datesReservees.stream()
                    .map(Date::toLocalDate)
                    .collect(Collectors.toSet());

            // Configurer le DatePicker pour la date de début
            dateDebutPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    // Désactiver et colorer les jours réservés
                    if (datesOccupees.contains(date)) {
                        setStyle("-fx-background-color: #ffcccc;"); // Rouge clair
                        setDisable(true); // Désactiver la sélection
                    }
                }
            });

            // Configurer le DatePicker pour la date de fin
            dateFinPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    // Désactiver et colorer les jours réservés
                    if (datesOccupees.contains(date)) {
                        setStyle("-fx-background-color: #ffcccc;"); // Rouge clair
                        setDisable(true); // Désactiver la sélection
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verifierPaiement(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return "paid".equals(session.getPaymentStatus());
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }

}