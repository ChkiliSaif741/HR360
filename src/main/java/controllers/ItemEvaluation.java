package controllers;

import entities.QuizQuestion;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import entities.Evaluation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.QuizApiService;
import services.ServiceEvaluation;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEvaluation {

    private AfficheEvaluation afficheEvaluation ;
    @FXML
    private AnchorPane anchorEva;
    @FXML
    private Label scorequiz;
    @FXML
    private Label titreEva;
    @FXML
    private ImageView star1;
    @FXML
    private ImageView star4;
    @FXML
    private ImageView star5;
    @FXML
    private ImageView star2;
    @FXML
    private ImageView star3;

    public void setEvaluation(AfficheEvaluation afficheEvaluation) {
        this.afficheEvaluation = afficheEvaluation;
    }

    @javafx.fxml.FXML
    private Label dateEvaluation;
    @javafx.fxml.FXML
    private Label noteTechnique;
    @javafx.fxml.FXML
    private Label noteSoftSkills;
    @javafx.fxml.FXML
    private Label commentaire;

    private Evaluation evaluation;

    private int idEntretien;
    private int idEvaluation;

    List<QuizQuestion> quizQuestions;

    private ServiceEvaluation serviceEvaluation;




    private static Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();

    public void setData(Evaluation evaluation) {
        this.evaluation = evaluation;
        idEntretien = evaluation.getIdEntretien();
        titreEva.setText(evaluation.getTitreEva());
        noteTechnique.setText("" + evaluation.getNoteTechnique());
        noteSoftSkills.setText("" + evaluation.getNoteSoftSkills());
        commentaire.setText(evaluation.getCommentaire().toString());

        dateEvaluation.setText("" + evaluation.getDateEvaluation().toString());
        scorequiz.setText("" + evaluation.getScorequiz());
        // Mettre à jour les étoiles en fonction du score du quiz
        updateStars(evaluation.getScorequiz());
    }


    @javafx.fxml.FXML
    public void modifierEva(Event event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvaluation.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
         ModifierEvaluation ModifEvaluation = controller.loadPage("/modifierEvaluation.fxml").getController();
        //controller.getIdEntretien(idEntretien);
        //controller.getIdEvaluation(idEvaluation);
        ModifEvaluation.setIdEntretien(idEntretien);
        ModifEvaluation.setEvaluationData(evaluation); // Passer l'objet Evaluation sélectionné

        // Passer l'entretien sélectionné au contrôleur de ajoute

        //controller.setIdEntretien(idEntretien);
        commentaire.getScene().setRoot(root);



    }

    @javafx.fxml.FXML
    public void deleteEva(Event event) {
        try {
            // Créer une instance du service d'évaluation
            ServiceEvaluation serviceEvaluation = new ServiceEvaluation();
            // Supprimer l'évaluation par son ID
            serviceEvaluation.supprimer(evaluation.getIdEvaluation());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Évaluation supprimé avec succès !");
            afficheEvaluation.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @Deprecated
    public void showQuiz(MouseEvent mouseEvent) {
        if (evaluation == null) {
            System.out.println("Aucune évaluation sélectionnée.");
            return;
        }

        if (evaluation.getScorequiz() > 0 || evaluation.getCommentaire() != utils.commentaire.EN_ATTENTE) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Quiz déjà validé");
            alert.setHeaderText(null);
            alert.setContentText("Ce quiz a déjà été validé et ne peut plus être accédé.");
            alert.showAndWait();
            return;
        }

        List<QuizQuestion> quizQuestions = evaluation.getQuestions();

        if (quizQuestions == null || quizQuestions.isEmpty()) {
            System.out.println("Aucun quiz associé à cette évaluation.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/QuizView.fxml"));
            Parent root = loader.load();

            QuizController quizController = loader.getController();
            quizController.setQuizData(quizQuestions, evaluation);
            quizController.setAfficheEvaluation(this.afficheEvaluation);
            quizController.setItemEvaluation(this); // Passer l'instance actuelle de ItemEvaluation


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz associé à l'évaluation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la vue du quiz : " + e.getMessage());
        }
    }

    @Deprecated
    void handleAssociateQuiz(Event event) {
        if (evaluation == null) {
            System.out.println("Aucune évaluation sélectionnée.");
            return;
        }

        if (evaluation.getCommentaire() != utils.commentaire.EN_ATTENTE) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Quiz déjà validé");
            alert.setHeaderText(null);
            alert.setContentText("Ce quiz a déjà été validé et ne peut plus être associé a ce quiz.");
            alert.showAndWait();
            return;
        }

        QuizApiService quizApiService = new QuizApiService();
        ServiceEvaluation serviceEvaluation = new ServiceEvaluation();

        try {
            String category = getQuizCategoryFromTitle(evaluation.getTitreEva());
            List<QuizQuestion> quizQuestions = quizApiService.fetchQuizQuestions(category, "easy", 10);
            evaluation.setQuestions(quizQuestions);
            serviceEvaluation.modifier(evaluation);

            System.out.println("Quiz associé avec succès !");

            if (afficheEvaluation != null) {
                afficheEvaluation.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'association du quiz : " + e.getMessage());
        }
    }

    @Deprecated
    private String getQuizCategoryFromTitle(String title) {
        String lowerTitle = title.toLowerCase();

        if (lowerTitle.contains("linux") || lowerTitle.contains("ubuntu")) {
            return "Linux";
        } else if (lowerTitle.contains("java") || lowerTitle.contains("javascript") || lowerTitle.contains("nodejs")) {
            return "Programming";
        } else if (lowerTitle.contains("sql") || lowerTitle.contains("mysql") || lowerTitle.contains("postgres")) {
            return "Databases";
        } else if (lowerTitle.contains("docker") || lowerTitle.contains("kubernetes") || lowerTitle.contains("terraform") || lowerTitle.contains("openshift")) {
            return "DevOps";
        } else if (lowerTitle.contains("php") || lowerTitle.contains("laravel") || lowerTitle.contains("wordpress") || lowerTitle.contains("cpanel")) {
            return "Web Development";
        } else if (lowerTitle.contains("bash")) {
            return "Scripting";
        } else if (lowerTitle.contains("react") || lowerTitle.contains("vuejs") || lowerTitle.contains("next.js") || lowerTitle.contains("html")) {
            return "Frontend";
        } else if (lowerTitle.contains("apache kafka")) {
            return "Big Data";
        } else if (lowerTitle.contains("python") || lowerTitle.contains("django")) {
            return "Python";
        } else if (lowerTitle.contains("bash")) {
            return "BASH";
        } else if (lowerTitle.contains("cpanel")) {
            return "cPanel";
        } else if (lowerTitle.contains("django")) {
            return "Django";
            //
        } else if (lowerTitle.contains("docker")) {
            return "Docker";
        } else if (lowerTitle.contains("html")) {
            return "HTML";
        } else if (lowerTitle.contains("javascript")) {
            return "JavaScript";
            //
        } else if (lowerTitle.contains("kubernetes")) {
            return "Kubernetes";
        } else if (lowerTitle.contains("laravel")) {
            return "Laravel";
        } else if (lowerTitle.contains("mysql")) {
            return "MySQL";
        } else if (lowerTitle.contains("next.js")) {
            return "Next.js";
        } else if (lowerTitle.contains("nodejs")) {
            return "nodeJS";
            //
        } else if (lowerTitle.contains("openshift")) {
            return "Openshift";
        } else if (lowerTitle.contains("php")) {
            return "PHP";
        } else if (lowerTitle.contains("postgres")) {
            return "Postgres";
        } else if (lowerTitle.contains("python")) {
            return "Python";
        } else if (lowerTitle.contains("react")) {
            return "React";
            //
        } else if (lowerTitle.contains("terraform")) {
            return "Terraform";
            //
        } else if (lowerTitle.contains("ubuntu")) {
            return "Ubuntu";
        } else if (lowerTitle.contains("vuejs")) {
            return "VueJS";
        } else if (lowerTitle.contains("wordpress")) {
            return "WordPress";
        } else {
            return "General";
        }
    }



    //rate

    public void updateStars(int score) {
        // Réinitialiser toutes les étoiles à l'état vide
        star1.setImage(new Image(getClass().getResourceAsStream("/image/rate.png")));
        star2.setImage(new Image(getClass().getResourceAsStream("/image/rate.png")));
        star3.setImage(new Image(getClass().getResourceAsStream("/image/rate.png")));
        star4.setImage(new Image(getClass().getResourceAsStream("/image/rate.png")));
        star5.setImage(new Image(getClass().getResourceAsStream("/image/rate.png")));

        // Définir les étoiles à remplir en fonction du score
        if (score >= 1 && score < 3) {
            star1.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
        } else if (score >= 3 && score < 5) {
            star1.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star2.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
        } else if (score >= 5 && score < 7) {
            star1.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star2.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star3.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
        } else if (score >= 7 && score < 9) {
            star1.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star2.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star3.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star4.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
        } else if (score == 10) {
            star1.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star2.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star3.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star4.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
            star5.setImage(new Image(getClass().getResourceAsStream("/image/ratej.png")));
        }
    }



}
