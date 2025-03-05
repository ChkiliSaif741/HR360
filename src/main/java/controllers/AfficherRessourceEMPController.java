package controllers;

import entities.Ressource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import services.ServiceRecommandation;
import services.ServiceReservation;
import services.ServiceRessource;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AfficherRessourceEMPController implements Initializable {

    @FXML
    private GridPane grid; // GridPane pour les ressources normales

    @FXML
    private GridPane gridRecommandations; // GridPane pour les recommandations

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private BorderPane borderpane;

    @FXML
    private BorderPane borderpaneheader;

    private ServiceRessource serviceRessource = new ServiceRessource();
    private ServiceRecommandation serviceRecommandation;
    private ServiceReservation serviceReservation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les services
        serviceRecommandation = new ServiceRecommandation();
        serviceReservation = new ServiceReservation();

        // Afficher les ressources normales
        afficherRessource();

        // Afficher les recommandations
        afficherRecommandations();
    }

    private List<Ressource> getData() {
        try {
            return serviceRessource.afficher();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void afficherRessource() {
        grid.getChildren().clear();
        List<Ressource> ressources = getData();
        int column = 0;
        int row = 1;

        try {
            for (Ressource ressource : ressources) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressourceEMP.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                RessourceControllerEMP ressourceController = fxmlLoader.getController();
                ressourceController.setParentController(this);
                ressourceController.setData(ressource);

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
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouterReservation(ActionEvent actionEvent) throws IOException {
        AnchorPane view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FormulaireAjoutReservation.fxml")));
        borderpane.setCenter(view);
    }

    private void afficherRecommandations() {
        gridRecommandations.getChildren().clear(); // Nettoyer la GridPane des recommandations

        try {
            // Récupérer les recommandations pour l'utilisateur connecté
            List<Ressource> recommandations = serviceRecommandation.recommanderRessources(1); // Remplacez 1 par l'ID de l'utilisateur connecté

            int column = 0;
            int row = 0;

            // Ajouter chaque ressource recommandée à la GridPane
            for (Ressource ressource : recommandations) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressourceEMP.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                RessourceControllerEMP ressourceController = fxmlLoader.getController();
                ressourceController.setParentController(this);
                ressourceController.setData(ressource);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                gridRecommandations.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}