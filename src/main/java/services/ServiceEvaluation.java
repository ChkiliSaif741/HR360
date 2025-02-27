package services;

import entities.Evaluation;
import interfaces.IService;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvaluation implements IService<Evaluation> {

    Connection connection;
    public ServiceEvaluation(){
        connection= MyDatabase.getInstance().getConnection();

    }

    @Override
    public void ajouter(Evaluation evaluation) throws SQLException {
        String req = "INSERT INTO evaluation (noteTechnique, noteSoftSkills, commentaire, dateEvaluation, idEntretien) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setFloat(1, evaluation.getNoteTechnique());
        preparedStatement.setFloat(2, evaluation.getNoteSoftSkills());
        preparedStatement.setString(3, evaluation.getCommentaire());
        preparedStatement.setTimestamp(4, Timestamp.valueOf(evaluation.getDateEvaluation()));
        preparedStatement.setInt(5, evaluation.getIdEntretien());
        //preparedStatement.setInt(6, evaluation.getId());
        preparedStatement.executeUpdate();
        System.out.println("Évaluation ajoutée avec succès !");
    }

    @Override
    public void modifier(Evaluation evaluation) throws SQLException {
        String req = "UPDATE evaluation SET noteTechnique=?, noteSoftSkills=?, commentaire=?, dateEvaluation=?, idEntretien=? WHERE idEvaluation=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setFloat(1, evaluation.getNoteTechnique());
        preparedStatement.setFloat(2, evaluation.getNoteSoftSkills());
        preparedStatement.setString(3, evaluation.getCommentaire());
        preparedStatement.setTimestamp(4, Timestamp.valueOf(evaluation.getDateEvaluation()));
        preparedStatement.setInt(5, evaluation.getIdEntretien()); // Correction ici
        //preparedStatement.setInt(6, evaluation.getId()); // Correction ici
        preparedStatement.setInt(6, evaluation.getIdEvaluation()); // Correction ici
        preparedStatement.executeUpdate();
        System.out.println("Évaluation modifiée avec succès !");
    }


    /*@Override
    public void modifier(Evaluation evaluation) throws SQLException {
        String req = "UPDATE evaluation SET noteTechnique=?, noteSoftSkills=?, commentaire=?, dateEvaluation=?, idEntretien=?, id=? WHERE idEvaluation=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setFloat(1, evaluation.getNoteTechnique());
        preparedStatement.setFloat(2, evaluation.getNoteSoftSkills());
        preparedStatement.setString(3, evaluation.getCommentaire());
        preparedStatement.setDate(4, Date.valueOf(evaluation.getDateEvaluation()));
        preparedStatement.setInt(5, evaluation.getIdEvaluation());
        preparedStatement.setInt(6, evaluation.getIdEntretien());
        preparedStatement.setInt(7, evaluation.getId());
        preparedStatement.executeUpdate();
        System.out.println("Évaluation modifiée avec succès !");

    }*/

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evaluation WHERE idEvaluation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Évaluation supprimée avec succès !");
    }

    @Override
    public List<Evaluation> afficher() throws SQLException {
        List<Evaluation> evaluations = new ArrayList<>();
        String req = "SELECT * FROM evaluation";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            Evaluation evaluation = new Evaluation();
            evaluation.setIdEvaluation(resultSet.getInt("idEvaluation"));
            evaluation.setNoteTechnique(resultSet.getFloat("noteTechnique"));
            evaluation.setNoteSoftSkills(resultSet.getFloat("noteSoftSkills"));
            evaluation.setCommentaire(resultSet.getString("commentaire"));
            evaluation.setDateEvaluation(resultSet.getTimestamp("dateEvaluation").toLocalDateTime());
            evaluation.setIdEntretien(resultSet.getInt("idEntretien"));
            //evaluation.setId(resultSet.getInt("id"));
            evaluations.add(evaluation);
        }
        return evaluations;
    }

    public List<Integer> getIdsEntretien() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String req = "SELECT idEntretien FROM entretien"; // Remplacez "candidature" par le nom de votre table
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            ids.add(resultSet.getInt("idEntretien"));
        }
        return ids;
    }

    /*public List<Integer> getIdsEvaluateur() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String req = "SELECT id FROM utilisateur"; // Remplacez "candidature" par le nom de votre table
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(req);

        while (resultSet.next()) {
            ids.add(resultSet.getInt("id"));
        }
        return ids;
    }*/

    public boolean evaluationExistsForEntretien(int idEntretien) throws SQLException {
        String query = "SELECT COUNT(*) FROM evaluation WHERE idEntretien = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idEntretien);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne true si une évaluation existe déjà
            }
        }
        return false;
    }
}

