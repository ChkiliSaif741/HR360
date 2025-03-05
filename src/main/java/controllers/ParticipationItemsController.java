package controllers;

import entities.Formation;
import entities.MyListener1;
import entities.Participation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import entities.MyListener;
import services.ServiceParticipation;

public class ParticipationItemsController {
    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLable;

    @FXML
    private ImageView img;

    @FXML
    private void click(MouseEvent mouseEvent) {
        myListener1.onClickListener(participation);
    }

    private Participation participation;
    private MyListener1 myListener1;




    public void setParticipationData(Participation participation,  MyListener1 myListener1) {
        this.participation = participation;
        this.myListener1 = myListener1;
        nameLabel.setText(participation.getFormationNom());
    }

}
