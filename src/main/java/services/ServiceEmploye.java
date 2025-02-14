package services;

import entities.Employe;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmploye implements IService<Employe> {

    Connection connection;

    public ServiceEmploye() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Employe employe) throws SQLException {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        // 1. Ajouter d'abord l'utilisateur (héritage remplacé par une jointure)
        serviceUtilisateur.ajouter(employe);

        // 2. Récupérer l'ID du dernier utilisateur inséré
        String reqId = "SELECT LAST_INSERT_ID() AS id";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(reqId);

        int idUtilisateur = -1;
        if (resultSet.next()) {
            idUtilisateur = resultSet.getInt("id");
        }

        // 3. Insérer les informations spécifiques de l'employé dans la table employe
        if (idUtilisateur != -1) {
            String req = "INSERT INTO employe (idEmploye, poste, salaire) VALUES ('"
                    + idUtilisateur + "', '"
                    + employe.getPoste() + "', "
                    + employe.getSalaire() + ")";

            statement.executeUpdate(req);
            System.out.println("Employé ajouté avec succès !");
        } else {
            System.out.println("Erreur : impossible de récupérer l'ID de l'utilisateur.");
        }
    }


    @Override
    public void modifier(Employe employe) throws SQLException {
        String req = "UPDATE employe SET poste=?, salaire=? WHERE idEmploye=?";

        PreparedStatement statement = connection.prepareStatement(req);
        statement.setString(1, employe.getPoste());
        statement.setDouble(2, employe.getSalaire());
        statement.setInt(3, employe.getId());

        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Employé mis à jour avec succès !");
        } else {
            System.out.println("Erreur : aucun employé trouvé avec cet ID.");
        }
    }

    public void inscrireEmployeFormation(int idEmploye, int idFormation) throws SQLException {
        String req = "UPDATE employe SET idFormation = ? WHERE idEmploye = ?";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.setInt(1, idFormation);
        statement.setInt(2, idEmploye);
        statement.executeUpdate();
        System.out.println("Employé inscrit à la formation.");
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM employe WHERE idEmploye = ?";

        PreparedStatement statement = connection.prepareStatement(req);
        statement.setInt(1, id);

        int rowsDeleted = statement.executeUpdate();

        if (rowsDeleted > 0) {
            System.out.println("Employé supprimé avec succès !");
        } else {
            System.out.println("Erreur : aucun employé trouvé avec cet ID.");
        }
    }


    @Override
    public List<Employe> afficher() throws SQLException {
        List<Employe> employes = new ArrayList<>();
        String req = "SELECT u.id, u.nom, u.prenom, u.email, u.role, e.poste, e.salaire , e.idFormation " +
                "FROM employe e " +
                "JOIN utilisateur u ON e.idEmploye = u.id";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            Employe employe = new Employe();
            employe.setId(resultSet.getInt("id"));
            employe.setNom(resultSet.getString("nom"));
            employe.setPrenom(resultSet.getString("prenom"));
            employe.setEmail(resultSet.getString("email"));
            employe.setRole(resultSet.getString("role"));
            employe.setPoste(resultSet.getString("poste"));
            employe.setSalaire(resultSet.getFloat("salaire"));
            employe.setIdFormation(resultSet.getInt("idFormation"));
            employes.add(employe);
        }

        return employes;
    }

}
