package services;

import utils.MyDatabase;

import java.sql.*;

public class ProjetEquipeService {
    private Connection con;

    public ProjetEquipeService() {
        con = MyDatabase.getInstance().getConnection();
    }

    // ✅ Assign a project to a team (update if it already exists)
    public void assignProjetToEquipe(int idProjet, int idEquipe) throws SQLException {
        String query = "INSERT INTO Projet_Equipe (id_projet, id_equipe) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE id_equipe = VALUES(id_equipe)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idProjet);
        pst.setInt(2, idEquipe);
        pst.executeUpdate();
    }

    // ✅ Unassign a project from a team
    public void unassignProjetFromEquipe(int idProjet) throws SQLException {
        String query = "DELETE FROM Projet_Equipe WHERE id_projet = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idProjet);
        pst.executeUpdate();
    }

    // ✅ Get the team assigned to a project
    public Integer getEquipeByProjet(int idProjet) throws SQLException {
        String query = "SELECT id_equipe FROM Projet_Equipe WHERE id_projet = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idProjet);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("id_equipe");
        }
        return null;
    }
}
