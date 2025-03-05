package controllers;

import entities.Formation;
import entities.MyListener;
import entities.Participation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import services.ServiceFormation;

import java.sql.SQLException;

public class FavoriteFormationItemsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLable;

    @FXML
    private ImageView img;

    @FXML
    private Button favoriteBtn;


    private FavoriteFormationsEMPController parentControler;


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

        // Vérifier si la formation est favorite et mettre à jour le bouton
        boolean isFavorite = formation.isFavorite();
        updateFavoriteButton(isFavorite);

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
            parentControler.refreshFormations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setParentControler(FavoriteFormationsEMPController parentControler) {
        this.parentControler = parentControler;
    }
}
