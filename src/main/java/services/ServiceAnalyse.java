package services;

import entities.Reservation;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceAnalyse {

    private Connection connection;

    public ServiceAnalyse() {
        connection = MyDatabase.getInstance().getConnection();
    }

    /**
     * Récupère le nombre total de réservations.
     */
    public int getNombreTotalReservations() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM reservation";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Récupère le taux d'occupation des ressources.
     */
    public Map<String, Double> getTauxOccupation() throws SQLException {
        Map<String, Double> tauxOccupation = new HashMap<>();
        String query = "SELECT r.nom, COUNT(res.id) AS reservations " +
                "FROM ressource r LEFT JOIN reservation res ON r.id = res.id_ressource " +
                "GROUP BY r.nom";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String nomRessource = rs.getString("nom");
                int reservations = rs.getInt("reservations");
                double taux = (reservations / 30.0) * 100; // Exemple : calcul sur 30 jours
                tauxOccupation.put(nomRessource, taux);
            }
        }
        return tauxOccupation;
    }

    /**
     * Récupère les ressources les plus réservées.
     */
    public List<String> getRessourcesPopulaires() throws SQLException {
        List<String> ressourcesPopulaires = new ArrayList<>();
        String query = "SELECT r.nom, COUNT(res.id) AS reservations " +
                "FROM ressource r JOIN reservation res ON r.id = res.id_ressource " +
                "GROUP BY r.nom ORDER BY reservations DESC LIMIT 5";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ressourcesPopulaires.add(rs.getString("nom"));
            }
        }
        return ressourcesPopulaires;
    }
}