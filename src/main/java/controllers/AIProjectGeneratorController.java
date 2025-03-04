package controllers;

import entities.Projet;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import services.AIProjectGenerator;
import javafx.concurrent.Task;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AIProjectGeneratorController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDatesLimits();
    }
    @FXML
    private TextField TasksNumber;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateStart;

    @FXML
    private TextField nomTF;

    public void setDatesLimits()
    {
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-text-fill: #3a3a3a;");
                        }
                    }
                };
            }
        };
        dateStart.setDayCellFactory(dayCellFactory);
        dateEnd.setDayCellFactory(dayCellFactory);
    }

    @FXML
    void GenerateProject(ActionEvent event) {
        LocalDate DateDebut = dateStart.getValue();
        LocalDate DateFin = dateEnd.getValue();

        if (nomTF.getText().isEmpty() || TasksNumber.getText().isEmpty() || DateDebut == null || DateFin == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
        } else if (DateDebut.isAfter(DateFin)) {
            showAlert("Erreur", "La date de début est avant la date fin !");
        } else if (DateDebut.isBefore(LocalDate.now()) || DateFin.isBefore(LocalDate.now())) {
            showAlert("Erreur", "Les dates doivent être après aujourd'hui: " + LocalDate.now());
        } else {
            try {
                int tacheNbr = Integer.parseInt(TasksNumber.getText());
                AIProjectGenerator generator = new AIProjectGenerator();
                // Run in a separate thread to prevent UI freezing
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        generator.GenerateTasksAndProjectDescript(nomTF.getText(), DateDebut, DateFin, tacheNbr);
                        return null;
                    }
                };

                task.setOnSucceeded(event1 -> {
                    Platform.runLater(() -> {
                        String projetDescript = generator.getProjectDescrip();
                        List<Tache> tacheList = generator.getTasks();
                        Projet projet = new Projet(nomTF.getText(), projetDescript, Date.valueOf(DateDebut), Date.valueOf(DateFin));

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                            Parent parent = loader.load();
                            Controller con = loader.getController();
                            AIConfirmTasksGeneratorController controller = con.loadPage("/AIConfirmTasksGenerator.fxml").getController();
                            controller.setProjet(projet);
                            controller.setTacheList(tacheList);
                            nomTF.getScene().setRoot(parent);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });

                task.setOnFailed(event1 -> {
                    showAlert("Erreur", "Erreur lors de la génération du projet: " + task.getException().getMessage());
                });

                new Thread(task).start(); // Start background task

            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le nombre de tâches doit être un entier.");
            }
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    public static boolean isParsableToInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
