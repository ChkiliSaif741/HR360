package services;

import entities.Offre;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffre implements IService<Offre> {
    private Connection connection;

    public ServiceOffre() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Offre offre) throws SQLException {
        String req = "INSERT INTO offre (titre, description, datePublication, dateExpiration, statut) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, offre.getTitre());
            preparedStatement.setString(2, offre.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(offre.getDatePublication()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(offre.getDateExpiration()));
            preparedStatement.setString(5, offre.getStatut());

            int verif = preparedStatement.executeUpdate();
            if (verif > 0) {
                System.out.println("Offre ajoutée avec succès !");
            } else {
                System.out.println("Erreur lors de l'ajout de l'offre.");
            }
        }
    }

    @Override
    public void modifier(Offre offre) throws SQLException {
        String req = "UPDATE offre SET titre=?, description=?, datePublication=?, dateExpiration=?, statut=? WHERE id_offre=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, offre.getTitre());
            preparedStatement.setString(2, offre.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(offre.getDatePublication()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(offre.getDateExpiration()));
            preparedStatement.setString(5, offre.getStatut());
            preparedStatement.setInt(6, offre.getId());

            int verif = preparedStatement.executeUpdate();
            if (verif > 0) {
                System.out.println("Offre modifiée avec succès !");
            } else {
                System.out.println("Erreur : l'offre avec ID " + offre.getId() + " n'existe pas.");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM offre WHERE id_offre = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            int verif = preparedStatement.executeUpdate();

            if (verif > 0) {
                System.out.println("Offre supprimée avec succès.");
            } else {
                System.out.println("ID non trouvé.");
            }
        }
    }

    @Override
    public List<Offre> afficher() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        String req = "SELECT * FROM offre";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Offre offre = new Offre();
                offre.setId(rs.getInt("id_offre"));
                offre.setTitre(rs.getString("titre"));
                offre.setDescription(rs.getString("description"));
                offre.setDatePublication(rs.getTimestamp("datePublication").toLocalDateTime());
                offre.setDateExpiration(rs.getTimestamp("dateExpiration").toLocalDateTime());
                offre.setStatut(rs.getString("statut"));

                offres.add(offre);
            }
        }

        return offres;
    }
}
