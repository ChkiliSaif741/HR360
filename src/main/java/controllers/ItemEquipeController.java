package controllers;

import entities.Equipe;
import entities.Equipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import services.EquipeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ItemEquipeController {

    private AffichageEquipeController parentController;

    public void setParentController(AffichageEquipeController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private Button btnDel;

    @FXML
    private Button btnMod;

    @FXML
    private VBox cardContainer;

    @FXML
    private Label nomProjet;

    Equipe equipe;

    @FXML
    void DeleteEquipe(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Voulez vous vraiment supprimer "+equipe.getNom()+" ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            EquipeService equipeService = new EquipeService();
            try {
                equipeService.deleteEquipe(equipe.getId());
                parentController.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ModifEquipe(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent parent = loader.load();
            Controller controller = loader.getController();
            ModifEquipeController ModifController = controller.loadPage("/ModifEquipe.fxml").getController();
            ModifController.setNomTF(equipe.getNom());
            ModifController.setIdEquipe(equipe.getId());
            nomProjet.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetData(Equipe equipe) {
        this.equipe = equipe;
        nomProjet.setText(equipe.getNom());
    }

    @FXML
    void handleCardClick(MouseEvent event) {

    }

}
