package controllers;

import entities.Employe;
import entities.Employe;
import entities.Employe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import services.ServiceEmploye;
import services.ServiceEmploye;
import utils.alertMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
/*implements Initializable*/
public class GestionEmployesController implements Initializable{



    @FXML
    private Button addEmployee_addBtn;

    @FXML
    private Button addEmployee_clearBtn;

    @FXML
    private Button addEmployee_deleteBtn;

    @FXML
    private TextField addEmployee_email;

    @FXML
    private TextField addEmployee_firstName;

    @FXML
    private ImageView addEmployee_image;

    @FXML
    private Button addEmployee_importBtn;

    @FXML
    private TextField addEmployee_lastName;

    @FXML
    private PasswordField addEmployee_password;

    @FXML
    private ComboBox<?> addEmployee_position;

    @FXML
    private TextField addEmployee_salaire;

    @FXML
    private TextField addEmployee_search;

    @FXML
    private Button addEmployee_updateBtn;

    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scroll;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button close;

    @FXML
    private Button minimize;

    @FXML
    private Label username;

    @FXML
    private Button home_btn;

    @FXML
    private Button addEmployee_btn;

    @FXML
    private Button salary_btn;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane home_form;

    @FXML
    private Label home_totalEmployees;

    @FXML
    private Label home_totalPresents;

    @FXML
    private Label home_totalInactiveEm;

    @FXML
    private BarChart<?, ?> home_chart;

    @FXML
    private AnchorPane addEmployee_form;

    @FXML
    private TextField addEmployee_employeeID;


    @FXML
    private ComboBox<?> addEmployee_gender;

    @FXML
    private TextField addEmployee_phoneNum;


    @FXML
    private AnchorPane salary_form;

    @FXML
    private TextField salary_employeeID;

    @FXML
    private Label salary_firstName;

    @FXML
    private Label salary_lastName;

    @FXML
    private Label salary_position;

    @FXML
    private TextField salary_salary;

    @FXML
    private Button salary_updateBtn;

    @FXML
    private Button salary_clearBtn;



    private String imagePath = null;

    private File selectedImageFile;

    alertMessage alert = new alertMessage();



    private ServiceEmploye serviceEmploye = new ServiceEmploye();
    List<Employe> employees = new ArrayList<>();

