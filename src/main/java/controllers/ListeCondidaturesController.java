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
import entities.Candidature;
import javafx.stage.Stage;
import services.ServiceCandidature;

import java.io.IOException;
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
                        String dateEntretienFormatted = "Non défini";

                        if (candidature.getDateEntretien() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                            dateEntretienFormatted = candidature.getDateEntretien().format(formatter);
                        }

                        String info = "Statut: " + candidature.getStatut() +
                                ", CV: " + candidature.getCv() +
                                ", Lettre: " + candidature.getLettreMotivation() +
                                ", Entretien: " + dateEntretienFormatted;

                        setText(info);
                    }
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
}
