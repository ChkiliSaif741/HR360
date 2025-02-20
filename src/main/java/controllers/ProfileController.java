package controllers;

import entities.Utilisateur;
import services.ServiceUtilisateur;
import services.ServiceUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;

public class ProfileController {

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


    private Utilisateur utilisateurActuel;
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private String imagePath = null; // Stocke le chemin de la nouvelle image

    /**
     * Initialise le profil de l'utilisateur (à appeler après le chargement du FXML).
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurActuel = utilisateur;
        remplirChamps();
    }

    /**
     * Remplit les champs avec les informations de l'utilisateur actuel.
     */
    private void remplirChamps() {
        if (utilisateurActuel != null) {
            nomField.setText(utilisateurActuel.getNom());
            prenomField.setText(utilisateurActuel.getPrenom());
            emailField.setText(utilisateurActuel.getEmail());

            // Chargement de l'image si elle existe
            if (utilisateurActuel.getImgSrc() != null && !utilisateurActuel.getImgSrc().isEmpty()) {
                Image image = new Image("file:" + utilisateurActuel.getImgSrc());
                photoProfil.setImage(image);
            }
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
            imagePath = file.getAbsolutePath();
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
            System.out.println("Aucun utilisateur sélectionné !");
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
            serviceUtilisateur.modifier(utilisateurActuel);
            System.out.println("Profil mis à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de la mise à jour du profil : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
