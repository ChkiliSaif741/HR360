package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ImageView Exit;

    @FXML
    private Label Menu;

    @FXML
    private Label MenuClose;

    @FXML
    private AnchorPane slider;

    @FXML
    private BorderPane mainPane;


    @FXML
    private JFXButton favoris;

    @FXML
    private JFXButton formationBtn;

    @FXML
    private JFXButton formationbtn;




    FXMLLoader loadPage(String page) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        try {
            Pane view=loader.load();
            mainPane.setCenter(view);
            return loader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void onFormationsEMPBtn(ActionEvent event) {
        try {
            loadPage("/FormationsEMP.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    void onFormationsRHBtn(ActionEvent event) {
        try {
            loadPage("/FormationsRH.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void onParticipationsBtn(ActionEvent event) {
        try {
            loadPage("/participations.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void onFavorisBtn(ActionEvent event) {
        try {
            loadPage("/FavoriteFormationsEMP.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    void onInfoBtn(ActionEvent event) {
        try {
            loadPage("/profilEMP.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    void AffichUser(ActionEvent event) {
        try {
            loadPage("/Display.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @FXML
    void onLogoutbtn(ActionEvent event) {
        // Créer une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Déconnexion");
        alert.setHeaderText("Vous allez vous déconnecter !");
        alert.setContentText("Êtes-vous sûr de vouloir quitter votre session ?");

        // Attendre la réponse de l'utilisateur
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) { // Si l'utilisateur confirme
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Login");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @FXML
    void AffichEmployes(ActionEvent event) {
        try {
            loadPage("/test.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void AffichProjet(ActionEvent event) {
        try {
            loadPage("/AffichageProjet.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Exit.setOnMouseClicked(event -> {
            System.exit(0);
        });
        slider.setTranslateX(0);
        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(0);
            slide.play();

            slider.setTranslateX(-176);

            slide.setOnFinished((ActionEvent e)-> {
                Menu.setVisible(false);
                MenuClose.setVisible(true);
            });
        });

        MenuClose.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(-176);
            slide.play();

            slider.setTranslateX(0);

            slide.setOnFinished((ActionEvent e)-> {
                Menu.setVisible(true);
                MenuClose.setVisible(false);
            });
        });
    }
}
