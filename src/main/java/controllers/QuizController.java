package controllers;

import entities.Evaluation;
import entities.QuizQuestion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceEvaluation;
import utils.commentaire;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class QuizController {
  // Conteneur pour les questions

    private List<QuizQuestion> quizQuestions; // Liste des questions
    private Evaluation evaluation; // L'évaluation associée
    private AfficheEvaluation afficheEvaluation; // Référence à AfficheEvaluation pour rafraîchir l'affichage
    private AfficheEvaluationFront afficheEvaluationFront; // Référence à AfficheEvaluation pour rafraîchir l'affichage
    private ServiceEvaluation serviceEvaluation = new ServiceEvaluation(); // Service pour gérer les évaluations
    private ItemEvaluation itemEvaluation;
    private ItemEvaluationFront itemEvaluationFront;

    public void setItemEvaluation(ItemEvaluation itemEvaluation) {
        this.itemEvaluation = itemEvaluation;
    }
    public void setItemEvaluationFront(ItemEvaluationFront itemEvaluationFront) {
        this.itemEvaluationFront = itemEvaluationFront;
    }
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button validateButton;
    @FXML
    private VBox quizContainer;

    public void setAfficheEvaluation(AfficheEvaluation afficheEvaluation) {
        this.afficheEvaluation = afficheEvaluation;
    }
    public void setAfficheEvaluationFront(AfficheEvaluationFront afficheEvaluationFront) {
        this.afficheEvaluationFront = afficheEvaluationFront;
    }

    /**
     * Méthode pour définir les questions du quiz et l'évaluation associée.
     * @param quizQuestions La liste des questions du quiz.
     * @param evaluation L'évaluation associée.
     */
    public void setQuizData(List<QuizQuestion> quizQuestions, Evaluation evaluation) {
        this.quizQuestions = quizQuestions;
        this.evaluation = evaluation;

        if (quizQuestions == null || quizQuestions.isEmpty()) {
            quizContainer.getChildren().add(new Label("Aucune question disponible."));
            return;
        }

        for (QuizQuestion question : quizQuestions) {
            Label questionLabel = new Label("Question: " + question.getQuestion());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            ToggleGroup toggleGroup = new ToggleGroup();
            VBox answersBox = new VBox(5);

            Map<String, String> answers = question.getAnswers();
            for (Map.Entry<String, String> entry : answers.entrySet()) {
                if (entry.getValue() != null) {
                    RadioButton radioButton = new RadioButton(entry.getKey() + ": " + entry.getValue());
                    radioButton.setToggleGroup(toggleGroup);
                    answersBox.getChildren().add(radioButton);
                }
            }

            // Stocker le ToggleGroup pour vérifier les réponses plus tard
            answersBox.setUserData(toggleGroup);

            quizContainer.getChildren().addAll(questionLabel, answersBox);
        }
    }

    @FXML
    private void handleValidate() throws IOException {
        int score = 0;
        int totalQuestions = quizQuestions.size();

        // Calculer le score
        for (int i = 0; i < totalQuestions; i++) {
            QuizQuestion question = quizQuestions.get(i);
            VBox answersBox = (VBox) quizContainer.getChildren().get(2 * i + 1); // Chaque question a un Label et un VBox

            ToggleGroup toggleGroup = (ToggleGroup) answersBox.getUserData();
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();

            if (selectedRadioButton != null) {
                String selectedAnswer = selectedRadioButton.getText().split(":")[0].trim();
                if (question.getCorrect_answers().get(selectedAnswer + "_correct").equals("true")) {
                    score++;
                }
            }
        }

        // Mettre à jour l'évaluation avec le score du quiz
        evaluation.setScorequiz(score); // Enregistrer le score du quiz

        // Mettre à jour le statut du test technique en fonction du score
        if (score >= 7) {
            evaluation.setCommentaire(commentaire.ACCEPTE);
        } else {
            evaluation.setCommentaire(commentaire.REFUSE);
        }



        // Mettre à jour l'évaluation dans la base de données
        try {
            serviceEvaluation.modifier(evaluation); // Mettre à jour l'évaluation
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la mise à jour de l'évaluation : " + e.getMessage());
        }
// Mettre à jour l'évaluation avec le score du quiz
        evaluation.setScorequiz(score); // Enregistrer le score du quiz

        // Afficher le résultat à l'utilisateur
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Résultat du quiz");
        alert.setHeaderText(null);
        alert.setContentText("Votre score est : " + score + " / " + totalQuestions +
                "\nStatut de l'évaluation : " + (score >= 7 ? "Accepté" : "Refusé"));
        alert.showAndWait();

        // Fermer la fenêtre du quiz après validation
        /*Stage stage = (Stage) quizContainer.getScene().getWindow();
        stage.close();*/

        // Fermer la fenêtre du quiz après validation
        Stage currentStage = (Stage) quizContainer.getScene().getWindow();
        currentStage.close();

        // Rouvrir la fenêtre afficheevaluationFront avec l'évaluation de l'entretien précédent
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarCAN.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        AfficheEvaluationFront afficheEvaluationFrontController = controller.loadPage("/afficheevaluationFront.fxml").getController();

        // Passer l'ID de l'entretien pour afficher l'évaluation correspondante
        afficheEvaluationFrontController.setIdEntretien(evaluation.getIdEntretien());

        // Rafraîchir les données dans AfficheEvaluationFront
        afficheEvaluationFrontController.refresh();

        // Créer une nouvelle scène et afficher la vue
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Évaluations");
        stage.show();


            // Rafraîchir l'affichage après la validation
        if (afficheEvaluation != null) {
            afficheEvaluation.refresh();
        }
        // Mettre à jour les étoiles si itemEvaluation n'est pas null
        if (itemEvaluation != null) {
            itemEvaluation.updateStars(score); // Mettre à jour les étoiles
        }// Rafraîchir l'affichage après la validation
        if (afficheEvaluationFront != null) {
            afficheEvaluationFront.refresh();
        }
        // Mettre à jour les étoiles si itemEvaluation n'est pas null
        if (itemEvaluationFront != null) {
            itemEvaluationFront.updateStars(score); // Mettre à jour les étoiles
        }



    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}