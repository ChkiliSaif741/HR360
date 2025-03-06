package controllers;

import entities.Formation;
import entities.Participation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import entities.MyListener;
import services.ServiceFormation;
import services.ServiceParticipation;
import entities.Sessions;

import java.sql.SQLException;

public class FormationItemsController {
    @FXML
    private Label nameLabel;

    @FXML
    private Label dateLable;


    @FXML
    private Label priceLable;

    @FXML
    private ImageView img;

    @FXML
    private Button favoriteBtn;

    @FXML
    private void click(MouseEvent mouseEvent) {
        myListener.onClickListener(formation);
    }

    private Formation formation;
    private Participation participation;
    private MyListener myListener;

    public void setData(Formation formation, MyListener myListener) {
        this.formation = formation;
        this.myListener = myListener;
        nameLabel.setText(formation.getTitre());
        dateLable.setText(formation.getDateFormation());

        // Vérifier si l'utilisateur connecté est un responsable RH
        if (Sessions.getInstance().getRole().equals("RH") || Sessions.getInstance().getRole().equals("ResponsableRH")) {
            favoriteBtn.setVisible(false); // Masquer le bouton de favoris
        } else {
            // Vérifier si la formation est favorite et mettre à jour le bouton
            boolean isFavorite = formation.isFavorite();
            updateFavoriteButton(isFavorite);
        }
    }

    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            favoriteBtn.setText("♥"); // Cœur rempli (formation en favori)
            favoriteBtn.setStyle("-fx-text-fill: red;"); // Changer la couleur
        } else {
            favoriteBtn.setText("♡"); // Cœur vide (formation non favorite)
            favoriteBtn.setStyle("-fx-text-fill: black;");
        }
    }

    public void setParticipationData(Participation participation,  MyListener myListener) {
        this.participation = participation;
        this.myListener = myListener;
        nameLabel.setText(participation.getFormationNom());
    }

    @FXML
    void onFavoriteBtn(ActionEvent event) {
        ServiceFormation serviceFormation = new ServiceFormation();
        try {
            boolean isFavorite = formation.isFavorite();
            if (isFavorite) {
                serviceFormation.removeFavoris(formation);
            } else {
                serviceFormation.addFavoris(formation);
            }
            // Mettre à jour le bouton après l'action
            updateFavoriteButton(!isFavorite);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}