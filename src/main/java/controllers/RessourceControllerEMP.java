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


import com.google.zxing.WriterException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.QRCodeService;

import java.io.File;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.QRCodeService;

import java.io.File;



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

    @FXML private ImageView qrCodeImageView;
    private boolean isQrCodeLarge = false;



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


    public void agrandirQRCode(ActionEvent actionEvent) {
        if (ressource == null) return;

        // Générer le QR Code si ce n'est pas déjà fait
        String qrPath = QRCodeService.generateQRCodeForRessource(ressource);
        File file = new File(qrPath);
        if (!file.exists()) {
            System.out.println("Erreur : Fichier QR Code introuvable !");
            return;
        }

        // Créer une nouvelle fenêtre pour afficher le QR Code
        Stage qrStage = new Stage();
        qrStage.initModality(Modality.APPLICATION_MODAL);
        qrStage.setTitle("QR Code - " + ressource.getNom());

        // Image du QR Code
        ImageView qrImageView = new ImageView(new Image(file.toURI().toString()));
        qrImageView.setFitWidth(300); // Grande taille
        qrImageView.setFitHeight(300);

        // Ajouter l'image dans un conteneur
        StackPane root = new StackPane(qrImageView);
        root.setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Créer la scène et l'afficher
        Scene scene = new Scene(root);
        qrStage.setScene(scene);
        qrStage.show();
    }



}
