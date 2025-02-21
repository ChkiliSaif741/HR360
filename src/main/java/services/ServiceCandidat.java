package services;

import entities.Candidat;
import services.ServiceUtilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidat implements IService<Candidat> {

    Connection connection;

    public ServiceCandidat() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Candidat candidat) throws SQLException {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        // 1. Ajouter d'abord l'utilisateur
        serviceUtilisateur.ajouter(candidat); // Puisque Candidat hérite de Utilisateur

        // 2. Récupérer l'ID du dernier utilisateur inséré
        String reqId = "SELECT LAST_INSERT_ID() AS id";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(reqId);

        int idUtilisateur = -1;
        if (resultSet.next()) {
            idUtilisateur = resultSet.getInt("id");
        }

        // 3. Insérer le CV dans la table candidat
        if (idUtilisateur != -1) {
            String req = "INSERT INTO candidat (idCandidat, cv) VALUES ('" + idUtilisateur + "', '" + candidat.getCv() + "')";
            statement.executeUpdate(req);
            System.out.println("Candidat ajouté avec succès !");
        } else {
            System.out.println("Erreur : impossible de récupérer l'ID de l'utilisateur.");
        }
    }

    @Override
    public void modifier(Candidat candidat) throws SQLException {
        // 1. Modifier les informations dans la table utilisateur
        String reqUtilisateur = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ? WHERE id = ?";
        PreparedStatement psUtilisateur = connection.prepareStatement(reqUtilisateur);
        psUtilisateur.setString(1, candidat.getNom());
        psUtilisateur.setString(2, candidat.getPrenom());
        psUtilisateur.setString(3, candidat.getEmail());
        psUtilisateur.setInt(4, candidat.getId());
        psUtilisateur.executeUpdate();

        // 2. Modifier le CV dans la table candidat
        String reqCandidat = "UPDATE candidat SET cv = ? WHERE idCandidat = ?";
        PreparedStatement psCandidat = connection.prepareStatement(reqCandidat);
        psCandidat.setString(1, candidat.getCv());
        psCandidat.setInt(2, candidat.getId());
        psCandidat.executeUpdate();

        System.out.println("Candidat modifié avec succès !");
    }


    @Override
    public void supprimer(int id) throws SQLException {
        // 1. Supprimer le candidat de la table candidat
        String reqCandidat = "DELETE FROM candidat WHERE idCandidat = ?";
        PreparedStatement psCandidat = connection.prepareStatement(reqCandidat);
        psCandidat.setInt(1, id);
        psCandidat.executeUpdate();

        // 2. Supprimer l'utilisateur de la table utilisateur
        String reqUtilisateur = "DELETE FROM utilisateur WHERE id = ?";
        PreparedStatement psUtilisateur = connection.prepareStatement(reqUtilisateur);
        psUtilisateur.setInt(1, id);
        psUtilisateur.executeUpdate();

        System.out.println("Candidat supprimé avec succès !");
    }


    @Override
    public List<Candidat> afficher() throws SQLException {
        List<Candidat> candidats = new ArrayList<>();

        String req = "SELECT u.id, u.nom, u.prenom, u.email, u.role, c.cv " +
                "FROM utilisateur u " +
                "INNER JOIN candidat c ON u.id = c.idCandidat";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            Candidat candidat = new Candidat();
            candidat.setId(resultSet.getInt("id"));
            candidat.setNom(resultSet.getString("nom"));
            candidat.setPrenom(resultSet.getString("prenom"));
            candidat.setEmail(resultSet.getString("email"));
            candidat.setRole(resultSet.getString("role"));
            candidat.setCv(resultSet.getString("cv"));
            candidats.add(candidat);
        }

        return candidats;
    }

}
