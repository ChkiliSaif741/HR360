package services;

import entities.Equipe;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeService {
    private Connection con;

    public EquipeService() {
        con = MyDatabase.getInstance().getConnection();
    }

    // ✅ Add a new team
    public void addEquipe(Equipe equipe) throws SQLException {
        String query = "INSERT INTO Equipe (nom) VALUES (?)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, equipe.getNom());
        pst.executeUpdate();
    }

    // ✅ Update a team's name
    public void updateEquipe(Equipe equipe) throws SQLException {
        String query = "UPDATE Equipe SET nom = ? WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, equipe.getNom());
        pst.setInt(2, equipe.getId());
        pst.executeUpdate();
    }

    // ✅ Delete a team (only if it's not assigned to a project)
    public void deleteEquipe(int idEquipe) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM Projet_Equipe WHERE id_equipe = ?";
        PreparedStatement checkStmt = con.prepareStatement(checkQuery);
        checkStmt.setInt(1, idEquipe);
        ResultSet rs = checkStmt.executeQuery();
        rs.next();

        if (rs.getInt(1) > 0) {
            ProjetEquipeService projetEquipeService = new ProjetEquipeService();
            List<Integer> projetsID=projetEquipeService.getProjetsByEquipe(idEquipe);
            for (Integer projetID : projetsID) {
                projetEquipeService.unassignProjetFromEquipe(projetID);
            }
        }

        String query = "DELETE FROM Equipe WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEquipe);
        pst.executeUpdate();
    }

    // ✅ Get all teams
    public List<Equipe> getAllEquipes() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String query = "SELECT * FROM Equipe";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            equipes.add(new Equipe(rs.getInt("id"), rs.getString("nom")));
        }
        return equipes;
    }

    public Equipe getEquipe(int idEquipe) throws SQLException {
        Equipe equipe = null;
        String query = "SELECT * FROM Equipe WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEquipe);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            equipe = new Equipe(rs.getInt("id"), rs.getString("nom"));
        }
        return equipe;
    }
}
