package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utils.alertMessage;

import java.net.URL;

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
                imagePath = imagePath.replace("%20", ""); // Décodage des espaces

                if (imagePath.startsWith("file:")) {
                    // Chemin absolu
                    this.image.setImage(new Image(imagePath));
                } else {
                    // Chemin relatif
                    URL imageUrl = getClass().getResource(imagePath);
                    if (imageUrl != null) {
                        this.image.setImage(new Image(imageUrl.toString()));
                    } else {
                        // Image par défaut si le chemin est incorrect
                        this.image.setImage(new Image(getClass().getResourceAsStream("/img/user.png")));
                        System.err.println("Chemin de l'image introuvable : " + imagePath);
                    }
                }
            } else {
                // Image par défaut si aucune image n'est spécifiée
                this.image.setImage(new Image(getClass().getResourceAsStream("/img/user.png")));
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