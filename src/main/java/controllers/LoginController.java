    package controllers;

    import entities.*;
    import jakarta.mail.MessagingException;
    import javafx.concurrent.Worker;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.web.WebEngine;
    import javafx.scene.web.WebView;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;
    import services.ServiceUtilisateur;
    import utils.CryptageUtil;
    import utils.alertMessage;

    import java.io.File;
    import java.io.IOException;
    import java.net.URL;
    import java.sql.SQLException;
    import java.util.Arrays;
    import java.util.List;
    import java.util.ResourceBundle;

    public class LoginController implements Initializable {


        @FXML
        private Button btnCaptcha;

        @FXML
        private Button btnCaptchaValidate;

        @FXML
        private Button btnChangerPhoto;

        @FXML
        private Button changepass_backBtn;

        @FXML
        private PasswordField confirmPasswordField;

        @FXML
        private Button forgot_backBtn;

        @FXML
        private TextField forgot_email;

        @FXML
        private AnchorPane forgot_form;

        @FXML
        private Button forgot_proceedBtn;

        @FXML
        private Button login_btn;

        @FXML
        private Button login_createAccount;

        @FXML
        private TextField login_email;

        @FXML
        private Hyperlink login_forgotP;

        @FXML
        private AnchorPane login_form;

        @FXML
        private PasswordField login_password;

        @FXML
        private CheckBox login_selectShowPassword;

        @FXML
        private TextField login_showPassword;

        @FXML
        private AnchorPane main_form;

        @FXML
        private PasswordField newPasswordField;

        @FXML
        private ImageView photoProfil;

        @FXML
        private AnchorPane recaptcha;

        @FXML
        private WebView recaptchaWebView;

        @FXML
        private TextField resetCodeField;

        @FXML
        private Button resetPasswordButton;

        @FXML
        private AnchorPane resetPasswordForm;

        @FXML
        private Button signup_btn;

        @FXML
        private PasswordField signup_cPassword;

        @FXML
        private TextField signup_competence;

        @FXML
        private TextField signup_email;

        @FXML
        private AnchorPane signup_form;

        @FXML
        private Button signup_loginAccount;

        @FXML
        private TextField signup_nom;

        @FXML
        private PasswordField signup_password;

        @FXML
        private TextField signup_prenom;



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




        /*@FXML
        private void initialize() {

            forgot_form.setVisible(false);
            login_form.setVisible(true);
            signup_form.setVisible(false);
            resetPasswordForm.setVisible(false);

            String siteKey = "6LdIE-cqAAAAADO53d6zqk7I5To8W4CyHfi3-UOV"; // Remplacez par votre clé de site
            //recaptchaWebView.getEngine().load(getClass().getResource("/test.html").toExternalForm());
            http://localhost/test.html
            recaptchaWebView.getEngine().load("http://localhost/test.html");

        }*/



        @Override
        public void initialize(URL location, ResourceBundle resources) {
            forgot_form.setVisible(false);
            login_form.setVisible(true);
            signup_form.setVisible(false);
            resetPasswordForm.setVisible(false);

            // Désactiver le bouton "Sign Up" par défaut
            signup_btn.setDisable(true);

            // Masquer le WebView au démarrage
            recaptcha.setVisible(false);
        }

        @FXML
        void showRecaptcha(ActionEvent event) {
            System.out.println("Bouton showRecaptcha cliqué !");

            // Afficher le WebView
            recaptcha.setVisible(true);
            System.out.println("WebView visible : " + recaptchaWebView.isVisible());

            // Charger le contenu reCAPTCHA
            WebEngine webEngine = recaptchaWebView.getEngine();
            webEngine.load("http://localhost/test.html"); // Mettez à jour l'URL si nécessaire
            System.out.println("Chargement du reCAPTCHA en cours...");
        }



        public void handleCaptchaResponse(String response) {
            alertMessage alert = new alertMessage();

            if (response == null || response.isEmpty()) {
                alert.errorMessage("Veuillez compléter le CAPTCHA !");
                return;
            }

            if (!RecaptchaValidator.verifyCaptcha(response)) {
                alert.errorMessage("CAPTCHA invalide !");
                return;
            }

            recaptcha.setVisible(false);
            alert.successMessage("CAPTCHA validé avec succès !");

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


        public void forgotPassword() throws MessagingException {
            alertMessage alert = new alertMessage();

            // Vérification des champs vides
            if (forgot_email.getText().isEmpty()) {
                alert.errorMessage("Veuillez remplir tous les champs vides !");
                return;
            }

            // Récupération des données saisies
            String email = forgot_email.getText();

            // Générer un code de vérification (exemple simplifié)
            String resetCode = generateResetCode(); // Méthode pour générer un code aléatoire

            // Envoyer l'e-mail de récupération via InfoBip
            //InfoBipSmtpClient.sendPasswordRecoveryEmail(email, resetCode);
            Mailpass mailpass=new Mailpass();
            mailpass.envoyerEmail(forgot_email.getText(),"Reset Password",
                    "Voici votre reset Code :"+resetCode);
            // Afficher un message à l'utilisateur
            alert.successMessage("Un e-mail de récupération a été envoyé à " + email);

            // Afficher le formulaire de réinitialisation de mot de passe dans l'application
            forgot_form.setVisible(false);
            resetPasswordForm.setVisible(true); // Afficher le formulaire de réinitialisation
        }

        // Méthode pour générer un code de vérification aléatoire
        private String generateResetCode() {
            return String.valueOf((int) (Math.random() * 900000) + 100000); // Code à 6 chiffres
        }

        public void resetPassword() {
            alertMessage alert = new alertMessage();

            // Vérification des champs vides
            if (resetCodeField.getText().isEmpty() || newPasswordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                alert.errorMessage("Veuillez remplir tous les champs !");
                return;
            }

            // Vérification de la correspondance des mots de passe
            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                alert.errorMessage("Les mots de passe ne correspondent pas !");
                return;
            }

            // Vérification du code de vérification
            String resetCode = resetCodeField.getText();
            if (!isValidResetCode(resetCode)) { // Implémentez cette méthode pour valider le code
                alert.errorMessage("Code de vérification invalide !");
                return;
            }

            // Mettre à jour le mot de passe dans la base de données
            try {
                ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
                Utilisateur utilisateur = serviceUtilisateur.getUserByEmail(forgot_email.getText());
                utilisateur.setPassword(newPasswordField.getText());
                serviceUtilisateur.modifierMotDePasse(utilisateur);
                alert.successMessage("Mot de passe mis à jour avec succès !");

                // Réinitialiser les champs et revenir au formulaire de connexion
                resetCodeField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                resetPasswordForm.setVisible(false);
                login_form.setVisible(true);
            } catch (SQLException e) {
                alert.errorMessage("Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
            }
        }

        // Méthode pour valider le code de vérification
        private boolean isValidResetCode(String code) {
            // Implémentez la logique de validation du code ici
            return true; // Retourne true pour l'exemple
        }

        public void changePassword() {
            alertMessage alert = new alertMessage();
            if (newPasswordField.getText().isEmpty()
                    || confirmPasswordField.getText().isEmpty()) {
                alert.errorMessage("Veuillez remplir tous les champs vides!");
            }

            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                alert.errorMessage("Les mots de passe ne correspondent pas!");
            } else if (newPasswordField.getText().length() < 5) {
                alert.errorMessage("Mot de passe invalide ! , 5 caractères au moins!");
            }

            if (utilisateurVerifie != null) {
                try {

                    utilisateurVerifie.setPassword(newPasswordField.getText());
                    serviceUtilisateur.modifierMotDePasse(utilisateurVerifie);
                    alert.successMessage("Mot de passe modifié avec succès!");

                    login_form.setVisible(true);
                    signup_form.setVisible(false);
                    resetPasswordForm.setVisible(false);
                    forgot_form.setVisible(false);

                    login_email.setText("");
                    login_password.setVisible(true);
                    login_password.setText("");
                    login_showPassword.setVisible(false);
                    login_selectShowPassword.setSelected(false);

                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
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

                // Initialisation de la sessions utilisateur
                Sessions sessions = Sessions.getInstance(utilisateur.getId()); // Créer ou récupérer la sessions
                sessions.setRole(utilisateur.getRole()); // Définir le rôle dans la sessions

                // Redirection en fonction du rôle
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
                        profilEMPController profilController = controller.loadPage("/profilEMP.fxml").getController();
                        profilController.setUtilisateur(utilisateur);
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
        void captchaValidate(ActionEvent event) {
            alertMessage alert = new alertMessage();

            WebEngine webEngine = recaptchaWebView.getEngine();

            // Vérifier si le reCAPTCHA est chargé
            if (webEngine.getLoadWorker().getState() != Worker.State.SUCCEEDED) {
                alert.errorMessage("Le reCAPTCHA n'est pas encore chargé. Veuillez patienter...");
                return;
            }

            // Récupérer la réponse du reCAPTCHA
            String gRecaptchaResponse = (String) webEngine.executeScript("grecaptcha.getResponse()");

            // Vérifier si le reCAPTCHA est rempli
            if (gRecaptchaResponse == null || gRecaptchaResponse.isEmpty()) {
                alert.errorMessage("Veuillez compléter le reCAPTCHA !");
                return;
            }

            // Valider la réponse du reCAPTCHA côté serveur
            if (!RecaptchaValidator.verifyCaptcha(gRecaptchaResponse)) {
                alert.errorMessage("reCAPTCHA invalide !");
                return;
            }

            // Si le reCAPTCHA est valide, masquer le WebView et activer le bouton "Sign Up"
            recaptchaWebView.setVisible(false);
            signup_btn.setDisable(false);

            alert.successMessage("reCAPTCHA validé avec succès !");
            recaptcha.setVisible(false);
        }


        @FXML
        public void registerBtnOnAction(ActionEvent event) {
            alertMessage alert = new alertMessage();

            // Vérifier si le CAPTCHA a été validé
            if (signup_btn.isDisabled()) {
                alert.errorMessage("Veuillez d'abord valider le CAPTCHA !");
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
                ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
                if (serviceUtilisateur.emailExists(signup_email.getText())) {
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
                resetPasswordForm.setVisible(false);
                forgot_form.setVisible(false);
            } else if (event.getSource() == login_createAccount) {
                signup_form.setVisible(true);
                login_form.setVisible(false);
                resetPasswordForm.setVisible(false);
                forgot_form.setVisible(false);
            } else if (event.getSource() == login_forgotP) {
                signup_form.setVisible(false);
                login_form.setVisible(false);
                forgot_form.setVisible(true);
                resetPasswordForm.setVisible(false);

            } else if (event.getSource() == changepass_backBtn) {
                login_form.setVisible(false);
                resetPasswordForm.setVisible(false);
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

