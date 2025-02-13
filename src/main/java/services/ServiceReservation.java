package services;

import entities.Reservation;
import entities.Ressource;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IService<Reservation> {

    private Connection connection;
    public ServiceReservation() {connection = MyDatabase.getInstance().getConnection();}


    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        if (!estDisponible(reservation)) {
            System.out.println("Erreur : La ressource est déjà réservée pour cette période !");
            return;
        }
        String query = "INSERT INTO reservation (id_ressource, date_debut, date_fin, utilisateur) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, reservation.getIdRessource());
        stmt.setDate(2, reservation.getDateDebut());
        stmt.setDate(3, reservation.getDateFin());
        stmt.setString(4, reservation.getUtilisateur());
        stmt.executeUpdate();
    }


    @Override
    public void modifier(Reservation reservation) throws SQLException {
        String selectQuery = "SELECT id_ressource FROM reservation WHERE id = ?";
        PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
        selectStmt.setInt(1, reservation.getId());
        ResultSet rsExisting = selectStmt.executeQuery();
        if (!rsExisting.next()) {
            System.out.println("Erreur : La réservation avec ID " + reservation.getId() + " n'existe pas !");
            return;
        }
        int currentRessourceId = rsExisting.getInt("id_ressource");

        if (reservation.getIdRessource() <= 0) {
            reservation.setIdRessource(currentRessourceId);
        }

        String checkRessourceQuery = "SELECT COUNT(*) FROM ressource WHERE id = ?";
        PreparedStatement checkRessourceStmt = connection.prepareStatement(checkRessourceQuery);
        checkRessourceStmt.setInt(1, reservation.getIdRessource());
        ResultSet rsRessource = checkRessourceStmt.executeQuery();
        rsRessource.next();
        if (rsRessource.getInt(1) == 0) {
            System.out.println("Erreur : La ressource avec ID " + reservation.getIdRessource() + " n'existe pas !");
            return;
        }

        String checkAvailabilityQuery = "SELECT COUNT(*) FROM reservation WHERE id_ressource = ? AND id != ? " +
                "AND ((date_debut BETWEEN ? AND ?) OR (date_fin BETWEEN ? AND ?) " +
                "OR (? BETWEEN date_debut AND date_fin) OR (? BETWEEN date_debut AND date_fin))";
        PreparedStatement checkAvailabilityStmt = connection.prepareStatement(checkAvailabilityQuery);
        checkAvailabilityStmt.setInt(1, reservation.getIdRessource());
        checkAvailabilityStmt.setInt(2, reservation.getId());
        checkAvailabilityStmt.setDate(3, reservation.getDateDebut());
        checkAvailabilityStmt.setDate(4, reservation.getDateFin());
        checkAvailabilityStmt.setDate(5, reservation.getDateDebut());
        checkAvailabilityStmt.setDate(6, reservation.getDateFin());
        checkAvailabilityStmt.setDate(7, reservation.getDateDebut());
        checkAvailabilityStmt.setDate(8, reservation.getDateFin());
        ResultSet rsAvailability = checkAvailabilityStmt.executeQuery();
        rsAvailability.next();
        if (rsAvailability.getInt(1) > 0) {
            System.out.println("La ressource est déjà réservée pour cette période !");
            return;
        }

        String updateQuery = "UPDATE reservation SET id_ressource = ?, date_debut = ?, date_fin = ?, utilisateur = ? WHERE id = ?";
        PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
        updateStmt.setInt(1, reservation.getIdRessource());
        updateStmt.setDate(2, reservation.getDateDebut());
        updateStmt.setDate(3, reservation.getDateFin());
        updateStmt.setString(4, reservation.getUtilisateur());
        updateStmt.setInt(5, reservation.getId());

        int rowsUpdated = updateStmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Réservation mise à jour avec succès !");
        } else {
            System.out.println("Erreur lors de la mise à jour !");
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
        String query = "SELECT re.id AS resv_id, re.date_debut, re.date_fin, re.utilisateur AS resv_utilisateur, " +
                "r.id AS res_id, r.nom, r.type, r.etat, r.utilisateur AS res_utilisateur " +
                "FROM reservation re " +
                "JOIN ressource r ON re.id_ressource = r.id";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Reservation reservation = new Reservation(
                    rs.getInt("resv_id"),
                    rs.getInt("res_id"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    rs.getString("resv_utilisateur")
            );

            Ressource ressource = new Ressource(
                    rs.getInt("res_id"),
                    rs.getString("nom"),
                    rs.getString("type"),
                    rs.getString("etat"),
                    rs.getString("res_utilisateur")
            );

            reservation.setRessource(ressource);
            reservations.add(reservation);
        }
        return reservations;
    }


    private boolean estDisponible(Reservation reservation) throws SQLException {
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
}



