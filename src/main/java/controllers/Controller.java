package controllers;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

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

    // Méthode pour charger une page dans le BorderPane
    FXMLLoader loadPage(String page) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        try {
            Pane view = loader.load();
            mainPane.setCenter(view);
            return loader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Exit.setOnMouseClicked(event -> System.exit(0));

        // Initialiser le slider visible
        slider.setTranslateX(0);
        Menu.setVisible(true);
        MenuClose.setVisible(false);

        // Cacher le slider quand on clique sur Menu
        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(-176);
            slide.play();

            slider.setTranslateX(0);

            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(false);
                MenuClose.setVisible(true);
            });
        });

        // Réafficher le slider quand on clique sur MenuClose
        MenuClose.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(0);
            slide.play();

            slider.setTranslateX(-176);

            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(true);
                MenuClose.setVisible(false);
            });
        });
    }

    // Méthode pour afficher le tableau de bord
    @FXML
    void afficherTableauDeBord(ActionEvent event) {
        try {
            loadPage("/TableauDeBord.fxml"); // Assurez-vous que le fichier TableauDeBord.fxml existe
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Autres méthodes existantes...
    @FXML
    void afficherRessource(ActionEvent event) {
        try {
            loadPage("/AfficherRessource.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ajouterRessource(ActionEvent actionEvent) {
        try {
            loadPage("/FormulaireAjoutRessource.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void afficherRessourceEMP(ActionEvent actionEvent) {
        try {
            loadPage("/AfficherRessourceEMP.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void afficherReservationEMP(ActionEvent actionEvent) {
        try {
            loadPage("/AfficherReservationEMP.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}