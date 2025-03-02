package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import services.ServiceAnalyse;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class TableauDeBordController implements Initializable {

    @FXML
    private Label labelTotalReservations;

    @FXML
    private Label labelRessourcesPopulaires;

    @FXML
    private BarChart<String, Number> barChart;

    private ServiceAnalyse serviceAnalyse;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceAnalyse = new ServiceAnalyse();

        try {
            // Afficher le nombre total de réservations
            int totalReservations = serviceAnalyse.getNombreTotalReservations();
            labelTotalReservations.setText("Total des Réservations : " + totalReservations);

            // Afficher les ressources les plus populaires
            StringBuilder ressourcesPopulaires = new StringBuilder("Ressources les Plus Populaires : ");
            for (String ressource : serviceAnalyse.getRessourcesPopulaires()) {
                ressourcesPopulaires.append(ressource).append(", ");
            }
            labelRessourcesPopulaires.setText(ressourcesPopulaires.toString());

            // Afficher le taux d'occupation dans le BarChart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Taux d'Occupation");
            for (Map.Entry<String, Double> entry : serviceAnalyse.getTauxOccupation().entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}