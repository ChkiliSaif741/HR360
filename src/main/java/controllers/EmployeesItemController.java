package controllers;

import entities.Utilisateur;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import services.ServiceFormation;
import utils.alertMessage;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class EmployeesItemController {

    @FXML
    private Button supprimer;

    @FXML
    private AnchorPane itemContainer;

    @FXML
    private Label name;

    @FXML
    private Label prenom;

    @FXML
    private ImageView image;


    private GestionEmployesController parentControler; // Référence au contrôleur parent

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

                try {
                    if (imagePath.startsWith("file:")) {
                        // Charger l’image depuis un chemin absolu
                        this.image.setImage(new Image(imagePath));
                    } else {
                        // Charger l’image depuis les ressources du projet
                        URL imageUrl = getClass().getResource(imagePath);
                        if (imageUrl != null) {
                            this.image.setImage(new Image(imageUrl.toString()));
                        } else {
                            throw new Exception("Image introuvable : " + imagePath);
                        }
                    }
                } catch (Exception e) {
                    this.image.setImage(new Image(getClass().getResourceAsStream("/img/user.png"))); // Image par défaut
                    System.err.println("Erreur lors du chargement de l’image : " + e.getMessage());
                }
            } else {
                this.image.setImage(new Image(getClass().getResourceAsStream("/img/user.png"))); // Image par défaut
            }


            // Logs pour débogage
            System.out.println("Chemin de l'image : " + utilisateur.getImgSrc());
            System.out.println("Image chargée : " + this.image.getImage());
        } catch (Exception e) {
            e.printStackTrace(); // Affichez la stack trace pour diagnostiquer l'erreur
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        // Ajouter un événement de clic pour sélectionner l'employé
        itemContainer.setOnMouseClicked(event -> {
            if (parentControler != null) {
                parentControler.setSelectedEmploye(utilisateur); // Sélectionner l'employé
                System.out.println("Employé sélectionné : " + utilisateur.getNom() + " " + utilisateur.getPrenom());
            }
        });
    }


    public void setParentControler(GestionEmployesController parentControler) {
        this.parentControler = parentControler;
    }


}