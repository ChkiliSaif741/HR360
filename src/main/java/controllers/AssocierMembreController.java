package controllers;

import entities.Equipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.EquipeEmployeService;
import tests.TempUser;
import tests.UserService;

import java.sql.SQLException;
import java.util.List;

public class AssocierMembreController {
    @FXML
    private ListView<String> userListView;

    private List<TempUser> users;

    private int currentequipeID;

    public void setequipe(int equipeid) {
        this.currentequipeID = equipeid;
        users = UserService.getAllUsers();
        ObservableList<String> userObservableList = FXCollections.observableArrayList(users.stream().map(e->e.getName()).toList());
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

