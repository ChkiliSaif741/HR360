package services;

import entities.Notification;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceNotification {

    private Connection connection;

    public ServiceNotification() {
        connection = MyDatabase.getInstance().getConnection();
    }


    public void ajouterNotification(Notification notification) throws SQLException {
        String query = "INSERT INTO notification (reservation_id, iduser, message, date) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, notification.getReservationId());
        stmt.setInt(2, notification.getUserId());
        stmt.setString(3, notification.getMessage());
        stmt.setTimestamp(4, notification.getDate());
        stmt.executeUpdate();
    }


    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification WHERE iduser = ? ORDER BY date DESC";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Notification notification = new Notification(
                    rs.getInt("id"),
                    rs.getInt("reservation_id"),
                    rs.getInt("iduser"),
                    rs.getString("message"),
                    rs.getTimestamp("date")
            );
            notifications.add(notification);
        }
        return notifications;
    }


    public void supprimerNotification(int id) throws SQLException {
        String query = "DELETE FROM notification WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }


    public void marquerCommeLue(int id) throws SQLException {
        String query = "UPDATE notification SET is_read = true WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
}