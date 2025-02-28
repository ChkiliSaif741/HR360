package services;

import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeEmployeService {
    private Connection con;

    public EquipeEmployeService() {
        con = MyDatabase.getInstance().getConnection();
    }

    // ✅ Assign an employee to a team (update if they already exist)
    public void assignEmployeToEquipe(int idEmploye, int idEquipe) throws SQLException {
        String query = "INSERT INTO Equipe_Employe (id_employe, id_equipe) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE id_equipe = VALUES(id_equipe)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEmploye);
        pst.setInt(2, idEquipe);
        pst.executeUpdate();
    }

    // ✅ Remove an employee from a team
    public void removeEmployeFromEquipe(int idEmploye) throws SQLException {
        String query = "DELETE FROM Equipe_Employe WHERE id_employe = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEmploye);
        pst.executeUpdate();
    }

    // ✅ Get all employees in a specific team
    public List<Integer> getEmployesByEquipe(int idEquipe) throws SQLException {
        List<Integer> employes = new ArrayList<>();
        String query = "SELECT id_employe FROM Equipe_Employe WHERE id_equipe = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEquipe);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            employes.add(rs.getInt("id_employe"));
        }
        return employes;
    }

    // ✅ Get the team of a specific employee
    public Integer getEquipeByEmploye(int idEmploye) throws SQLException {
        String query = "SELECT id_equipe FROM Equipe_Employe WHERE id_employe = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idEmploye);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("id_equipe");
        }
        return null; // Employee is not assigned to any team
    }
}
