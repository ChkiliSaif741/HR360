package controllers;

import entities.Projet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import services.ServiceProjet;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class AffichageProjetController implements Initializable {

    @FXML
    private GridPane gridProjet;

    @FXML
    private ScrollPane scrollProjet;

    @FXML
    private TextField SearchBar;

    private List<Projet> projets=new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getProjects();
            loadProjects();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getProjects() throws SQLException
    {
        ServiceProjet service = new ServiceProjet();
        projets.addAll(service.afficher());
    }

    private void loadProjects() {
        gridProjet.getChildren().clear(); // Vider le GridPane avant de recharger les projets
        try {
            int column = 0;
            int row = 0;
            for (int i = 0; i < projets.size(); i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemProjet.fxml"));
                AnchorPane anchorPane = loader.load();
                ItemProjetController itemProjetController = loader.getController();
                itemProjetController.SetData(projets.get(i));
                itemProjetController.setParentController(this);
                gridProjet.add(anchorPane, column, row);
                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
                gridProjet.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridProjet.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridProjet.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridProjet.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridProjet.setMaxWidth(Region.USE_PREF_SIZE);
                gridProjet.setMaxHeight(Region.USE_PREF_SIZE);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour rafraîchir la vue
    public void refresh() {
        projets.clear();
        try {
            getProjects();
            loadProjects();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AjoutProjet(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        try {
            Parent parent = loader.load();
            Controller con=loader.getController();
            con.loadPage("/AjoutProjet.fxml");
            scrollProjet.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void SearchProject(KeyEvent event) {
        try {
            projets.clear();
            getProjects();
            List<Projet> projetSearched = new ArrayList<>();
            projetSearched.addAll(projets.stream().filter(p->p.getNom().startsWith(SearchBar.getText())).toList());
            projets.clear();
            projets.addAll(projetSearched);
            loadProjects();
            System.out.println(projets);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void triOldest(ActionEvent event) {

    }

    @FXML
    void triRecent(ActionEvent event) {

    }

    @FXML
    void RefreshPage(ActionEvent event) {
        refresh();
    }

}
