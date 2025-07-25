package controllers;

import entities.Projet;
import entities.Tache;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AffichageTacheEMPController implements Initializable {

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
    private VBox TaskDetails;

    @FXML
    private Label nomTacheL;

    @FXML
    private Button ViewTrelloBtn;

    private ItemTacheEMPController itemTacheControllerSelected;

    private int indiceTacheSelected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setTaches() throws SQLException {

        ServiceTache serviceTache = new ServiceTache();
        System.out.println(idProjet);
        taches=serviceTache.afficher().stream().filter(t->t.getIdProjet()==idProjet).collect(Collectors.toList());
    }
    public void loadTasks(){
        contentBox.getChildren().clear();
        try {
            setTaches();
            if (taches.isEmpty()) {
                // Load image
                ImageView imageView = new ImageView(new Image("/Images/NoData.jpg")); // Adjust path if needed
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
                ViewTrelloBtn.setVisible(false);
            }
            else {
                for (int i = 0; i < taches.size(); i++) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ItemTacheEMP.fxml"));
                        HBox taskItem = loader.load();
                        ItemTacheEMPController controller = loader.getController();
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
            loadTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Prioritize(ActionEvent event) {
        taches.sort(Comparator.comparing(Tache::getDateFin));
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
        if (tache.getBoardId()==null)
        {
            ViewTrelloBtn.setVisible(false);
        }
        else {
            ViewTrelloBtn.setVisible(true);
        }
    }
    public void setTacheSelected(MouseEvent event) {
        HBox clickedButton = (HBox) event.getSource();
        ItemTacheEMPController ItemCon = (ItemTacheEMPController) clickedButton.getUserData();
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
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
        try {
            Parent root = loader.load();
            Controller controller = loader.getController();
            DetailsProjetEMPController detailsProjetController = controller.loadPage("/DetailsProjetEMP.fxml").getController();
            detailsProjetController.setIdProjet(idProjet);
            contentBox.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

@FXML
void ViewTrello(ActionEvent event) throws URISyntaxException, IOException {
    java.awt.Desktop.getDesktop().browse(new URI("https://trello.com/b/" + taches.get(indiceTacheSelected).getBoardId()));
}
}
