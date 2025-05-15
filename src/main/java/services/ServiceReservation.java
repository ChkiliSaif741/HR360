package services;

import entities.Reservation;
import entities.Ressource;
import interfaces.IService;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceReservation implements IService<Reservation> {

    private Connection connection;

    public ServiceReservation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        if (!estDisponible(reservation)) {
            //System.out.println("Erreur : La ressource est déjà réservée pour cette période !");
            return;
        }
        String query = "INSERT INTO reservation (id_ressource, date_debut, date_fin, iduser) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, reservation.getIdRessource());
        stmt.setDate(2, reservation.getDateDebut());
        stmt.setDate(3, reservation.getDateFin());
        stmt.setInt(4, reservation.getIduser());
        stmt.executeUpdate();
    }

    public void modifier(Reservation reservation) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM reservation WHERE id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, reservation.getId());
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            String query = "UPDATE reservation SET date_debut = ?, date_fin = ?, iduser = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setDate(1, reservation.getDateDebut());
            stmt.setDate(2, reservation.getDateFin());
            stmt.setInt(3, reservation.getIduser());
            stmt.setInt(4, reservation.getId());
            stmt.executeUpdate();
        } else {
            System.out.println("Erreur : La reservation avec l'ID " + reservation.getId() + " n'existe pas !");
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM reservation WHERE id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            String query = "DELETE FROM reservation WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("La ressource avec ID " + id + " supprimer !");
        } else {
            System.out.println("Erreur : La réservation avec ID " + id + " n'existe pas !");
        }
    }

    @Override
    public List<Reservation> afficher() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation ";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Reservation reservation = new Reservation(
                    rs.getInt("resv_id"),
                    rs.getInt("res_id"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    rs.getInt("resv_utilisateur")
            );

            Ressource ressource = new Ressource(
                    rs.getInt("res_id"),
                    rs.getString("nom"),
                    rs.getString("type"),
                    rs.getString("etat"),
                    rs.getDouble("prix")
            );

            reservation.setRessource(ressource);
            reservations.add(reservation);
        }
        return reservations;
    }

    public List<Reservation> afficherEMP() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = " SELECT * from reservation ";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Reservation reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getInt("id_ressource"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    rs.getInt("iduser")
            );

            Ressource ressource = new Ressource(
                    "azer",
                    "azer",
                    "bien",
                    123
            );

            reservation.setRessource(ressource);
            reservations.add(reservation);
        }
        return reservations;
    }

    public boolean estDisponible(Reservation reservation) throws SQLException {
        String query = "SELECT COUNT(*) FROM reservation WHERE id_ressource = ? AND " +
                "(date_debut BETWEEN ? AND ? OR date_fin BETWEEN ? AND ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, reservation.getIdRessource());
        stmt.setDate(2, reservation.getDateDebut());
        stmt.setDate(3, reservation.getDateFin());
        stmt.setDate(4, reservation.getDateDebut());
        stmt.setDate(5, reservation.getDateFin());
        ResultSet rs = stmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            return false;
        }
        return true;
    }

    public List<Date> getDatesReservees(int idRessource) throws SQLException {
        List<Date> datesReservees = new ArrayList<>();
        String query = "SELECT date_debut, date_fin FROM reservation WHERE id_ressource = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idRessource);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date dateDebut = rs.getDate("date_debut");
                    Date dateFin = rs.getDate("date_fin");

                    // Ajouter toutes les dates entre dateDebut et dateFin
                    java.util.Date start = new java.util.Date(dateDebut.getTime());
                    java.util.Date end = new java.util.Date(dateFin.getTime());

                    long startTime = start.getTime();
                    long endTime = end.getTime();

                    for (long time = startTime; time <= endTime; time += 86400000) { // 86400000 ms = 1 jour
                        datesReservees.add(new Date(time));
                    }
                }
            }
        }

        return datesReservees;
    }


    public List<Ressource> recommanderRessources(int idUser) throws SQLException {
        List<Ressource> recommandations = new ArrayList<>();

        // Récupérer l'historique des réservations de l'utilisateur
        String queryHistorique = "SELECT id_ressource FROM reservation WHERE iduser = ?";
        try (PreparedStatement stmt = connection.prepareStatement(queryHistorique)) {
            stmt.setInt(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idRessource = rs.getInt("id_ressource");

                    // Récupérer les ressources similaires
                    String querySimilaires = "SELECT * FROM ressource WHERE type = (SELECT type FROM ressource WHERE id = ?) AND id != ?";
                    try (PreparedStatement stmtSimilaires = connection.prepareStatement(querySimilaires)) {
                        stmtSimilaires.setInt(1, idRessource);
                        stmtSimilaires.setInt(2, idRessource);
                        try (ResultSet rsSimilaires = stmtSimilaires.executeQuery()) {
                            while (rsSimilaires.next()) {
                                Ressource ressource = new Ressource(
                                        rsSimilaires.getInt("id"),
                                        rsSimilaires.getString("nom"),
                                        rsSimilaires.getString("type"),
                                        rsSimilaires.getString("etat"),
                                        rsSimilaires.getDouble("prix")
                                );
                                recommandations.add(ressource);
                            }
                        }
                    }
                }
            }
        }

        return recommandations;
    }


    public List<String> getUnavailableDatesForRessource(int idRessource) {
        List<String> unavailableDates = new ArrayList<>();
        String query = "SELECT date_debut, date_fin FROM reservation WHERE id_ressource = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRessource);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String dateDebut = rs.getDate("date_debut").toString();
                String dateFin = rs.getDate("date_fin").toString();
                unavailableDates.add(dateDebut + " → " + dateFin);
            }

            // Debug : Vérifier si des dates sont bien récupérées
            if (unavailableDates.isEmpty()) {
                System.out.println("Aucune réservation trouvée pour la ressource ID: " + idRessource);
            } else {
                System.out.println("Dates réservées pour la ressource ID " + idRessource + " : " + unavailableDates);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unavailableDates;
    }

}