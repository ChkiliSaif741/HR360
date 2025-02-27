package services;

import interfaces.IService;
import entities.Entretien;
import utils.MyDatabase;
import utils.statut;
import utils.type;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEntretien implements IService<Entretien> {

    Connection connection;
    private String accessToken;

    public ServiceEntretien(){
        connection= MyDatabase.getInstance().getConnection();

    }

    /*@Override
    public void ajouter(Entretien entretien) throws SQLException {
        if ((entretien.getType() == type.En_ligne && (entretien.getLien_meet() == null || entretien.getLien_meet().isEmpty())) ||
                (entretien.getType() == type.Presentiel && (entretien.getLocalisation() == null || entretien.getLocalisation().isEmpty()))) {
            throw new IllegalArgumentException("Le type d'entretien impose une valeur obligatoire pour le lien Meet ou la localisation.");
        }

        String req = "INSERT INTO entretien (date, heure, type, statut, lien_meet, localisation, idCandidature) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setDate(1, Date.valueOf(entretien.getDate()));
        preparedStatement.setTime(2, Time.valueOf(entretien.getHeure()));
        preparedStatement.setString(3, entretien.getType().name());
        preparedStatement.setString(4, entretien.getStatut().name());

        if (entretien.getType() == type.En_ligne) {
            preparedStatement.setString(5, entretien.getLien_meet());
            preparedStatement.setNull(6, Types.VARCHAR);
        } else {
            preparedStatement.setNull(5, Types.VARCHAR);
            preparedStatement.setString(6, entretien.getLocalisation());
        }
        preparedStatement.setInt(7, entretien.getIdCandidature());
        preparedStatement.executeUpdate();
        System.out.println("Entretien ajouté avec succès !");

    }*/


    @Override
    public void ajouter(Entretien entretien) throws SQLException, GeneralSecurityException, IOException {
// Vérifier que la date et l'heure ne sont pas null
        if (entretien.getDate() == null || entretien.getHeure() == null) {
            throw new IllegalArgumentException("La date et l'heure de l'entretien ne peuvent pas être null.");
        }
        // Forcer le statut à "Planifié"
        entretien.setStatut(statut.Planifié);
        String meetLink = null;

        if (entretien.getType() == type.En_ligne) {
            ZonedDateTime startDateTime = ZonedDateTime.of(entretien.getDate(), entretien.getHeure(), ZoneId.systemDefault());
            ZonedDateTime endDateTime = startDateTime.plusHours(1); // Durée par défaut 1 heure
            meetLink = GoogleMeetAPI.createMeetLink(startDateTime, endDateTime);
        }

        entretien.setLien_meet(meetLink);

        String req = "INSERT INTO entretien (date, heure, type, statut, lien_meet, localisation, idCandidature) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setDate(1, Date.valueOf(entretien.getDate()));
            preparedStatement.setTime(2, Time.valueOf(entretien.getHeure()));
            preparedStatement.setString(3, entretien.getType().name());
            preparedStatement.setString(4, entretien.getStatut().name());
            preparedStatement.setString(5, entretien.getLien_meet());
            preparedStatement.setString(6, entretien.getLocalisation());


            preparedStatement.setInt(7, entretien.getIdCandidature());
            preparedStatement.executeUpdate();
            System.out.println("Entretien ajouté avec succès ! Lien Meet : " + meetLink);
        }
    }


    @Override
    public void modifier(Entretien entretien) throws SQLException {
        String req = "UPDATE entretien SET date=?, heure=?, type=?, statut=?, lien_meet=?, localisation=?, idCandidature=? WHERE idEntretien=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setDate(1, Date.valueOf(entretien.getDate()));
        preparedStatement.setTime(2, Time.valueOf(entretien.getHeure()));
        preparedStatement.setString(3, entretien.getType().name());
        preparedStatement.setString(4, entretien.getStatut().name());

        if (entretien.getType() == type.En_ligne) {
            preparedStatement.setString(5, entretien.getLien_meet());
            preparedStatement.setNull(6, Types.VARCHAR);
        } else {
            preparedStatement.setNull(5, Types.VARCHAR);
            preparedStatement.setString(6, entretien.getLocalisation());
        }
        preparedStatement.setInt(7, entretien.getIdCandidature());
        preparedStatement.setInt(8, entretien.getIdEntretien());
        preparedStatement.executeUpdate();
        System.out.println("Entretien modifié avec succès !");

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM entretien WHERE idEntretien = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Entretien supprimé avec succès !");
    }

    @Override
    public List<Entretien> afficher() throws SQLException {

        List<Entretien> entretiens = new ArrayList<>();
        String req = "SELECT * FROM entretien";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            Entretien entretien = new Entretien();
            entretien.setIdEntretien(resultSet.getInt("idEntretien"));
            entretien.setDate(resultSet.getDate("date").toLocalDate());
            entretien.setHeure(resultSet.getTime("heure").toLocalTime());
            entretien.setType(type.valueOf(resultSet.getString("type")));
            entretien.setStatut(utils.statut.valueOf(resultSet.getString("statut")));
            entretien.setLien_meet(resultSet.getString("lien_meet"));
            entretien.setLocalisation(resultSet.getString("localisation"));
            entretien.setIdCandidature(resultSet.getInt("idCandidature"));
            entretiens.add(entretien);
        }
        return entretiens;
    }



    public List<Integer> getIdsCandidature() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String req = "SELECT idCandidature FROM candidature"; // Remplacez "candidature" par le nom de votre table
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            ids.add(resultSet.getInt("idCandidature"));
        }
        return ids;
    }


    /*public List<Entretien> getEntretiensDans24Heures() throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        String query = "SELECT * FROM entretien WHERE date = ? AND heure BETWEEN ? AND ?";
        LocalDate aujourdHui = LocalDate.now();
        LocalTime maintenant = LocalTime.now();
        LocalTime dans24Heures = maintenant.plusHours(24);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(aujourdHui));
            ps.setTime(2, Time.valueOf(maintenant));
            ps.setTime(3, Time.valueOf(dans24Heures));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Entretien entretien = new Entretien(
                        rs.getInt("idEntretien"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("heure").toLocalTime(),
                        type.valueOf(rs.getString("type")),
                        statut.valueOf(rs.getString("statut")),
                        rs.getString("lien_meet"),
                        rs.getString("localisation"),
                        rs.getInt("idCandidature")
                );
                entretiens.add(entretien);
            }
        }
        return entretiens;
    }*/

    public List<Entretien> getEntretiensDans24Heures() throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        String query = "SELECT * FROM entretien WHERE date = ? AND heure = ?";
        LocalDate aujourdHui = LocalDate.now();
        LocalTime maintenant = LocalTime.now();
        LocalDate dateDans24Heures = aujourdHui.plusDays(1); // Date dans 24 heures
        LocalTime heureDans24Heures = maintenant; // Heure actuelle (pour correspondre à l'heure exacte)

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDate(1, Date.valueOf(dateDans24Heures)); // Date dans 24 heures
            ps.setTime(2, Time.valueOf(heureDans24Heures)); // Heure actuelle

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Entretien entretien = new Entretien(
                        rs.getInt("idEntretien"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("heure").toLocalTime(),
                        type.valueOf(rs.getString("type")),
                        statut.valueOf(rs.getString("statut")),
                        rs.getString("lien_meet"),
                        rs.getString("localisation"),
                        rs.getInt("idCandidature")
                );
                entretiens.add(entretien);
            }
        }
        return entretiens;
    }

}
