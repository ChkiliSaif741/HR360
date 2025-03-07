package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.EquipeService;
import services.ServiceCandidature;
import services.ServiceProjet;
import services.ServiceUtilisateur;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class dashController implements Initializable {

    @FXML
    private Text NbrEmployes;

    @FXML
    private Text NbrEquipes;

    @FXML
    private VBox body;

    @FXML
    private GridPane footer;

    @FXML
    private GridPane gridTiles;

    @FXML
    private Text nbrCandidatures;

    @FXML
    private Text nbrProjets;

    @FXML
    private StackPane root;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String css = getClass().getResource("/style/css/colors.css").toExternalForm();
        root.getStylesheets().add(css);

        ServiceProjet projet = new ServiceProjet();
        EquipeService equipeService = new EquipeService();
        ServiceCandidature serviceCandidature = new ServiceCandidature();
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        try {
            nbrProjets.setText(String.valueOf(projet.afficher().size()));
            NbrEquipes.setText(String.valueOf(equipeService.getAllEquipes().size()));
            nbrCandidatures.setText(String.valueOf(serviceCandidature.afficher().size()));
            NbrEmployes.setText(String.valueOf(serviceUtilisateur.afficher().stream().filter(u->u.getRole().equals("Employe")).toList().size()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}