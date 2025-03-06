package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entities.Entretien;
import entities.Evaluation;
import entities.QuizQuestion;
import interfaces.IService;
import utils.MyDatabase;
import utils.commentaire;
import utils.statut;
import utils.type;

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
        evaluation.setCommentaire(commentaire.EN_ATTENTE);

        String req = "INSERT INTO evaluation (titreEva, noteTechnique, noteSoftSkills, commentaire, dateEvaluation, scorequiz, idEntretien) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, evaluation.getTitreEva());
        preparedStatement.setFloat(2, evaluation.getNoteTechnique());
        preparedStatement.setFloat(3, evaluation.getNoteSoftSkills());
        preparedStatement.setString(4, evaluation.getCommentaire().name());
        preparedStatement.setTimestamp(5, Timestamp.valueOf(evaluation.getDateEvaluation()));
        preparedStatement.setInt(6, evaluation.getScorequiz());
        preparedStatement.setInt(7, evaluation.getIdEntretien());

        //preparedStatement.setInt(6, evaluation.getId());
        preparedStatement.executeUpdate();
        System.out.println("Évaluation ajoutée avec succès !");
    }

    @Override
    public void modifier(Evaluation evaluation) throws SQLException {
        String req = "UPDATE evaluation SET titreEva=?,noteTechnique=?, noteSoftSkills=?, commentaire=?, dateEvaluation=?,scorequiz=?, questions=?, idEntretien=? WHERE idEvaluation=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1,evaluation.getTitreEva());
        preparedStatement.setFloat(2, evaluation.getNoteTechnique());
        preparedStatement.setFloat(3, evaluation.getNoteSoftSkills());
        preparedStatement.setString(4, evaluation.getCommentaire().name());
        preparedStatement.setTimestamp(5, Timestamp.valueOf(evaluation.getDateEvaluation()));
        preparedStatement.setInt(6,evaluation.getScorequiz());
        // Convertir la liste de questions en JSON
        Gson gson = new Gson();
        String questionsJson = gson.toJson(evaluation.getQuestions());
        preparedStatement.setString(7, questionsJson);
        preparedStatement.setInt(8, evaluation.getIdEntretien()); // Correction ici
        //preparedStatement.setInt(6, evaluation.getId()); // Correction ici
        preparedStatement.setInt(9, evaluation.getIdEvaluation()); // Correction ici



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
            evaluation.setTitreEva(resultSet.getString("titreEva"));
            evaluation.setNoteTechnique(resultSet.getFloat("noteTechnique"));
            evaluation.setNoteSoftSkills(resultSet.getFloat("noteSoftSkills"));
            evaluation.setCommentaire(commentaire.valueOf(resultSet.getString("commentaire")));
            evaluation.setDateEvaluation(resultSet.getTimestamp("dateEvaluation").toLocalDateTime());
            evaluation.setScorequiz(resultSet.getInt("scorequiz"));
            // Récupérer les questions de quiz depuis la base de données
            String questionsJson = resultSet.getString("questions");
            if (questionsJson != null) {
                Gson gson = new Gson();
                List<QuizQuestion> questions = gson.fromJson(questionsJson, new TypeToken<List<QuizQuestion>>() {}.getType());
                evaluation.setQuestions(questions);
            }

            evaluation.setIdEntretien(resultSet.getInt("idEntretien"));



            //evaluation.setId(resultSet.getInt("id"));
            evaluations.add(evaluation);
        }
        return evaluations;
    }

    public Evaluation getById(int idEvaluation) {
        String SQL = "SELECT * FROM evaluation WHERE idEvaluation = ?";
        Evaluation evaluation = null;

        try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
            pstmt.setInt(1, idEvaluation);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                evaluation = new Evaluation();
                evaluation.setIdEvaluation(rs.getInt("idEvaluation"));
                evaluation.setTitreEva(rs.getString("titreEva")); // Ajout du titre
                evaluation.setNoteTechnique(rs.getFloat("noteTechnique"));
                evaluation.setNoteSoftSkills(rs.getFloat("noteSoftSkills"));
                evaluation.setCommentaire(commentaire.valueOf(rs.getString("commentaire"))); // Convertir en enum
                evaluation.setDateEvaluation(rs.getTimestamp("dateEvaluation").toLocalDateTime());
                evaluation.setIdEntretien(rs.getInt("idEntretien"));
                evaluation.setScorequiz(rs.getInt("scorequiz")); // Ajout du score du quiz

                // Convertir le JSON en liste de questions (si applicable)
                String questionsJson = rs.getString("questions");
                if (questionsJson != null) {
                    Gson gson = new Gson();
                    List<QuizQuestion> questions = gson.fromJson(questionsJson, new TypeToken<List<QuizQuestion>>() {}.getType());
                    evaluation.setQuestions(questions);
                }
            } else {
                System.out.println("Aucune évaluation trouvée avec l'ID : " + idEvaluation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des détails de l'évaluation : " + e.getMessage());
            e.printStackTrace();
        }

        return evaluation;
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

