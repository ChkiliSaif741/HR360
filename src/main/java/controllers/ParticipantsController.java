package controllers;

import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import services.ServiceParticipation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ParticipantsController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scroll;

    private int idFormation;

    public void setIdFormation(int idFormation) {
        this.idFormation = idFormation;
        System.out.println("ID Formation : " + idFormation); // 🛠️ Vérification
        List<Utilisateur> participants = getParticipants();
        int column = 0;
        int row = 0;

        // S'assurer que la GridPane est bien réinitialisée avant d'ajouter les éléments
        grid.getChildren().clear();
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        grid.setMinHeight(Region.USE_PREF_SIZE);
        grid.setMinWidth(Region.USE_PREF_SIZE);

        try {
            for (Utilisateur user : participants) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                EmployeesItemController itemController = fxmlLoader.getController();
                itemController.setUtilisateur(user);

                // Ajouter l'élément avant de modifier les colonnes et lignes
                grid.add(anchorPane, column, row);
                GridPane.setMargin(anchorPane, new Insets(20, 50, 50, 50));

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private List<Utilisateur> participants = new ArrayList<>();

    ServiceParticipation serviceParticipation = new ServiceParticipation();

    private List<Utilisateur> getParticipants() {
        try {
            participants = serviceParticipation.afficherParticipants(idFormation);
            System.out.println("Nombre de participants : " + participants.size()); // 🛠️ Vérification
            return participants;
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
