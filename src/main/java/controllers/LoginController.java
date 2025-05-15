    package controllers;

    import com.google.gson.stream.JsonReader;
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
    import javafx.util.Duration;
    import org.controlsfx.control.Notifications;
    import services.ServiceEntretien;
    import services.ServiceUtilisateur;
    import utils.CryptageUtil;
    import utils.alertMessage;

    import java.io.*;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.sql.SQLException;
    import java.time.LocalDateTime;
    import java.time.temporal.ChronoUnit;
    import java.util.Arrays;
    import java.util.List;
    import java.util.ResourceBundle;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledExecutorService;
    import java.util.concurrent.TimeUnit;

    import com.google.gson.JsonObject;
    import com.google.gson.JsonParser;

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

            // Si le reCAPTCHA est valide, masquer le WebView et activer le bouton "Sign Up"
            recaptchaWebView.setVisible(false);
            signup_btn.setDisable(false);

            alert.successMessage("reCAPTCHA validé avec succès !");
            recaptcha.setVisible(false);
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
                String email = forgot_email.getText();
                String newPassword = newPasswordField.getText();

                String jsonInput = String.format(
                        "{\"email\": \"%s\", \"password\": \"%s\"}", email, newPassword
                );

                URL url = new URL("http://127.0.0.1:8000/reset-password/api/reset-password");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                try (OutputStream os = con.getOutputStream()) {
                    os.write(jsonInput.getBytes("utf-8"));
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                if (json.get("success").getAsBoolean()) {
                    alert.successMessage("Mot de passe réinitialisé avec succès !");
                    resetCodeField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    resetPasswordForm.setVisible(false);
                    login_form.setVisible(true);
                } else {
                    alert.errorMessage("Erreur : " + json.get("message").getAsString());
                }

            } catch (IOException e) {
                e.printStackTrace();
                alert.errorMessage("Erreur lors de la réinitialisation du mot de passe !");
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

        private String loginAPI(String email, String password) throws IOException {
            URL url = new URL("http://127.0.0.1:8000/api/login"); // ← remplace par l'URL réelle
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            String jsonInput = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();

            InputStream responseStream = (code >= 400) ? con.getErrorStream() : con.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        }


        public void loginBtnOnAction(ActionEvent actionEvent) {
            alertMessage alert = new alertMessage();
            String email = login_email.getText().trim();
            String password = login_password.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                alert.errorMessage("Veuillez entrer votre Email et votre mot de passe !");
                return;
            }

            try {
                String jsonResponse = loginAPI(email, password);

                JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

                if (json.get("success").getAsBoolean()) {
                    int id = json.get("id").getAsInt();
                    String role = json.get("role").getAsString();
                    String emailUser = json.get("email").getAsString();
                    String nom = json.has("nom") ? json.get("nom").getAsString() : "";

                    // Création d’un utilisateur
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setId(id);
                    utilisateur.setEmail(emailUser);
                    utilisateur.setRole(role);
                    utilisateur.setNom(nom);

                    // Session utilisateur
                    Sessions sessions = Sessions.getInstance(utilisateur.getId());
                    sessions.setRole(utilisateur.getRole());

                    // Redirection en fonction du rôle
                    FXMLLoader loader;
                    Parent root;

                    if (role.equals("RH") || role.equals("ResponsableRH")) {
                        loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                        root = loader.load();
                        Controller controller = loader.getController();
                        controller.loadPage("/dash.fxml");
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

                } else {
                    String message = json.has("message") ? json.get("message").getAsString() : "Email ou mot de passe incorrect !";
                    alert.errorMessage(message);
                }

            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                alert.errorMessage("Erreur de connexion avec le serveur !");
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
            if (signup_btn.isDisabled()) {
                alert.errorMessage("Veuillez d'abord valider le CAPTCHA !");
                return;
            }

            if (signup_nom.getText().isEmpty() || signup_prenom.getText().isEmpty() || signup_email.getText().isEmpty()
                    || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()) {
                alert.errorMessage("Tous les champs doivent être remplis !");
                return;
            }

            if (!isValidEmail(signup_email.getText())) {
                alert.errorMessage("Veuillez entrer une adresse email valide !");
                return;
            }

            if (!isStrongPassword(signup_password.getText())) {
                alert.errorMessage("Le mot de passe doit contenir au moins 8 caractères, " +
                        "une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial !");
                return;
            }

            if (!signup_password.getText().equals(signup_cPassword.getText())) {
                alert.errorMessage("Les mots de passe ne correspondent pas !");
                return;
            }

            String imagePath = (selectedImageFile != null)
                    ? selectedImageFile.toURI().toString()
                    : getClass().getResource("/img/user.png").toString();

            try {
                String jsonInput = String.format(
                        "{" +
                                "\"nom\": \"%s\", " +
                                "\"prenom\": \"%s\", " +
                                "\"email\": \"%s\", " +
                                "\"password\": \"%s\", " +
                                "\"imgSrc\": \"%s\", " +
                                "\"competence\": \"%s\"" +
                                "}",
                        signup_nom.getText(), signup_prenom.getText(), signup_email.getText(),
                        signup_password.getText(), imagePath, signup_competence.getText()
                );

                URL url = new URL("http://127.0.0.1:8000/api/register"); // Vérifie que cette URL retourne bien du JSON
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                try (OutputStream os = con.getOutputStream()) {
                    os.write(jsonInput.getBytes("utf-8"));
                }

                int status = con.getResponseCode();
                InputStream responseStream = (status >= 400) ? con.getErrorStream() : con.getInputStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                System.out.println("Réponse brute : " + response);

                String rawResponse = response.toString().trim();

                if (!rawResponse.startsWith("{")) {
                    alert.errorMessage("La réponse du serveur n'est pas au format JSON : " + rawResponse);
                    return;
                }

                JsonReader reader = new JsonReader(new StringReader(rawResponse));
                reader.setLenient(true);
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                if (json.get("success").getAsBoolean()) {
                    alert.successMessage("Inscription réussie !");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent parent = loader.load();
                    LoginController controller = loader.getController();
                    controller.setNom(signup_nom.getText());
                    controller.setPrenom(signup_prenom.getText());
                    controller.setEmail(signup_email.getText());
                    controller.setPassword(signup_password.getText());
                    controller.setImgSrc(imagePath);

                    signup_nom.getScene().setRoot(parent);
                    registerClearFields();
                } else {
                    String msg = json.has("message") ? json.get("message").getAsString() : "Erreur inconnue";
                    alert.errorMessage("Échec de l'inscription : " + msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
                alert.errorMessage("Erreur lors de la requête d'inscription !");
            } catch (Exception e) {
                e.printStackTrace();
                alert.errorMessage("Erreur inattendue lors de la lecture de la réponse !");
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


        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);



        @FXML
        public void startScheduler() {
            System.out.println("Démarrage du planificateur...");
            scheduler.scheduleAtFixedRate(this::checkUpcomingEntretiens, 0, 3, TimeUnit.HOURS);
        }

        private void checkUpcomingEntretiens() {
            try {
                ServiceEntretien serviceEntretien=new ServiceEntretien();
                List<Entretien> entretiens = serviceEntretien.afficher(); // Récupérer tous les entretiens
                System.out.println("Nombre total d'entretiens : " + entretiens.size());

                for (Entretien entretien : entretiens) {
                    LocalDateTime maintenant = LocalDateTime.now();
                    LocalDateTime dateHeureEntretien = LocalDateTime.of(entretien.getDate(), entretien.getHeure());
                    long delai = ChronoUnit.MILLIS.between(maintenant, dateHeureEntretien.minusHours(24)); // 24 heures avant l'entretien

                    if (delai > 0) {
                        System.out.println("Planification de la notification pour : " + dateHeureEntretien.minusHours(24));
                        scheduleNotification(entretien);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération des entretiens : " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void scheduleNotification(Entretien entretien) {
            LocalDateTime maintenant = LocalDateTime.now();
            LocalDateTime dateHeureEntretien = LocalDateTime.of(entretien.getDate(), entretien.getHeure());
            long delai = ChronoUnit.MILLIS.between(maintenant, dateHeureEntretien.minusHours(24)); // 24 heures avant l'entretien

            if (delai > 0) {
                scheduler.schedule(() -> showNotification(entretien), delai, TimeUnit.MILLISECONDS);
                System.out.println("Notification planifiée pour : " + dateHeureEntretien.minusHours(24));
            }
        }

        private void showNotification(Entretien entretien) {
            javafx.application.Platform.runLater(() -> {
                // Charger l'image depuis les ressources
                Image image = new Image("/image/error.png"); // Chemin de l'image dans le dossier des ressources

                // Créer la notification
                Notifications notifications = Notifications.create()
                        .title("Rappel d'entretien") // Titre de la notification
                        .text("Un entretien est prévu dans 24 heures : " + entretien.getDate() + " à " + entretien.getHeure()) // Texte de la notification
                        .graphic(new ImageView(image)) // Ajouter l'image à la notification
                        .hideAfter(Duration.seconds(4)); // Durée d'affichage de la notification (4 secondes)

                // Afficher la notification
                notifications.show();
            });
        }

    }

