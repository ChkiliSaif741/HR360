package controllers;

import com.itextpdf.text.DocumentException;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import services.ServiceAnalyse;
import utils.ExportExcel;
import utils.ExportPDF;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class TableauDeBordController {

    @FXML
    private Label labelTotalReservations;

    @FXML
    private Label labelRessourcesPopulaires;

    @FXML
    private PieChart pieChart; // Remplacement de BarChart par PieChart

    private ServiceAnalyse serviceAnalyse;

    @FXML
    public void initialize() {
        serviceAnalyse = new ServiceAnalyse();

        try {
            // Récupérer les données
            int totalReservations = serviceAnalyse.getNombreTotalReservations();
            Map<String, Double> tauxOccupation = serviceAnalyse.getTauxOccupation();

            // Afficher les données
            labelTotalReservations.setText("Total des Réservations : " + totalReservations);
            StringBuilder ressourcesPopulaires = new StringBuilder("Ressources les Plus Populaires : ");
            for (String ressource : serviceAnalyse.getRessourcesPopulaires()) {
                ressourcesPopulaires.append(ressource).append(", ");
            }
            labelRessourcesPopulaires.setText(ressourcesPopulaires.toString());

            // Afficher le taux d'occupation dans le PieChart
            for (Map.Entry<String, Double> entry : tauxOccupation.entrySet()) {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                pieChart.getData().add(slice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exporterEnExcel() {
        try {
            // Récupérer les données
            Map<String, Double> tauxOccupation = serviceAnalyse.getTauxOccupation();

            // Exporter en Excel
            ExportExcel.exporterTauxOccupation(tauxOccupation, "TableauDeBord.xlsx");

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportation réussie");
            alert.setHeaderText(null);
            alert.setContentText("Le tableau de bord a été exporté en Excel avec succès !");
            alert.showAndWait();

        } catch (SQLException | IOException e) {
            e.printStackTrace();

            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de l'exportation en Excel.");
            alert.showAndWait();
        }
    }

    @FXML
    public void exporterEnPDF() {
        try {
            // Récupérer les données
            Map<String, Double> tauxOccupation = serviceAnalyse.getTauxOccupation();

            // Exporter en PDF
            ExportPDF.exporterTauxOccupation(tauxOccupation, "TableauDeBord.pdf");

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportation réussie");
            alert.setHeaderText(null);
            alert.setContentText("Le tableau de bord a été exporté en PDF avec succès !");
            alert.showAndWait();

        } catch (SQLException | IOException | DocumentException e) {
            e.printStackTrace();

            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de l'exportation en PDF.");
            alert.showAndWait();
        }
    }
}