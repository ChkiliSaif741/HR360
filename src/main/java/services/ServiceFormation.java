package services;

import entities.Formation;
import entities.Utilisateur;
import interfaces.IService;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFormation implements IService<Formation> {
    Connection connection;

    public ServiceFormation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Formation formation) throws SQLException {
        String req = "INSERT INTO formation (titre, description, duree, dateFormation) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setString(1, formation.getTitre());
        stmt.setString(2, formation.getDescription());
        stmt.setInt(3, formation.getDuree());
        stmt.setString(4, formation.getDateFormation());
        stmt.executeUpdate();
        System.out.println("Formation ajoutée !");
    }

    @Override
    public void modifier(Formation formation) throws SQLException {
        String req = "update formation " +
                "set titre=?,description=?,duree=?,dateFormation=? where id =?";

        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setString(1, formation.getTitre());
        stmt.setString(2, formation.getDescription());
        stmt.setInt(3, formation.getDuree());
        stmt.setString(4, formation.getDateFormation());
        stmt.setInt(5, formation.getId());
        stmt.executeUpdate();
        System.out.println("Formation modifiée!");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "delete from formation where id =?";
        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        System.out.println("Formation supprimée!");
    }

    @Override
    public List<Formation> afficher() throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String req = "SELECT * FROM formation f "; // Utilisez LEFT JOIN pour inclure les formations sans employés
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            Formation formation = new Formation();
            formation.setId(rs.getInt("id"));
            formation.setTitre(rs.getString("titre"));
            formation.setDescription(rs.getString("description"));
            formation.setDuree(rs.getInt("duree"));
            formation.setDateFormation(rs.getString("dateFormation"));
            formation.setFavorite(rs.getBoolean("isFavorite"));

            formations.add(formation);
        }
        return formations;
    }

    public Formation getFormationById(int idFormation) throws SQLException {
        String query = "SELECT * FROM formation WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idFormation);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Formation(
                        resultSet.getInt("id"),
                        resultSet.getString("titre"),
                        resultSet.getString("description"),
                        resultSet.getInt("duree"),
                        resultSet.getString("dateFormation"),
                        resultSet.getBoolean("isFavorite")
                );
            }
        }
        return null;
    }


    public void addFavoris(Formation formation) throws SQLException {
        String req = "update formation " +
                "set isFavorite=true where id =?";

        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, formation.getId());
        stmt.executeUpdate();
        System.out.println("Formation ajoutée aux favoris!");
    }



    public void removeFavoris(Formation formation) throws SQLException {
        String req = "update formation " +
                "set isFavorite=false where id =?";

        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, formation.getId());
        stmt.executeUpdate();
        System.out.println("Formation supprimée des favoris!");
    }

}
