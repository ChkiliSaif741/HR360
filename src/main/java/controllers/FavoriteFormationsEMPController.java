package controllers;

import entities.Formation;
import entities.MyListener;
import entities.Participation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceFormation;
import services.ServiceParticipation;
import entities.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FavoriteFormationsEMPController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scroll;


    @FXML
    private VBox chosenFormationCard;

    @FXML
    private Label formationNameLable;

    private Formation selectedFormation;

    private int idFormationSelectionnee;



    private List<Formation> formations = new ArrayList<>();
    private MyListener myListener;

    private ServiceFormation serviceFormation = new ServiceFormation();
    private ServiceParticipation serviceParticipation = new ServiceParticipation();

    private int idUser = Session.getInstance().getIdUtilisateur(); // ID de l'utilisateur connecté



    private List<Formation> getData() {
        try {
            formations=serviceFormation.afficher().stream().filter(Formation::isFavorite).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return formations;
    }

    private void setChosenFormation(Formation formation) {
        formationNameLable.setText(formation.getTitre());
        chosenFormationCard.setStyle(
                "-fx-background-color: #146886;\n" + // Couleur de fond
                        "    -fx-background-radius: 30;\n" + // Bord arrondi
                        "    -fx-border-color: WHITE;\n" + // Couleur de la bordure
                        "    -fx-border-width: 0px 0px 0px 3px;\n" + // Largeur de la bordure (gauche uniquement)
                        "    -fx-border-radius: 30;" // Bord arrondi pour la bordure
        );
        selectedFormation = formation;
        idFormationSelectionnee = formation.getId(); // Mets à jour l'ID de la formation sélectionnée
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vérifier si la session est initialisée
        if (Session.getInstance() == null || Session.getInstance().getIdUtilisateur() == 0) {
            try {
                // Rediriger vers la page de connexion
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) grid.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        idUser = Session.getInstance().getIdUtilisateur();
        formations.addAll(getData());
        if (formations.size() > 0) {
            setChosenFormation(formations.get(0));
            myListener = formation -> setChosenFormation(formation);
        }
        refreshFormations();

    }


    public void refreshFormations(){
        grid.getChildren().clear();
        formations.clear();
        formations.addAll(getData());
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < formations.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/favoriteitems.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                FavoriteFormationItemsController itemController = fxmlLoader.getController();
                itemController.setParentControler(this);
                itemController.setData(formations.get(i),myListener);


                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
            Parent parent = loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/FormationsEMP.fxml");
            grid.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}