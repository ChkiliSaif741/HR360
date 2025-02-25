package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entities.Offre;
import javafx.util.Duration;
import org.json.JSONArray;
import services.ServiceOffre;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AjouterOffre implements Initializable {




    @FXML
    private TextField titreField;
    @FXML
    private Label titreError;

    @FXML
    private TextArea descriptionField;
    @FXML
    private Label descriptionError;
    @FXML
    private Label labelDatePublication;
    @FXML
    private Label labelDateExpiration;

    @FXML
    private Label datePublicationError;
    @FXML
    private Label dateExpirationError;

    private LocalDateTime datePublication;
    private LocalDateTime dateExpiration;

    private final ServiceOffre serviceOffre = new ServiceOffre();

    // Implémentation de la méthode initialize avec les paramètres requis
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void choisirDatePublication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DatePublication.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            DatePublication controller = loader.getController();
            stage.showAndWait();

            datePublication = controller.getSelectedDateTime();

            if (datePublication != null) {
                labelDatePublication.setText("Date de publication : " + datePublication.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void choisirDateExpiration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DateExpiration.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);

            DateExpiration controller = loader.getController();
            stage.showAndWait();

            dateExpiration = controller.getSelectedDateTime();

            if (dateExpiration != null) {
                labelDateExpiration.setText("Date d'expiration : " + dateExpiration.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Cette méthode est appelée lors du clic sur un bouton
    public void animateButton(MouseEvent event) {
        Node button = (Node) event.getSource();

        // Appliquer un effet "glow" (luminosité) au bouton
        Glow glow = new Glow();
        glow.setLevel(0.7); // Ajustez la luminosité selon vos préférences
        button.setEffect(glow);

        // Créer une animation de changement de couleur du bouton
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(button.styleProperty(), "-fx-background-color: #FF9800;") // Couleur plus claire
                ),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(button.styleProperty(), "-fx-background-color: #212121;") // Retour à la couleur d'origine
                )
        );

        timeline.setOnFinished(e -> {
            button.setEffect(null); // Retirer l'effet glow après l'animation
        });

        timeline.play(); // Joue l'animation
    }
    @FXML
    public void genererDescriptionAI() {
        String titre = titreField.getText().trim();
        if (titre.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un titre pour générer une description.");
            return;
        }

        String description = generateDescriptionFromAI(titre);

        if (description != null && !description.isEmpty()) {
            descriptionField.setText(description); // Afficher la description dans le champ
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de générer une description.");
        }
    }
    private String generateDescriptionFromAI(String titre) {
        try {
            // Créer la requête JSON
            JSONObject json = new JSONObject();
            json.put("model", "meta-llama/Llama-3.3-70B-Instruct-Turbo");
            json.put("messages", new JSONObject[]{new JSONObject()
                    .put("role", "user")
                    .put("content", "Générer seulement une description pour une offre intitulée '" + titre + "' en tenant compte de l'état du marché.")});

            // Créer l'URL de la requête
            URL url = new URL("https://api.together.xyz/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + "39aaaa3ca5dadee1f4964c0c61902ce51f28fcb9cb347820c36813694bce89bc");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));

            // Lire la réponse de l'API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Analyser la réponse JSON
            JSONObject responseJson = new JSONObject(response.toString());

            // Vérifier si "choices" contient des objets avec la clé "message"
            if (responseJson.has("choices")) {
                JSONObject choice = responseJson.getJSONArray("choices").getJSONObject(0);
                if (choice.has("message")) {
                    JSONObject message = choice.getJSONObject("message");
                    if (message.has("content")) {
                        // Extraire le contenu complet de la description
                        String fullDescription = message.getString("content").trim();

                        // Supprimer le titre (la première ligne) de la description
                        String[] lines = fullDescription.split("\n");
                        if (lines.length > 1) {
                            // Rejoindre les lignes à partir de la deuxième ligne
                            StringBuilder descriptionWithoutTitle = new StringBuilder();
                            for (int i = 1; i < lines.length; i++) {
                                descriptionWithoutTitle.append(lines[i]).append("\n");
                            }
                            return descriptionWithoutTitle.toString().trim();
                        } else {
                            return fullDescription; // Retourner la description complète si elle n'a qu'une seule ligne
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Retourner une valeur par défaut en cas d'erreur
        return "Impossible de générer une description pour le moment.";
    }





    @FXML
    public void enregistrerOffre() {
        boolean isValid = true;

        // Validation du titre
        String titre = titreField.getText().trim();
        if (titre.isEmpty() || titre.length() < 5 || titre.length() > 100) {
            titreError.setText("Le titre doit comporter entre 5 et 100 caractères.");
            isValid = false;
            showAlert(Alert.AlertType.ERROR, "Erreur de titre", "Attention!!!!");
        } else {
            titreError.setText("");
        }

        // Validation de la description
        String description = descriptionField.getText().trim();
        if (description.isEmpty() || description.length() < 10) {
            descriptionError.setText("La description doit comporter entre 10 et 500 caractères.");
            isValid = false;
            showAlert(Alert.AlertType.ERROR, "Erreur de description", "Attention!!!!");
        } else {
            descriptionError.setText("");
        }

        // Vérifier si la date de publication a été sélectionnée
        if (datePublication == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "Veuillez sélectionner une date et une heure de publication.");
            datePublicationError.setText("Veuillez sélectionner une date de publication.");
            isValid = false;
        } else {
            datePublicationError.setText("");
        }

        // Vérifier si la date d'expiration a été sélectionnée
        if (dateExpiration == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "Veuillez sélectionner une date et une heure d'expiration.");
            dateExpirationError.setText("Veuillez sélectionner une date d'expiration.");
            isValid = false;
        } else {
            dateExpirationError.setText("");
        }

        // Vérifier si la date de publication est avant la date d'expiration
        if (datePublication != null && dateExpiration != null && datePublication.isAfter(dateExpiration)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de dates", "La date de publication ne peut pas être après la date d'expiration.");
            isValid = false;
        }

        // Si tout est valide, on enregistre l'offre
        if (isValid) {
            // Création de l'objet Offre
            Offre offre = new Offre(
                    titre,                     // Titre de l'offre
                    description,                // Description
                    datePublication,             // Date de publication
                    dateExpiration               // Date d'expiration
            );

            // Mettre à jour le statut en fonction de la date d'expiration
            if (dateExpiration.isBefore(LocalDateTime.now())) {
                offre.setStatut("Expirée");
            } else {
                offre.setStatut("Publiée");
            }

            // Enregistrement dans la base de données
            try {
                serviceOffre.ajouter(offre);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre enregistrée avec succès.");
                try {
                    // Charger la scène pour l'ajout d'une offre
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
                    Parent root=loader.load();
                    Controller controller = loader.getController();
                    controller.loadPage("/ListeOffres.fxml");

                    titreField.getScene().setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR,"Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout.");
                }


            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout de l'offre.");
            }

        }
    }


    @FXML
    private void annuler() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
