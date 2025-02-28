package controllers;

import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangeView;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import services.EquipeService;
import services.ProjetEquipeService;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DetailsProjetController {

    @FXML
    private BarChart<String, Number> BarChartTasks;

    @FXML
    private Label DescriptionProjet;

    @FXML
    private DateRangeView DureeProjet;

    @FXML
    private Label PourcentProgress;

    @FXML
    private Label nomProjetL;

    @FXML
    private Button DissasocierBtn;

    @FXML
    private ProgressBar progressTasks;

    @FXML
    private Label nomEquipe;

    private int idProjet;

    private int NbrtachesTerminee;

    private int NbrtachesEn_cours;

    private int NbrtachesA_faire;

    private int Nbrtaches;

    @FXML
    private Button BtnAssocier;

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        loadInfo();
        loadBarChart();
    }

    @FXML
    void ViewTasks(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            AffichageTacheController affichageTacheController = controller.loadPage("/AffichageTache.fxml").getController();
            affichageTacheController.setIdProjet(idProjet);
            nomProjetL.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInfo() {
        ServiceProjet serviceProjet = new ServiceProjet();
        ProjetEquipeService serviceProjetEquipe = new ProjetEquipeService();
        EquipeService equipeService = new EquipeService();
        try {
            if (serviceProjetEquipe.getEquipeByProjet(this.idProjet) != null) {
                BtnAssocier.setVisible(false);
                nomEquipe.setVisible(true);
                nomEquipe.setText(equipeService.getEquipe(serviceProjetEquipe.getEquipeByProjet(idProjet)).getNom());
                DissasocierBtn.setVisible(true);
            }
            else {
                BtnAssocier.setVisible(true);
                nomEquipe.setVisible(false);
                DissasocierBtn.setVisible(false);
            }
            Projet projet = serviceProjet.getById(idProjet);
            nomProjetL.setText(projet.getNom());
            DescriptionProjet.setText(projet.getDescription());
            DateRange dateRange = new DateRange(projet.getNom(), projet.getDateDebut().toLocalDate(), projet.getDateFin().toLocalDate());
            DureeProjet.setValue(dateRange);
            double progress = CalculateProgress();
            progressTasks.setProgress(progress);
            int progressInt = (int) (progress * 100);
            PourcentProgress.setText(progressInt + " %");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double CalculateProgress() {
        ServiceTache serviceTache = new ServiceTache();
        try {
            List<Tache> taches = serviceTache.afficher().stream().filter(t -> t.getIdProjet() == idProjet).toList();
            Nbrtaches = taches.size();
            NbrtachesA_faire = taches.stream().filter(t -> t.getStatut() == TacheStatus.A_FAIRE).toList().size();
            NbrtachesEn_cours = taches.stream().filter(t -> t.getStatut() == TacheStatus.EN_COURS).toList().size();
            NbrtachesTerminee = taches.stream().filter(t -> t.getStatut() == TacheStatus.TERMINEE).toList().size();
            return (NbrtachesTerminee + NbrtachesEn_cours * 0.5) / Nbrtaches;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadBarChart() {
        BarChartTasks.getData().clear(); // Clear existing data

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Creating bars with the correct categories
        XYChart.Data<String, Number> aFaireBar = new XYChart.Data<>("À Faire", NbrtachesA_faire);
        XYChart.Data<String, Number> enCoursBar = new XYChart.Data<>("En Cours", NbrtachesEn_cours);
        XYChart.Data<String, Number> termineeBar = new XYChart.Data<>("Terminée", NbrtachesTerminee);

        // Add data to the series
        series.getData().addAll(aFaireBar, enCoursBar, termineeBar);
        BarChartTasks.getData().add(series);

        // Apply colors using inline styles directly after bars are added
        aFaireBar.getNode().setStyle("-fx-bar-fill: red;");
        enCoursBar.getNode().setStyle("-fx-bar-fill: orange;");
        termineeBar.getNode().setStyle("-fx-bar-fill: green;");
    }

    @FXML
    void BackToListProjet(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent parent=loader.load();
        Controller con=loader.getController();
        con.loadPage("/AffichageProjet.fxml");
        nomProjetL.getScene().setRoot(parent);
    }

    @FXML
    void AssocierEquipe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AssocierEquipe.fxml"));
            Parent root = loader.load();
            AssocierEquipeController controller = loader.getController();
            controller.setIdProjet(idProjet);
            Stage stage = new Stage();
            stage.setTitle("Associer une Équipe");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void DisaocierEquipe(ActionEvent event) throws SQLException {
        ProjetEquipeService serviceProjetEquipe = new ProjetEquipeService();
        serviceProjetEquipe.unassignProjetFromEquipe(idProjet);
        loadInfo();
    }
}

