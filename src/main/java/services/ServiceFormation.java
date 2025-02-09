package services;

import entities.Formation;
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

        String req = "INSERT INTO formation (titre, description, duree, dateFormation) VALUES " +
                "('" + formation.getTitre() + "', '" + formation.getDescription() + "', "
                + formation.getDuree() + ", '" + formation.getDateFormation() + "')";

        Statement stmt = connection.createStatement();
        stmt.executeUpdate(req);
        System.out.println("Formation ajouté!");
    }

    @Override
    public void modifier(Formation formation) throws SQLException {
        String req = "update formation " +
                "set titre='Sensibilisation',description=?,duree=?,dateFormation=? where id =7";

        PreparedStatement stmt = connection.prepareStatement(req);
        //stmt.setString(1, formation.getTitre());
        stmt.setString(1, formation.getDescription());
        stmt.setInt(2, formation.getDuree());
        stmt.setString(3, formation.getDateFormation());
        //stmt.setInt(5, formation.getId());
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
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from formation");

        while (rs.next()) {
            Formation formation = new Formation();
            formation.setId(rs.getInt("id"));
            formation.setTitre(rs.getString("titre"));
            formation.setDescription(rs.getString("description"));
            formation.setDuree(rs.getInt("duree"));
            formation.setDateFormation(rs.getString("dateFormation"));
            formations.add(formation);
        }
        return formations;
    }
}
