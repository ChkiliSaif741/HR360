package controllers;

import com.dlsc.gemsfx.AutoscrollListView;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangeView;
import entities.Equipe;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import services.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DetailsProjetEMPController {

    @FXML
    private Label DescriptionProjet;

    @FXML
    private DateRangeView DureeProjet;

    @FXML
    private Label nomProjetL;

    @FXML
    private Label nomEquipe;

    private int idProjet;

    @FXML
    private AutoscrollListView<Integer> listTeamMembers; //intergation bdlha

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        loadInfo();
    }

    @FXML
    void ViewTasks(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            AffichageTacheEMPController affichageTacheController = controller.loadPage("/AffichageTacheEMP.fxml").getController();
            affichageTacheController.setIdProjet(idProjet);
            nomProjetL.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInfo() {
        ServiceProjet serviceProjet = new ServiceProjet();
        ProjetEquipeService serviceProjetEquipe = new ProjetEquipeService();
        EquipeService equipeService = new EquipeService();
        EquipeEmployeService equipeEmployeService = new EquipeEmployeService();
        try {
            Equipe equipe=equipeService.getEquipe(serviceProjetEquipe.getEquipeByProjet(idProjet));
            nomEquipe.setText(equipe.getNom());
            Projet projet = serviceProjet.getById(idProjet);
            nomProjetL.setText(projet.getNom());
            DescriptionProjet.setText(projet.getDescription());
            DateRange dateRange = new DateRange(projet.getNom(), projet.getDateDebut().toLocalDate(), projet.getDateFin().toLocalDate());
            DureeProjet.setValue(dateRange);
            List<Integer> employeesID=equipeEmployeService.getEmployesByEquipe(equipe.getId());
            //getemploye by id fil integration w t3ml List<Utilisateur>
            /*
            List<String> teamMembers =new ArrayList<>();
            ServiceUtilisateur serviceuser= new ServiceUtilisateur();
            Utilisateur u=new Utilisateur();
            for (Integer employeid : employeesID)
            {
                u=serviceuser.getuserByid(employeid)
                teamMembers.add(u.getNom()+" "+u.getPrenom());
            }
            ObservableList<String> membersObservable= FXCollections.observableArrayList(teamMembers);
            listTeamMembers.setItems(membersObservable);
             */
            ObservableList<Integer> membersObservable= FXCollections.observableArrayList(employeesID);
            listTeamMembers.setItems(membersObservable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void BackToListProjet(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        Parent parent=loader.load();
        Controller con=loader.getController();
        con.loadPage("/AffichageProjetEMP.fxml");
        nomProjetL.getScene().setRoot(parent);
    }
    
}

