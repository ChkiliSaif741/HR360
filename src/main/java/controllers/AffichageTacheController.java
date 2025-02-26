package controllers;

import entities.Projet;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.events.Event;
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

    private ItemTacheController itemTacheControllerSelected;

    private int indiceTacheSelected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setTaches() throws SQLException {

        ServiceTache serviceTache = new ServiceTache();
        taches=serviceTache.afficher().stream().filter(t->t.getIdProjet()==idProjet).collect(Collectors.toList());
    }
    public void loadTasks(){
        contentBox.getChildren().clear();
        try {
            setTaches();
            for (int i = 0; i < taches.size(); i++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemTache.fxml"));
                    HBox taskItem = loader.load();
                    ItemTacheController controller = loader.getController();
                    controller.setTaskData(taches.get(i));
                    controller.setParentController(this);
                    taskItem.setUserData(controller);
                    taskItem.setOnMouseClicked(this::setTacheSelected);
                    contentBox.getChildren().add(taskItem);
                    if (i==indiceTacheSelected) {
                        controller.SetSelectedTacheColor();
                    }
                    else {
                        controller.ResetTacheColor();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            displayTacheData(taches.get(indiceTacheSelected));
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

    public void displayTacheData(Tache tache)
    {
        nomTacheL.setText(tache.getNom());
        DescriptionTache.setText(tache.getDescription());
        DateEnd.setText(tache.getDateFin().toString());
        DateStart.setText(tache.getDateDebut().toString());
    }
    public void setTacheSelected(MouseEvent event) {
        HBox clickedButton = (HBox) event.getSource();
        ItemTacheController ItemCon = (ItemTacheController) clickedButton.getUserData();
        itemTacheControllerSelected = ItemCon;
        Tache tacheSelected =itemTacheControllerSelected.getTache();
        System.out.println(tacheSelected);
        indiceTacheSelected=taches.indexOf(tacheSelected);
        loadTasks();
    }

    public void setIndiceTacheSelected(int indiceTacheSelected) {
        this.indiceTacheSelected = indiceTacheSelected;
    }
}
