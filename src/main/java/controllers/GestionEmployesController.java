package controllers;

import entities.Formation;
import entities.MyListener;
import entities.Utilisateur;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceUtilisateur;
import utils.alertMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*implements Initializable*/
public class GestionEmployesController implements Initializable {


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




    private Utilisateur currentDisplayedEmploye;

    private String imagePath = null;

    private File selectedImageFile;

    private Utilisateur selectedEmploye = null;

    alertMessage alert = new alertMessage();


    private ServiceUtilisateur serviceEmploye = new ServiceUtilisateur();
    List<Utilisateur> employees = new ArrayList<>();

    private List<Utilisateur> getEmployees() {
        try {
            employees = serviceEmploye.afficher();
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }


    public void employeeList() {
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

        // Charger les employés dans le GridPane
        refreshEmployees();

        List<Utilisateur> employees = getEmployees();
        int column = 0;
        int row = 0;

        // S'assurer que la GridPane est bien réinitialisée avant d'ajouter les éléments
        grid.getChildren().clear();
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        grid.setMinHeight(Region.USE_COMPUTED_SIZE);
        grid.setMinWidth(Region.USE_COMPUTED_SIZE);

        try {
            for (Utilisateur employe : employees) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employee_item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                EmployeesItemController controller = fxmlLoader.getController();
                controller.setParentControler(this);
                controller.setUtilisateur(employe);

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
    void addEmployeeImportImage(ActionEvent event) {
        // Créer un FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Ouvrir la boîte de dialogue pour sélectionner un fichier
        selectedImageFile = fileChooser.showOpenDialog(null);

        // Si un fichier est sélectionné
        if (selectedImageFile != null) {
            // Afficher l'image dans l'interface utilisateur (si nécessaire)
            addEmployee_image.setImage(new Image(selectedImageFile.toURI().toString()));
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
        if (addEmployee_salaire.getText().isEmpty()) {
            salaire = 0;
        } else {
            salaire = Float.parseFloat(addEmployee_salaire.getText());
        }

        // Utiliser le chemin de l'image importée, ou une image par défaut si aucune image n'est sélectionnée
        String imgSrc = (selectedImageFile != null) ? selectedImageFile.toURI().toString() : getClass().getResource("/img/user.png").toString();

        // Créer l'objet Employe
        Utilisateur employe = new Utilisateur();
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setEmail(email);
        employe.setPassword(password);
        employe.setPoste(poste);
        employe.setSalaire(salaire);
        employe.setImgSrc(imgSrc);
        employe.setRole("Employe");

        // Ajouter l'employé dans la base de données
        try {
            serviceEmploye.ajouter(employe);
            alert.successMessage("Employé ajouté avec succès !");
            // Réinitialiser les champs après l'ajout
            registerClearFields();
            refreshEmployees();
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de l'ajout de l'employé : " + e.getMessage());
            e.printStackTrace();
        }
    }




    public void setSelectedEmploye(Utilisateur employe) {
        this.selectedEmploye = employe; // Stocker l'employé sélectionné
    }


    @FXML
    void addEmployeeDelete(ActionEvent event) {
        if (selectedEmploye == null) {
            alert.errorMessage("Veuillez sélectionner un employé à supprimer !");
            return;
        }

        try {
            serviceEmploye.supprimer(selectedEmploye.getId());
            alert.successMessage("Employé supprimé avec succès !");
            selectedEmploye = null; // Réinitialiser l'employé sélectionné
            refreshEmployees(); // Rafraîchir la liste des employés
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de la suppression : " + e.getMessage());
        }
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
        if (selectedEmploye == null) {
            alert.errorMessage("Veuillez sélectionner un employé à modifier !");
            return;
        }

        // Mettre à jour l'employé dans la base de données
        try {
            Utilisateur updatedEmploye = new Utilisateur();
            updatedEmploye.setId(selectedEmploye.getId());
            updatedEmploye.setNom(addEmployee_firstName.getText());
            updatedEmploye.setPrenom(addEmployee_lastName.getText());
            updatedEmploye.setEmail(addEmployee_email.getText());
            updatedEmploye.setPassword(selectedEmploye.getPassword()); // Conserver le mot de passe actuel
            ComboBox<String> positionComboBox = (ComboBox<String>) addEmployee_position;
            updatedEmploye.setPoste(positionComboBox.getSelectionModel().getSelectedItem());
            updatedEmploye.setSalaire(Float.parseFloat(addEmployee_salaire.getText()));
            updatedEmploye.setImgSrc(selectedEmploye.getImgSrc()); // Conserver l'image actuelle

            // Log des données avant mise à jour
            System.out.println("Données avant mise à jour : " + updatedEmploye);

            // Mettre à jour l'employé
            serviceEmploye.modifier1(updatedEmploye);
            alert.successMessage("Employé mis à jour avec succès !");

            // Rafraîchir l'affichage
            refreshEmployees();

            // Réinitialiser les champs de texte après la mise à jour
            registerClearFields();
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
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


    void handleEmployeeSelection(Utilisateur employe) {
        // Mettre à jour l'employé sélectionné
        selectedEmploye = employe;

        if (selectedEmploye != null) {
            // Remplir les champs du formulaire avec les données de l'employé sélectionné
            addEmployee_firstName.setText(selectedEmploye.getNom());
            addEmployee_lastName.setText(selectedEmploye.getPrenom());
            addEmployee_email.setText(selectedEmploye.getEmail());

            // Désactiver le champ de mot de passe pour empêcher la modification
            addEmployee_password.setDisable(true);
            addEmployee_password.setText(selectedEmploye.getPassword());

            // Sélectionner la position dans le ComboBox
            ComboBox<String> positionComboBox = (ComboBox<String>) addEmployee_position;
            positionComboBox.getSelectionModel().select(selectedEmploye.getPoste());

            // Remplir le champ de salaire
            addEmployee_salaire.setText(String.valueOf(selectedEmploye.getSalaire()));

            // Charger l'image de profil
            if (selectedEmploye.getImgSrc() != null) {
                String imagePath = selectedEmploye.getImgSrc();
                imagePath = imagePath.replace("%20", " "); // Décodage des espaces

                if (imagePath.startsWith("file:")) {
                    // Chemin absolu
                    addEmployee_image.setImage(new Image(imagePath));
                } else {
                    // Chemin relatif
                    addEmployee_image.setImage(new Image(getClass().getResource(imagePath).toString()));
                }
            } else {
                // Image par défaut
                addEmployee_image.setImage(new Image(getClass().getResourceAsStream("/img/user.png")));
            }
        }
    }



    public void refreshEmployees() {
        // Recharger la liste des employés depuis la base de données
        List<Utilisateur> employees = getEmployees();

        // Effacer le contenu actuel de la GridPane
        grid.getChildren().clear();

        // Réafficher les employés dans la GridPane
        int column = 0;
        int row = 0;

        try {
            for (Utilisateur employe : employees) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employee_item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                EmployeesItemController controller = fxmlLoader.getController();
                controller.setParentControler(this);
                controller.setUtilisateur(employe);

                // Ajouter un gestionnaire d'événements pour la sélection/désélection
                anchorPane.setOnMouseClicked(event -> {
                    if (selectedEmploye != null && selectedEmploye.equals(employe)) {
                        // Désélectionner l'employé
                        selectedEmploye = null;
                        currentDisplayedEmploye = null;
                        registerClearFields(); // Vider les champs de texte
                        alert.successMessage("Employé désélectionné avec succès !");
                    } else {
                        // Sélectionner l'employé
                        selectedEmploye = employe;
                        System.out.println("Employé sélectionné : " + selectedEmploye.getNom());
                    }
                });

                // Ajouter l'élément à la GridPane
                grid.add(anchorPane, column, row);
                GridPane.setMargin(anchorPane, new Insets(20));

                column++;
                if (column == 3) { // 3 colonnes par ligne
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addEmployeeSelect(ActionEvent event) {
        if (selectedEmploye == null) {
            alert.errorMessage("Veuillez sélectionner un employé dans la liste !");
            return;
        }

        // Remplir les champs du formulaire avec les données de l'employé sélectionné
        addEmployee_firstName.setText(selectedEmploye.getNom());
        addEmployee_lastName.setText(selectedEmploye.getPrenom());
        addEmployee_email.setText(selectedEmploye.getEmail());

        // Désactiver le champ de mot de passe pour empêcher la modification
        addEmployee_password.setDisable(true);
        addEmployee_password.setText(selectedEmploye.getPassword());

        // Sélectionner la position dans le ComboBox
        ComboBox<String> positionComboBox = (ComboBox<String>) addEmployee_position;
        positionComboBox.getSelectionModel().select(selectedEmploye.getPoste());

        // Remplir le champ de salaire
        addEmployee_salaire.setText(String.valueOf(selectedEmploye.getSalaire()));

        // Charger l'image de profil
        if (selectedEmploye.getImgSrc() != null) {
            String imagePath = selectedEmploye.getImgSrc();
            imagePath = imagePath.replace("%20", " "); // Décodage des espaces (remplacement de %20 par un espace)

            if (imagePath.startsWith("file:")) {
                // Chemin absolu (si c'est un chemin complet sur le système)
                addEmployee_image.setImage(new Image(imagePath));
            } else {
                // Chemin relatif (relatif au répertoire des ressources)
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    // Vérifier si l'URL est valide
                    addEmployee_image.setImage(new Image(imageUrl.toString()));
                } else {
                    // Si l'image n'est pas trouvée, utiliser une image par défaut
                    addEmployee_image.setImage(new Image(getClass().getResource("/img/user.png").toString()));
                }
            }
        } else {
            // Image par défaut si l'employé n'a pas d'image
            addEmployee_image.setImage(new Image(getClass().getResource("/img/user.png").toString()));
        }
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

    @FXML
    void addEmployeeUnselect(ActionEvent event) {
        // Réinitialiser l'employé sélectionné
        selectedEmploye = null;

        // Vider les champs de texte
        addEmployee_firstName.clear();
        addEmployee_lastName.clear();
        addEmployee_email.clear();
        addEmployee_password.clear();
        addEmployee_salaire.clear();

        // Réactiver le champ de mot de passe
        addEmployee_password.setDisable(false);

        // Réinitialiser la sélection dans le ComboBox
        ComboBox<String> positionComboBox = (ComboBox<String>) addEmployee_position;
        positionComboBox.getSelectionModel().clearSelection();

        // Réinitialiser l'image de profil
        addEmployee_image.setImage(new Image(getClass().getResourceAsStream("/img/user.png")));

        // Afficher un message de succès
        alert.successMessage("Employé désélectionné avec succès !");
    }
}