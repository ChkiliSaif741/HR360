//package controllers;
//
//import entities.Utilisateur;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import services.ServiceUtilisateur;
//
//import java.io.IOException;
//import java.net.URL;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.ResourceBundle;
//
//public class DisplayController implements Initializable {
//    @FXML
//    private VBox chosenUserCard;
//
//    @FXML
//    private ImageView userImage;
//
//    @FXML
//    private Label userName;
//
//    @FXML
//    private GridPane grid;
//
//    @FXML
//    private ScrollPane scroll;
//
//    @FXML
//    private Button logOut;
//
//    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
//    List<Utilisateur> users = new ArrayList<>();
//
//    private Utilisateur selectedEmploye = null;
//
//    private List<Utilisateur> getUsers() {
//        try {
//            users = serviceUtilisateur.afficher();
//            return users;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return List.of();
//        }
//    }
//
//    public void setChosenUser(Utilisateur user) {
//        userName.setText(user.getNom());
//        userImage.setImage(new Image("/images/user.png"));
//        chosenUserCard.setStyle(".chosen-user-card{\n" +
//                "    -fx-background-color: #F16C31;\n" +
//                "    -fx-background-radius: 30;\n" +
//                "}");
//    }
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        List<Utilisateur> users = getUsers();
//        int column = 0;
//        int row = 0;
//
//        // S'assurer que la GridPane est bien réinitialisée avant d'ajouter les éléments
//        grid.getChildren().clear();
//        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
//        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
//        grid.setMinHeight(Region.USE_PREF_SIZE);
//        grid.setMinWidth(Region.USE_PREF_SIZE);
//
//        try {
//            for (Utilisateur user : users) {
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Item.fxml"));
//                AnchorPane anchorPane = fxmlLoader.load();
//                EmployeesItemController itemController = fxmlLoader.getController();
//                itemController.setParentControler(this);
//                itemController.setUtilisateur(user);
//
//                // Ajouter l'élément avant de modifier les colonnes et lignes
//                grid.add(anchorPane, column, row);
//                GridPane.setMargin(anchorPane, new Insets(20, 50, 50, 50));
//
//                column++;
//                if (column == 3) {
//                    column = 0;
//                    row++;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void setSelectedEmploye(Utilisateur employe) {
//        this.selectedEmploye = employe; // Stocker l'employé sélectionné
//    }
//
//
//    public void deleteUser(Utilisateur user) {
//        try {
//            serviceUtilisateur.supprimer(user.getId()); // Suppression dans la base de données
//            users.remove(user); // Supprimer de la liste locale
//
//            // Rafraîchir l'affichage
//            grid.getChildren().clear();
//            initialize(null, null);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*public DisplayController getController(){
//        return this;
//    }*/
//
//    public void Logout(ActionEvent event) {
//        // Créer une boîte de dialogue de confirmation
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation de Déconnexion");
//        alert.setHeaderText("Vous allez vous déconnecter !");
//        alert.setContentText("Êtes-vous sûr de vouloir quitter votre session ?");
//
//        // Attendre la réponse de l'utilisateur
//        alert.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.OK) { // Si l'utilisateur confirme
//                try {
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
//                    Parent root = loader.load();
//
//                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//                    stage.setScene(new Scene(root));
//                    stage.setTitle("Login");
//                    stage.show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//}
