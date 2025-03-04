package controllers;
import javafx.fxml.FXML;
import entities.Ressource;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import services.ServiceRessource;
import javafx.scene.Node;
import javafx.application.Platform;
import java.io.IOException;
import java.sql.SQLException;
import javafx.scene.Parent;

public class RessourceController {

    @FXML
    private Label etatL;

    @FXML
    private Label nameL;

    @FXML
    private Label typeL;

    @FXML
    private Label prixL;

    @FXML private Label resourceIdLabel;
    @FXML private Button deleteBtn;

    private Ressource ressource;

    private AfficherRessourceController parentController;

    public void setParentController(AfficherRessourceController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private BorderPane borderpane;


    private ServiceRessource serviceRessource = new ServiceRessource();


    public void setData(Ressource ressource) {
        this.ressource=ressource;
        nameL.setText(ressource.getNom());
        typeL.setText("Type: " + ressource.getType());
        etatL.setText("Etat: " + ressource.getEtat());
        prixL.setText("Prix: " + ressource.getPrix());
        resourceIdLabel.setText(String.valueOf(ressource.getId()));


    }




    public void initialize(Ressource ressource) {
        nameL.setText(ressource.getNom());
        typeL.setText(ressource.getType());
        etatL.setText(ressource.getEtat());
        prixL.setText(String.valueOf(ressource.getPrix()));
        resourceIdLabel.setText(String.valueOf(ressource.getId()));

    }




    public void deleteBtn(ActionEvent actionEvent) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer Ressource");
        confirmation.setHeaderText("Voulez-vous vraiment supprimer cette ressource ?");
        confirmation.setContentText("Ressource: " + nameL.getText());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(resourceIdLabel.getText());
                    serviceRessource.supprimer(id);

                    Platform.runLater(() -> {
                        Node cardToRemove = deleteBtn.getParent().getParent();
                        if (cardToRemove != null) {
                            ((GridPane) cardToRemove.getParent()).getChildren().remove(cardToRemove);
                        }
                    });

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Erreur lors de la suppression !");
                    alert.show();
                    e.printStackTrace();
                }
            }
        });
        parentController.afficherRessource();
    }


    public void modifierRessource(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent parent=loader.load();
        Controller controller0 = loader.getController();
        ModifierRessourceController controller =controller0.loadPage("/ModifierRessource.fxml").getController();
        controller.setRessource(ressource);
        nameL.getScene().setRoot(parent);

    }




    public void ajouterRessource(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent=loader.load();
            Controller controller=loader.getController();
            FormulaireAjoutReservationController controller1=controller.loadPage("/FormulaireAjoutReservation.fxml").getController();
            controller1.setIdRessource(ressource.getId());
            nameL.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void ClikedRessource(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent parent=loader.load();
            Controller controller=loader.getController();
            AfficherReservationController controller1=controller.loadPage("/AfficherReservation.fxml").getController();
            controller1.setIdRessource(ressource.getId());
            nameL.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
