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

public class ModifierRessourceController {

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnRetour;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private TextField nomField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField utilisateurField;

    private int idRessource;

    private Ressource ressource;

    public void annulerModification(ActionEvent actionEvent) {
        nomField.clear();
        typeField.clear();
        etatComboBox.getSelectionModel().clearSelection();
        utilisateurField.clear();
    }

    public void ModifierRessource(ActionEvent actionEvent) {
        String nouveauNom = nomField.getText().trim();
        String nouveauType = typeField.getText().trim();
        String nouvelEtat = etatComboBox.getValue();
        String nouvelUtilisateur = utilisateurField.getText().trim();

        if (nouveauNom.isEmpty() || nouveauType.isEmpty() || nouvelEtat == null || nouvelUtilisateur.isEmpty()) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        if (!isValidName(nouveauNom)) {
            showErrorAlert("Erreur de saisie", "Le nom ne doit contenir que des lettres !");
            return;
        }

        if (!isValidName(nouveauType)) {
            showErrorAlert("Erreur de saisie", "Le type ne doit contenir que des lettres !");
            return;
        }

        if (!isValidUser(nouvelUtilisateur)) {
            showErrorAlert("Erreur de saisie", "L'utilisateur ne doit pas contenir de caractères spéciaux !");
            return;
        }

        ServiceRessource serviceRessource = new ServiceRessource();
        Ressource res = new Ressource(ressource.getId(), nouveauNom, nouveauType, nouvelEtat, nouvelUtilisateur);
        try {
            serviceRessource.modifier(res);
            showConfirmationAlert("Succès", "Ressource mise à jour avec succès !");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessource.fxml");
            typeField.getScene().setRoot(parent);
        } catch (SQLException | IOException e) {
            showErrorAlert("Erreur", "Erreur lors de la modification de la ressource : " + e.getMessage());
        }
    }

    public void retour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AfficherRessource.fxml");
            typeField.getScene().setRoot(parent);
        } catch (IOException e) {
            showErrorAlert("Erreur", "Une erreur est survenue lors du chargement de la page.");
        }
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
        nomField.setText(ressource.getNom());
        typeField.setText(ressource.getType());
        etatComboBox.setValue(ressource.getEtat());
        utilisateurField.setText(ressource.getUtilisateur());
    }

    public void setIdRessource(int idRessource) {
        this.idRessource = idRessource;
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
