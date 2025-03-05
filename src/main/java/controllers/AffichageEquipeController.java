package controllers;

import entities.Equipe;
import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import services.EquipeService;
import services.ServiceProjet;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AffichageEquipeController implements Initializable {

    @FXML
    private TextField SearchBar;

    @FXML
    private Button btnAdd;

    @FXML
    private GridPane gridEquipe;

    @FXML
    private ScrollPane scrollEquipe;

    @FXML
    void AjoutEquipe(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent parent = loader.load();
            Controller con=loader.getController();
            con.loadPage("/AjoutEquipe.fxml");
            scrollEquipe.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void RefreshPage(ActionEvent event) {
        refresh();
        SearchBar.clear();
    }

    @FXML
    void SearchEquipe(KeyEvent event) {
        try {
            equipes.clear();
            getEquipes();
            List<Equipe> equipesearched = new ArrayList<>();
            equipesearched.addAll(equipes.stream().filter(p->p.getNom().startsWith(SearchBar.getText())).toList());
            equipes.clear();
            equipes.addAll(equipesearched);
            loadEquipes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private List<Equipe> equipes=new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getEquipes();
            loadEquipes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getEquipes() throws SQLException
    {
        EquipeService service = new EquipeService();
        equipes.addAll(service.getAllEquipes());
    }
    private void loadEquipes() {
        gridEquipe.getChildren().clear(); // Vider le GridPane avant de recharger les equipes
        try {
            int column = 0;
            int row = 0;
            for (int i = 0; i < equipes.size(); i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemEquipe.fxml"));
                AnchorPane anchorPane = loader.load();
                ItemEquipeController itemequipeController = loader.getController();
                itemequipeController.SetData(equipes.get(i));
                itemequipeController.setParentController(this);
                gridEquipe.add(anchorPane, column, row);
                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
                gridEquipe.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridEquipe.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridEquipe.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridEquipe.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridEquipe.setMaxWidth(Region.USE_PREF_SIZE);
                gridEquipe.setMaxHeight(Region.USE_PREF_SIZE);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refresh() {
        equipes.clear();
        try {
            getEquipes();
            loadEquipes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
