package controllers;

import entities.Entretien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import services.ServiceEntretien;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheEntretien implements Initializable {

    private ServiceEntretien serviceEntretien;
    @FXML
    private GridPane gridPane; // Référence au GridPane dans le FXML
    @FXML
    private ScrollPane scrollPane; // Référence au ScrollPane dans le FXML
    @FXML
    private Button addButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceEntretien = new ServiceEntretien();
        configurerStyleGridPane();
        afficherEntretiens();
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
        for (int i = 0; i < 9; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 9);
            gridPane.getColumnConstraints().add(col);
        }

        // Configurer le ScrollPane pour activer le défilement horizontal et vertical
        scrollPane.setContent(gridPane);  // Ajouter le GridPane au ScrollPane
        scrollPane.setFitToWidth(true);    // Ajuste automatiquement la largeur
        scrollPane.setFitToHeight(true);   // Ajuste automatiquement la hauteur
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Barre de défilement horizontale visible si nécessaire
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Barre de défilement verticale visible si nécessaire
    }

    // Méthode pour afficher les entretiens dynamiquement avec des en-têtes fixes
    private void afficherEntretiens() {
        try {
            List<Entretien> entretiens = serviceEntretien.afficher();

            if (entretiens == null || entretiens.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Aucun entretien", "Aucun entretien trouvé !");
                return;
            }

            // Nettoyer le GridPane tout en gardant les en-têtes
            gridPane.getChildren().clear();
            gridPane.getRowConstraints().clear();

            // Ajouter les en-têtes
            String[] headers = {"Date", "Heure", "Type", "Statut", "Lien Meet", "Localisation"};
            for (int col = 0; col < headers.length ; col++) {
                //
                Label headerLabel = creerLabel(headers[col], true);
                gridPane.add(headerLabel, col, 0); // Ajout des en-têtes à la première ligne
            }

            // Ajout des données à partir de la ligne 1
            int row = 1;
            for (Entretien entretien : entretiens) {
                gridPane.add(creerLabel(safeToString(entretien.getDate()), false), 0, row);
                gridPane.add(creerLabel(safeToString(entretien.getHeure()), false), 1, row);
                gridPane.add(creerLabel(safeToString(entretien.getType()), false), 2, row);
                gridPane.add(creerLabel(safeToString(entretien.getStatut()), false), 3, row);
                gridPane.add(creerLabel(safeToString(entretien.getLien_meet()), false), 4, row);
                gridPane.add(creerLabel(safeToString(entretien.getLocalisation()), false), 5, row);

                // Bouton Delete
                Button deleteButton = new Button("Delete");
                deleteButton.getStyleClass().add("delete-button"); // Appliquer la classe CSS
                deleteButton.setUserData(entretien);  // Associer l'objet entretien au bouton
                deleteButton.setOnAction(e -> {
                    try {
                        DeleteEntretien(e);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                // Bouton Update
                Button updateButton = new Button("Update");
                updateButton.getStyleClass().add("update-button"); // Appliquer la classe CSS
                updateButton.setUserData(entretien);  // Associer l'objet entretien au bouton
                updateButton.setOnAction(this::UpdateEntretien);

                // Bouton Update
                Button evaluationButton = new Button("Evaluation");
                evaluationButton.getStyleClass().add("evaluation-button");
                ActionEvent Event =new ActionEvent();
                evaluationButton.setUserData(entretien);
                evaluationButton.setOnAction(this::EntretienEva);

                // Ajouter les boutons dans la dernière colonne
                gridPane.add(deleteButton, 6, row);
                gridPane.add(updateButton, 7, row);
                gridPane.add(evaluationButton, 8, row);

                row++;
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les entretiens : " + e.getMessage());
        }
    }



    private void EntretienEva(ActionEvent actionEvent) {
        // Récupérer l'objet Entretien à partir du bouton
        Button clickedButton = (Button) actionEvent.getSource();
        Entretien entretien = (Entretien) clickedButton.getUserData();
        System.out.println(entretien);

        // Fermer la fenêtre actuelle
        /*Stage currentStage = (Stage) gridPane.getScene().getWindow();
        currentStage.close();*/

        // Charger la fenêtre de modification (modifierEntretien.fxml)
        System.out.println(entretien.getIdEntretien());
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficheevaluation.fxml"));
        Parent root = null;
        try {

            root = loader.load();
            Controller controller = loader.getController();
            AfficheEvaluation affichageEvaluController = controller.loadPage("/afficheevaluation.fxml").getController();
            affichageEvaluController.setIdEntretien(entretien.getIdEntretien());
            scrollPane.getScene().setRoot(root);

            // Passer l'entretien sélectionné au contrôleur de modification
            /*AfficheEvaluation affichageEva =loader.getController();
            System.out.println(entretien.getIdEntretien());
            affichageEva.setIdEntretien(entretien.getIdEntretien());*/

            // Créer une nouvelle scène
            /*Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier un entretien");

            // Afficher la fenêtre
            stage.show();*/
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    private void refreshEntretiens() {
        afficherEntretiens();
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
    public void DeleteEntretien(ActionEvent actionEvent) throws SQLException {
        Button clickedButton = (Button) actionEvent.getSource();
        Entretien entretien = (Entretien) clickedButton.getUserData();  // Récupérer l'Entretien associé au bouton

        try {
            serviceEntretien.supprimer(entretien.getIdEntretien());
            refreshEntretiens();  // Rafraîchir l'affichage
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Entretien supprimé avec succès !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de supprimer l'entretien : " + e.getMessage());
        }
    }

    // Méthode pour ouvrir la fenêtre de modification
    public void UpdateEntretien(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));

        try {
            // Récupérer l'objet Entretien à partir du bouton
            Button clickedButton = (Button) actionEvent.getSource();
            Entretien entretien = (Entretien) clickedButton.getUserData();
            //System.out.println(entretien.getIdEntretien());
            // Fermer la fenêtre actuelle
            /*Stage currentStage = (Stage) gridPane.getScene().getWindow();
            currentStage.close();*/

            // Charger la fenêtre de modification (modifierEntretien.fxml)
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEntretien.fxml"));
            Parent root = loader.load();

            // Passer l'entretien sélectionné au contrôleur de modification
            Controller controller = loader.getController();
            ModifierEntretien Modifentretien = controller.loadPage("/modifierEntretien.fxml").getController();

            Modifentretien.setEntretien(entretien);
            scrollPane.getScene().setRoot(root);

            // Créer une nouvelle scène
            /*Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier un entretien");

            // Afficher la fenêtre
            stage.show();*/
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    public void AddE(ActionEvent actionEvent) {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));

        try {


            // Fermer la fenêtre actuelle
            //Stage currentStage = (Stage) gridPane.getScene().getWindow();
            //currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));

            // Charger la fenêtre d'ajout (ajoutEntretien.fxml)
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutEntretien.fxml"));
            Parent root = loader.load();

            Controller con=loader.getController();
            con.loadPage("/ajoutEntretien.fxml");
            scrollPane.getScene().setRoot(root);

            // Créer une nouvelle scène
            //Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (Stage)
            //Stage stage = new Stage();
            //stage.setScene(scene);
            //stage.setTitle("Ajouter un entretien");

            // Afficher la fenêtre
            //stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout.");
        }
    }



}