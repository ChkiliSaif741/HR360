package controllers;

import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.EquipeEmployeService;
import services.ServiceUtilisateur;

import java.sql.SQLException;
import java.util.List;

public class AssocierMembreController {
    @FXML
    private ListView<String> userListView;

    private List<Utilisateur> users;

    private int currentequipeID;
    
    private ServiceUtilisateur serviceUtilisateur=new ServiceUtilisateur();

    public void setequipe(int equipeid) throws SQLException {
        this.currentequipeID = equipeid;
        users = serviceUtilisateur.afficher().stream().filter(u->u.getRole().equals("Employe")).toList();
        ObservableList<String> userObservableList = FXCollections.observableArrayList(users.stream().map(e->e.getNom()+" "+e.getPrenom()+" "+e.getEmail()).toList());
        userListView.setItems(userObservableList);
    }


    @FXML
    private void handleAddMembers() throws SQLException {
        EquipeEmployeService equipeEmployeService = new EquipeEmployeService();
        int selectedUser = userListView.getSelectionModel().getSelectedIndex();
        if (selectedUser == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez choisir une option");
            alert.show();
            return;
        }
        try {
            equipeEmployeService.assignEmployeToEquipe(users.get(selectedUser).getId(),currentequipeID);
            ((Stage) userListView.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

