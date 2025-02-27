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
            stmt.setInt(4,reservation.getId());
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
                    rs.getString("etat")
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
                    137,
                    "azer",
                    "bien",
                    "azer"
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
}



