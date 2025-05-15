package services;

import entities.Formation;
import entities.Participation;
import entities.Utilisateur;
import interfaces.IService;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceParticipation implements IService<Participation> {

    Connection connection;

    public ServiceParticipation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Participation participation) throws SQLException {
        if (!verifParticipation(participation.getIdUser(), participation.getIdFormation())) {
            String req = "INSERT INTO participation (idFormation , idUser) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(req);
            stmt.setInt(1, participation.getIdFormation());
            stmt.setInt(2, participation.getIdUser());
            stmt.executeUpdate();
            System.out.println("Participation ajoutée !");
        } else {
            System.out.println("Ajout de participation impossible!");
        }

    }

    @Override
    public void modifier(Participation participation) throws SQLException {

        /*String req = "update participation " +
                "set titre=?,description=? where id =?";

        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setString(1, formation.getTitre());
        stmt.setString(2, formation.getDescription());
        stmt.setInt(3, formation.getDuree());
        stmt.setString(4, formation.getDateFormation());
        stmt.setInt(5, formation.getId());
        stmt.executeUpdate();
        System.out.println("Formation modifiée!");*/

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM participation WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        System.out.println("Participation supprime !");
    }

    @Override
    public List<Participation> afficher() throws SQLException {
        return List.of();
    }

    public List<Utilisateur> afficherParticipants(int id) throws SQLException {

        String req = "select idUser from participation where idFormation = ?";
        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        List<Utilisateur> utilisateurs = new ArrayList<>();
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        while (rs.next()) {
            utilisateurs.add(serviceUtilisateur.getUserById(rs.getInt("idUser")));
        }
        return utilisateurs;
    }

    public boolean verifParticipation(int idUser, int idFormation) throws SQLException {


        String req = "select idUser,idFormation from participation";
        PreparedStatement stmt = connection.prepareStatement(req);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            if (rs.getInt("idUser") == idUser && rs.getInt("idFormation") == idFormation) {
                return true;
            }
        }
        return false;
    }

    public List<Participation> getParticipationsByEmploye(int employeId) {
        List<Participation> participations = new ArrayList<>();
        String query = "SELECT p.id, p.idFormation, p.idUser, f.titre FROM participation p JOIN formation f ON p.idFormation = f.id WHERE p.idUser = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, employeId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id"); // Récupérer l'ID de la participation
                int idFormation = rs.getInt("idFormation");
                String formationTitre = rs.getString("titre");
                participations.add(new Participation(id, idFormation, employeId, formationTitre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participations;
    }


    public void annulerParticipation(int idUser, int idFormation) {
        String query = "DELETE FROM participation WHERE idUser = ? AND idFormation = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idFormation);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Suppression réussie : " + rowsAffected + " ligne(s) supprimée(s)");
            } else {
                System.out.println("Aucune ligne supprimée. Vérifiez les IDs fournis.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Participation> getParticipationsByUser(int idUser) throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String query = "SELECT * FROM participation WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUser);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Participation participation = new Participation(
                        resultSet.getInt("idFormation"),
                        resultSet.getInt("idUser")
                );
                participations.add(participation);
            }
        }
        return participations;
    }

    public List<Formation> getFormationsByEmployee(int idEmployee) throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String query = "SELECT f.id, f.titre, f.description, f.duree, f.dateFormation " +
                "FROM formation f " +
                "JOIN participation p ON f.id = p.idFormation " +
                "WHERE p.idUser = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, idEmployee);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Formation formation = new Formation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getInt("duree"),
                        rs.getString("dateFormation")
                );
                formations.add(formation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Vous pouvez choisir de relancer l'exception ou de la gérer différemment
        }

        return formations;
    }






}
