package controllers;

import com.dlsc.gemsfx.AutoscrollListView;
import com.dlsc.gemsfx.daterange.DateRange;
import entities.Equipe;
import entities.Projet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EquipeEmployeService;
import services.EquipeService;
import services.ProjetEquipeService;
import services.ServiceProjet;
import tests.TempUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailsTeamController {

    @FXML
    private AutoscrollListView<String> listTeamMembers;

    @FXML
    private Label nomEquipe;

    List<TempUser> employees;
    private int idEquipe;

    @FXML
    void AddMember(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AssocierMembre.fxml"));
            Parent root = loader.load();

            AssocierMembreController controller = loader.getController();
            controller.setequipe(idEquipe);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Team Members");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void BackToListEquipes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent parent=loader.load();
        Controller con=loader.getController();
        con.loadPage("/AffichageEquipe.fxml");
        nomEquipe.getScene().setRoot(parent);
    }

    @FXML
    void Refresh(ActionEvent event) {
        loadInfo();
    }

    public void loadInfo() {
        EquipeService equipeService = new EquipeService();
        EquipeEmployeService equipeEmployeService = new EquipeEmployeService();
        try {
            Equipe equipe=equipeService.getEquipe(idEquipe);
            nomEquipe.setText(equipe.getNom());
            employees=equipeEmployeService.getEmployesByEquipe(equipe.getId());
            System.out.println(employees);
            List<String> teamMembers =new ArrayList<>();
            /*
            ServiceUtilisateur serviceuser= new ServiceUtilisateur();
            Utilisateur u=new Utilisateur();*/
            for (TempUser employe : employees)
            {
                teamMembers.add(employe.getName());
            }
            ObservableList<String> membersObservable= FXCollections.observableArrayList(teamMembers);
            listTeamMembers.setItems(membersObservable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIdEquipe(int idEquipe) {
        this.idEquipe = idEquipe;
        loadInfo();
    }
    @FXML
    void DeleteMember(ActionEvent event) throws SQLException {
        int selectedUser = listTeamMembers.getSelectionModel().getSelectedIndex();
        int idUser=employees.get(selectedUser).getId();
        EquipeEmployeService equipeEmployeService = new EquipeEmployeService();
        equipeEmployeService.removeEmployeFromEquipe(idUser,idEquipe);
        loadInfo();
    }

    @FXML
    void VoirChargeEquipe(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent parent=loader.load();
        Controller con=loader.getController();
        ChargeTravailController controller=con.loadPage("/charge_travail.fxml").getController();
        EquipeService equipeService = new EquipeService();
        controller.setEquipe(equipeService.getEquipe(idEquipe));
        nomEquipe.getScene().setRoot(parent);
    }
}
