package controllers;

import entities.Ressource;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import services.ServiceRessource;

import java.io.IOException;
import java.sql.SQLException;

public class RessourceControllerEMP {

    @FXML
    private Label etatL;

    @FXML
    private Label nameL;

    @FXML
    private Label typeL;

    @FXML private Label resourceIdLabel;
    @FXML private Button deleteBtn;

    private Ressource ressource;

    private AfficherRessourceEMPController parentController;

    public void setParentController(AfficherRessourceEMPController parentController) {
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
        resourceIdLabel.setText(String.valueOf(ressource.getId()));

    }




    public void initialize(Ressource ressource) {
        nameL.setText(ressource.getNom());
        typeL.setText(ressource.getType());
        etatL.setText(ressource.getEtat());
        resourceIdLabel.setText(String.valueOf(ressource.getId()));
    }




    public void ajouterRessource(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
            Parent parent=loader.load();
            Controller controller=loader.getController();
            FormulaireAjoutReservationEMPController controller1=controller.loadPage("/FormulaireAjoutReservationEMP.fxml").getController();
            controller1.setIdRessource(ressource.getId());
            nameL.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
