package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import entities.Offre;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceOffre;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListeOffresFront {

    @FXML
    private ListView<Offre> listViewOffres;

    private ObservableList<Offre> items;
    private ServiceOffre serviceOffre;

    public void initialize() {
        serviceOffre = new ServiceOffre();
        items = FXCollections.observableArrayList();

        try {
            // Retrieve data from the database
            List<Offre> offres = serviceOffre.afficher();
            items.addAll(offres);

            // Set the items in the ListView
            listViewOffres.setItems(items);
            listViewOffres.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            listViewOffres.setOnMouseClicked(event -> {
                Offre selectedOffre = listViewOffres.getSelectionModel().getSelectedItem();
                if (selectedOffre != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Postuler à l'offre");
                    alert.setHeaderText("Voulez-vous postuler à cette offre ?");
                    alert.setContentText("Titre : " + selectedOffre.getTitre());

                    // Ajouter les boutons Oui et Non
                    javafx.scene.control.ButtonType buttonOui = new javafx.scene.control.ButtonType("Oui");
                    javafx.scene.control.ButtonType buttonNon = new javafx.scene.control.ButtonType("Non");

                    alert.getButtonTypes().setAll(buttonOui, buttonNon);

                    // Gérer l'action selon le bouton cliqué
                    alert.showAndWait().ifPresent(response -> {
                        if (response == buttonOui) {
                            ouvrirAjouterCandidature(selectedOffre.getId());
                        }
                    });
                }
            });

            // Use a custom CellFactory to display relevant details
            listViewOffres.setCellFactory(param -> new ListCell<Offre>() {
                @Override
                protected void updateItem(Offre offre, boolean empty) {
                    super.updateItem(offre, empty);
                    if (empty || offre == null) {
                        setText(null);
                    } else {
                        // Create a VBox to display the data vertically
                        javafx.scene.layout.VBox vBox = new javafx.scene.layout.VBox(5); // Spacing between elements

                        // Format the dates if they are present
                        String datePublicationFormatted = "Non définie";
                        String dateExpirationFormatted = "Non définie";

                        if (offre.getDatePublication() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            datePublicationFormatted = offre.getDatePublication().format(formatter);
                        }

                        if (offre.getDateExpiration() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            dateExpirationFormatted = offre.getDateExpiration().format(formatter);
                        }

                        // Create labels for each piece of information with icons
                        vBox.getChildren().addAll(
                                createLabelWithIcon("Titre: ", offre.getTitre(), "titre.png"),
                                createLabelWithIcon("Description: ", (offre.getDescription() != null ? offre.getDescription() : "Non définie"), "icon_description.png"),
                                createLabelWithIcon("Publication: ", datePublicationFormatted, "publier.png"),
                                createLabelWithIcon("Expiration: ", dateExpirationFormatted, "expire.png"),
                                createLabelWithIcon("Statut: ", offre.getStatut(), "icon_statut.png")
                        );

                        // Set the VBox as the content of the cell
                        setGraphic(vBox);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger les offres");
            alert.setContentText("Une erreur est survenue lors du chargement des données.");
            alert.showAndWait();
        }
    }
    private void ouvrirAjouterCandidature(int idOffre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCondidature.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de l'interface AjouterCandidature
            AjouterCondidature controller = loader.getController();
            controller.initData(idOffre); // Passer l'ID de l'offre

            Stage stage = new Stage();
            stage.setTitle("Ajouter Candidature");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HBox createLabelWithIcon(String labelText, String valueText, String iconName) {
        HBox hBox = new HBox(5); // Spacing between icon and label

        // Load the icon using the correct path
        InputStream iconStream = getClass().getResourceAsStream("/icons/" + iconName);
        javafx.scene.image.Image iconImage = new javafx.scene.image.Image(iconStream); // Create Image from InputStream
        javafx.scene.image.ImageView icon = new javafx.scene.image.ImageView(iconImage); // Create ImageView from Image

        icon.setFitWidth(16); // Set icon width
        icon.setFitHeight(16); // Set icon height
        icon.setPreserveRatio(true);

        javafx.scene.control.Label label = new javafx.scene.control.Label(labelText);
        label.setStyle("-fx-font-weight: bold;"); // Make the label text bold

        javafx.scene.control.Label valueLabel = new javafx.scene.control.Label(valueText);

        hBox.getChildren().addAll(icon, label, valueLabel);
        return hBox;
    }




}
