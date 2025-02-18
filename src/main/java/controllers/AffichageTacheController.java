package controllers;

import entities.Tache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import services.ServiceTache;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AffichageTacheController implements Initializable {

    private int idProjet;
    @FXML
    private VBox contentBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void loadTasks(){
        ServiceTache serviceTache = new ServiceTache();
        try {
            List<Tache> taches=serviceTache.afficher().stream().filter(t->t.getIdProjet()==idProjet).toList();
            for (Tache tache : taches) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemTache.fxml"));
                    VBox taskItem = loader.load();
                    ItemTacheController controller = loader.getController();
                    controller.setTaskData(tache);
                    controller.setParentController(this);
                    contentBox.getChildren().add(taskItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        loadTasks();
    }
}
