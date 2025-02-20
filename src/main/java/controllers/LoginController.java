package controllers;

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
import javafx.stage.Stage;
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
                        loader = new FXMLLoader(getClass().getResource("/Display.fxml"));
                        root = loader.load();
                        Stage stage = (Stage) login_username.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                        return;
                    } else {
                        loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));

                        if (loader.getLocation() == null) {
                            System.out.println("Erreur : Profile.fxml introuvable !");
                            alert.errorMessage("Fichier Profile.fxml introuvable !");
                            return;
                        }

                        // Charger la vue avant d'accéder au contrôleur
                        root = loader.load();
                        // Accéder au contrôleur après avoir chargé la vue
                        ProfileController profileController = loader.getController();
                        if (profileController == null) {
                            System.out.println("Erreur : Impossible de charger ProfileController !");
                            alert.errorMessage("Impossible de charger le contrôleur Profile.fxml !");
                            return;
                        }

                        // Passer l'utilisateur au contrôleur
                        profileController.setUtilisateur(utilisateur);
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

        // Création de l'utilisateur
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        Utilisateur utilisateur = new Utilisateur(
                signup_nom.getText(),
                signup_prenom.getText(),
                signup_email.getText(),
                signup_password.getText(),
                role,
                imagePath
        );

        try {
            // Ajout de l'utilisateur à la base de données
            serviceUtilisateur.ajouter(utilisateur);
            alert.successMessage("Inscription réussie !");

            // Chargement du contrôleur de connexion
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

        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de l'inscription : " + e.getMessage());
        } catch (IOException e) {
            alert.errorMessage("Erreur de chargement de la page de connexion.");
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

