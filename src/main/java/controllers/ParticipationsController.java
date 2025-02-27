package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import entities.Participation;
import services.ServiceParticipation;
import entities.Session;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ParticipationsController implements Initializable {

    @FXML
    private GridPane grid; // Récupéré depuis le FXML
    @FXML
    private Button btnAnnuler;

    private ServiceParticipation serviceParticipation = new ServiceParticipation();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherParticipations();
    }

    private void afficherParticipations() {
        grid.getChildren().clear(); // On vide le GridPane avant d'ajouter les nouvelles participations

        int employeId = Session.getInstance().getIdUtilisateur(); // Récupère l'ID de l'utilisateur connecté
        List<Participation> participations = serviceParticipation.getParticipationsByEmploye(employeId);

        int column = 0;
        int row = 0;

        for (Participation participation : participations) {
            VBox participationCard = creerParticipationCard(participation);

            grid.add(participationCard, column, row);
            column++;

            if (column == 3) { // 3 colonnes max par ligne
                column = 0;
                row++;
            }
        }
    }

    private VBox creerParticipationCard(Participation participation) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-padding: 10px;");

        Label labelFormation = new Label("Formation : " + participation.getFormationNom());
        labelFormation.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/img/User1.png")));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        btnAnnuler.setOnAction(e -> annulerParticipation(participation));


        Button btnAnnuler = new Button("Annuler");
        btnAnnuler.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        btnAnnuler.setOnAction(e -> annulerParticipation(participation));

        card.getChildren().addAll(labelFormation, imageView, btnAnnuler);

        return card;
    }

    private void annulerParticipation(Participation participation) {
        serviceParticipation.annulerParticipation(participation.getIdUser(), participation.getIdFormation());
        afficherParticipations(); // Rafraîchir l'affichage après suppression
    }

}
