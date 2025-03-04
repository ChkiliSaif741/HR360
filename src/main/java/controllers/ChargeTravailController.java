package controllers;

import entities.Equipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import services.ServiceChargeTravail;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public class ChargeTravailController {

    @FXML
    private GridPane gridPane;
    @FXML
    private Label monthLabel;
    @FXML
    private Button prevMonthButton;
    @FXML
    private Button nextMonthButton;

    private final ServiceChargeTravail serviceChargeTravail = new ServiceChargeTravail();
    private YearMonth currentMonth;
    private Equipe equipe; // Stocker l'équipe sélectionnée

    public void setEquipe(Equipe equipe) throws SQLException {
        System.out.println(YearMonth.now().toString());
        this.equipe = equipe;
        currentMonth = YearMonth.now();
        gridPane.setAlignment(Pos.CENTER);
        try {
            afficherCharge(equipe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        try {
            afficherCharge(equipe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        try {
            afficherCharge(equipe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherCharge(Equipe equipe) throws SQLException {
        gridPane.getChildren().clear(); // Nettoyer l'affichage précédent
        monthLabel.setText(currentMonth.getMonth().name() + " " + currentMonth.getYear());

        Map<LocalDate, Integer> chargeParJour = serviceChargeTravail.calculerChargeParJour(equipe);
        LocalDate firstOfMonth = currentMonth.atDay(1);
        LocalDate lastOfMonth = currentMonth.atEndOfMonth();

        int row = 1; // Ligne pour les jours
        int column = firstOfMonth.getDayOfWeek().getValue() % 7; // Positionner le premier jour
        LocalDate date = firstOfMonth;

        // Ajouter les en-têtes des jours de la semaine
        for (DayOfWeek day : DayOfWeek.values()) {
            Label label = new Label(day.name().substring(0, 3)); // "LUN", "MAR", ...
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gridPane.add(label, day.getValue() % 7, 0);
        }

        while (!date.isAfter(lastOfMonth)) {
            int charge = chargeParJour.getOrDefault(date, 0);
            Color color = switch (charge) {
                case 0 -> Color.WHITE;   // Aucun projet -> Blanc
                case 1 -> Color.LIGHTGREEN; // 1 projet -> Vert
                case 2 -> Color.ORANGE;   // 2 projets -> Orange
                default -> Color.RED;    // 3+ projets -> Rouge
            };

            // Créer une cellule avec un rectangle coloré et la date
            StackPane cell = new StackPane();
            Rectangle rect = new Rectangle(80, 60, color);
            rect.setStroke(Color.GRAY); // Bordure
            Label label = new Label(date.getDayOfMonth() + "");
            label.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            cell.getChildren().addAll(rect, label);
            gridPane.add(cell, column, row);

            date = date.plusDays(1);
            column++;

            if (column == 7) {
                column = 0;
                row++;
            }
        }
    }
    @FXML
    void BackToTeamDetails(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SideBarRH.fxml"));
        Parent parent=loader.load();
        Controller con=loader.getController();
        DetailsTeamController controller =con.loadPage("/DetailsTeam.fxml").getController();
        controller.setIdEquipe(equipe.getId());
        gridPane.getScene().setRoot(parent);
    }
}
