package services;

import entities.Participation;
import entities.Utilisateur;
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
        String query = "SELECT p.idFormation, p.idUser, f.titre FROM participation p JOIN formation f ON p.idFormation = f.id WHERE p.idUser = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, employeId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int idFormation = rs.getInt("idFormation");
                String formationTitre = rs.getString("titre");
                participations.add(new Participation(idFormation, employeId, formationTitre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participations;
    }


    public void annulerParticipation(int idUser, int idFormation) {
        String query = "DELETE FROM participation WHERE idUser = ? AND idFormation = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, idUser);
            pst.setInt(2, idFormation);
            pst.executeUpdate();
            System.out.println("Participation annulée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