    private List<Employe> getEmployees() {
        try {
            employees = serviceEmploye.afficher();
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }


    public void employeeList(){
        ObservableList listData = FXCollections.observableArrayList("Developpeur", "Designer");
        addEmployee_position.setItems(listData);
    }


    public void setImgSrc(String imgSrc) {
        if (imgSrc != null && !imgSrc.isEmpty()) {
            Image image = new Image(imgSrc); // Utilisez directement l'URI ou le chemin
            addEmployee_image.setImage(image);
        }
    }


    public void registerClearFields() {
        addEmployee_firstName.clear();
        addEmployee_lastName.clear();
        addEmployee_email.clear();
        addEmployee_password.clear();
        addEmployee_salaire.clear();
        addEmployee_position.getSelectionModel().clearSelection();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList listData = FXCollections.observableArrayList("Developpeur", "Designer");
        addEmployee_position.setItems(listData);
        List<Employe> employees = getEmployees();
        int column = 0;
        int row = 0;

        // S'assurer que la GridPane est bien réinitialisée avant d'ajouter les éléments
        grid.getChildren().clear();
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        grid.setMinHeight(Region.USE_COMPUTED_SIZE);
        grid.setMinWidth(Region.USE_COMPUTED_SIZE);

        try {
            for (Employe employe : employees) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employee_item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                Employee_itemController controller = fxmlLoader.getController();
                controller.setParentControler(this);
                controller.setEmploye(employe);

                // Ajouter l'élément avant de modifier les colonnes et lignes
                grid.add(anchorPane, column, row);
                GridPane.setMargin(anchorPane, new Insets(20, 50, 50, 50));

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addEmployeeAdd(ActionEvent event) {
        alertMessage alert = new alertMessage();

        // Vérifier que tous les champs sont remplis
        if (addEmployee_firstName.getText().isEmpty() || addEmployee_firstName.getText().isEmpty()) {
            alert.errorMessage("Veuillez remplir tous les champs !");
            return;
        }

        // Récupérer les valeurs des champs
        String nom = addEmployee_firstName.getText();
        String prenom = addEmployee_lastName.getText();
        String email = addEmployee_email.getText();
        String password = addEmployee_password.getText();
        String poste = addEmployee_position.getSelectionModel().getSelectedItem().toString();
        float salaire;
        if(addEmployee_salaire.getText().isEmpty()) {
            salaire = 0;
        }else{
            salaire = Float.parseFloat(addEmployee_salaire.getText());
        }
        //float salaire = Float.parseFloat(addEmployee_salaire.getText());
        String imgSrc = (selectedImageFile != null) ? selectedImageFile.toURI().toString() : getClass().getResource("/img/user.png").toString();
        //int duree = Integer.parseInt(dureeField.getText());
        //String date = dateField.getText();

        // Créer l'objet Employe
        Employe employe = new Employe();
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setEmail(email);
        employe.setPassword(password);
        employe.setPoste(poste);
        employe.setSalaire(salaire);
        employe.setImgSrc(imgSrc);

        // Ajouter la formation dans la base de données
        try {
            serviceEmploye.ajouter(employe);
            alert.successMessage("Employe ajoutée avec succès !");

            // Réinitialiser les champs après l'ajout
            registerClearFields();

            // Fermer la fenêtre actuelle et ouvrir la fenêtre d'affichage
            Stage currentStage = (Stage) addEmployee_firstName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Employe.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (Stage)
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Affichage des formations");

            // Afficher la fenêtre
            newStage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de l'ajout de la formation : " + e.getMessage());
            e.printStackTrace();
        }

    }


//    @FXML
//    void addEmployeeAdd(ActionEvent event) {
//
//    }


    @FXML
    void addEmployeeDelete(ActionEvent event) {

    }

    @FXML
    void addEmployeeGendernList(ActionEvent event) {

    }

    @FXML
    void addEmployeeInsertImage(MouseEvent event) {

    }

    @FXML
    void addEmployeePositionList(ActionEvent event) {

    }

    @FXML
    void addEmployeeReset(ActionEvent event) {

    }

    @FXML
    void addEmployeeSearch(KeyEvent event) {

    }

    @FXML
    void addEmployeeSelect(MouseEvent event) {

    }

    @FXML
    void addEmployeeUpdate(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {

    }

    @FXML
    void minimize(ActionEvent event) {

    }

    @FXML
    void salaryReset(ActionEvent event) {

    }

    @FXML
    void salarySelect(MouseEvent event) {

    }

    @FXML
    void salaryUpdate(ActionEvent event) {

    }

    public void switchForm(ActionEvent event) {

        if (event.getSource() == home_btn) {
            home_form.setVisible(true);
            addEmployee_form.setVisible(false);
            salary_form.setVisible(false);

            home_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #3a4368, #28966c);");
            addEmployee_btn.setStyle("-fx-background-color:transparent");
            salary_btn.setStyle("-fx-background-color:transparent");

            /*homeTotalEmployees();
            homeEmployeeTotalPresent();
            homeTotalInactive();
            homeChart();*/

        } else if (event.getSource() == addEmployee_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(true);
            salary_form.setVisible(false);

            addEmployee_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #3a4368, #28966c);");
            home_btn.setStyle("-fx-background-color:transparent");
            salary_btn.setStyle("-fx-background-color:transparent");

            /*addEmployeeGendernList();
            addEmployeePositionList();
            addEmployeeSearch();*/

        } else if (event.getSource() == salary_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(false);
            salary_form.setVisible(true);

            salary_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #3a4368, #28966c);");
            addEmployee_btn.setStyle("-fx-background-color:transparent");
            home_btn.setStyle("-fx-background-color:transparent");

            //salaryShowListData();

        }

    }


}
