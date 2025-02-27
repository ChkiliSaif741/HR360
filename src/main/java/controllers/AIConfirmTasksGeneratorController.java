package controllers;

import com.jfoenix.controls.JFXTextArea;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import services.ServiceProjet;
import services.ServiceTache;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AIConfirmTasksGeneratorController {

    @FXML
    private VBox TasksNamesButtons;

    @FXML
    private VBox cardContainer;

    @FXML
    private Label dateDebut;

    @FXML
    private DatePicker dateEndTask;

    @FXML
    private Label dateFin;

    @FXML
    private DatePicker dateStartTask;

    @FXML
    private JFXTextArea descriptionProjetTF;

    @FXML
    private JFXTextArea descriptionTaskTF;

    @FXML
    private Label nomProjet;

    @FXML
    private TextField nomTaskTF;

    private Projet projet;

    private List<Tache> tacheList=new ArrayList<>();

    private int selectedTache=0;

    private Button selectedButton;

    public void loadGeneratedInfo() {
        TasksNamesButtons.getChildren().clear();
        int column = 0;
        int row = 0;

        for (int i = 0; i < tacheList.size(); i++) {
            final int taskIndex = i;
            Button loadTaskButton = new Button(tacheList.get(i).getNom());
            loadTaskButton.setPrefWidth(300);
            loadTaskButton.setPrefHeight(40);
            GridPane.setHalignment(loadTaskButton, HPos.CENTER);
            if (i == selectedTache) {
                loadTaskButton.getStyleClass().add("button-44-selected");
                selectedButton = loadTaskButton;
            } else {
                loadTaskButton.getStyleClass().add("button-44");
            }
           loadTaskButton.setUserData(i);
            loadTaskButton.setOnAction(this::loadTachesDetails);
            TasksNamesButtons.getChildren().add(loadTaskButton);
        }
        resetTasksFieldsdetails();
    }

    public void resetTasksFieldsdetails()
    {
        nomTaskTF.setText(tacheList.get(selectedTache).getNom());
        descriptionTaskTF.setText(tacheList.get(selectedTache).getDescription());
        dateStartTask.setValue(LocalDate.parse(tacheList.get(selectedTache).getDateDebut().toString()));
        dateEndTask.setValue(LocalDate.parse(tacheList.get(selectedTache).getDateFin().toString()));
    }

    public void loadTachesDetails(ActionEvent event){
        if (nomTaskTF.getText().isEmpty() || descriptionTaskTF.getText().isEmpty() || dateStartTask.getValue() == null || dateEndTask.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.show();
            resetTasksFieldsdetails();
            return;
        } else if (dateStartTask.getValue().isAfter(dateEndTask.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("La date de début est avant la date fin !");
            alert.show();
            resetTasksFieldsdetails();
            return;
        } else if (dateStartTask.getValue().isBefore(projet.getDateDebut().toLocalDate())||dateEndTask.getValue().isAfter(projet.getDateFin().toLocalDate())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("L'intervalle du travail tache doit etre dans entre "+projet.getDateDebut()+" et "+projet.getDateFin()+" (Dates du projets) !");
            alert.show();
            resetTasksFieldsdetails();
            return;
        }
        tacheList.get(selectedTache).setNom(nomTaskTF.getText());
        tacheList.get(selectedTache).setDescription(descriptionTaskTF.getText());
        tacheList.get(selectedTache).setDateDebut(Date.valueOf(dateStartTask.getValue()));
        tacheList.get(selectedTache).setDateFin(Date.valueOf(dateEndTask.getValue()));
        Button clickedButton = (Button) event.getSource();
        int i = (int) clickedButton.getUserData();
        clickedButton.getStyleClass().add("button-44-selected");
        selectedTache=i;
        loadGeneratedInfo();
    }
    public void setTacheList(List<Tache> tacheList) {
        this.tacheList = tacheList;
        descriptionProjetTF.setText(projet.getDescription());
        nomProjet.setText(projet.getNom());
        dateDebut.setText(projet.getDateDebut().toString());
        dateFin.setText(projet.getDateFin().toString());
        loadGeneratedInfo();
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
        setDatesLimits();
    }

    @FXML
    void ConfirmGeneration(ActionEvent event) {
        if (nomTaskTF.getText().isEmpty() || descriptionTaskTF.getText().isEmpty() || dateStartTask.getValue() == null || dateEndTask.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Veuillez remplir tous les champs !");
            alert.show();
            resetTasksFieldsdetails();
        } else if (dateStartTask.getValue().isAfter(dateEndTask.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("La date de début est avant la date fin !");
            alert.show();
            resetTasksFieldsdetails();
        } else if (dateStartTask.getValue().isBefore(projet.getDateDebut().toLocalDate())||dateEndTask.getValue().isAfter(projet.getDateFin().toLocalDate())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("L'intervalle du travail tache doit etre dans entre "+projet.getDateDebut()+" et "+projet.getDateFin()+" (Dates du projets) !");
            alert.show();
            resetTasksFieldsdetails();
        }else {
            ServiceProjet serviceProjet = new ServiceProjet();
            ServiceTache serviceTache = new ServiceTache();
            projet.setDescription(descriptionProjetTF.getText());
            tacheList.get(selectedTache).setNom(nomTaskTF.getText());
            tacheList.get(selectedTache).setDescription(descriptionTaskTF.getText());
            tacheList.get(selectedTache).setDateDebut(Date.valueOf(dateStartTask.getValue()));
            tacheList.get(selectedTache).setDateFin(Date.valueOf(dateEndTask.getValue()));
            try {
                int idP = serviceProjet.ajouterGenrated(projet);
                for (Tache tache : tacheList) {
                    tache.setStatut(TacheStatus.A_FAIRE);
                    tache.setIdProjet(idP);
                    serviceTache.ajouter(tache);
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
                Parent parent = loader.load();
                Controller con = loader.getController();
                con.loadPage("/AffichageProjet.fxml");
                nomTaskTF.getScene().setRoot(parent);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setDatesLimits(){
            LocalDate startDate = projet.getDateDebut().toLocalDate();
            LocalDate endDate = projet.getDateFin().toLocalDate();
            Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isAfter(endDate) || item.isBefore(startDate)) {
                                setDisable(true);
                                setStyle("-fx-text-fill: #3a3a3a;");
                            }
                        }
                    };
                }
            };
            dateStartTask.setDayCellFactory(dayCellFactory);
            dateEndTask.setDayCellFactory(dayCellFactory);
    }
}
