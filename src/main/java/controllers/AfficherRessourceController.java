package controllers;
import entities.Ressource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import services.ServiceRessource;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AfficherRessourceController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private BorderPane borderpane;

    @FXML
    private BorderPane borderpaneheader;

    private ServiceRessource serviceRessource = new ServiceRessource();

    private List<Ressource> getData() {
        try {
            return serviceRessource.afficher();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherRessource();
    }

    public void afficherRessource() {
        grid.getChildren().clear();
        List<Ressource> ressources = getData();
        int column = 0;
        int row = 1;

        try {
            for (Ressource ressource : ressources) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressource.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                RessourceController ressourceController = fxmlLoader.getController();
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
}