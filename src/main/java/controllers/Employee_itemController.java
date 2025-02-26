//package controllers;
//
//import entities.Utilisateur;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//
//public class Employee_itemController {
//
//    @FXML
//    private Label dateMember;
//
//    @FXML
//    private Label employeeID;
//
//
//    @FXML
//    private AnchorPane itemContainer;
//
//
//    @FXML
//    private Label firstName;
//
//    @FXML
//    private Label gender;
//
//    @FXML
//    private Label lastName;
//
//    @FXML
//    private Label phoneNum;
//
//    @FXML
//    private Label position;
//
//    @FXML
//    private ImageView profilePhoto;
//
//    private GestionEmployesController parentControler;
//    private Utilisateur employe;
//
//
//
//    public void setEmploye(Utilisateur employee) {
//        this.employe = employee;
//        firstName.setText(employee.getNom());
//        lastName.setText(employee.getPrenom());
//        position.setText(employee.getPoste());
//
//        Image image = new Image(getClass().getResourceAsStream("/img/user.png"));
//        profilePhoto.setImage(image);
//
//        // Ajouter un événement de clic pour sélectionner l'employé
//        itemContainer.setOnMouseClicked(event -> {
//            if (parentControler != null) {
//                parentControler.setSelectedEmploye(utilisateur); // Sélectionner l'employé
//            }
//        });
//    }
//
//    public void setParentControler(GestionEmployesController parentControler) {
//        this.parentControler = parentControler;
//    }
//
//}
