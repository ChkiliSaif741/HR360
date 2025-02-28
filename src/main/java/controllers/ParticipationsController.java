package controllers;

import entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.ServiceParticipation;
import utils.alertMessage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ParticipationsController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private Label formationNameLable;


    @FXML
    private VBox chosenParticipationCard;
    private final ServiceParticipation serviceParticipation = new ServiceParticipation();
    private List<Participation> participations = new ArrayList<>();
    private MyListener1 myListener1;
    private Participation selectedParticipation;
    private int idParticipationSelectionnee;
    private int idUser = Session.getInstance().getIdUtilisateur();
    private alertMessage alert = new alertMessage();


    @FXML
    void annulerParticipation(ActionEvent event) {
        idUser = Session.getInstance().getIdUtilisateur();

        try {
            // Utiliser la méthode supprimer pour annuler la participation
            serviceParticipation.supprimer(idParticipationSelectionnee);

            // Recharger la liste des participations depuis la base de données
            participations.clear();
            participations.addAll(getData());

            if (!participations.isEmpty()) {
                setChosenParticipation(participations.get(0));
            } else {
                formationNameLable.setText("Aucune participation");
                chosenParticipationCard.setStyle("-fx-background-color: #CCCCCC;");
            }

            refreshParticipations();  // Rafraîchir l'affichage

            alert.successMessage("Participation annulée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            alert.errorMessage("Erreur lors de l'annulation de la participation !");
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vérifier si l'utilisateur est connecté
        if (Session.getInstance() == null || Session.getInstance().getIdUtilisateur() == 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) grid.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Charger les participations
        participations.addAll(getData());

        // Si des participations existent, sélectionner la première
        if (!participations.isEmpty()) {
            setChosenParticipation(participations.get(0));
            myListener1
                    = participation -> setChosenParticipation(participation);
        }

        refreshParticipations();
    }

    private List<Participation> getData() {
        int employeId = Session.getInstance().getIdUtilisateur();
        return serviceParticipation.getParticipationsByEmploye(employeId);
    }



    private void setChosenParticipation(Participation participation) {
        formationNameLable.setText(participation.getFormationNom());
        chosenParticipationCard.setStyle("-fx-background-color: #" + "F16C31" + ";\n" +
                "    -fx-background-radius: 30;");
        selectedParticipation = participation;
        idParticipationSelectionnee = participation.getId(); // Mets à jour l'ID de la formation sélectionnée
    }




    public void refreshParticipations() {
        System.out.println("🔄 Rafraîchissement des participations...");
        grid.getChildren().clear();
        participations.clear();
        participations.addAll(getData());

        int column = 0;
        int row = 1;

        try {
            for (Participation participation : participations) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/items1.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ParticipationItemsController itemController = fxmlLoader.getController();
                itemController.setParticipationData(participation, myListener1);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
            System.out.println("✅ Rafraîchissement terminé !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
