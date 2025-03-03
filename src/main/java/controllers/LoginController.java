package controllers;

import entities.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import services.ServiceUtilisateur;
import utils.CryptageUtil;
import utils.alertMessage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class LoginController {

    @FXML
    private TextField signup_competence;

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
    private TextField login_email;

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
    private Button signup_btn, signup_loginAccount;

    @FXML
    private WebView recaptchaWebView;

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
    private static final List<String> ROLE_LIST = Arrays.asList("Candidat", "Employé", "RH");
    public static String hCaptchaToken;

    @FXML
    private void initialize() {

        forgot_form.setVisible(false);
        login_form.setVisible(true);
        signup_form.setVisible(false);
        changepass_form.setVisible(false);

        String siteKey = "6LdIE-cqAAAAADO53d6zqk7I5To8W4CyHfi3-UOV"; // Remplacez par votre clé de site
        String htmlContent = "<html>" +
                "<head>" +
                "<script src='https://www.google.com/recaptcha/api.js'></script>" +
                "</head>" +
                "<body>" +
                "<form action='?' method='POST'>" +
                "<div class='g-recaptcha' data-sitekey='" + siteKey + "'></div>" +
                "<br/>" +
                "<input type='submit' value='Submit'>" +
                "</form>" +
                "</body>" +
                "</html>";
        recaptchaWebView.getEngine().loadContent(htmlContent);


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


    public void forgotPassword() {
        alertMessage alert = new alertMessage();

        // Vérification des champs vides
        if (forgot_username.getText().isEmpty() ||
                forgot_email.getText().isEmpty()) {

            alert.errorMessage("Veuillez remplir tous les champs vides !");
            return;
        }

        // Récupération des données saisies
        String username = forgot_username.getText();
        String email = forgot_email.getText();
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        try {
            Utilisateur user = serviceUtilisateur.getUserForPasswordReset(username, email);

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

                login_email.setText("");
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


    public void loginBtnOnAction(ActionEvent actionEvent) throws SQLException {
        alertMessage alert = new alertMessage();

        // Vérification des champs vides
        String email = login_email.getText().trim();
        String password = login_password.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            alert.errorMessage("Veuillez entrer votre Email et votre mot de passe !");
            return;
        }

        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        Utilisateur utilisateur = serviceUtilisateur.authentifier(email, password);

        if (utilisateur != null) {
            System.out.println("Utilisateur authentifié : " + utilisateur.getEmail());
            System.out.println("Rôle utilisateur connecté : " + utilisateur.getRole());

            // Initialisation de la session utilisateur
            Session.getInstance(utilisateur.getId());

            try {
                FXMLLoader loader;
                Parent root;

                String role = utilisateur.getRole().trim(); // Suppression des espaces parasites

                if (role.equals("RH") || role.equals("ResponsableRH")) {  // Vérification multiple
                    loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                    root = loader.load();
                } else if (role.equals("Employe")) {
                    loader = new FXMLLoader(getClass().getResource("/SideBarEMP.fxml"));
                    root = loader.load();

                    Controller controller = loader.getController();
                    FormationsControllerEMP formationsController = controller.loadPage("/FormationsEMP.fxml").getController();
                } else if (role.equals("Candidat")) {
                    loader = new FXMLLoader(getClass().getResource("/SideBarCAN.fxml"));
                    root = loader.load();

                    Controller controller = loader.getController();
                    ProfileController profileController = controller.loadPage("/Profile.fxml").getController();
                    profileController.setUtilisateur(utilisateur);
                } else {
                    alert.errorMessage("Rôle utilisateur inconnu !");
                    return;
                }

                // Changement de scène
                Stage stage = (Stage) login_email.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                alert.errorMessage("Erreur lors du chargement de la page !");
            }

        } else {
            alert.errorMessage("Email ou mot de passe incorrect !");
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

        // Récupérer le jeton reCAPTCHA
        String gRecaptchaResponse = (String) recaptchaWebView.getEngine().executeScript("grecaptcha.getResponse()");

        // Vérifier si le CAPTCHA est rempli
        if (gRecaptchaResponse == null || gRecaptchaResponse.isEmpty()) {
            alert.errorMessage("Veuillez compléter le CAPTCHA !");
            return;
        }

        // Valider le jeton reCAPTCHA côté serveur
        if (!RecaptchaValidator.verifyCaptcha(gRecaptchaResponse)) {
            alert.errorMessage("CAPTCHA invalide !");
            return;
        }

        // Vérification des champs vides
        if (signup_nom.getText().isEmpty() || signup_prenom.getText().isEmpty() || signup_email.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()) {
            alert.errorMessage("Tous les champs doivent être remplis !");
            return;
        }

        // Validation de l'email
        if (!isValidEmail(signup_email.getText())) {
            alert.errorMessage("Veuillez entrer une adresse email valide !");
            return;
        }

        // Vérification de l'unicité de l'email
        try {
            ServiceUtilisateur serviceCandidat = new ServiceUtilisateur();
            if (serviceCandidat.emailExists(signup_email.getText())) {
                alert.errorMessage("Cet email est déjà utilisé. Veuillez en choisir un autre !");
                return;
            }
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de la vérification de l'email : " + e.getMessage());
            return;
        }

        // Validation du mot de passe
        if (!isStrongPassword(signup_password.getText())) {
            alert.errorMessage("Le mot de passe doit contenir au moins 8 caractères, " +
                    "une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial !");
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

        // Cryptage du mot de passe
        String motDePasseCrypte = CryptageUtil.crypterMotDePasse(signup_password.getText());

        // Création de l'utilisateur, de l'employé ou du candidat
        // Créer un Candidat si le rôle est "Candidat"
        Utilisateur candidat = new Utilisateur();
        candidat.setNom(signup_nom.getText());
        candidat.setPrenom(signup_prenom.getText());
        candidat.setEmail(signup_email.getText());
        candidat.setPassword(motDePasseCrypte); // Utiliser le mot de passe crypté
        candidat.setRole("Candidat");
        candidat.setImgSrc(imagePath);
        candidat.setCompetence(signup_competence.getText());

        try {
            // Ajouter le candidat à la table Candidat
            ServiceUtilisateur serviceCandidat = new ServiceUtilisateur();
            serviceCandidat.ajouter(candidat);
            alert.successMessage("Candidat ajouté avec succès !");
        } catch (SQLException e) {
            alert.errorMessage("Erreur lors de l'ajout du candidat : " + e.getMessage());
            return;
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



    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }


    private boolean isStrongPassword(String password) {
        // Au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordRegex);
    }

    public void registerClearFields() {
        signup_nom.clear();
        signup_prenom.clear();
        signup_email.clear();
        signup_password.clear();
        signup_cPassword.clear();
        signup_competence.clear();
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

