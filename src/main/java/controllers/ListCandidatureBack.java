package controllers;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import entities.Offre;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import entities.Candidature;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;


import javafx.stage.FileChooser;

import java.io.*;

import javafx.stage.Stage;
import services.ServiceCandidature;
import services.ServiceOffre;
import services.ServiceUtilisateur;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ListCandidatureBack implements Initializable {

    @FXML
    private ImageView imageViewUpdt;
    @FXML
    private ListView<Candidature> listViewCandidatures;

    private ServiceCandidature serviceCandidature;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1); // Désature l'image (noir et blanc)
        colorAdjust.setBrightness(1); // Rend l'image blanche

        imageViewUpdt.setEffect(colorAdjust);
        serviceCandidature = new ServiceCandidature();

        // Charger les candidatures depuis la base de données
        try {

            List<Candidature> candidatures = serviceCandidature.afficher();
            listViewCandidatures.getItems().setAll(candidatures);

            // Utilisation d'une CellFactory personnalisée pour afficher les détails comme un tableau
            listViewCandidatures.setCellFactory(param -> new ListCell<Candidature>() {
                @Override
                protected void updateItem(Candidature candidature, boolean empty) {
                    super.updateItem(candidature, empty);
                    if (empty || candidature == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Créer un HBox pour afficher les titres et les attributs horizontalement
                        HBox hbox = new HBox(20); // Espacement entre les éléments

                        // Récupérer l'offre en fonction de l'id_offre de la candidature
                        Offre offre = getOffreById(candidature.getId_offre());
                        String titreOffre = (offre != null) ? offre.getTitre() : "Offre non trouvée";
                        ServiceUtilisateur serviceUtilisateur=new ServiceUtilisateur();
                        Utilisateur can=serviceUtilisateur.getUserById(candidature.getId_user());
                        String nomC=can.getNom()+" "+can.getPrenom();
                        // Si c'est la première ligne, afficher uniquement les titres
                        if (getIndex() == 0) {
                            hbox.getChildren().addAll(
                                    createLabeledItem("Titre Offre", ""),
                                    createLabeledItem("Candidat", ""),
                                    createLabeledItem("Date Candidature", ""),
                                    createLabeledItem("Statut", ""),
                                    createLabeledItem("CV", ""),
                                    createLabeledItem("Lettre Motivation", ""),
                                    createLabeledItem("Description", ""),
                                    createLabeledItem("Date Modification", "")
                            );
                        } else {
                            // Ajouter chaque attribut avec son titre et sa valeur dans un cadre
                            hbox.getChildren().addAll(
                                    createLabeledItem("", titreOffre),
                                    createLabeledItem("", nomC),
                                    createLabeledItem("", candidature.getDateCandidature().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                                    createLabeledItem("", candidature.getStatut()),
                                    createLabeledItem("", extractFileName(candidature.getCv())), // Afficher uniquement le nom du fichier
                                    createLabeledItem("", extractFileName(candidature.getLettreMotivation())), // Afficher uniquement le nom du fichier
                                    createLabeledItem("", candidature.getDescription()),
                                    createLabeledItem("", candidature.getDateModification() != null ? candidature.getDateModification().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Non modifié")
                            );
                        }

                        setGraphic(hbox); // Appliquer le HBox à la cellule
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des candidatures : " + e.getMessage());
        }
    }

    private VBox createLabeledItem(String title, String value) {
        // Création des Labels pour le titre et la valeur
        Label titleLabel = new Label(title);
        Label valueLabel = new Label(value);

        // Appliquer la classe CSS pour rendre le titre en gras
        titleLabel.getStyleClass().add("title-label");

        // Appliquer la classe CSS pour le retour à la ligne dans les Labels
        valueLabel.getStyleClass().add("value-label");

        // Création d'un cadre (Rectangle) autour de chaque paire de titre/valeur
        Rectangle rectangle = new Rectangle(150, 40); // Largeur et hauteur du cadre
        rectangle.getStyleClass().add("rectangle-frame"); // Appliquer le style CSS du rectangle

        // Création du VBox pour aligner les labels et le cadre
        VBox vbox = new VBox(5, titleLabel, valueLabel);
        vbox.getStyleClass().add("vbox-item"); // Appliquer le style CSS du VBox

        return vbox;
    }

    // Méthode pour extraire le nom du fichier à partir du chemin complet
    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "Aucun fichier";
        }
        // Extraire le nom du fichier à partir du chemin
        int lastSeparatorIndex = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (lastSeparatorIndex == -1) {
            return filePath; // Si aucun séparateur n'est trouvé, retourner le chemin complet
        }
        return filePath.substring(lastSeparatorIndex + 1);
    }
    private Offre getOffreById(int id_offre) {
        ServiceOffre serviceOffre = new ServiceOffre();
        try {
            List<Offre> offres = serviceOffre.afficher();
            for (Offre offre : offres) {
                if (offre.getId() == id_offre) {
                    return offre;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the offer is not found
    }
    // Méthode pour créer des éléments Label avec titre et valeur dans un cadre

    private boolean peutEtreModifiee(Candidature candidature) {
        return "En attente".equalsIgnoreCase(candidature.getStatut());
    }
    @FXML
    private void modifierCandidature() {
        Candidature selectedCandidature = listViewCandidatures.getSelectionModel().getSelectedItem();
        if (selectedCandidature != null) {
            // Vérifier si la candidature peut être modifiée
            if (peutEtreModifiee(selectedCandidature)) {
                try {
                    // Chargement du fichier FXML
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifCandidatureBack.fxml"));
                    Parent root = loader.load();

                    // Récupération du contrôleur et transmission des données
                    ModifCandidatureBack controller = loader.getController();
                    controller.setCandidatureSelectionnee(selectedCandidature);

                    // Création et affichage de la nouvelle fenêtre
                    Stage stage = new Stage();
                    stage.setTitle("Modifier Candidature");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.");
                }
            } else {
                showAlert("Modification interdite", "Cette candidature ne peut plus être modifiée car son statut a déjà été changé.");
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une candidature à modifier.");
        }
    }
    @FXML
    private void afficherStatistiques() {
        try {
            // Charger le fichier FXML de la fenêtre des statistiques
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatistiquesCandidatures.fxml"));
            Parent root = loader.load();

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Statistiques des Candidatures");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les statistiques.");
        }
    }


    @FXML
    private void enregistrerListeCandidaturesEnPdf() {
        try {
            // Choisir l'emplacement pour enregistrer le fichier PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                String dest = file.getAbsolutePath();
                PdfWriter writer = new PdfWriter(dest);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4); // Utiliser une page A4 en orientation portrait

                // Ajouter une marge à gauche du document pour rapprocher le contenu du bord gauche
                document.setMargins(30, 30, 30, 30); // top, right, bottom, left margin

                // Chemin du logo
                String logoPath = "C:\\Users\\Dell\\Desktop\\piDevJava\\HR360\\src\\main\\resources\\Images\\logoRH360.png";

                // Ajouter le logo et le titre de l'entreprise
                com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(ImageDataFactory.create(logoPath));
                logo.scaleToFit(100, 100); // Redimensionner le logo
                document.add(logo);

                document.add(new Paragraph("RH Entreprise 360").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("Liste des Candidatures").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));

                // Ajouter chaque candidature
                List<Candidature> candidatures = listViewCandidatures.getItems();

                for (Candidature candidature : candidatures) {
                    // Récupérer l'offre associée
                    Offre offre = getOffreById(candidature.getId_offre());
                    String titreOffre = (offre != null) ? offre.getTitre() : "Offre non trouvée";

                    // Titre de la candidature
                    document.add(new Paragraph("Candidature ID : " + candidature.getId_candidature())
                            .setBold().setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("Titre de l'offre : " + titreOffre).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("Date de la candidature : " + candidature.getDateCandidature().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                            .setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("Statut : " + candidature.getStatut()).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("CV : " + extractFileName(candidature.getCv())).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("Lettre de Motivation : " + extractFileName(candidature.getLettreMotivation()))
                            .setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("Description : " + candidature.getDescription()).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
                    document.add(new Paragraph("Date Modification : "
                            + (candidature.getDateModification() != null
                            ? candidature.getDateModification().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "Non modifié")).setFontSize(12).setTextAlignment(TextAlignment.LEFT));

                    document.add(new Paragraph("\n")); // Ajouter un espace entre les candidatures

                    // Ajouter une ligne de séparation après chaque candidature pour la clarté
                    SolidLine line = new SolidLine(); // Crée une ligne solide
                    LineSeparator separator = new LineSeparator(line); // Crée un séparateur avec la ligne
                    document.add(separator); // Ajoute le séparateur au document

                    // Ajouter un espace avant la prochaine candidature
                    document.add(new Paragraph("\n"));
                }

                // Fermer le document
                document.close();

                // Afficher un message de succès
                showAlert("Succès", "La liste des candidatures a été enregistrée dans le fichier PDF : " + dest);
            } else {
                showAlert("Annulé", "L'enregistrement du PDF a été annulé.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }
    @FXML
    private void analyserCandidature() {
        Candidature selectedCandidature = listViewCandidatures.getSelectionModel().getSelectedItem();
        if (selectedCandidature != null) {
            // Lancer l'analyse du CV et de l'offre d'emploi
            try {
                // Ouvrir la fenêtre UploadJobOffer et passer le fichier CV
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UploadJobOffer.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur de la fenêtre UploadJobOffer
                UploadJobOffer controller = loader.getController();
                controller.setCvFile(new File(selectedCandidature.getCv())); // Passer le fichier CV à la fenêtre

                // Afficher la fenêtre
                Stage stage = new Stage();
                stage.setTitle("Télécharger l'offre d'emploi");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de téléchargement de l'offre d'emploi.");
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une candidature à analyser.");
        }
    }
    public void AfficherEntretien() throws SQLException, IOException {
        int selectedIndex = listViewCandidatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            List<Candidature> candidatures = serviceCandidature.afficher();
            int id = candidatures.get(selectedIndex).getId_candidature();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarCAN.fxml"));
            Controller controller = loader.getController();
            Affentretien controller1 = controller.loadPage("/affentretien.fxml").getController();
            controller1.setIdCandidature(id);
        }else {
            showAlert("Aucune sélection", "Veuillez sélectionner une candidature Pour voir entretien.");
        }

    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}