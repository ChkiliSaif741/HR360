package controllers;

import entities.Formation;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceFormation;
import services.ServiceParticipation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FormationController implements Initializable {

    private ServiceParticipation serviceParticipation;
    private ServiceFormation serviceFormation;
    @FXML
    private GridPane gridPane; // Référence au GridPane dans le FXML
    @FXML
    private ScrollPane scrollPane; // Référence au ScrollPane dans le FXML
    @FXML
    private Button addButton;

    @FXML
    private Button logOut;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceFormation = new ServiceFormation();
        serviceParticipation = new ServiceParticipation();
        configurerStyleGridPane();
        afficherFormations();
        addButton.getStyleClass().add("add-button"); // Applique le style
    }


    // Configuration du GridPane (espacements, couleurs, styles)
    private void configurerStyleGridPane() {
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5));
        gridPane.getStyleClass().add("grid-pane"); // Appliquer la classe CSS

        // Définition des colonnes (8 colonnes pour inclure les boutons)
        gridPane.getColumnConstraints().clear();
        for (int i = 0; i < 6; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 8);
            gridPane.getColumnConstraints().add(col);
        }

        // Configurer le ScrollPane pour activer le défilement horizontal et vertical
        scrollPane.setContent(gridPane);  // Ajouter le GridPane au ScrollPane
        scrollPane.setFitToWidth(true);    // Ajuste automatiquement la largeur
        scrollPane.setFitToHeight(true);   // Ajuste automatiquement la hauteur
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Barre de défilement horizontale visible si nécessaire
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Barre de défilement verticale visible si nécessaire
    }

    /*public void afficherParticipants(){

    }*/

    // Méthode pour afficher les formations dynamiquement avec des en-têtes fixes
    private void afficherFormations() {
        try {
            List<Formation> formations = serviceFormation.afficher();
            System.out.println(formations);
            if (formations == null || formations.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Aucun formation", "Aucun formation trouvé !");
                return;
            }

            // Nettoyer le GridPane tout en gardant les en-têtes
            gridPane.getChildren().clear();
            gridPane.getRowConstraints().clear();

            // Ajouter les en-têtes
            String[] headers = {"titre", "description", "duree", "date"};
            for (int col = 0; col < headers.length; col++) {
                Label headerLabel = creerLabel(headers[col], true);
                gridPane.add(headerLabel, col, 0); // Ajout des en-têtes à la première ligne
            }

            // Ajout des données à partir de la ligne 1
            int row = 1;
            for (Formation formation : formations) {
                gridPane.add(creerLabel(safeToString(formation.getTitre()), false), 0, row);
                gridPane.add(creerLabel(safeToString(formation.getDescription()), false), 1, row);
                gridPane.add(creerLabel(safeToString(formation.getDuree()), false), 2, row);
                gridPane.add(creerLabel(safeToString(formation.getDateFormation()), false), 3, row);
                // Bouton Delete
                Button deleteButton = new Button("Delete");
                deleteButton.getStyleClass().add("delete-button"); // Appliquer la classe CSS
                deleteButton.setUserData(formation);  // Associer l'objet formation au bouton
                deleteButton.setOnAction(e -> {
                    try {
                        DeleteFormation(e);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                // Bouton Update
                Button updateButton = new Button("Update");
                updateButton.getStyleClass().add("update-button"); // Appliquer la classe CSS
                updateButton.setUserData(formation);  // Associer l'objet formation au bouton
                updateButton.setOnAction(this::UpdateFormation);

                // Bouton Participants
                Button participantsButton = new Button("Participants");
                participantsButton.getStyleClass().add("delete-button");
                participantsButton.setUserData(formation);
                participantsButton.setOnAction(e -> {
                    try {
                        serviceParticipation.afficherParticipants(formation.getId());
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                // Ajouter les boutons dans la dernière colonne
                HBox buttonsContainer = new HBox(5); // Espacement de 5 entre les boutons
                buttonsContainer.getChildren().addAll(deleteButton, updateButton, participantsButton);
                gridPane.add(buttonsContainer, 4, row);

                row++;
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les formations : " + e.getMessage());
        }
    }

    // Crée un Label stylisé pour l'affichage
    private Label creerLabel(String texte, boolean isHeader) {
        Label label = new Label(texte);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(5));

        if (isHeader) {
            label.getStyleClass().add("header-label"); // Appliquer la classe CSS pour les en-têtes
        } else {
            label.getStyleClass().add("normal-label"); // Appliquer la classe CSS pour les labels normaux
        }

        return label;
    }

    // Rafraîchir l'affichage après un ajout ou une mise à jour
    private void refreshFormations() {
        afficherFormations();
    }

    // Convertir null en "N/A" pour éviter les erreurs d'affichage
    private String safeToString(Object obj) {
        return (obj == null) ? "NULL" : obj.toString();
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // Méthode de suppression
    public void DeleteFormation(ActionEvent actionEvent) throws SQLException {
        Button clickedButton = (Button) actionEvent.getSource();
        Formation formation = (Formation) clickedButton.getUserData();  // Récupérer l'Entretien associé au bouton
        System.out.println(formation);
        try {
            serviceFormation.supprimer(formation.getId());
            refreshFormations();  // Rafraîchir l'affichage
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation supprimé avec succès !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de supprimer la formation : " + e.getMessage());
        }
    }

    // Méthode pour ouvrir la fenêtre de modification
    public void UpdateFormation(ActionEvent actionEvent) {
        try {
            // Récupérer l'objet Entretien à partir du bouton
            Button clickedButton = (Button) actionEvent.getSource();
            Formation formation = (Formation) clickedButton.getUserData();


            // Charger la fenêtre de modification (modifierEntretien.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/ModifierFormation.fxml");
            gridPane.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    public void AddF(ActionEvent actionEvent) {
        try {

            // Charger la scène pour l'ajout d'une offre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
            Parent root=loader.load();
            Controller controller = loader.getController();
            controller.loadPage("/AjoutFormation.fxml");
            gridPane.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout.");
        }
    }


    public void logout(ActionEvent event) {
        // Créer une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Déconnexion");
        alert.setHeaderText("Vous allez vous déconnecter !");
        alert.setContentText("Êtes-vous sûr de vouloir quitter votre session ?");

        // Attendre la réponse de l'utilisateur
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) { // Si l'utilisateur confirme
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Login");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


