package controllers;

import entities.Projet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
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

    private List<Projet> projets=new ArrayList<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServiceProjet serviceProjet = new ServiceProjet();
        try {
            projets.addAll(serviceProjet.afficher());
            int column=0;
            int row=0;
            for (int i=0;i<projets.size();i++){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemProjet.fxml"));
                AnchorPane anchorPane = loader.load();
                ItemProjetController itemProjetController = loader.getController();
                itemProjetController.SetData(projets.get(i));
                gridProjet.add(anchorPane,column++,row);
                if (column==3)
                {
                    column=0;
                    row++;
                }

                gridProjet.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridProjet.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridProjet.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridProjet.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridProjet.setMaxWidth(Region.USE_PREF_SIZE);
                gridProjet.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane,new Insets(15));
            }


        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }


}
