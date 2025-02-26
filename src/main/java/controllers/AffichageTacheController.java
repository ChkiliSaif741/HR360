package controllers;

import entities.Projet;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ExcelExporterProjet;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AffichageTacheController implements Initializable {

    private int idProjet;

    private List<Tache> taches=new ArrayList<>();
    @FXML
    private Label DateEnd;

    @FXML
    private Label DateStart;

    @FXML
    private Label DescriptionTache;

    @FXML
    private TextField SearchBar;

    @FXML
    private VBox contentBox;

    @FXML
    private Label nomTacheL;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setTaches() throws SQLException {

        ServiceTache serviceTache = new ServiceTache();
        taches=serviceTache.afficher().stream().filter(t->t.getIdProjet()==idProjet).collect(Collectors.toList());;
    }
    public void loadTasks(){
        contentBox.getChildren().clear();
        try {
            setTaches();
            for (Tache tache : taches) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemTache.fxml"));
                    HBox taskItem = loader.load();
                    ItemTacheController controller = loader.getController();
                    controller.setTaskData(tache);
                    controller.setParentController(this);
                    contentBox.getChildren().add(taskItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setTacheSelected(taches.get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        try {
            setTaches();
            loadTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void AjouterTache(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        try {
            Parent parent = loader.load();
            Controller controller = loader.getController();
            AjoutTacheController controller1 = controller.loadPage("/AjoutTache.fxml").getController();
            controller1.setIdProjet(idProjet);
            contentBox.getScene().setRoot(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Prioritize(ActionEvent event) {
        taches.sort(Comparator.comparing(Tache::getDateFin));
        System.out.println(taches);
        loadTasks();
    }

    @FXML
    void Refresh(ActionEvent event) {
        taches.clear();
        try {
            setTaches();
            loadTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SearchBar.clear();
    }

    @FXML
    void SearchTasks(KeyEvent event) {
        try {
            taches.clear();
            setTaches();
            List<Tache> TacheSearched = new ArrayList<>();
            TacheSearched.addAll(taches.stream().filter(t->t.getNom().startsWith(SearchBar.getText())).toList());
            taches.clear();
            taches.addAll(TacheSearched);
            loadTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void ExportExcel(ActionEvent event) {
        ExcelExporterProjet excelExporterProjet = new ExcelExporterProjet();
        ServiceProjet serviceProjet=new ServiceProjet();
        try {
            Projet projet=serviceProjet.getById(idProjet);
            excelExporterProjet.exportToExcel(projet,taches,(Stage) contentBox.getScene().getWindow());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTacheSelected(Tache tacheSelected) {
        nomTacheL.setText(tacheSelected.getNom());
        DescriptionTache.setText(tacheSelected.getDescription());
        DateEnd.setText(tacheSelected.getDateFin().toString());
        DateStart.setText(tacheSelected.getDateDebut().toString());
    }
}
