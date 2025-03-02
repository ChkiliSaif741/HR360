package services;

import entities.Ressource;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRessource implements IService<Ressource> {

    private Connection connection;

    public ServiceRessource() {connection = MyDatabase.getInstance().getConnection();}

    @Override
    public void ajouter(Ressource ressource) throws SQLException {
        String query = "INSERT INTO ressource (nom, type, etat) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, ressource.getNom());
        stmt.setString(2, ressource.getType());
        stmt.setString(3, ressource.getEtat());
        stmt.executeUpdate();
    }


    @Override
    public void modifier(Ressource ressource) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM ressource WHERE id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, ressource.getId());
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            String query = "UPDATE ressource SET nom = ?, type = ?, etat = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, ressource.getNom());
            stmt.setString(2, ressource.getType());
            stmt.setString(3, ressource.getEtat());

            stmt.setInt(4, ressource.getId());
            stmt.executeUpdate();
        } else {
            System.out.println("Erreur : La ressource avec l'ID " + ressource.getId() + " n'existe pas !");
        }
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM ressource WHERE id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            String query = "DELETE FROM ressource WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("La ressource avec ID " + id + " supprimer !");
        } else {
            System.out.println("Erreur : La ressource avec ID " + id + " n'existe pas !");
        }
    }


    @Override
    public List<Ressource> afficher() throws SQLException {
        List<Ressource> ressources = new ArrayList<>();
        String query = "SELECT * FROM ressource";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Ressource ressource = new Ressource(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("type"),
                    rs.getString("etat")
            );
            ressources.add(ressource);
        }
        return ressources;
    }


    public Ressource getRessourceById(int id) throws SQLException {
        String query = "SELECT * FROM ressource WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new Ressource(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("type"),
                    rs.getString("etat")
            );
        }
        return null; // Si aucune ressource trouvée
    }


    public double getPrixRessource(int idRessource) throws SQLException {
        String query = "SELECT prix FROM ressource WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, idRessource);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("prix");
        }
        return 0.0;
    }



}
