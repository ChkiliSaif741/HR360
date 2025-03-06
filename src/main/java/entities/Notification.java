package entities;


import java.sql.Timestamp;

public class Notification {
    private int id;
    private int reservationId;
    private int userId;
    private String message;
    private Timestamp date;

    // Constructeur par défaut
    public Notification() {}

    // Constructeur avec paramètres
    public Notification(int id, int reservationId, int userId, String message, Timestamp date) {
        this.id = id;
        this.reservationId = reservationId;
        this.userId = userId;
        this.message = message;
        this.date = date;
    }
    public Notification(int reservationId, int userId, String message, Timestamp date) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.message = message;
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", reservationId=" + reservationId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}