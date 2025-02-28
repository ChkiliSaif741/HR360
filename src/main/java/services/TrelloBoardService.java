package services;

import entities.TrelloBoard;
import utils.MyDatabase;

import java.sql.*;

public class TrelloBoardService {
    private Connection con;

    public TrelloBoardService() {
        con = MyDatabase.getInstance().getConnection();
    }

    public void addTrelloBoard(int idTache, String boardId) throws SQLException {
        String query = "INSERT INTO Trello_Board (id_tache, board_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE board_id = VALUES(board_id)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idTache);
        pst.setString(2, boardId);
        pst.executeUpdate();
    }

    public TrelloBoard getTrelloBoard(int idTache) throws SQLException {
        String query = "SELECT * FROM Trello_Board WHERE id_tache = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, idTache);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new TrelloBoard(rs.getInt("id_tache"), rs.getString("board_id"));
        }
        return null;
    }
}
