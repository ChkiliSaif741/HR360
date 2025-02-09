package services;

import entities.Tache;
import entities.TacheStatus;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTache implements IService<Tache> {
    Connection connection;
    public ServiceTache() {
        connection= MyDatabase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Tache tache) throws SQLException {
        String req="INSERT INTO tache (nom,description,dateDebut,dateFin,statut,idProjet) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, tache.getNom());
        ps.setString(2, tache.getDescription());
        ps.setDate(3,tache.getDateDebut());
        ps.setDate(4,tache.getDateFin());
        ps.setString(5,tache.getStatut().getValue());
        ps.setInt(6,tache.getIdProjet());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Tache tache) throws SQLException {
        String req="UPDATE tache SET nom=?,description=?,dateDebut=?,dateFin=?,statut=?,idProjet=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, tache.getNom());
        ps.setString(2, tache.getDescription());
        ps.setDate(3,tache.getDateDebut());
        ps.setDate(4,tache.getDateFin());
        ps.setString(5,tache.getStatut().getValue());
        ps.setInt(6,tache.getIdProjet());
        ps.setInt(7,tache.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req="DELETE FROM tache WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1,id);
        ps.executeUpdate();
    }

    @Override
    public List<Tache> afficher() throws SQLException {
        List<Tache> taches=new ArrayList<>();
        String req="SELECT * FROM tache";
        Statement statement= connection.createStatement();
        ResultSet rs= statement.executeQuery(req);
        while (rs.next()) {
            Tache tache=new Tache();
            tache.setId(rs.getInt("id"));
            tache.setNom(rs.getString("nom"));
            tache.setDescription(rs.getString("description"));
            tache.setDateDebut(rs.getDate("dateDebut"));
            tache.setDateFin(rs.getDate("dateFin"));
            tache.setStatut(TacheStatus.fromValue(rs.getString("statut")));
            tache.setIdProjet(rs.getInt("idProjet"));
            taches.add(tache);
        }
        return taches;
    }
}
