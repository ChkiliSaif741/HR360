package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import entities.Offre;
import services.ServiceOffre;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListeOffresController {

    @FXML
    private ComboBox<String> comboTrieDateExpiration;

    @FXML
    private ComboBox<String> comboTrieStatut;

    @FXML
    private TextField textFieldRecherche;
    @FXML
    private Button btnModifier, btnSupprimer;

    @FXML
    private ImageView imageViewModifier, imageViewSupprimer ,imageViewAjouter;
    @FXML
    private ListView<Offre> listViewOffres;
    @FXML
    private ComboBox<String> comboFiltreStatut;

    private ServiceOffre serviceOffre;
    private Map<Offre, Boolean> expandedItems = new HashMap<>();

    public void initialize() {
        // Appliquer l'effet pour rendre l'image blanche
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1); // Désature l'image (noir et blanc)
        colorAdjust.setBrightness(1);  // Rend l'image blanche

        // Appliquer l'effet aux icônes
        imageViewModifier.setEffect(colorAdjust);
        imageViewSupprimer.setEffect(colorAdjust);
        imageViewAjouter.setEffect(colorAdjust);
        serviceOffre = new ServiceOffre();

        // Initialiser les ComboBox pour le tri
        comboTrieDateExpiration.getItems().addAll("Croissant", "Décroissant");
        comboTrieStatut.getItems().addAll("Publiée", "Expirée");

        // Modification en temps réel
        textFieldRecherche.textProperty().addListener((observable, oldValue, newValue) -> rechercherOffre());

        // Filtre
        comboFiltreStatut.getItems().addAll("Tous", "Publiée", "Expirée");
        comboFiltreStatut.setValue("Tous"); // Valeur par défaut

        // Charger les offres depuis la base de données
        try {
            List<Offre> offres = serviceOffre.afficher();
            listViewOffres.getItems().setAll(offres);

            // Utilisation d'une CellFactory personnalisée pour afficher les détails comme un tableau
            listViewOffres.setCellFactory(param -> new ListCell<Offre>() {
                @Override
                protected void updateItem(Offre offre, boolean empty) {
                    listViewOffres.setOnMouseClicked(event -> {
                        Offre selectedOffre = listViewOffres.getSelectionModel().getSelectedItem();
                        if (selectedOffre != null) {
                            boolean currentState = expandedItems.getOrDefault(selectedOffre, false);
                            expandedItems.put(selectedOffre, !currentState); // Inverser l'état actuel
                            listViewOffres.refresh(); // Rafraîchir la vue pour appliquer le changement
                        }
                    });

                    super.updateItem(offre, empty);
                    if (empty || offre == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Créer un HBox pour afficher les éléments horizontalement
                        HBox hbox = new HBox(20); // Espacement entre les éléments

                        // Créer un Label pour la description
                        Label descriptionLabel = new Label();
                        String fullDescription = offre.getDescription();
                        String shortDescription = (fullDescription.length() > 50) ? fullDescription.substring(0, 50) + "..." : fullDescription;

                        // Vérifier si l'élément est déjà étendu ou non
                        Boolean isExpanded = expandedItems.getOrDefault(offre, false);
                        if (isExpanded) {
                            descriptionLabel.setText(fullDescription); // Afficher la description complète
                        } else {
                            descriptionLabel.setText(shortDescription); // Afficher la description tronquée
                        }
                        descriptionLabel.setWrapText(true); // Activer le retour à la ligne

                        // Gérer le clic sur la description pour basculer entre tronqué et complet
                        descriptionLabel.setOnMouseClicked(event -> {
                            // Basculer l'état de l'élément (étendu ou non)
                            boolean newState = !expandedItems.getOrDefault(offre, false);
                            expandedItems.put(offre, newState);
                            // Actualiser l'affichage en fonction du nouvel état
                            if (newState) {
                                descriptionLabel.setText(fullDescription);  // Set full description
                            } else {
                                descriptionLabel.setText(shortDescription);  // Set truncated description
                            }
                            // Re-render the cell to reflect the change in the description
                            updateItem(offre, false);  // Force the update of the ListCell
                        });

                        // Ajouter les éléments au HBox
                        hbox.getChildren().addAll(
                                createLabeledItem("Titre", offre.getTitre()),
                                createLabeledItem("Description", descriptionLabel.getText()), // Utiliser le texte du label
                                createLabeledItem("Date de Publication", offre.getDatePublication().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                                createLabeledItem("Date d'Expiration", offre.getDateExpiration().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                                createLabeledItem("Statut", offre.getStatut())
                        );

                        // Appliquer le HBox à la cellule
                        setGraphic(hbox);
                    }
                }
            });

            // Écouter la sélection d'une ligne dans la ListView
            listViewOffres.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Basculer l'état de l'élément sélectionné
                    boolean isExpanded = expandedItems.getOrDefault(newValue, false);
                    expandedItems.put(newValue, !isExpanded); // Inverser l'état actuel
                    // Forcer la mise à jour de la cellule pour refléter le changement
                    listViewOffres.refresh();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des offres : " + e.getMessage());
        }
    }

    // Méthode pour créer des éléments Label avec titre et valeur dans un cadre
    // Méthode pour créer des éléments Label avec titre et valeur dans un cadre
    private VBox createLabeledItem(String title, String value) {
        // Création des Labels pour le titre et la valeur
        Label titleLabel = new Label(title);
        Label valueLabel = new Label(value);

        // Appliquer la classe CSS pour rendre le titre en gras
        titleLabel.getStyleClass().add("title-label");

        // Appliquer la classe CSS pour le retour à la ligne dans les Labels
        valueLabel.getStyleClass().add("value-label");

        // Création d'un cadre (Rectangle) autour de chaque paire de titre/valeur
        Rectangle rectangle = new Rectangle(150, 40); // Largeur et hauteur du cadre
        rectangle.getStyleClass().add("rectangle-frame"); // Appliquer le style CSS du rectangle

        // Création du VBox pour aligner les labels et le cadre
        VBox vbox = new VBox(5, titleLabel, valueLabel);
        vbox.getStyleClass().add("vbox-item"); // Appliquer le style CSS du VBox

        return vbox;
    }
    @FXML
    private void ajouterOff() {
        try {
            // Charger la scène pour l'ajout d'une offre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AjouterOffre.fxml");

            listViewOffres.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout.");
        }
    }
    @FXML
    private void trierParDateExpiration() {
        String choix = comboTrieDateExpiration.getValue();
        if (choix != null) {
            List<Offre> offresTriees = listViewOffres.getItems().stream()
                    .sorted((o1, o2) -> choix.equals("Croissant") ?
                            o1.getDateExpiration().compareTo(o2.getDateExpiration()) :
                            o2.getDateExpiration().compareTo(o1.getDateExpiration()))
                    .collect(Collectors.toList());
            listViewOffres.getItems().setAll(offresTriees);
        }
    }

    @FXML
    private void trierParStatut() {
        String choix = comboTrieStatut.getValue();
        if (choix != null) {
            try {
                List<Offre> offres = serviceOffre.afficher(); // Récupérer toutes les offres depuis la base de données

                // Trier en plaçant les offres avec le statut sélectionné en premier
                List<Offre> offresTriees = offres.stream()
                        .sorted((o1, o2) -> {
                            if (o1.getStatut().equals(choix) && !o2.getStatut().equals(choix)) {
                                return -1; // o1 avant o2
                            } else if (!o1.getStatut().equals(choix) && o2.getStatut().equals(choix)) {
                                return 1; // o2 avant o1
                            }
                            return 0; // Conserver l'ordre relatif sinon
                        })
                        .collect(Collectors.toList());

                listViewOffres.getItems().setAll(offresTriees);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du tri des offres : " + e.getMessage());
            }
        }
    }



    @FXML
    private void filtrerParStatut() {
        String statutChoisi = comboFiltreStatut.getValue();

        try {
            List<Offre> toutesLesOffres = serviceOffre.afficher(); // Toujours récupérer toutes les offres depuis la base de données

            List<Offre> offresFiltrees;
            if (statutChoisi == null || statutChoisi.equals("Tous")) {
                offresFiltrees = toutesLesOffres; // Afficher toutes les offres
            } else {
                offresFiltrees = toutesLesOffres.stream()
                        .filter(offre -> offre.getStatut().equals(statutChoisi))
                        .collect(Collectors.toList());
            }

            listViewOffres.getItems().setAll(offresFiltrees);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du filtrage des offres : " + e.getMessage());
        }
    }



    @FXML
    private void rechercherOffre() {
        String recherche = textFieldRecherche.getText().trim().toLowerCase();

        if (recherche.isEmpty()) {
            // Si la recherche est vide, afficher la liste complète
            refreshListView();
        } else {
            // Filtrer uniquement selon le titre
            List<Offre> offresFiltrees = listViewOffres.getItems().stream()
                    .filter(offre -> offre.getTitre().toLowerCase().contains(recherche))
                    .collect(Collectors.toList());

            listViewOffres.getItems().setAll(offresFiltrees);
        }
    }


    public void refreshListView() {
        try {
            List<Offre> offres = serviceOffre.afficher();

            // Appliquer le filtre actuel après le rafraîchissement
            String statutChoisi = comboFiltreStatut.getValue();
            if (statutChoisi != null && !statutChoisi.equals("Tous")) {
                offres = offres.stream()
                        .filter(offre -> offre.getStatut().equals(statutChoisi))
                        .collect(Collectors.toList());
            }

            listViewOffres.getItems().setAll(offres);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des offres : " + e.getMessage());
        }
    }









    @FXML
    private void modifierOffre() {
        Offre selectedOffre = listViewOffres.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierOffre.fxml"));
                Parent root = loader.load();

                ModifierOffre controller = loader.getController();
                controller.setOffreSelectionnee(selectedOffre);

                Stage stage = new Stage();
                stage.setTitle("Modifier Offre");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une offre à modifier.");
        }
    }

    @FXML
    private void supprimerOffre() {
        Offre selectedOffre = listViewOffres.getSelectionModel().getSelectedItem();
        if (selectedOffre != null) {
            try {
                serviceOffre.supprimer(selectedOffre.getId());
                listViewOffres.getItems().remove(selectedOffre);

                showAlert("Succès", "L'offre a été supprimée avec succès.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression de l'offre : " + e.getMessage());
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une offre à supprimer.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}