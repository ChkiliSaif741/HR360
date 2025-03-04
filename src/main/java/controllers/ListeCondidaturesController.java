package controllers;

import entities.Offre;
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
import entities.Candidature;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceCandidature;
import services.ServiceOffre;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListeCondidaturesController {

    @FXML
    private ListView<Candidature> listViewCondidatures;

    private ObservableList<Candidature> items;
    private ServiceCandidature serviceCandidature;

    public void initialize() {
        serviceCandidature = new ServiceCandidature();
        items = FXCollections.observableArrayList();

        try {
            // Retrieve data from the database
            List<Candidature> candidatures = serviceCandidature.afficher();
            items.addAll(candidatures);

            // Set the items in the ListView
            listViewCondidatures.setItems(items);
            listViewCondidatures.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            // Use a custom CellFactory to display relevant details
            listViewCondidatures.setCellFactory(param -> new ListCell<Candidature>() {
                @Override
                protected void updateItem(Candidature candidature, boolean empty) {
                    super.updateItem(candidature, empty);
                    if (empty || candidature == null) {
                        setText(null);
                    } else {
                        // Créer une VBox pour afficher les informations de la candidature
                        javafx.scene.layout.VBox vBox = new javafx.scene.layout.VBox(5);

                        // Format des dates si présentes
                        String dateCandidatureFormatted = "Non défini";
                        String dateModificationFormatted = "Non modifié";

                        if (candidature.getDateCandidature() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                            dateCandidatureFormatted = candidature.getDateCandidature().format(formatter);
                        }
                        if (candidature.getDateModification() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                            dateModificationFormatted = "Modifié à: " + candidature.getDateModification().format(formatter);
                        }

                        // Extraire les noms de fichier pour le CV et la Lettre
                        String cvFileName = getFileName(candidature.getCv());
                        String lettreFileName = getFileName(candidature.getLettreMotivation());

                        // Récupérer l'offre en fonction de l'id_offre de la candidature
                        Offre offre = getOffreById(candidature.getId_offre());
                        String titreOffre = (offre != null) ? offre.getTitre() : "Offre non trouvée";

                        // Ajouter les informations à la VBox
                        vBox.getChildren().addAll(

                                new javafx.scene.control.Label("Offre: " + titreOffre){{
                                    getStyleClass().add("bold-label"); // Applique la classe CSS 'bold-label'
                                }},
                                createLabelWithIcon("Statut: ", candidature.getStatut(), "icon_statut.png"),
                                createLabelWithIcon("CV: ", cvFileName, "icon_cv.png"),
                                createLabelWithIcon("Lettre: ", lettreFileName, "icon_lettre.png"),
                                createLabelWithIcon("Date: ", dateCandidatureFormatted, "icon_entretien.png"),
                                createLabelWithIcon("Description: ", (candidature.getDescription() != null ? candidature.getDescription() : "Non définie"), "icon_description.png"),
                                new javafx.scene.control.Label(dateModificationFormatted)
                        );

                        // Définir la VBox comme contenu de la cellule
                        setGraphic(vBox);
                    }
                }

                private String getFileName(String filePath) {
                    if (filePath != null && !filePath.isEmpty()) {
                        java.io.File file = new java.io.File(filePath);
                        return file.getName();  // Extract file name from path
                    }
                    return "Aucun fichier"; // Return a default value if no file path is provided
                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger les candidatures");
            alert.setContentText("Une erreur est survenue lors du chargement des données.");
            alert.showAndWait();
        }
    }
    private Offre getOffreById(int id_offre) {
        ServiceOffre serviceOffre = new ServiceOffre();
        try {
            List<Offre> offres = serviceOffre.afficher();
            for (Offre offre : offres) {
                if (offre.getId() == id_offre) {
                    return offre;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the offer is not found
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

    public void modifierCondidature() {
        int selectedIndex = listViewCondidatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Candidature selectedCandidature = listViewCondidatures.getItems().get(selectedIndex);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCondidature.fxml"));
                Parent root = loader.load();

                ModifierCondidature controller = loader.getController();
                controller.setCandidatureSelectionnee(selectedCandidature);

                Stage stage = new Stage();
                stage.setTitle("Modifier Candidature");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une candidature à modifier.");
            alert.showAndWait();
        }
    }



    public void supprimerCondidature() {
        int selectedIndex = listViewCondidatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            try {
                List<Candidature> candidatures = serviceCandidature.afficher();
                int idToDelete = candidatures.get(selectedIndex).getId_candidature();
                serviceCandidature.supprimer(idToDelete);
                listViewCondidatures.getItems().remove(selectedIndex);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Suppression");
                alert.setHeaderText(null);
                alert.setContentText("Candidature supprimée avec succès !");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur de suppression");
                alert.setHeaderText("Impossible de supprimer la candidature");
                alert.setContentText("Une erreur est survenue lors de la suppression.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une candidature à supprimer.");
            alert.showAndWait();
        }
    }
    public void VoirEntretien()
    {
        int selectedIndex = listViewCondidatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {

        }else {
            showAlert("Aucune sélection", "Veuillez sélectionner une candidature Pour voir entretien.");
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
