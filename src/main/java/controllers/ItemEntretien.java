package controllers;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import entities.Entretien;
import entities.QuizQuestion;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.QuizApiService;
import services.ServiceEntretien;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();

    List<QuizQuestion> quizQuestions;



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
        modifierEntretien.setEntretien(entretien);
        modifierEntretien.setIdCandidature(entretien.getIdCandidature());// Passer l'objet Entretien sélectionné
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

    @FXML
    public void handleOpenMeet(MouseEvent mouseEvent) {
        if (entretien == null) {
            System.out.println("Aucun entretien sélectionné.");
            showAlert(Alert.AlertType.WARNING, "Aucun entretien", "Aucun entretien n'est sélectionné.");
            return;
        }

        String meetLink = entretien.getLien_meet();
        if (meetLink == null || meetLink.isEmpty()) {
            System.out.println("Aucun lien Google Meet disponible pour cet entretien.");
            showAlert(Alert.AlertType.WARNING, "Lien manquant", "Aucun lien Google Meet n'est disponible pour cet entretien.");
            return;
        }

        // Vérifier que le lien est un lien Google Meet valide
        if (!meetLink.startsWith("https://meet.google.com/")) {
            System.out.println("Le lien n'est pas un lien Google Meet valide : " + meetLink);
            showAlert(Alert.AlertType.ERROR, "Lien invalide", "Le lien n'est pas un lien Google Meet valide.");
            return;
        }

        try {
            // Charger la vue WebView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/webviewmeet.fxml"));
            Parent root = loader.load();

            // Passer le lien Google Meet au contrôleur Webviewmeet
            Webviewmeet webviewmeetController = loader.getController();
            webviewmeetController.setMeetLink(meetLink);

            // Créer une nouvelle fenêtre pour afficher la WebView
            Stage stage = new Stage();
            stage.setTitle("Google Meet - " + entretien.getIdEntretien());
            stage.setScene(new Scene(root, 924 , 634.4));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la fenêtre Google Meet : " + e.getMessage());
        }
    }




    /*
    @FXML
public void handleOpenMeet(MouseEvent mouseEvent) {
    if (entretien == null) {
        System.out.println("Aucun entretien sélectionné.");
        showAlert(Alert.AlertType.WARNING, "Aucun entretien", "Aucun entretien n'est sélectionné.");
        return;
    }

    String meetLink = entretien.getLien_meet();
    if (meetLink == null || meetLink.isEmpty()) {
        System.out.println("Aucun lien Google Meet disponible pour cet entretien.");
        showAlert(Alert.AlertType.WARNING, "Lien manquant", "Aucun lien Google Meet n'est disponible pour cet entretien.");
        return;
    }

    // Vérifier que le lien est un lien Google Meet valide
    if (!meetLink.startsWith("https://meet.google.com/")) {
        System.out.println("Le lien n'est pas un lien Google Meet valide : " + meetLink);
        showAlert(Alert.AlertType.ERROR, "Lien invalide", "Le lien n'est pas un lien Google Meet valide.");
        return;
    }

    try {
        // Ouvrir le lien dans le navigateur par défaut de l'utilisateur
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(meetLink));
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du lien Google Meet : " + e.getMessage());
    }
}
     */

}