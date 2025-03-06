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

import java.sql.SQLException;

public class FormationItemsControllerRH {
    @FXML
    private Label nameLabel;

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


    }



    public void setParticipationData(Participation participation,  MyListener myListener) {
        this.participation = participation;
        this.myListener = myListener;
        nameLabel.setText(participation.getFormationNom());
    }

}



