package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import services.ServiceCandidature;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StatistiquesCandidatures implements Initializable {
    @FXML
    private PieChart pieChart; // Assurez-vous que cet ID est bien le même que dans le FXML

    private final ServiceCandidature serviceCandidature = new ServiceCandidature();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            chargerStatistiques();
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }

    private void chargerStatistiques() throws SQLException {
        int enAttente = serviceCandidature.compterParStatut("En attente");
        int accepte = serviceCandidature.compterParStatut("Accepté");
        int refuse = serviceCandidature.compterParStatut("Refusé");

        ObservableList<PieChart.Data> dataList = FXCollections.observableArrayList(
                new PieChart.Data("En attente", enAttente),
                new PieChart.Data("Accepté", accepte),
                new PieChart.Data("Refusé", refuse)
        );

        pieChart.setData(dataList);
        pieChart.setTitle("Statistiques des Candidatures");

        // Définir les couleurs pour chaque segment
        String[] colors = {"#7baddb", "#4CAF50", "#ff0000"}; // Bleu, Vert, Rouge

        // Appliquer les couleurs aux segments et à la légende
        for (int i = 0; i < pieChart.getData().size(); i++) {
            PieChart.Data data = pieChart.getData().get(i);
            String color = colors[i];

            // Appliquer la couleur au segment
            data.getNode().setStyle("-fx-pie-color: " + color + ";");

            // Ajouter un Tooltip
            Tooltip tooltip = new Tooltip((int) data.getPieValue() + " candidatures");
            Tooltip.install(data.getNode(), tooltip);

            // Effet au survol
            data.getNode().setOnMouseEntered((MouseEvent event) -> {
                data.getNode().setStyle("-fx-pie-color: " + color + "; -fx-opacity: 0.7;");
                tooltip.setText((int) data.getPieValue() + " candidatures");
            });

            data.getNode().setOnMouseExited((MouseEvent event) -> {
                data.getNode().setStyle("-fx-pie-color: " + color + "; -fx-opacity: 1;");
            });
        }

        // Appliquer les couleurs à la légende
        for (int i = 0; i < pieChart.getData().size(); i++) {
            PieChart.Data data = pieChart.getData().get(i);
            String color = colors[i];

            // Trouver le symbole de la légende correspondant
            pieChart.lookupAll(".chart-legend-item").forEach(legendItem -> {
                if (legendItem instanceof javafx.scene.layout.HBox) {
                    javafx.scene.layout.HBox hBox = (javafx.scene.layout.HBox) legendItem;
                    javafx.scene.Node labelNode = hBox.getChildren().get(1); // Le deuxième enfant est le Label
                    if (labelNode instanceof javafx.scene.control.Label) {
                        String labelText = ((javafx.scene.control.Label) labelNode).getText();

                        // Si le texte de la légende correspond au nom du segment, appliquer la couleur
                        if (labelText.equals(data.getName())) {
                            hBox.lookupAll(".chart-legend-symbol").forEach(symbol -> {
                                symbol.setStyle("-fx-fill: " + color + ";");
                            });
                        }
                    }
                }
            });
        }
    }
}
