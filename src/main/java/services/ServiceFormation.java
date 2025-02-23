package services;

import entities.Formation;
import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFormation implements IService<Formation> {
    Connection connection;

    public ServiceFormation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Formation formation) throws SQLException {
        String req = "INSERT INTO formation (titre, description, duree, dateFormation) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setString(1, formation.getTitre());
        stmt.setString(2, formation.getDescription());
        stmt.setInt(3, formation.getDuree());
        stmt.setString(4, formation.getDateFormation());
        stmt.executeUpdate();
        System.out.println("Formation ajoutée !");
    }

    @Override
    public void modifier(Formation formation) throws SQLException {
        String req = "update formation " +
                "set titre=?,description=?,duree=?,dateFormation=? where id =?";

        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setString(1, formation.getTitre());
        stmt.setString(2, formation.getDescription());
        stmt.setInt(3, formation.getDuree());
        stmt.setString(4, formation.getDateFormation());
        stmt.setInt(5, formation.getId());
        stmt.executeUpdate();
        System.out.println("Formation modifiée!");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "delete from formation where id =?";
        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        System.out.println("Formation supprimée!");
    }

    @Override
    public List<Formation> afficher() throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String req = "SELECT f.id, f.titre, f.description, f.duree, f.dateFormation, e.idEmploye, e.poste, e.salaire " +
                "FROM formation f " +
                "LEFT JOIN employe e ON f.id = e.idFormation"; // Utilisez LEFT JOIN pour inclure les formations sans employés
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            Formation formation = new Formation();
            formation.setId(rs.getInt("id"));
            formation.setTitre(rs.getString("titre"));
            formation.setDescription(rs.getString("description"));
            formation.setDuree(rs.getInt("duree"));
            formation.setDateFormation(rs.getString("dateFormation"));

            if (rs.getInt("idEmploye") != 0) { // Vérifiez si l'employé existe
                Utilisateur employe = new Utilisateur();
                employe.setId(rs.getInt("idEmploye"));
                employe.setPoste(rs.getString("poste"));
                employe.setSalaire(rs.getInt("salaire"));
                formation.getEmployees().add(employe);
            }

            formations.add(formation);
        }
        return formations;
    }

    /*public void afficherEmployesFormation(int idFormation) throws SQLException {
        String req = "select f.id , f.titre , f.description , f.duree , f.dateFormation " +
                "e.poste AS poste_employe , e.salaire AS salaire_employe" +
                "from formation f" +
                "join employe e on f.id = e.idFormation" +
                "where f.id = ?";

        PreparedStatement stmt = connection.prepareStatement(req);
        stmt.setInt(1, idFormation);
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {

            // Récupérer les informations de la formation
            String titreFormation = resultSet.getString("titre");
            String descriptionFormation = resultSet.getString("description");
            int dureeFormation = resultSet.getInt("duree");
            String dateFormation = resultSet.getString("dateFormation");

            // Récupérer les informations des employés
            String posteEmploye = resultSet.getString("poste");
            float salaireEmploye = resultSet.getFloat("salaire");

            // Afficher les informations
            System.out.println("Formation: " + titreFormation);
            System.out.println("Description: " + descriptionFormation);
            System.out.println("Durée: " + dureeFormation + " jours");
            System.out.println("Date: " + dateFormation);
            System.out.println("Employé: " +" (poste : " + posteEmploye + "salaire : " + salaireEmploye +")");
            System.out.println("-----------------------------");


        }
    }*/
}
