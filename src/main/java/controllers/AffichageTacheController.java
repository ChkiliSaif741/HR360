package controllers;

import entities.Projet;
import entities.Tache;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
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

    @FXML
    private VBox TaskDetails;

    @FXML
    private Button EnableTrelloBtn;

    @FXML
    private Button ViewTrelloBtn;

    private ItemTacheController itemTacheControllerSelected;

    private int indiceTacheSelected;

    private boolean ProjectHasTeam;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void CheckProjectHasTeam() throws SQLException {
        ProjetEquipeService projetEquipeService = new ProjetEquipeService();
        ProjectHasTeam=(projetEquipeService.getEquipeByProjet(idProjet)!=null&&projetEquipeService.getEquipeByProjet(idProjet)!=0);
        System.out.println(ProjectHasTeam);
    }

    public void setTaches() throws SQLException {

        ServiceTache serviceTache = new ServiceTache();
        serviceTache.synchronizeDeletedBoards();
        taches=serviceTache.afficher().stream().filter(t->t.getIdProjet()==idProjet).collect(Collectors.toList());
    }
    public void loadTasks(){
        contentBox.getChildren().clear();
        try {
            setTaches();
            if (taches.isEmpty()) {
                // Load image
                ImageView imageView = new ImageView(new Image("/Images/NoData.jpg"));
                imageView.setFitWidth(200); // Set width
                imageView.setPreserveRatio(true); // Keep aspect ratio

                // Optional: Add text
                Label label = new Label("No data available");
                label.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");

                // Center image and label
                contentBox.getChildren().addAll(imageView, label);
                contentBox.setSpacing(10);
                contentBox.setAlignment(javafx.geometry.Pos.CENTER);
                TaskDetails.setVisible(false);
                EnableTrelloBtn.setVisible(false);
                ViewTrelloBtn.setVisible(false);
            }
            else {
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
                        if (i == indiceTacheSelected) {
                            controller.SetSelectedTacheColor();
                        } else {
                            controller.ResetTacheColor();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                displayTacheData(taches.get(indiceTacheSelected));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
        try {
            setTaches();
            CheckProjectHasTeam();
            loadTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void AjouterTache(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
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
        loadTasks();
    }

    @FXML
    void Refresh(ActionEvent event) throws SQLException {
        taches.clear();
        try {
            setTaches();
            loadTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SearchBar.clear();
        CheckProjectHasTeam();
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
        if (ProjectHasTeam){
            if (tache.getBoardId()==null)
            {
                EnableTrelloBtn.setText("Enable Trello");
                EnableTrelloBtn.setOnAction(this::EnableTrello);
                EnableTrelloBtn.setVisible(true);
                ViewTrelloBtn.setVisible(false);
            }
            else {
                EnableTrelloBtn.setText("Disable Trello");
                EnableTrelloBtn.setOnAction(this::disableTrello);
                EnableTrelloBtn.setVisible(true);
                ViewTrelloBtn.setVisible(true);
            }
        }else {
            EnableTrelloBtn.setVisible(false);
            ViewTrelloBtn.setVisible(false);
        }

    }

    private void disableTrello(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez vous supprimer board Trello? ");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String boardid = taches.get(indiceTacheSelected).getBoardId();
            boolean sucesss = TrelloAPI.deleteBoard(boardid);
            if (sucesss) {
                ServiceTache serviceTache=new ServiceTache();
                try {
                    serviceTache.clearBoardId(taches.get(indiceTacheSelected).getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Sucesss");
                alert1.setHeaderText(null);
                alert1.setContentText("Le trello board a été supprimee avec succees");
                alert1.show();
                loadTasks();
            } else {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Erreur");
                alert2.setHeaderText(null);
                alert2.setContentText("Erreur lors de supression du trello board");
                alert2.show();
            }
        }
    }

    public void setTacheSelected(MouseEvent event) {
        HBox clickedButton = (HBox) event.getSource();
        ItemTacheController ItemCon = (ItemTacheController) clickedButton.getUserData();
        itemTacheControllerSelected = ItemCon;
        Tache tacheSelected =itemTacheControllerSelected.getTache();
        indiceTacheSelected=taches.indexOf(tacheSelected);
        loadTasks();
    }

    public void setIndiceTacheSelected(int indiceTacheSelected) {
        this.indiceTacheSelected = indiceTacheSelected;
    }

    @FXML
    void BackToProjetsDetails(ActionEvent event) {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            DetailsProjetController detailsProjetController = controller.loadPage("/DetailsProjet.fxml").getController();
            detailsProjetController.setIdProjet(idProjet);
            contentBox.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void EnableTrello(ActionEvent event) {
        try {
            ProjetEquipeService projetEquipeService = new ProjetEquipeService();
            EquipeEmployeService equipeEmployeService = new EquipeEmployeService();
            ServiceTache tacheService = new ServiceTache();

            // Get team assigned to the project
            int idProjet = taches.get(indiceTacheSelected).getIdProjet();
            int idEquipe = projetEquipeService.getEquipeByProjet(idProjet);
            List<Utilisateur> teamMembers = equipeEmployeService.getEmployesByEquipe(idEquipe);

            // Enable Trello for the task
            tacheService.enableTrelloForTask(taches.get(indiceTacheSelected), teamMembers);

            System.out.println("Trello enabled for task: " + taches.get(indiceTacheSelected).getNom());
            loadTasks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }/*
@FXML
void EnableTrello(ActionEvent event) {
    try {
        EquipeService equipeService = new EquipeService();
        ProjetEquipeService projetEquipeService = new ProjetEquipeService();
        ServiceTache tacheService = new ServiceTache();

        // Get team assigned to the project
        int idProjet = taches.get(indiceTacheSelected).getIdProjet();
        int idEquipe = projetEquipeService.getEquipeByProjet(idProjet);
        // Enable Trello for the task
        tacheService.enableTrelloForTask(taches.get(indiceTacheSelected));

        System.out.println("Trello enabled for task: " + taches.get(indiceTacheSelected).getNom());
        loadTasks();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}*/
    @FXML
    void ViewTrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root = loader.load();
            Controller controller0 = loader.getController();
            FXMLLoader loader1=controller0.loadPage("/TrelloBoard.fxml");
            TrelloBoardController controller = loader1.getController();
            System.out.println(taches.get(indiceTacheSelected).getBoardId());
            controller.setBoardId(taches.get(indiceTacheSelected).getBoardId());
            contentBox.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
