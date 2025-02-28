package controllers;

import entities.Formation;
import entities.Participation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import entities.MyListener;
import services.ServiceParticipation;

public class FormationItemsController {
    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLable;

    @FXML
    private ImageView img;

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
        //priceLable.setText(Main.CURRENCY + fruit.getPrice());
        //Image image = new Image(getClass().getResourceAsStream(fruit.getImgSrc()));
        //img.setImage(image);
    }

    public void setParticipationData(Participation participation,  MyListener myListener) {
        this.participation = participation;
        this.myListener = myListener;
        nameLabel.setText(participation.getFormationNom());
    }

}
