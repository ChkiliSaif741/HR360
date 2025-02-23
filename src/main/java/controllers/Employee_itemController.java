package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import entities.Employe;

public class Employee_itemController {

    @FXML
    private Label dateMember;

    @FXML
    private Label employeeID;

    @FXML
    private AnchorPane employeeItem;

    @FXML
    private Label firstName;

    @FXML
    private Label gender;

    @FXML
    private Label lastName;

    @FXML
    private Label phoneNum;

    @FXML
    private Label position;

    @FXML
    private ImageView profilePhoto;

    private GestionEmployesController parentControler;
    private Employe employe;



    public void setEmploye(Employe employee) {
        this.employe = employee;
        firstName.setText(employee.getNom());
        lastName.setText(employee.getPrenom());
        position.setText(employee.getPoste());

        Image image = new Image(getClass().getResourceAsStream("/img/user.png"));
        profilePhoto.setImage(image);
    }

    public void setParentControler(GestionEmployesController parentControler) {
        this.parentControler = parentControler;
    }
}
