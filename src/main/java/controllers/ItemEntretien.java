package controllers;

import entities.Entretien;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import services.ServiceEntretien;

import java.io.IOException;
import java.sql.SQLException;

public class ItemEntretien {

    @FXML
    private Label StatutEntretien;
    @FXML
    private Label DateEntretien;
    @FXML
    private Label TypeEntretien;
    @FXML
    private Label Localisation;
    @FXML
    private Label HeureEntretien;
    @FXML
    private Label LienMeet;

    private Entretien entretien;
    private Affentretien affentretien;
    @FXML
    private VBox anchorEnt;

    public void setData(Entretien entretien) {
        this.entretien = entretien;
        DateEntretien.setText(entretien.getDate().toString());
        HeureEntretien.setText(entretien.getHeure().toString());
        TypeEntretien.setText(entretien.getType().toString());
        StatutEntretien.setText(entretien.getStatut().toString());
        // Afficher "N/A" si le lien Meet est null ou vide
        LienMeet.setText(entretien.getLien_meet() != null && !entretien.getLien_meet().isEmpty() ? entretien.getLien_meet() : "N/A");

        // Afficher "N/A" si la localisation est null ou vide
        Localisation.setText(entretien.getLocalisation() != null && !entretien.getLocalisation().isEmpty() ? entretien.getLocalisation() : "N/A");
    }

    public void setAffentretien(Affentretien affentretien) {
        this.affentretien = affentretien;
    }

    @FXML
    public void modifierEnt(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        ModifierEntretien modifierEntretien = controller.loadPage("/modifierEntretien.fxml").getController();
        modifierEntretien.setEntretien(entretien); // Passer l'objet Entretien sélectionné
        anchorEnt.getScene().setRoot(root);
    }

    @FXML
    public void deleteEnt(Event event) {
        try {
            ServiceEntretien serviceEntretien = new ServiceEntretien();
            serviceEntretien.supprimer(entretien.getIdEntretien());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été supprimé avec succès !");

            affentretien.refresh(); // Rafraîchir la liste des entretiens
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void AffEntEva(Event event) {
        // Récupérer l'objet Entretien associé à cet élément
        Entretien entretien = this.entretien; // Utiliser l'entretien déjà défini dans setData
        System.out.println("Redirection vers les évaluations de l'entretien : " + entretien.getIdEntretien());

        // Charger la vue AfficheEvaluation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            AfficheEvaluation affichageEvaluController = controller.loadPage("/afficheevaluation.fxml").getController();

            // Passer l'ID de l'entretien sélectionné au contrôleur AfficheEvaluation
            affichageEvaluController.setIdEntretien(entretien.getIdEntretien());

            // Changer la scène actuelle pour afficher la vue des évaluations
            anchorEnt.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de la vue des évaluations", e);
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}