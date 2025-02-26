package controllers;

import com.dlsc.gemsfx.CalendarView;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangeView;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    private ProgressBar progressTasks;

    private int idProjet;

    private int NbrtachesTerminee;

    private int NbrtachesEn_cours;

    private int NbrtachesA_faire;

    private int Nbrtaches;

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        loadInfo();
        loadBarChart();
    }

    @FXML
    void ViewTasks(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
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
        try {
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

}

