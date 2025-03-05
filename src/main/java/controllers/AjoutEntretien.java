package controllers;

import entities.Entretien;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import services.ServiceEntretien;
import utils.statut;
import utils.type;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class AjoutEntretien implements Initializable {
    @FXML
    private ComboBox<type> typeComboBox;
    @FXML
    private TextField heureField;
    @FXML
    private TextField localisationField;
    @FXML
    private DatePicker datePicker;

    private ServiceEntretien serviceEntretien;
    @FXML
    private Label labellocalisation;
    @FXML
    private Label labeldate;
    @FXML
    private Label labelheure;
    @FXML
    private Label labeltype;

    @FXML
    private Button btnAdd;

    private boolean formSubmitted = false;
    @FXML
    private Label labeltypeC;
    @FXML
    private Label labeldateC;
    @FXML
    private Label labelheureC;
    @FXML
    private Label labellocalisationC;


    private int idCandidature;

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
        serviceEntretien = new ServiceEntretien();


        // Gérer la sélection du nom d'utilisateur pour filtrer les prénoms

        // Remplir les ComboBox avec les valeurs des énumérations
        typeComboBox.setItems(FXCollections.observableArrayList(type.values()));
        //statutComboBox.setItems(FXCollections.observableArrayList(statut.values()));


        // Désactiver le ComboBox de statut pour empêcher la modification
        //statutComboBox.setDisable(true);
        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == type.En_ligne) {
                localisationField.setVisible(false);
                labellocalisation.setVisible(false);
                labellocalisationC.setVisible(false);
            } else {
                localisationField.setVisible(true);
                labellocalisation.setVisible(true);
                labellocalisationC.setVisible(true);

            }
        });

        localisationField.setVisible(false);
        labellocalisation.setVisible(false);
        labellocalisationC.setVisible(false);

        //validateNumericField(idCandidatureComboBox.getEditor());

        //validateURLField(lienMeetField);
        //validateTextOnlyField(localisationField);

        // Vérifier la validité avant d'activer le bouton
        addFormListeners();
        // Charger les idCandidature depuis la base de données
    }

    public void setFormFields(){

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //chargerIdCandidature();
    }

    private void checkFormValidity() {
        if (!formSubmitted) return; // Ne fait rien tant que l'utilisateur n'a pas cliqué sur Ajouter

        boolean isValid = true;

        // Contrôle de la date
        if (datePicker.getValue() == null || !datePicker.getValue().isAfter(LocalDate.now())) {
            labeldate.setText("Sélectionnez une date future !");
            isValid = false;
        } else {
            labeldate.setText("");
        }

        // Contrôle de l'heure
        try {
            LocalTime heure = LocalTime.parse(heureField.getText());
            if (heure.isBefore(LocalTime.of(8, 0)) || heure.isAfter(LocalTime.of(18, 0))) {
                labelheure.setText("L'heure doit être entre 8h et 18h !");
                isValid = false;
            } else {
                labelheure.setText("");
            }
        } catch (DateTimeParseException e) {
            labelheure.setText("Format d'heure invalide (HH:mm) !");
            isValid = false;
        }

        // Contrôle du type
        if (typeComboBox.getValue() == null) {
            labeltype.setText("Sélectionnez un type !");
            isValid = false;
        } else {
            labeltype.setText("");
        }

        // Contrôle du statut
        /*if (statutComboBox.getValue() == null) {
            labelstatut.setText("Sélectionnez un statut !");
            isValid = false;
        } else {
            labelstatut.setText("");
        }*/

        // Contrôle de la localisation
        if (typeComboBox.getValue() == type.Presentiel) {
            if (localisationField.getText().isEmpty() || !localisationField.getText().matches("^[a-zA-Z\\s]+$")) {
                labellocalisation.setText("Localisation invalide (lettres et espaces uniquement) !");
                isValid = false;
            } else {
                labellocalisation.setText("");
            }
        }

        // Contrôle du lien Meet
        /*if (typeComboBox.getValue() == type.En_ligne) {
            if (lienMeetField.getText().isEmpty() || !isValidURL(lienMeetField.getText())) {
                labellienmeet.setText("Lien Meet invalide !");
                isValid = false;
            } else {
                labellienmeet.setText("");
            }
        }*/


        // Activer/désactiver le bouton Ajouter
        btnAdd.setDisable(!isValid);
    }


    private void addFormListeners() {
        //idCandidatureComboBox.valueProperty().addListener((obs, oldVal, newVal) -> checkFormValidity());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> checkFormValidity());
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> checkFormValidity());
        localisationField.textProperty().addListener((obs, oldVal, newVal) -> checkFormValidity());
        heureField.textProperty().addListener((obs, oldVal, newVal) -> checkFormValidity());
    }

    private void validateNumericField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void validateTextOnlyField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z\\s]*$")) {
                field.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });
    }


    /*private void validateURLField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isValidURL(newValue)) {
                field.setText("Lien Meet invalide !");
            } else {
                field.setText("");
            }
        });
    }

    private boolean isValidURL(String url) {
        return url.matches("^(https?://)?(www\\.)?meet\\.google\\.com/[a-zA-Z0-9-]+$");
    }*/

    // Méthode pour charger les idCandidature dans la ComboBox
    /*private void chargerIdCandidature() {
        try {
            List<Integer> idCandidatures = serviceEntretien.getIdsCandidature(); // À implémenter dans ServiceEntretien
            idCandidatureComboBox.setItems(FXCollections.observableArrayList(idCandidatures));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les idCandidature : " + e.getMessage());
        }
    }*/

    //@FXML
    /*public void AddEntretien(ActionEvent actionEvent) throws GeneralSecurityException, IOException {
        // Récupérer les valeurs des champs
        LocalDate date = datePicker.getValue();
        String heureText = heureField.getText();
        type type = (utils.type) typeComboBox.getValue();
        statut statut = (utils.statut) statutComboBox.getValue();
        //String lienMeet = lienMeetField.getText();
        String localisation = localisationField.getText();
        Integer idCandidature = (Integer) idCandidatureComboBox.getValue();

        String meetLink = GoogleCalendarAPI.createEventWithMeet();

        //Entretien entretien = new Entretien();



        // Contrôle de saisie
        if (date == null || heureText == null || heureText.isEmpty() || type == null || statut == null || idCandidature == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Tous les champs obligatoires doivent être remplis.");
            return;
        }*/

    // Vérifier si le type est "En_ligne" et que le lien Meet est vide
        /*if (type == type.En_ligne && (lienMeet == null || lienMeet.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le lien Meet est obligatoire pour un entretien en ligne.");
            return;
        }*/

    // Vérifier si le type est "Presentiel" et que la localisation est vide
        /*if (type == type.Presentiel && (localisation == null || localisation.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "La localisation est obligatoire pour un entretien présentiel.");
            return;
        }*/

    // Convertir l'heure en LocalTime
        /*LocalTime heure;
        try {
            heure = LocalTime.parse(heureText);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le format de l'heure est invalide. Utilisez HH:mm.");
            return;
        }
        if (typeComboBox.getValue() == type.En_ligne) {
            if (lienMeetField.getText().isEmpty() || !isValidURL(lienMeetField.getText())) {
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Lien Meet invalide !");
                return;
            }
        }

        // Créer l'objet Entretien
        Entretien entretien = new Entretien(date, heure, type, statut,  meetLink, localisation, idCandidature);

        //entretien.setLien_meet(meetLink);

        // Ajouter l'entretien dans la base de données
        try {
            serviceEntretien.ajouter(entretien);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            reinitialiserChamps();

            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));



            // Fermer la fenêtre actuelle et ouvrir la fenêtre d'affichage
            Stage currentStage = (Stage) datePicker.getScene().getWindow();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));


                // Charger le fichier FXML de la fenêtre d'affichage
                //FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheentretien.fxml"));
                Parent root = loader.load();
                Controller con=loader.getController();
                con.loadPage("/afficheentretien.fxml");
                statutComboBox.getScene().setRoot(root);
                // Créer une nouvelle scène
                //Scene scene = new Scene(root);

                // Créer une nouvelle fenêtre (Stage)
                //Stage newStage = new Stage(); // Utiliser un nom différent pour éviter le conflit
                //newStage.setScene(scene);
                //newStage.setTitle("Affichage des entretiens");

                // Afficher la fenêtre
                //newStage.show();

                // Fermer la fenêtre actuelle
                //currentStage.close();
                System.out.println("Entretien ajouté avec succès ! Lien Meet : " + meetLink);

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'affichage.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter l'entretien : " + e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/



    @FXML
    public void AddEntretien(ActionEvent actionEvent) throws IOException, URISyntaxException {

        boolean isValid = true;


        // Récupérer les valeurs des champs
        LocalDate date = datePicker.getValue();
        String heureText = heureField.getText();
        type type = (utils.type) typeComboBox.getValue();
        //statut statut = (utils.statut) statutComboBox.getValue();
        String localisation = localisationField.getText();
        //Integer idCandidature = (Integer) idCandidatureComboBox.getValue();

        // Contrôle de saisie
        if (date == null || heureText == null || heureText.isEmpty() || type == null || statut.Planifié == null ) {
            labelheureC.setText("Sélectionnez une heure !");
            labellocalisationC.setText("Sélectionnez une localisation !");
            labeldateC.setText("Sélectionnez une date !");
            labeltypeC.setText("Sélectionnez un type !");
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Tous les champs obligatoires doivent être remplis.");
            return;
        }

        // Vérifier si le type est "Presentiel" et que la localisation est vide

        if (type == type.Presentiel && (localisation == null || localisation.isEmpty())) {
            labellocalisationC.setText("Sélectionnez une localisation !");
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "La localisation est obligatoire pour un entretien présentiel.");
            return;
        }
        if (heureField == null) {
            labelheureC.setText("Sélectionnez une heure !");
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'heure est obligatoire pour un entretien.");
            return;
        }
        if (datePicker == null) {
            labeldateC.setText("Sélectionnez un date !");
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'heure est obligatoire pour un entretien.");
            return;
        }
        if (typeComboBox == null) {
            labeltypeC.setText("Sélectionnez un type !");
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'heure est obligatoire pour un entretien.");
            return;
        }

        // Convertir l'heure en LocalTime
        /*LocalTime heure;
        try {
            heure = LocalTime.parse(heureText);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le format de l'heure est invalide. Utilisez HH:mm.");
            return;
        }*/

        // Contrôle de la date
        if (datePicker.getValue() == null || !datePicker.getValue().isAfter(LocalDate.now())) {
            labeldateC.setText("Sélectionnez une date future !");
            isValid = false;
        } else {
            labeldateC.setText("");
        }

        // Contrôle de l'heure
        LocalTime heure = null;
        try {
            heure = LocalTime.parse(heureField.getText());
            if (heure.isBefore(LocalTime.of(8, 0)) || heure.isAfter(LocalTime.of(20, 0))) {
                labelheureC.setText("L'heure doit être entre 8h et 18h !");
                isValid = false;
            } else {
                labelheureC.setText("");
            }
        } catch (DateTimeParseException e) {
            labelheureC.setText("Format d'heure invalide (HH:mm) !");
            isValid = false;
        }

        // Contrôle du type
        if (typeComboBox.getValue() == null) {
            labeltypeC.setText("Sélectionnez un type !");
            isValid = false;
        } else {
            labeltypeC.setText("");
        }
        // Contrôle de la localisation
        if (typeComboBox.getValue() == type.Presentiel) {
            if (localisationField.getText().isEmpty() || !localisationField.getText().matches("^[a-zA-Z\\s]+$")) {
                labellocalisationC.setText("Localisation invalide (lettres et espaces uniquement) !");
                isValid = false;
            } else {
                labellocalisationC.setText("");
            }
        }


        // Si le formulaire est invalide, ne pas continuer
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez soumettre des champs valides.");
            return;
        }

        // Vérifier que l'heure n'est pas null
        if (heure == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'heure est invalide ou manquante.");
            return;
        }


        // Créer l'objet Entretien

        Entretien entretien = new Entretien(date, heure, type, statut.Planifié, null, localisation ,idCandidature);

        // Ajouter l'entretien dans la base de données
        try {
            serviceEntretien.ajouter(entretien);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            reinitialiserChamps();

            // Naviguer vers la page d'affichage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root = loader.load();
            Controller con = loader.getController();
            Affentretien cont=con.loadPage("/affentretien.fxml").getController();
            cont.setIdCandidature(idCandidature);
            typeComboBox.getScene().setRoot(root);

            System.out.println("Entretien ajouté avec succès !");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'entretien : " + e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }




    // Méthode pour réinitialiser les champs après l'ajout
    private void reinitialiserChamps() {
        datePicker.setValue(null);
        heureField.clear();
        typeComboBox.setValue(null);
        localisationField.clear();
        //idCandidatureComboBox.setValue(null);
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}