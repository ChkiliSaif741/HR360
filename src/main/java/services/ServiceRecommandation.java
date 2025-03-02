package services;

import entities.Ressource;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecommandation {

    private Connection connection;

    public ServiceRecommandation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    /**
     * Recommande des ressources similaires à celles que l'utilisateur a déjà réservées.
     *
     * @param idUser L'ID de l'utilisateur pour lequel générer des recommandations.
     * @return Une liste de ressources recommandées.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     */
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
                                        rsSimilaires.getString("etat")
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
}