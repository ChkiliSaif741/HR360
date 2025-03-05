package controllers;

import entities.Entretien;
import entities.QuizQuestion;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEntretienFront {
    @javafx.fxml.FXML
    private Label StatutEntretien;
    @javafx.fxml.FXML
    private Label DateEntretien;
    @javafx.fxml.FXML
    private Label TypeEntretien;
    @javafx.fxml.FXML
    private Label Localisation;
    @javafx.fxml.FXML
    private VBox anchorEnt;
    @javafx.fxml.FXML
    private Label HeureEntretien;
    @javafx.fxml.FXML
    private Label LienMeet;

    private Entretien entretien;
    private Affentretien affentretien;
    private static Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();

    List<QuizQuestion> quizQuestions;
    private Affentretienfront affentretienfront;

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



    public void setAffentretien(Affentretien affentretienfront) {
        this.affentretien = affentretienfront;
    }

    public void setAffentretienfront(Affentretienfront affentretienfront) {
        this.affentretienfront = affentretienfront;
    }


    @FXML
    public void AffEntEva(Event event) {
// Récupérer l'objet Entretien associé à cet élément
        Entretien entretien = this.entretien; // Utiliser l'entretien déjà défini dans setData
        System.out.println("Redirection vers les évaluations de l'entretien : " + entretien.getIdEntretien());

        // Charger la vue AfficheEvaluation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarCAN.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            AfficheEvaluationFront affichageEvaluController = controller.loadPage("/afficheevaluationFront.fxml").getController();

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


}
