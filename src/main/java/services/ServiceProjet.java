package services;

import entities.Projet;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProjet implements IService<Projet> {
    Connection connection;
    public ServiceProjet() {
        connection= MyDatabase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Projet projet) throws SQLException {
        String req="INSERT INTO projet (nom,description,dateDebut,dateFin) VALUES (?,?,?,?)";
        PreparedStatement preparedStatement= connection.prepareStatement(req);
        preparedStatement.setString(1, projet.getNom());
        preparedStatement.setString(2, projet.getDescription());
        preparedStatement.setDate(3,projet.getDateDebut());
        preparedStatement.setDate(4,projet.getDateFin());
        preparedStatement.executeUpdate();
    }

    @Override
    public void modifier(Projet projet) throws SQLException {
        String req="UPDATE projet SET nom=?,description=?,dateDebut=?,dateFin=? WHERE id=?";
        PreparedStatement preparedStatement= connection.prepareStatement(req);
        preparedStatement.setString(1, projet.getNom());
        preparedStatement.setString(2, projet.getDescription());
        preparedStatement.setDate(3,projet.getDateDebut());
        preparedStatement.setDate(4,projet.getDateFin());
        preparedStatement.setInt(5, projet.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req="DELETE FROM projet WHERE id=?";
        PreparedStatement preparedStatement= connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Projet> afficher() throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String req="SELECT * FROM projet";
        Statement statement= connection.createStatement();
        ResultSet rs= statement.executeQuery(req);
        while (rs.next()) {
            Projet projet= new Projet();
            projet.setId(rs.getInt("id"));
            projet.setNom(rs.getString("nom"));
            projet.setDescription(rs.getString("description"));
            projet.setDateDebut(rs.getDate("dateDebut"));
            projet.setDateFin(rs.getDate("dateFin"));
            projets.add(projet);
        }
        return projets;
    }
}
