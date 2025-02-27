package controllers;

import entities.Formation;
import entities.MyListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import services.ServiceFormation;
import utils.alertMessage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FormationsControllerRH implements Initializable {
    @FXML
    private VBox chosenFormationCard;

    @FXML
    private Label formationNameLable;

    @FXML
    private Label formationPriceLabel;

    @FXML
    private ImageView formationImg;

    @FXML
    private ScrollPane scroll;

    @FXML
    private GridPane grid;

    private Formation selectedFormation;

    private alertMessage alert = new alertMessage();

    @FXML
    void modifierBtn(ActionEvent event) {

        try {
            serviceFormation.modifier(selectedFormation);
            // Charger la fenêtre de modification (modifierEntretien.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root=loader.load();
            Controller controller = loader.getController();
            ModifierFormation con=controller.loadPage("/ModifierFormation.fxml").getController();
            con.setFormation(selectedFormation);
            grid.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            alert.errorMessage("Impossible d'ouvrir la fenêtre de modification!");
        } catch (SQLException e) {
            e.printStackTrace();
            alert.errorMessage("Impossible d'effectuer' la modification!");
            throw new RuntimeException(e);
        }


    }

    @FXML
    void onParticipantsBtn(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent parent=loader.load();
            Controller controller = loader.getController();
            ParticipantsController con=controller.loadPage("/participants.fxml").getController();
            con.setIdFormation(selectedFormation.getId());
            grid.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void supprimerBtn(ActionEvent event) {
        try {
            serviceFormation.supprimer(selectedFormation.getId());
            refreshFormations();  // Rafraîchir l'affichage
            alert.successMessage("Formation supprimé avec succès !");
            if (formations.size() > 0) {
                setChosenFormation(formations.get(0));
                myListener = new MyListener() {
                    @Override
                    public void onClickListener(Formation formation) {
                        setChosenFormation(formation);
                    }
                };
            }
            //showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation supprimé avec succès !");
        } catch (SQLException e) {
            //showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de supprimer la formation : " + e.getMessage());
            e.printStackTrace();
            alert.errorMessage("Impossible de supprimer la formation : " + e.getMessage());
        }
    }

    @FXML
    void onajouterBtn(ActionEvent event) {
        try {
            // Charger la scène pour l'ajout d'une offre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AjoutFormation.fxml");
            grid.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            //showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout.");
            alert.errorMessage("Impossible de ajouter la formation : " + e.getMessage());
        }
    }

    private MyListener myListener;
    private List<Formation> formations = new ArrayList<>();
    private Image image;
    private ServiceFormation serviceFormation =new ServiceFormation();

    private List<Formation> getData() {
        try {
            formations=serviceFormation.afficher();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return formations;
    }

    private void setChosenFormation(Formation formation) {
        formationNameLable.setText(formation.getTitre());
        //formationPriceLabel.setText(Main.CURRENCY + formation.getPrice());
        //image = new Image(getClass().getResourceAsStream(formation.getImgSrc()));
        //formationImg.setImage(image);
        chosenFormationCard.setStyle("-fx-background-color: #" + "F16C31" + ";\n" +
                "    -fx-background-radius: 30;");
        selectedFormation = formation;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formations.addAll(getData());
        if (formations.size() > 0) {
            setChosenFormation(formations.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Formation formation) {
                    setChosenFormation(formation);
                }
            };
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
                fxmlLoader.setLocation(getClass().getResource("/items.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                FormationItemsController itemController = fxmlLoader.getController();
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

}
