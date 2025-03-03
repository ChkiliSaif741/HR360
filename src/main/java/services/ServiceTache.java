package services;

import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import tests.TempUser;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceTache implements IService<Tache> {
    Connection connection;
    public ServiceTache() {
        connection= MyDatabase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Tache tache) throws SQLException {
        String req="INSERT INTO tache (nom,description,dateDebut,dateFin,statut,idProjet) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, tache.getNom());
        ps.setString(2, tache.getDescription());
        ps.setDate(3,tache.getDateDebut());
        ps.setDate(4,tache.getDateFin());
        ps.setString(5,tache.getStatut().getValue());
        ps.setInt(6,tache.getIdProjet());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Tache tache) throws SQLException {
        String req="UPDATE tache SET nom=?,description=?,dateDebut=?,dateFin=?,statut=?,idProjet=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, tache.getNom());
        ps.setString(2, tache.getDescription());
        ps.setDate(3,tache.getDateDebut());
        ps.setDate(4,tache.getDateFin());
        ps.setString(5,tache.getStatut().getValue());
        ps.setInt(6,tache.getIdProjet());
        ps.setInt(7,tache.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        Tache tache=getTacheById(id);
        TrelloAPI.deleteBoard(tache.getBoardId());
        String req="DELETE FROM tache WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1,id);
        ps.executeUpdate();
    }

    @Override
    public List<Tache> afficher() throws SQLException {
        List<Tache> taches=new ArrayList<>();
        String req="SELECT * FROM tache";
        Statement statement= connection.createStatement();
        ResultSet rs= statement.executeQuery(req);
        while (rs.next()) {
            Tache tache=new Tache();
            tache.setId(rs.getInt("id"));
            tache.setNom(rs.getString("nom"));
            tache.setDescription(rs.getString("description"));
            tache.setDateDebut(rs.getDate("dateDebut"));
            tache.setDateFin(rs.getDate("dateFin"));
            tache.setStatut(TacheStatus.fromValue(rs.getString("statut")));
            tache.setIdProjet(rs.getInt("idProjet"));
            if (rs.getString("trello_board_id")!=null)
                tache.setBoardId(rs.getString("trello_board_id"));
            taches.add(tache);
        }
        return taches;
    }

    public void enableTrelloForTask(Tache task, List<TempUser> teamMembers) throws SQLException {
        ServiceProjet serviceProjet=new ServiceProjet();
    Projet projet=serviceProjet.getById(task.getIdProjet());
    // Step 1: Create Trello board
    String boardId = TrelloAPI.createBoardWithLists(projet.getNom()+" - "+task.getNom(),task.getDateFin().toLocalDate(),task.getDateDebut().toLocalDate());

        if (boardId != null) {
            // Step 2: Store Trello board ID in database
            String query = "UPDATE Tache SET trello_board_id = ? WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, boardId);
            pst.setInt(2, task.getId());
            pst.executeUpdate();

            // Step 3: Add team members to Trello board
            List<String> memberEmails = teamMembers.stream()
                    .map(TempUser::getEmail) // Assuming Employe has an getEmail() method
                    .toList();
            TrelloAPI.addMembersToBoard(boardId, memberEmails);
        }
    }

    public void updateTaskStatus(Tache tache) throws SQLException {
        String query = "UPDATE Tache SET statut = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, tache.getStatut().getValue());
        pst.setInt(2, tache.getId());
        pst.executeUpdate();
    }

    public void clearBoardId(int tacheId) throws SQLException {
        String query = "UPDATE Tache SET trello_board_id = NULL WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, tacheId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("🗑️ Successfully cleared board_id for Task ID: " + tacheId);
            } else {
                System.out.println("⚠️ No update performed for Task ID: " + tacheId);
            }
        }
    }
    public void synchronizeDeletedBoards() throws SQLException {
        String query = "SELECT id, trello_board_id FROM Tache WHERE trello_board_id IS NOT NULL";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int taskId = rs.getInt("id");
                String boardId = rs.getString("trello_board_id");

                if (!TrelloAPI.isBoardStillOnTrello(boardId)) {
                    clearBoardId(taskId);
                    System.out.println("✅ Board " + boardId + " was deleted. Cleared from Task ID: " + taskId);
                } else {
                    System.out.println("ℹ️ Board " + boardId + " still exists on Trello.");
                }
            }
        }
    }
    public List<Tache> getTachesByProjetId(int projetId) throws SQLException {
        List<Tache> taches=new ArrayList<>();
        String query = "SELECT * FROM tache WHERE idProjet = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, projetId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Tache tache=new Tache();
                tache.setId(rs.getInt("id"));
                tache.setNom(rs.getString("nom"));
                tache.setDescription(rs.getString("description"));
                tache.setDateDebut(rs.getDate("dateDebut"));
                tache.setDateFin(rs.getDate("dateFin"));
                tache.setStatut(TacheStatus.fromValue(rs.getString("statut")));
                if (rs.getString("trello_board_id")!=null)
                    tache.setBoardId(rs.getString("trello_board_id"));
                taches.add(tache);
            }
        }
        return taches;
    }
    public Tache getTacheById(int id) throws SQLException {
        String query = "SELECT * FROM tache WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Tache tache=new Tache();
                tache.setId(rs.getInt("id"));
                tache.setNom(rs.getString("nom"));
                tache.setDescription(rs.getString("description"));
                tache.setDateDebut(rs.getDate("dateDebut"));
                tache.setDateFin(rs.getDate("dateFin"));
                tache.setStatut(TacheStatus.fromValue(rs.getString("statut")));
                if (rs.getString("trello_board_id")!=null)
                    tache.setBoardId(rs.getString("trello_board_id"));
                return tache;
            }
        }
        return null;
    }

    }
