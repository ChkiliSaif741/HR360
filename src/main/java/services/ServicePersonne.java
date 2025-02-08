package services;

import entities.Personne;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePersonne implements IService<Personne>{
    Connection connection;
    public ServicePersonne(){
        connection= MyDatabase.getInstance().getConnection();

    }
    @Override
    public void ajouter(Personne personne) throws SQLException {

        String req="insert into personne (nom,prenom,age)"+
                "values('"+personne.getNom()+"','"+personne.getPrenom()+"',"+personne.getAge()+")";

        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("personne ajoute");


    }

    @Override
    public void modifier(Personne personne) throws SQLException {
        String req="update personne set nom=?,prenom=?,age=?  where id=?";
        PreparedStatement preparedStatement= connection.prepareStatement(req);
        preparedStatement.setString(1,personne.getNom());
        preparedStatement.setString(2,personne.getPrenom());
        preparedStatement.setInt(3,personne.getAge());
        preparedStatement.setInt(4,personne.getId());
        preparedStatement.executeUpdate();


    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM personne WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            //preparedStatement.setString(2, nom);
            int verif = preparedStatement.executeUpdate();

            if ( verif > 0) {
                System.out.println(" Personne supprimée avec succès.");
            } else {
                System.out.println(" ID non trouvé.");
            }
        }
    }


    @Override
    public List<Personne> afficher() throws SQLException {
        List<Personne> personnes= new ArrayList<>();
        String req="select * from personne";
        Statement statement= connection.createStatement();

       ResultSet rs= statement.executeQuery(req);
       while (rs.next()){
           Personne personne= new Personne();
           personne.setNom(rs.getString("nom"));
           personne.setPrenom(rs.getString("prenom"));
           personne.setId(rs.getInt("id"));
           personne.setAge(rs.getInt("age"));

           personnes.add(personne);
       }


        return personnes;
    }
}
