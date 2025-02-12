package services;

import interfaces.IEntretien;
import entities.Entretien;
import utils.MyDatabase;
import utils.type;
import utils.statut;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEntretien implements IEntretien<Entretien> {

    Connection connection;
    public ServiceEntretien(){
        connection= MyDatabase.getInstance().getConnection();

    }

    @Override
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

}
