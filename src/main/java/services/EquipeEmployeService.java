package services;

import entities.Utilisateur;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeEmployeService {
    private Connection con;
    ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    public EquipeEmployeService() {
        con = MyDatabase.getInstance().getConnection();
    }

    // ✅ Assign an employee to a team (now supports multiple teams)
    public void assignEmployeToEquipe(int idEmploye, int idEquipe) throws SQLException {
        String query = "INSERT IGNORE INTO Equipe_Employe (id_employe, id_equipe) VALUES (?, ?)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEmploye);
        pst.setInt(2, idEquipe);
        pst.executeUpdate();
        ProjetEquipeService projetEquipeService = new ProjetEquipeService();
        List<String> boardIds=projetEquipeService.getBoardsByEquipe(idEquipe);
        Utilisateur employe=serviceUtilisateur.getUserById(idEmploye);
        if (boardIds.size()>0) {
            for (String boardId : boardIds) {
                TrelloAPI.addMemberToBoard(boardId,employe.getEmail());
            }
        }
    }

    // ✅ Remove an employee from a specific team (or all teams)
    public void removeEmployeFromEquipe(int idEmploye, Integer idEquipe) throws SQLException {
        String query;
        PreparedStatement pst;

        if (idEquipe == null) {
            // Remove employee from all teams
            query = "DELETE FROM Equipe_Employe WHERE id_employe = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, idEmploye);
        } else {
            // Remove employee from a specific team
            query = "DELETE FROM Equipe_Employe WHERE id_employe = ? AND id_equipe = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, idEmploye);
            pst.setInt(2, idEquipe);
        }
        pst.executeUpdate();
        ProjetEquipeService projetEquipeService = new ProjetEquipeService();
        List<String> boardIds=projetEquipeService.getBoardsByEquipe(idEquipe);
        Utilisateur employe=serviceUtilisateur.getUserById(idEmploye);
        if (boardIds.size()>0) {
            for (String boardId : boardIds) {
                TrelloAPI.removeMemberFromBoardByEmail(boardId,employe.getEmail());
            }
        }
    }

    // ✅ Get all employees in a specific team
    public List<Utilisateur> getEmployesByEquipe(int idEquipe) throws SQLException {
        List<Utilisateur> employes = new ArrayList<>();
        String query = "SELECT id_employe FROM Equipe_Employe WHERE id_equipe = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEquipe);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getInt("id_employe"));
            System.out.println(serviceUtilisateur.getUserById(rs.getInt("id_employe")));
            employes.add(serviceUtilisateur.getUserById(rs.getInt("id_employe")));
        }
        return employes;
    }

    // ✅ Get all teams of a specific employee (returns a list)
    public List<Integer> getEquipesByEmploye(int idEmploye) throws SQLException {
        List<Integer> equipes = new ArrayList<>();
        String query = "SELECT id_equipe FROM Equipe_Employe WHERE id_employe = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEmploye);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            equipes.add(rs.getInt("id_equipe"));
        }
        return equipes; // Returns empty list if employee is not assigned to any team
    }
}
