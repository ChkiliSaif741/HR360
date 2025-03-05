package controllers;

import entities.Entretien;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceEntretien;
import utils.type;
import utils.statut;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ModifierEntretien {

    @FXML
    private ComboBox<type> typeComboBoxMod;
    @FXML
    private TextField heureFieldMod;
    @FXML
    private ComboBox<statut> statutComboBoxMod;
    @FXML
    private DatePicker datePickerMod;
    @FXML
    private TextField localisationFieldMod;

    private Entretien entretien;

    ServiceEntretien serviceEntretien;
    @FXML
    private Label datePickerLabel;
    @FXML
    private Label localisationFieldLabel;
    @FXML
    private Label typeComboBoxLabel;
    @FXML
    private Label statutComboBoxLabel;
    @FXML
    private Label heureFieldLabel;
    @FXML
    private Button btnMod;

    private boolean formSubmittedMod = false;
    @FXML
    private Label localisationFieldLabelC;
    @FXML
    private Label heureFieldLabelC;
    @FXML
    private Label datePickerLabelC;

    private int idCandidature;

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
        setFormFields();
    }
    public void setFormFields(){
        serviceEntretien = new ServiceEntretien();

        typeComboBoxMod.getItems().setAll(type.values());
        statutComboBoxMod.getItems().setAll(statut.values());
        /*try {
            idCandidatureComboBoxMod.getItems().addAll(new ServiceEntretien().getIdsCandidature());
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        // Remplir les ComboBox avec les valeurs des énumérations
        //typeComboBoxMod.setItems(FXCollections.observableArrayList(type.values()));
        //statutComboBoxMod.setItems(FXCollections.observableArrayList(statut.values()));
        typeComboBoxMod.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == type.En_ligne) {
                //lienMeetFieldMod.setVisible(true);
                localisationFieldMod.setVisible(false);
                //lienMeetFieldLabel.setVisible(true);
                localisationFieldLabel.setVisible(false);
            } else {
                //lienMeetFieldMod.setVisible(false);
                localisationFieldMod.setVisible(true);
                localisationFieldLabel.setVisible(true);
                //lienMeetFieldLabel.setVisible(false);

            }
        });

        //lienMeetFieldMod.setVisible(false);
        localisationFieldMod.setVisible(false);
        localisationFieldLabel.setVisible(false);
        //lienMeetFieldLabel.setVisible(false);

        //validateNumericField(idCandidatureComboBox.getEditor());

    }
    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
        datePickerMod.setValue(entretien.getDate());
        heureFieldMod.setText(entretien.getHeure().toString());
        typeComboBoxMod.setValue(entretien.getType());
        statutComboBoxMod.setValue(entretien.getStatut());
        //lienMeetFieldMod.setText(entretien.getLien_meet());
        localisationFieldMod.setText(entretien.getLocalisation());
        //idCandidatureComboBoxMod.setValue(entretien.getIdCandidature());
    }

    @FXML
    public void initialize() {

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



    @FXML
    public void ModifierEntretien(ActionEvent actionEvent) {
        boolean isValid = true;

        try {

            /*if (typeComboBoxMod.getValue() == type.En_ligne) {
                if (lienMeetFieldMod.getText().isEmpty() || !isValidURL(lienMeetFieldMod.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Lien Meet invalide !");
                    return;
                }
            }*/

            /*if (idCandidatureComboBoxMod.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'ID Candidature est obligatoire !");
                return;
            }*/



            // Vérifier si le type est "Presentiel" et que la localisation est vide
            if (typeComboBoxMod.getValue() == type.Presentiel && (localisationFieldMod == null)) {
                localisationFieldLabelC.setText("Sélectionnez une localisation !");
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "La localisation est obligatoire pour un entretien présentiel.");
                return;
            }
            if (heureFieldMod == null) {
                heureFieldLabelC.setText("Sélectionnez une heure !");
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'heure est obligatoire pour un entretien.");
                return;
            }
            // Contrôle de la date
            if (datePickerMod.getValue() == null) {
                datePickerLabelC.setText("Sélectionnez une date !");
                isValid = false;
            } else if (datePickerMod.getValue().isBefore(LocalDate.now())) {
                datePickerLabelC.setText("Sélectionnez une date future !");
                isValid = false;
            } else {
                datePickerLabelC.setText("");
            }

            // Contrôle de l'heure
            LocalTime heure = null;
            try {
                heure = LocalTime.parse(heureFieldMod.getText());
                if (heure.isBefore(LocalTime.of(8, 0)) || heure.isAfter(LocalTime.of(20, 0))) {
                    heureFieldLabelC.setText("L'heure doit être entre 8h et 18h !");
                    isValid = false;
                } else {
                    heureFieldLabelC.setText("");
                }
            } catch (DateTimeParseException e) {
                heureFieldLabelC.setText("Format d'heure invalide (HH:mm) !");
                isValid = false;
            }

            // Contrôle de la localisation
            if (typeComboBoxMod.getValue() == type.Presentiel) {
                String localisationText = localisationFieldMod.getText();
                if (localisationText == null || localisationText.isEmpty() || !localisationText.matches("^[a-zA-Z\\s]+$")) {
                    localisationFieldLabelC.setText("Localisation invalide (lettres et espaces uniquement) !");
                    isValid = false;
                } else {
                    localisationFieldLabelC.setText("");
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




            // Mettre à jour les informations de l'entretien
            entretien.setIdCandidature(idCandidature);
            entretien.setDate(datePickerMod.getValue());
            entretien.setHeure(LocalTime.parse(heureFieldMod.getText()));
            entretien.setType(typeComboBoxMod.getValue());
            entretien.setStatut(statutComboBoxMod.getValue());
            //entretien.setLien_meet(lienMeetFieldMod.getText());
            entretien.setLocalisation(localisationFieldMod.getText());
            //entretien.setIdCandidature(idCandidatureComboBoxMod.getValue());
            // Mettre à jour les informations de l'entretien


            // Enregistrer les modifications dans la base de données
            ServiceEntretien serviceEntretien = new ServiceEntretien();
            serviceEntretien.modifier(entretien);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été modifié avec succès !");

            // Ouvrir la fenêtre afficheEntretien.fxml
            ouvrirAfficheEntretien();

            // Contrôle de saisie
           /* if (datePickerMod == null || heureFieldMod == null || typeComboBoxMod == null || statutComboBoxMod == null || lienMeetFieldMod == null || localisationFieldMod == null || idCandidatureComboBoxMod == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Tous les champs obligatoires doivent être remplis.");
                return;
            }*/





            // Fermer la fenêtre de modification
            //Stage currentStage = (Stage) datePickerMod.getScene().getWindow();
            //currentStage.close();


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirAfficheEntretien() throws IOException {
        // Charger le fichier FXML de la fenêtre afficheEntretien.fxml
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheEntretien.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Affentretien con=controller.loadPage("/affentretien.fxml").getController();
        con.setIdCandidature(idCandidature);
        statutComboBoxMod.getScene().setRoot(root);

        // Créer une nouvelle scène
        /*Scene scene = new Scene(root);

        // Créer une nouvelle fenêtre (Stage)
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Liste des entretiens");

        // Afficher la fenêtre
        stage.show();*/
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