package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.alertMessage;

import java.io.File;

public class ItemController {




    @FXML
    private Button supprimer;

    @FXML
    private VBox itemContainer;

    @FXML
    private Label name;

    @FXML
    private Label prenom;

    @FXML
    private ImageView image;

    private DisplayController parentControler;

    private Utilisateur utilisateur;

    private alertMessage alert = new alertMessage();



    public void setUtilisateur(Utilisateur utilisateur) {
        try {
            this.utilisateur = utilisateur;
            this.name.setText(utilisateur.getNom());
            this.prenom.setText(utilisateur.getPrenom());

            if (utilisateur.getImgSrc() != null) {
                String imagePath = utilisateur.getImgSrc();
                imagePath = imagePath.replace("%20", " "); // Décodage des espaces

                if (imagePath.startsWith("file:")) {
                    // Chemin absolu
                    this.image.setImage(new Image(imagePath));
                } else {
                    // Chemin relatif
                    this.image.setImage(new Image(getClass().getResource(imagePath).toString()));
                }
            } else {
                // Image par défaut
                this.image.setImage(new Image(getClass().getResourceAsStream("/img/user.png")));
            }

            // Logs pour débogage
            System.out.println("Chemin de l'image : " + utilisateur.getImgSrc());
            System.out.println("Image chargée : " + this.image.getImage());
        } catch (Exception e) {
            e.printStackTrace(); // Affichez la stack trace pour diagnostiquer l'erreur
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }
    @FXML
    private void handleDelete(ActionEvent event) {

            parentControler.deleteUser(utilisateur);
            alert.successMessage("Utilisateur supprimé avec succès!");


    }
    public void setParentControler(DisplayController parentControler) {
        this.parentControler = parentControler;
    }



}