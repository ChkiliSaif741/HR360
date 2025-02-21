package entities;

import java.sql.Date;

public class Reservation {
    private int id;
    private int idRessource;
    private Date dateDebut;
    private Date dateFin;
    private String utilisateur;
    private Ressource ressource;

    public Reservation(int id, int idRessource, Date dateDebut, Date dateFin, String utilisateur) {
        this.id = id;
        this.idRessource = idRessource;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateur = utilisateur;
    }

    public Reservation(int idRessource, Date dateDebut, Date dateFin, String utilisateur) {
        this.idRessource = idRessource;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateur = utilisateur;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdRessource() { return idRessource; }
    public void setIdRessource(int idRessource) { this.idRessource = idRessource; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }
    public Ressource getRessource() {return ressource;}
    public void setRessource(Ressource ressource) {this.ressource = ressource;}

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", idRessource=" + idRessource +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", utilisateur='" + utilisateur + '\'' +
                '}';
    }
}
