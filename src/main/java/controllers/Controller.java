
package controllers;
import com.jfoenix.controls.JFXButton;
import entities.Sessions;
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
import services.ServiceUtilisateur;
import utils.alertMessage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.apache.poi.sl.usermodel.PresetColor.Menu;

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


    @FXML
    void AffichOffres(ActionEvent event) {
        try {
            loadPage("/ListeOffresFront.fxml");
        } catch (IOException e) {
            e.printStackTrace();
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
    @FXML
    void AffichEquip(ActionEvent event) {
        try {
            loadPage("/AffichageEquipe.fxml");
        }
        catch (IOException e){
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
    void AffichProjetEMP(ActionEvent event) {
        try{
            loadPage("/AffichageProjetEMP.fxml");
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
            ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
            profilEMPController con=loadPage("/profilEMP.fxml").getController();
            con.setUtilisateur(serviceUtilisateur.getUserById(Sessions.getInstance().getIdUtilisateur()));
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
                // Détruire la session
                Sessions.destroySession();

                // Afficher un message de confirmation
                alertMessage alertMsg = new alertMessage();
                alertMsg.successMessage("Déconnexion réussie !");

                // Rediriger vers la page de connexion
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Login");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors du chargement de la page de connexion.");
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
    void AffichcondBack(ActionEvent event) {
        try {

            loadPage("/ListCandidatureBack.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Exit.setOnMouseClicked(event -> System.exit(0));
        Exit.setOnMouseClicked(event -> {
            System.exit(0);
        });
        slider.setTranslateX(0);
        Menu.setVisible(true);
        MenuClose.setVisible(false);

        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);

            slide.setToX(0);
            slide.play();

            slider.setTranslateX(-176);

            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(false);
                MenuClose.setVisible(true);
            });
        });


    }

    @FXML
    void afficherTableauDeBord(ActionEvent event) {
        try {
            loadPage("/TableauDeBord.fxml"); // Assurez-vous que le fichier TableauDeBord.fxml existe
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void AffichoffresBack(ActionEvent event) {
        try {
            loadPage("/ListeOffres.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void afficherRessource (ActionEvent event){
        try {
            loadPage("/AfficherRessource.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void AffichCandidature (ActionEvent event){
        try {
            loadPage("/ListeCondidatures.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void AffichProjet (ActionEvent event){
        try {
            loadPage("/AffichageProjet.fxml");
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





    public void afficherNotification(ActionEvent actionEvent) {

        try {
            loadPage("/AfficherNotification.fxml");
        } catch (IOException e) {
            //afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement de la page.");
        }

    }

    @FXML
    void OnprofilBtn(ActionEvent event) {
        try {
            ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
            ProfileController con=loadPage("/Profile.fxml").getController();
            con.setUtilisateur(serviceUtilisateur.getUserById(Sessions.getInstance().getIdUtilisateur()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

