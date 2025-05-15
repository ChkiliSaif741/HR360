package services;

import entities.Candidature;
import interfaces.IService;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature implements IService<Candidature> {
    private Connection connection;

    public ServiceCandidature() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Candidature candidature) throws SQLException {
        String req = "INSERT INTO candidature (dateCandidature, statut, cv, lettreMotivation, id_offre, description, dateModification, id_user) " +
                "VALUES (?, ?, ?, ?, ?, ?, NULL, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(candidature.getDateCandidature()));
            preparedStatement.setString(2, candidature.getStatut());
            preparedStatement.setString(3, candidature.getCv());
            preparedStatement.setString(4, candidature.getLettreMotivation());
            preparedStatement.setInt(5, candidature.getId_offre());
            preparedStatement.setString(6, candidature.getDescription());
            preparedStatement.setInt(7, candidature.getId_user());

            preparedStatement.executeUpdate();
            System.out.println("Candidature ajoutée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la candidature: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Candidature candidature) throws SQLException {
        String req = "UPDATE candidature SET dateCandidature=?, statut=?, cv=?, lettreMotivation=?, description=?, dateModification=?, id_user=? WHERE id_candidature=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(candidature.getDateCandidature()));
            preparedStatement.setString(2, candidature.getStatut());
            preparedStatement.setString(3, candidature.getCv());
            preparedStatement.setString(4, candidature.getLettreMotivation());
            preparedStatement.setString(5, candidature.getDescription());

            // Mise à jour de la dateModification avec la date et heure actuelle
            LocalDateTime now = LocalDateTime.now();
            preparedStatement.setTimestamp(6, Timestamp.valueOf(now));
            candidature.setDateModification(now);

            preparedStatement.setInt(7, candidature.getId_user());
            preparedStatement.setInt(8, candidature.getId_candidature());

            int verif = preparedStatement.executeUpdate();
            if (verif > 0) {
                System.out.println("Candidature modifiée avec succès !");
            } else {
                System.out.println("Erreur : la candidature avec ID " + candidature.getId_candidature() + " n'existe pas.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la candidature: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM candidature WHERE id_candidature = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            int verif = preparedStatement.executeUpdate();

            if (verif > 0) {
                System.out.println("Candidature supprimée avec succès.");
            } else {
                System.out.println("ID non trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la candidature: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Candidature> afficher() throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String req = "SELECT c.*, o.titre FROM candidature c JOIN offre o ON c.id_offre = o.id_offre";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Candidature candidature = new Candidature();
                candidature.setId_candidature(rs.getInt("id_candidature"));
                candidature.setDateCandidature(rs.getTimestamp("dateCandidature").toLocalDateTime());
                candidature.setStatut(rs.getString("statut"));
                candidature.setCv(rs.getString("cv"));
                candidature.setLettreMotivation(rs.getString("lettreMotivation"));
                candidature.setId_offre(rs.getInt("id_offre"));
                candidature.setDescription(rs.getString("description"));
                candidature.setId_user(rs.getInt("id_user"));

                Timestamp dateMod = rs.getTimestamp("dateModification");
                if (dateMod != null) {
                    candidature.setDateModification(dateMod.toLocalDateTime());
                }

                candidatures.add(candidature);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des candidatures: " + e.getMessage());
            throw e;
        }
        return candidatures;
    }
    public int compterParStatut(String statut) throws SQLException {
        String query = "SELECT COUNT(*) FROM candidature WHERE statut = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) { // Correction ici
            pst.setString(1, statut);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


}