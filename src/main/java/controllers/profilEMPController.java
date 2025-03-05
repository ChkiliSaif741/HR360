package controllers;

import entities.Utilisateur;
import javafx.scene.layout.VBox;
import services.ServiceUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import utils.alertMessage;

import java.io.File;
import java.sql.SQLException;

public class profilEMPController {

    @FXML
    private Button btnChangerPhoto;

    @FXML
    private Button btnEnregistrer;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nomField;

    @FXML
    private ImageView photoProfil;

    @FXML
    private TextField prenomField;

    @FXML
    private VBox container;

    private Utilisateur utilisateurActuel;
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private String imagePath = null; // Stocke le chemin de la nouvelle image
    private alertMessage alert = new alertMessage();

    /**
     * Initialise le profil de l'utilisateur (à appeler après le chargement du FXML).
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurActuel = utilisateur;
        System.out.println("Utilisateur reçu : " + utilisateur); // Log pour vérifier
        remplirChamps();
    }

    /**
     * Remplit les champs avec les informations de l'utilisateur actuel.
     */
    private void remplirChamps() {
        if (utilisateurActuel != null) {
            System.out.println("Nom de l'utilisateur : " + utilisateurActuel.getNom());
            System.out.println("Prénom de l'utilisateur : " + utilisateurActuel.getPrenom());
            System.out.println("Email de l'utilisateur : " + utilisateurActuel.getEmail());

            nomField.setText(utilisateurActuel.getNom());
            prenomField.setText(utilisateurActuel.getPrenom());
            emailField.setText(utilisateurActuel.getEmail());

            // Chargement de l'image si elle existe
            if (utilisateurActuel.getImgSrc() != null && !utilisateurActuel.getImgSrc().isEmpty()) {
                System.out.println("Chemin de l'image : " + utilisateurActuel.getImgSrc()); // Log pour vérifier
                try {
                    Image image = new Image(utilisateurActuel.getImgSrc());
                    photoProfil.setImage(image);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                    photoProfil.setImage(new Image(getClass().getResource("/img/user.png").toString())); // Image par défaut
                }
            } else {
                System.out.println("Aucune image définie pour l'utilisateur."); // Log pour vérifier
                photoProfil.setImage(new Image(getClass().getResource("/img/user.png").toString()));
            }
        } else {
            alert.errorMessage("Aucun utilisateur n'est connecté !");
        }
    }

    /**
     * Gère le changement de photo de profil.
     */
    @FXML
    private void changerPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imagePath = file.toURI().toString(); // Utiliser l'URI pour le chemin de l'image
            Image image = new Image(file.toURI().toString());
            photoProfil.setImage(image);
        }
    }

    /**
     * Enregistre les modifications de l'utilisateur.
     */
    @FXML
    private void enregistrerModifications(ActionEvent event) {
        if (utilisateurActuel == null) {
            alert.errorMessage("Aucun utilisateur n'est connecté !");
            return;
        }

        // Vérification des champs vides
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty()) {
            alert.errorMessage("Tous les champs doivent être remplis !");
            return;
        }

        // Mise à jour des données de l'utilisateur
        utilisateurActuel.setNom(nomField.getText());
        utilisateurActuel.setPrenom(prenomField.getText());
        utilisateurActuel.setEmail(emailField.getText());

        if (imagePath != null) {
            utilisateurActuel.setImgSrc(imagePath);
        }

        try {
            serviceUtilisateur.modifier_employe(utilisateurActuel);
            alert.successMessage("Profil mis à jour avec succès !");
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de la mise à jour du profil : " + e.getMessage());
            e.printStackTrace();
        }
    }
}