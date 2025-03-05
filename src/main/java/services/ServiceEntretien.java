package services;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import entities.QuizQuestion;
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

        // Récupérer l'ID de l'utilisateur à partir du nom et du prénom
        int idUtilisateur = getUtilisateurIdByNomPrenom(entretien.getNom(), entretien.getPrenom());
        if (idUtilisateur == -1) {
            throw new SQLException("Aucun utilisateur trouvé avec le nom et prénom : " + entretien.getNom() + " " + entretien.getPrenom());
        }
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

        String req = "INSERT INTO entretien (date, heure, type, statut, lien_meet, localisation, id, nom, prenom) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setDate(1, Date.valueOf(entretien.getDate()));
            preparedStatement.setTime(2, Time.valueOf(entretien.getHeure()));
            preparedStatement.setString(3, entretien.getType().name());
            preparedStatement.setString(4, entretien.getStatut().name());
            preparedStatement.setString(5, entretien.getLien_meet());
            preparedStatement.setString(6, entretien.getLocalisation());
            preparedStatement.setInt(7, idUtilisateur);
            preparedStatement.setString(8, entretien.getNom());
            preparedStatement.setString(9, entretien.getPrenom());



            //preparedStatement.setInt(7, entretien.getIdCandidature());
            preparedStatement.executeUpdate();
            System.out.println("Entretien ajouté avec succès ! Lien Meet : " + meetLink);
        }
    }


    @Override
    public void modifier(Entretien entretien) throws SQLException {

        // Récupérer l'ID de l'utilisateur à partir du nom et du prénom
        int idUtilisateur = getUtilisateurIdByNomPrenom(entretien.getNom(), entretien.getPrenom());
        if (idUtilisateur == -1) {
            throw new SQLException("Aucun utilisateur trouvé avec le nom et prénom : " + entretien.getNom() + " " + entretien.getPrenom());
        }

        String req = "UPDATE entretien SET date=?, heure=?, type=?, statut=?, lien_meet=?, localisation=?, id=?, nom=?, prenom=? WHERE idEntretien=?";
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
        //preparedStatement.setInt(7, entretien.getIdCandidature());
        preparedStatement.setInt(7, idUtilisateur);
        preparedStatement.setString(8, entretien.getNom());
        preparedStatement.setString(9, entretien.getPrenom()); // Assurez-vous que cette valeur est correcte
        preparedStatement.setInt(10, entretien.getIdEntretien());


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
            entretien.setNom(resultSet.getString("nom"));
            entretien.setPrenom(resultSet.getString("prenom"));
            entretien.setDate(resultSet.getDate("date").toLocalDate());
            entretien.setHeure(resultSet.getTime("heure").toLocalTime());
            entretien.setType(type.valueOf(resultSet.getString("type")));
            entretien.setStatut(utils.statut.valueOf(resultSet.getString("statut")));
            entretien.setLien_meet(resultSet.getString("lien_meet"));
            entretien.setLocalisation(resultSet.getString("localisation"));
            entretien.setIdCandidature(resultSet.getInt("idCandidature"));
            entretien.setId(resultSet.getInt("id"));
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





    public Entretien getById(int idEntretien) {
        String SQL = "SELECT * FROM entretien WHERE idEntretien = ?";
        Entretien entretien = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setInt(1, idEntretien);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                entretien = new Entretien();
                entretien.setIdEntretien(rs.getInt("idEntretien"));
                entretien.setDate(rs.getDate("date").toLocalDate());
                entretien.setHeure(rs.getTime("heure").toLocalTime());
                entretien.setType(type.valueOf(rs.getString("type")));
                entretien.setStatut(statut.valueOf(rs.getString("statut")));
                entretien.setLien_meet(rs.getString("lien_meet"));
                entretien.setLocalisation(rs.getString("localisation"));
                entretien.setIdCandidature(rs.getInt("idCandidature"));


            } else {
                System.out.println("Aucun entretien trouvé avec l'ID : " + idEntretien);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des détails de l'entretien : " + e.getMessage());
            e.printStackTrace();
        }

        return entretien;
    }



    private int getUtilisateurIdByNomPrenom(String nom, String prenom) throws SQLException {
        String SQL = "SELECT id FROM utilisateur WHERE nom = ? AND prenom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // Retourne -1 si aucun utilisateur n'est trouvé
    }





    public List<String> getPrenomsUtilisateurByNom(String nom) throws SQLException {
        List<String> prenoms = new ArrayList<>();
        String SQL = "SELECT prenom FROM utilisateur WHERE nom = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prenoms.add(rs.getString("prenom"));
            }
        }
        return prenoms;
    }
    public String getEmailByNomPrenom(String nom, String prenom) throws SQLException {
        String SQL = "SELECT email FROM utilisateur WHERE nom = ? AND prenom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        }
        return null; // Retourne null si aucun e-mail n'est trouvé
    }
    public List<Entretien> getEntretiensByUserId(int id) {
        List<Entretien> entretiens = new ArrayList<>();
        String query = "SELECT e.* FROM entretien e " +
                "JOIN utilisateur u ON i.id = u.id " +
                "WHERE u.id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

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
                // Ajoutez d'autres champs si nécessaire
                entretiens.add(entretien);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des interviews : " + e.getMessage());
            e.printStackTrace();
        }

        return entretiens;
    }

    public List<String> getAllNomsUtilisateurs() throws SQLException {
        List<String> noms = new ArrayList<>();
        String SQL = "SELECT nom FROM utilisateur";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL)) {

            while (resultSet.next()) {
                noms.add(resultSet.getString("nom"));
            }
        }
        return noms;
    }
}