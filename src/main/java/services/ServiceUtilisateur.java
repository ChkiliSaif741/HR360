package services;

import entities.Session;
import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur>{

    Connection connection;

    public ServiceUtilisateur() {
        connection = MyDatabase.getInstance().getConnection();
        if (connection == null) {
            System.err.println("Erreur : la connexion est null !");
        }
    }

    public Utilisateur authentifier(String email, String password) {
        String query = "SELECT * FROM utilisateur WHERE email = ? AND password = ?"; // 🔥 Vérifie si le hashage est nécessaire

        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");  // ✅ Récupération correcte du nom
                String role = rs.getString("role"); // ✅ Récupération correcte du rôle

                return new Utilisateur(id, email, nom, role); // ✅ Assure-toi que ce constructeur existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retourne `null` si l'authentification échoue
    }


    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {
        if(utilisateur.getRole() == "Employe"){
            String req = "insert into utilisateur   (nom, prenom, email,password, role , image,salaire,poste)"+
                    "values('"+utilisateur.getNom()+"','"+utilisateur.getPrenom()+"','"
                    +utilisateur.getEmail()+"','"+utilisateur.getPassword()+"','"+utilisateur.getRole()+"'" +
                    " , '"+utilisateur.getImgSrc()+"','"+utilisateur.getSalaire()+"','"+utilisateur.getPoste()+"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(req);
            System.out.println("Utilisateur ajouté!");
        }else{
            String req = "insert into utilisateur   (nom, prenom, email,password, role , image , competence)"+
                    "values('"+utilisateur.getNom()+"','"+utilisateur.getPrenom()+"','"
                    +utilisateur.getEmail()+"','"+utilisateur.getPassword()+"','"+utilisateur.getRole()+"'" +
                    " , '"+utilisateur.getImgSrc()+"','"+utilisateur.getCompetence()+"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(req);
            System.out.println("Utilisateur ajouté!");
        }

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



    public void modifier1(Utilisateur utilisateur) throws SQLException {
        // Requête SQL sans la colonne 'password'
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, poste=?, salaire=?, image=? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(req);

        // Définir les valeurs des colonnes
        statement.setString(1, utilisateur.getNom());
        statement.setString(2, utilisateur.getPrenom());
        statement.setString(3, utilisateur.getEmail());
        statement.setString(4, utilisateur.getPoste()); // Ajouter le poste
        statement.setFloat(5, utilisateur.getSalaire()); // Ajouter le salaire
        statement.setString(6, utilisateur.getImgSrc()); // Ajouter l'image
        statement.setInt(7, utilisateur.getId()); // L'ID pour localiser l'utilisateur à modifier

        // Exécution de la mise à jour
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Aucune modification effectuée !");
        }
    }

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

    public Utilisateur getUserByEmail(String email) {
        String req = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setString(1, email);
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

    public Utilisateur getUserById(int id) {
        String req = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
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

    public Utilisateur getUserForPasswordReset(String username, String email) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE nom = ? AND email = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, email);

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

