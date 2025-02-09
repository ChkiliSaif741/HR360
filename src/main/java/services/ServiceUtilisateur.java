package services;

import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur>{

    Connection connection;

    public ServiceUtilisateur() {
        connection = MyDatabase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {

        String req = "insert into utilisateur   (nom, prenom, email, role)"+
                "values('"+utilisateur.getNom()+"','"+utilisateur.getPrenom()+"','"+utilisateur.getEmail()+"','"+utilisateur.getRole()+"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Utilisateur ajouté");

    }

    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {

        String req = "update utilisateur " +
                "set nom=?, prenom='Taha', email=?, role=? where id=14";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.setString(1, utilisateur.getNom());
        //statement.setString(2, utilisateur.getPrenom());
        statement.setString(2, utilisateur.getEmail());
        statement.setString(3, utilisateur.getRole());
        //statement.setInt(4, utilisateur.getId());
        statement.executeUpdate();
        System.out.println("Utilisateur modifié");
    }

    @Override
    public void supprimer(int id) throws SQLException {

        String req = "delete from utilisateur where id=?";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.setInt(1, id);
        statement.executeUpdate();
        System.out.println("Utilisateur supprimé");
    }

    @Override
    public List<Utilisateur> afficher() throws SQLException {

        List<Utilisateur> utilisateurs = new ArrayList<>();
        String req = "select * from utilisateur";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(resultSet.getInt("id"));
            utilisateur.setNom(resultSet.getString("nom"));
            utilisateur.setPrenom(resultSet.getString("prenom"));
            utilisateur.setEmail(resultSet.getString("email"));
            utilisateur.setRole(resultSet.getString("role"));
            utilisateurs.add(utilisateur);
        }
        return utilisateurs;
    }
}

