package controllers;

import entities.Entretien;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import services.ServiceEntretien;
import utils.MyDatabase;
import utils.statut;
import utils.type;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Affentretienfront implements Initializable {
    Connection connection;

    @FXML
    private GridPane gridEnt;
    @FXML
    private TextField Search;
    @FXML
    private ScrollPane scrollEnt;
    @FXML
    private CheckBox tridate;
    @FXML
    private CheckBox filtreenligne;
    @FXML
    private CheckBox filtrepresentiel;

    private ServiceEntretien serviceEntretien;



    @FXML
    private GridPane gridPane;


    private int interviewId;
    private int loggedInUserId; // Variable pour stocker l'ID de l'utilisateur connecté
    private List<Entretien> entretiens = new ArrayList<>();


    public Affentretienfront() {
        this.connection = MyDatabase.getInstance().getConnection();

        serviceEntretien = new ServiceEntretien();
    }

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //afficherEntretiens(entretiens);


        // Utiliser l'ID utilisateur 2 pour les tests
        int id = 2; // ID utilisateur statique
        System.out.println("ID de l'utilisateur connecté (simulé) : " + id);

        // Récupérer les entretiens de l'utilisateur avec l'ID 2
        ObservableList<Entretien> entretiens = getEntretien(id);
        System.out.println("Nombre d'entretiens chargés : " + entretiens.size());

        if (entretiens.isEmpty()) {
            System.out.println("Aucun entretien trouvé pour l'utilisateur : " + id);
        } else {
            try {
                afficherEntretiens(entretiens); // Afficher les entretiens
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        startScheduler();



        // Ajouter un écouteur à la CheckBox pour trier les entretiens par date et heure
        tridate.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue) {
                    trierEntretiensParDateEtHeure();
                } else {
                    afficherEntretiens(entretiens);
                }
            } catch (SQLException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de trier les entretiens : " + e.getMessage());
            }
        });

        // Ajouter un écouteur au champ de recherche pour filtrer par statut
        Search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                rechercherEntretiensParStatut(newValue);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de rechercher les entretiens : " + e.getMessage());
            }
        });

        // Ajouter des écouteurs aux CheckBox pour filtrer par type (En_ligne ou Presentiel)
        filtreenligne.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filtrerEntretiensParType();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de filtrer les entretiens : " + e.getMessage());
            }
        });

        filtrepresentiel.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filtrerEntretiensParType();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de filtrer les entretiens : " + e.getMessage());
            }
        });
    }




    public ObservableList<Entretien> getEntretien(int id) {
        ObservableList<Entretien> entretiens = FXCollections.observableArrayList();
        String query = "SELECT * FROM entretien WHERE id = ?"; // Filtrer par ID utilisateur

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id); // Utiliser l'ID de l'utilisateur connecté
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Entretien entretien = new Entretien();
                entretien.setIdEntretien(rs.getInt("idEntretien"));
                entretien.setDate(rs.getDate("date").toLocalDate());
                entretien.setHeure(rs.getTime("heure") != null ? rs.getTime("heure").toLocalTime() : null);
                entretien.setType(type.valueOf(rs.getString("type")));
                entretien.setStatut(statut.valueOf(rs.getString("statut")));
                entretien.setLien_meet(rs.getString("lien_meet") != null ? rs.getString("lien_meet") : "Aucun");
                entretien.setLocalisation(rs.getString("localisation") != null ? rs.getString("localisation") : "Aucune");
                entretien.setIdCandidature(rs.getInt("idCandidature"));
                entretien.setNom(rs.getString("nom"));
                entretien.setPrenom(rs.getString("prenom"));
                entretien.setId(rs.getInt("id"));

                entretiens.add(entretien); // Ajouter l'entretien à la liste
                System.out.println("Entretien trouvé : " + entretien.getIdEntretien());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des entretiens : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Nombre d'entretiens récupérés : " + entretiens.size());
        return entretiens;
    }
    
    private void trierEntretiensParDateEtHeure() throws SQLException {
        List<Entretien> entretiens = serviceEntretien.afficher(); // Récupérer tous les entretiens

        if (entretiens == null || entretiens.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun entretien", "Aucun entretien trouvé !");
            return;
        }

        // Trier les entretiens par date et heure (du plus récent au plus ancien)
        entretiens.sort((e1, e2) -> {
            // Comparer d'abord par date
            int compareDate = e1.getDate().compareTo(e2.getDate());
            if (compareDate != 0) {
                return compareDate; // Si les dates sont différentes, retourner le résultat de la comparaison des dates
            } else {
                // Si les dates sont identiques, comparer par heure
                return e1.getHeure().compareTo(e2.getHeure());
            }
        });

        // Afficher les entretiens triés
        afficherEntretiensTries(entretiens);
    }


    private void rechercherEntretiensParStatut(String statutRecherche) throws SQLException {
        System.out.println("Recherche par statut : " + statutRecherche); // Log pour vérifier la valeur de recherche

        List<Entretien> entretiens = serviceEntretien.afficher(); // Récupérer tous les entretiens

        if (entretiens == null || entretiens.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun entretien", "Aucun entretien trouvé !");
            return;
        }

        // Filtrer les entretiens par statut (Planifié, Terminé, Annulé, Reporté)
        List<Entretien> entretiensFiltres = new ArrayList<>();
        for (Entretien entretien : entretiens) {
            System.out.println("Statut de l'entretien : " + entretien.getStatut()); // Log pour vérifier le statut de chaque entretien
            if (entretien.getStatut().toString().toLowerCase().contains(statutRecherche.toLowerCase())) {
                entretiensFiltres.add(entretien);
            }
        }

        System.out.println("Nombre d'entretiens filtrés : " + entretiensFiltres.size()); // Log pour vérifier le nombre d'entretiens filtrés

        // Afficher les entretiens filtrés
        afficherEntretiensTries(entretiensFiltres);
    }

    private void filtrerEntretiensParType() throws SQLException {
        System.out.println("Filtrage par type : En_ligne = " + filtreenligne.isSelected() + ", Presentiel = " + filtrepresentiel.isSelected()); // Log pour vérifier les filtres

        List<Entretien> entretiens = serviceEntretien.afficher(); // Récupérer tous les entretiens

        if (entretiens == null || entretiens.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun entretien", "Aucun entretien trouvé !");
            return;
        }

        // Filtrer les entretiens en fonction des CheckBox cochés
        List<Entretien> entretiensFiltres = new ArrayList<>();
        for (Entretien entretien : entretiens) {
            System.out.println("Type de l'entretien : " + entretien.getType()); // Log pour vérifier le type de chaque entretien
            if (filtreenligne.isSelected() && entretien.getType().toString().equalsIgnoreCase("En_ligne")) {
                entretiensFiltres.add(entretien);
            } else if (filtrepresentiel.isSelected() && entretien.getType().toString().equalsIgnoreCase("Presentiel")) {
                entretiensFiltres.add(entretien);
            }
        }

        // Si aucun CheckBox n'est coché, afficher tous les entretiens
        if (!filtreenligne.isSelected() && !filtrepresentiel.isSelected()) {
            entretiensFiltres = entretiens;
        }

        System.out.println("Nombre d'entretiens filtrés : " + entretiensFiltres.size()); // Log pour vérifier le nombre d'entretiens filtrés

        // Afficher les entretiens filtrés
        afficherEntretiensTries(entretiensFiltres);
    }


    private void afficherEntretiensTries(List<Entretien> entretiens) {
        gridEnt.getChildren().clear(); // Vider le GridPane avant de recharger les entretiens

        int column = 0;
        int row = 0;

        for (Entretien entretien : entretiens) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemEntretienFront.fxml"));
                AnchorPane anchorPane = loader.load();

                ItemEntretienFront itemEntretien = loader.getController();
                itemEntretien.setData(entretien);
                itemEntretien.setAffentretienfront(this);

                if (column == 3) {
                    column = 0;
                    row++;
                }
                gridEnt.add(anchorPane, column++, row);

                gridEnt.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridEnt.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridEnt.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridEnt.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridEnt.setMaxWidth(Region.USE_PREF_SIZE);
                gridEnt.setMaxHeight(Region.USE_PREF_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void afficherEntretiens(ObservableList<Entretien> entretiens) throws IOException {
        gridEnt.getChildren().clear(); // Vider le GridPane avant de recharger les entretiens
        for (Entretien entretien : entretiens) {

            try {
                //List<Entretien> entretiens = serviceEntretien.afficher();
                afficherEntretiensTries(entretiens);

                int column = 0;
                int row = 0;

                //for (Entretien entretien : entretiens) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemEntretienFront.fxml"));
                    AnchorPane anchorPane = loader.load();

                    ItemEntretienFront itemEntretien = loader.getController();
                    itemEntretien.setData(entretien);
                    itemEntretien.setAffentretienfront(this);

                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                    gridEnt.add(anchorPane, column++, row);

                    gridEnt.setMinWidth(Region.USE_COMPUTED_SIZE);
                    gridEnt.setMinHeight(Region.USE_COMPUTED_SIZE);
                    gridEnt.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    gridEnt.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    gridEnt.setMaxWidth(Region.USE_PREF_SIZE);
                    gridEnt.setMaxHeight(Region.USE_PREF_SIZE);

            } catch (IOException e){
            e.printStackTrace();
        }
    }
        }









    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /////////////NOTIF



    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);



    @FXML
    public void startScheduler() {
        System.out.println("Démarrage du planificateur...");
        scheduler.scheduleAtFixedRate(this::checkUpcomingEntretiens, 0, 3, TimeUnit.HOURS);
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
        Platform.runLater(() -> {
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
