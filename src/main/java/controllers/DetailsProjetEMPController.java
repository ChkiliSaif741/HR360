package controllers;

import com.dlsc.gemsfx.AutoscrollListView;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangeView;
import entities.Equipe;
import entities.Projet;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import services.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private AutoscrollListView<String> listTeamMembers;

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
            List<Utilisateur> employees=equipeEmployeService.getEmployesByEquipe(equipe.getId());
            System.out.println(employees);
            List<String> teamMembers =new ArrayList<>();
            for (Utilisateur employe : employees)
            {
                teamMembers.add(employe.getNom()+" "+employe.getPrenom()+" "+employe.getEmail());
            }
            ObservableList<String> membersObservable= FXCollections.observableArrayList(teamMembers);
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

