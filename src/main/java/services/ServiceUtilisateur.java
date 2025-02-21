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

        String req = "insert into utilisateur   (nom, prenom, email,password, role , image)"+
                "values('"+utilisateur.getNom()+"','"+utilisateur.getPrenom()+"','"
                +utilisateur.getEmail()+"','"+utilisateur.getPassword()+"','"+utilisateur.getRole()+"'" +
                " , '"+utilisateur.getImgSrc()+"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Utilisateur ajouté!");

    }

    /*@Override
    public void modifier(Utilisateur utilisateur) throws SQLException {
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, password=?, role=?, imgSrc=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getPassword()); // Ajout du password
        statement.setString(5, utilisateur.getRole());
        statement.setString(6, utilisateur.getImgSrc()); // Ajout de l'image
        statement.setInt(7, utilisateur.getId()); // Correction de l'ID

        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }*/


    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'role'
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, password=?, image=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getPassword()); // Ajouter le mot de passe
        statement.setString(5, utilisateur.getImgSrc()); // Ajouter l'image
        statement.setInt(6, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
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
            utilisateur.setImgSrc(resultSet.getString("image"));
            utilisateurs.add(utilisateur);
        }
        return utilisateurs;
    }

    public Utilisateur getUserByNom(String nom) {
        String req = "SELECT * FROM utilisateur WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    public Utilisateur getUserForPasswordReset(String username, String role, String email) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE nom = ? AND role = ? AND email = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null; // Si aucun utilisateur ne correspond
    }

    public void modifierMotDePasse(Utilisateur utilisateur) throws SQLException {
        String req = "UPDATE utilisateur SET password = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, utilisateur.getPassword()); // Pas de hashage, comme demandé
            ps.setInt(2, utilisateur.getId());
            ps.executeUpdate();
        }
    }



}

