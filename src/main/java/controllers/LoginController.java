package controllers;

import entities.Candidat;
import entities.Employe;
import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ServiceCandidat;
import services.ServiceEmploye;
import services.ServiceUtilisateur;
import utils.alertMessage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginController {

    @FXML
    private ImageView photoProfil;

    @FXML
    private Button btnChangerPhoto;

    @FXML
    private TextField changepass_cPassword;

    @FXML
    private TextField changePass_password;

    @FXML
    private TextField forgot_email;

    @FXML
    private TextField forgot_username;

    @FXML
    private ComboBox<?> forgot_selectRole;

    @FXML
    private Button forgot_backBtn;

    @FXML
    private Button changepass_backBtn;

    @FXML
    private Hyperlink login_forgotP;

    @FXML
    private TextField login_showPassword;

    @FXML
    private CheckBox login_selectShowPassword;

    @FXML
    private TextField login_username;

    @FXML
    private PasswordField login_password;

    @FXML
    private Button login_btn;

    @FXML
    private AnchorPane changepass_form;

    @FXML
    private Button login_createAccount;

    @FXML
    private AnchorPane login_form;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private AnchorPane forgot_form;

    @FXML
    private TextField signup_nom, signup_prenom, signup_email;

    @FXML
    private PasswordField signup_password, signup_cPassword;

    @FXML
    private ComboBox<String> signup_selectRole;

    @FXML
    private Button signup_btn, signup_loginAccount;

    private File selectedImageFile;

    private Employe utilisateurTemporaire; // Utilisez Employe si vous avez choisi la Solution 2
    private Candidat utilisateurTemporaire1;


    public void setNom(String nom) {
        this.signup_nom.setText(nom);
    }

    public void setPrenom(String prenom) {
        this.signup_prenom.setText(prenom);
    }

    public void setEmail(String email) {
        this.signup_email.setText(email);
    }

    public void setPassword(String password) {
        this.signup_password.setText(password);
    }


    private String imagePath = null;
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private Utilisateur utilisateurVerifie;
    private static final List<String> ROLE_LIST = Arrays.asList("Candidat", "Employé");

    @FXML
    private void initialize() {

        forgot_form.setVisible(false);
        login_form.setVisible(true);
        signup_form.setVisible(false);
        changepass_form.setVisible(false);
        signup_selectRole.setItems(FXCollections.observableArrayList(ROLE_LIST));
        forgotListRole();

        signup_selectRole.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("Employé".equals(newValue)) {
                // Ouvrir la fenêtre de saisie du salaire et du poste
                openEmployeeDetailsWindow();
            } else if ("Candidat".equals(newValue)) {
                // Ouvrir la fenêtre de saisie du CV
                openCandidatDetailsWindow();
            }
        });

    }


    public void showPassword() {
        if (login_selectShowPassword.isSelected()) {
            login_showPassword.setText(login_password.getText());
            login_showPassword.setVisible(true);
            login_password.setVisible(false);
        } else {
            login_password.setText(login_showPassword.getText());
            login_showPassword.setVisible(false);
            login_password.setVisible(true);
        }
    }

    public void forgotListRole() {

        List<String> listR = new ArrayList<String>();
        for (String role : ROLE_LIST) {
            listR.add(role);
        }
        ObservableList listData = FXCollections.observableArrayList(listR);
        forgot_selectRole.setItems(listData);
    }

    public void forgotPassword() {
        alertMessage alert = new alertMessage();

        // Vérification des champs vides
        if (forgot_username.getText().isEmpty() ||
                forgot_selectRole.getSelectionModel().isEmpty() ||
                forgot_email.getText().isEmpty()) {

            alert.errorMessage("Veuillez remplir tous les champs vides !");
            return;
        }

        // Récupération des données saisies
        String username = forgot_username.getText();
        String role = (String) forgot_selectRole.getSelectionModel().getSelectedItem();
        String email = forgot_email.getText();
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        try {
            Utilisateur user = serviceUtilisateur.getUserForPasswordReset(username, role, email);

            if (user != null) {
                utilisateurVerifie = user;
                alert.successMessage("Utilisateur trouvé ! Vous pouvez réinitialiser votre mot de passe.");
                login_form.setVisible(false);
                changepass_form.setVisible(true);
                forgot_form.setVisible(false);
                signup_form.setVisible(false);
            } else {
                alert.errorMessage("Aucun utilisateur trouvé avec ces informations !");
            }
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de la récupération des données : " + e.getMessage());
        }
    }

    public void changePassword() {
        alertMessage alert = new alertMessage();
        if (changePass_password.getText().isEmpty()
                || changepass_cPassword.getText().isEmpty()) {
            alert.errorMessage("Veuillez remplir tous les champs vides!");
        }

        if (!changePass_password.getText().equals(changepass_cPassword.getText())) {
            alert.errorMessage("Les mots de passe ne correspondent pas!");
        } else if (changePass_password.getText().length() < 5) {
            alert.errorMessage("Mot de passe invalide ! , 5 caractères au moins!");
        }

        if (utilisateurVerifie != null) {
            try {

                utilisateurVerifie.setPassword(changePass_password.getText());
                serviceUtilisateur.modifierMotDePasse(utilisateurVerifie);
                alert.successMessage("Mot de passe modifié avec succès!");

                login_form.setVisible(true);
                signup_form.setVisible(false);
                changepass_form.setVisible(false);
                forgot_form.setVisible(false);

                login_username.setText("");
                login_password.setVisible(true);
                login_password.setText("");
                login_showPassword.setVisible(false);
                login_selectShowPassword.setSelected(false);

                changePass_password.setText("");
                changepass_cPassword.setText("");
                utilisateurVerifie = null;

            } catch (SQLException e) {
                alert.errorMessage("Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
            }

        } else {
            alert.errorMessage("Aucun utilisateur vérifié trouvé!");
        }

    }


    public void loginBtnOnAction(ActionEvent actionEvent) {
        alertMessage alert = new alertMessage();

        if (login_username.getText().trim().isEmpty() || login_password.getText().trim().isEmpty()) {
            alert.errorMessage("Veuillez entrer votre Username et votre mot de passe !");
            return;
        }

        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        Utilisateur utilisateur = serviceUtilisateur.getUserByNom(login_username.getText().trim());

        if (utilisateur != null) {
            if (utilisateur.getPassword().equals(login_password.getText().trim())) {

                try {

                    FXMLLoader loader;
                    Parent root = null;

                    if ("Farhani".equals(utilisateur.getNom()) && "zzzzz".equals(utilisateur.getPassword())) {
                        loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                        root = loader.load();
                        Stage stage = (Stage) login_username.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                        return;
                    } else if ("zz".equals(utilisateur.getNom()) && "ee".equals(utilisateur.getPassword())) {
                        loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
                        root = loader.load();
                        Stage stage = (Stage) login_username.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                        return;
                    } else {
                        // Charger la scène pour l'ajout d'une offre
                        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/SideBarCAN.fxml"));
                        Parent root1 = loader1.load();
                        Controller controller = loader1.getController();
                        ProfileController controller1 = controller.loadPage("/Profile.fxml").getController();

                        //controller.container.getScene().setRoot(root1);
                        Stage stage = (Stage) login_username.getScene().getWindow();
                        stage.setScene(new Scene(root1));
                        stage.show();


                        if (loader1.getLocation() == null) {
                            System.out.println("Erreur : Profile.fxml introuvable !");
                            alert.errorMessage("Fichier Profile.fxml introuvable !");
                            return;
                        }

                        // Passer l'utilisateur au contrôleur
                        controller1.setUtilisateur(utilisateur);
                    }

                    // Changer la scène
                    Stage stage = (Stage) login_username.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Profil");
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    alert.errorMessage("Erreur lors du chargement de la page !");
                }


            } else {
                alert.errorMessage("Mot de passe incorrect !");
            }
        } else {
            alert.errorMessage("Veuillez vérifier votre username !");
        }
    }

    public void setImgSrc(String imgSrc) {
        if (imgSrc != null && !imgSrc.isEmpty()) {
            Image image = new Image(imgSrc); // Utilisez directement l'URI ou le chemin
            photoProfil.setImage(image);
        }
    }


    @FXML
    public void registerBtnOnAction(ActionEvent event) {
        alertMessage alert = new alertMessage();

        // Vérification des champs vides
        if (signup_nom.getText().isEmpty() || signup_prenom.getText().isEmpty() || signup_email.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()) {
            alert.errorMessage("Tous les champs doivent être remplis !");
            return;
        }

        // Vérification du rôle sélectionné
        String role = signup_selectRole.getValue();
        if (role == null || role.isEmpty()) {
            alert.errorMessage("Veuillez sélectionner un rôle !");
            return;
        }

        // Vérification de la correspondance des mots de passe
        if (!signup_password.getText().equals(signup_cPassword.getText())) {
            alert.errorMessage("Les mots de passe ne correspondent pas !");
            return;
        }

        // Définition du chemin de l'image de profil (par défaut si aucune sélectionnée)
        String imagePath = (selectedImageFile != null) ? selectedImageFile.toURI().toString() : getClass().getResource("/img/user.png").toString();

        System.out.println("Chemin de l'image : " + imagePath);

        // Création de l'utilisateur, de l'employé ou du candidat
        if ("Employé".equals(role)) {
            // Créer un Employe si le rôle est "Employé"
            Employe employe = new Employe();
            employe.setNom(signup_nom.getText());
            employe.setPrenom(signup_prenom.getText());
            employe.setEmail(signup_email.getText());
            employe.setPassword(signup_password.getText());
            employe.setRole(role);
            employe.setImgSrc(imagePath);

            // Utiliser les informations saisies dans EmployeeDetails
            if (utilisateurTemporaire != null) {
                employe.setPoste(utilisateurTemporaire.getPoste());
                employe.setSalaire(utilisateurTemporaire.getSalaire());
                employe.setIdFormation(utilisateurTemporaire.getIdFormation());
            }

            try {
                // Ajouter l'employé à la table Employe
                ServiceEmploye serviceEmploye = new ServiceEmploye();
                serviceEmploye.ajouter(employe);
                alert.successMessage("Employé ajouté avec succès !");
            } catch (SQLException e) {
                alert.errorMessage("Erreur lors de l'ajout de l'employé : " + e.getMessage());
                return;
            }
        } else if ("Candidat".equals(role)) {
            // Créer un Candidat si le rôle est "Candidat"
            Candidat candidat = new Candidat();
            candidat.setNom(signup_nom.getText());
            candidat.setPrenom(signup_prenom.getText());
            candidat.setEmail(signup_email.getText());
            candidat.setPassword(signup_password.getText());
            candidat.setRole(role);
            candidat.setImgSrc(imagePath);

            // Utiliser les informations saisies dans CandidateDetails
            if (utilisateurTemporaire != null) {
                candidat.setCv(((Candidat) utilisateurTemporaire1).getCv());
            }

            try {
                // Ajouter le candidat à la table Candidat
                ServiceCandidat serviceCandidat = new ServiceCandidat();
                serviceCandidat.ajouter(candidat);
                alert.successMessage("Candidat ajouté avec succès !");
            } catch (SQLException e) {
                alert.errorMessage("Erreur lors de l'ajout du candidat : " + e.getMessage());
                return;
            }
        } else {
            // Créer un Utilisateur standard
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(signup_nom.getText());
            utilisateur.setPrenom(signup_prenom.getText());
            utilisateur.setEmail(signup_email.getText());
            utilisateur.setPassword(signup_password.getText());
            utilisateur.setRole(role);
            utilisateur.setImgSrc(imagePath);

            try {
                // Ajouter l'utilisateur à la table Utilisateur
                ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
                serviceUtilisateur.ajouter(utilisateur);
                alert.successMessage("Utilisateur ajouté avec succès !");
            } catch (SQLException e) {
                alert.errorMessage("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
                return;
            }
        }

        // Chargement du contrôleur de connexion
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent parent = loader.load();
            LoginController controller = loader.getController();

            // Passage des informations à la page de connexion
            controller.setNom(signup_nom.getText());
            controller.setPrenom(signup_prenom.getText());
            controller.setEmail(signup_email.getText());
            controller.setPassword(signup_password.getText());
            controller.setImgSrc(imagePath); // Passage de l'image de profil

            // Passage à la page de connexion
            signup_nom.getScene().setRoot(parent);

            // Réinitialisation des champs
            registerClearFields();
        } catch (IOException e) {
            alert.errorMessage("Erreur de chargement de la page de connexion.");
        }
    }


    private void openEmployeeDetailsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EmployeeDetails.fxml"));
            Parent root = loader.load();

            EmployeeDetailsController controller = loader.getController();

            // Initialiser utilisateurTemporaire
            utilisateurTemporaire = new Employe();
            controller.setEmploye(utilisateurTemporaire);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du candidat");
            stage.showAndWait(); // Utiliser showAndWait() pour attendre que la fenêtre soit fermée

            // Après la fermeture de la fenêtre, récupérer les informations saisies
            if (utilisateurTemporaire.getPoste() != null && utilisateurTemporaire.getSalaire() > 0) {
                System.out.println("Poste : " + utilisateurTemporaire.getPoste());
                System.out.println("Salaire : " + utilisateurTemporaire.getSalaire());
                System.out.println("ID Formation : " + utilisateurTemporaire.getIdFormation());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openCandidatDetailsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CandidatDetails.fxml"));
            Parent root = loader.load();

            CandidatDetailsController controller = loader.getController();

            // Initialiser utilisateurTemporaire
            utilisateurTemporaire1 = new Candidat();
            controller.setCandidat(utilisateurTemporaire1);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du Candidat");
            stage.showAndWait(); // Utiliser showAndWait() pour attendre que la fenêtre soit fermée

            // Après la fermeture de la fenêtre, récupérer les informations saisies
            if (utilisateurTemporaire1.getCv() != null) {
                System.out.println("Cv : " + utilisateurTemporaire1.getCv());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void registerClearFields() {
        signup_nom.clear();
        signup_prenom.clear();
        signup_email.clear();
        signup_password.clear();
        signup_cPassword.clear();
        signup_selectRole.getSelectionModel().clearSelection();
    }


    public void switchForm(ActionEvent event) {
        if (event.getSource() == signup_loginAccount || event.getSource() == forgot_backBtn) {
            signup_form.setVisible(false);
            login_form.setVisible(true);
            changepass_form.setVisible(false);
            forgot_form.setVisible(false);
        } else if (event.getSource() == login_createAccount) {
            signup_form.setVisible(true);
            login_form.setVisible(false);
            changepass_form.setVisible(false);
            forgot_form.setVisible(false);
        } else if (event.getSource() == login_forgotP) {
            signup_form.setVisible(false);
            login_form.setVisible(false);
            forgot_form.setVisible(true);
            changepass_form.setVisible(false);

            forgotListRole();
        } else if (event.getSource() == changepass_backBtn) {
            login_form.setVisible(false);
            changepass_form.setVisible(false);
            forgot_form.setVisible(true);
            signup_form.setVisible(false);
        }
    }


    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            imagePath = selectedImageFile.getAbsolutePath();
            Image image = new Image(selectedImageFile.toURI().toString());
            photoProfil.setImage(image);
        }
    }


}

