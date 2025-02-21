package controllers;

import entities.Ressource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import services.ServiceRessource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class FormulaireAjoutRessourceController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField typeField;
    @FXML
    private ComboBox<String> etatComboBox;
    @FXML
    private TextField utilisateurField;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnAnnuler;
    @FXML
    private Button btnRetour;

    private ServiceRessource serviceRessource = new ServiceRessource();

    @FXML
    private void ajouterRessource(ActionEvent event) {
        String nom = nomField.getText().trim();
        String type = typeField.getText().trim();
        String etat = etatComboBox.getValue();
        String utilisateur = utilisateurField.getText().trim();


        if (nom.isEmpty() || type.isEmpty() || etat == null || utilisateur.isEmpty()) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }


        if (!isValidName(nom)) {
            showErrorAlert("Erreur de saisie", "Le nom ne doit contenir que des lettres !");
            return;
        }


        if (!isValidName(type)) {
            showErrorAlert("Erreur de saisie", "Le type ne doit contenir que des lettres !");
            return;
        }


        if (!isValidUser(utilisateur)) {
            showErrorAlert("Erreur de saisie", "L'utilisateur ne doit pas contenir de caractères spéciaux !");
            return;
        }

        Ressource ressource = new Ressource(nom, type, etat, utilisateur);
        try {
            serviceRessource.ajouter(ressource);
            showConfirmationAlert("Succès", "La ressource a été ajoutée avec succès !");
            System.out.println("Ressource ajoutée avec succès !");
            annulerAjout(null);
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Erreur lors de l'ajout de la ressource : " + e.getMessage());
        }
    }

    @FXML
    private void annulerAjout(ActionEvent event) {
        nomField.clear();
        typeField.clear();
        etatComboBox.getSelectionModel().clearSelection();
        utilisateurField.clear();
    }

    @FXML
    private void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessource.fxml");
            typeField.getScene().setRoot(parent);
        } catch (IOException e) {
            showErrorAlert("Erreur", "Une erreur est survenue lors du chargement de la page.");
        }
    }

    @FXML
    public void initialize() {
        btnRetour.setOnAction(event -> retour());
    }


    private boolean isValidName(String input) {
        return Pattern.matches("^[A-Za-zÀ-ÿ\\s]+$", input);
    }


    private boolean isValidUser(String input) {
        return Pattern.matches("^[A-Za-z0-9\\s]+$", input);
    }


    private void showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
