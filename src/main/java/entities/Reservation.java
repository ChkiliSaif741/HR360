package entities;

import java.sql.Date;

public class Reservation {
    private int id;
    private int idRessource;
    private Date dateDebut;
    private Date dateFin;
    private int iduser;
    private Ressource ressource;

    public Reservation(int id, int idRessource, Date dateDebut, Date dateFin, int iduser) {
        this.id = id;
        this.idRessource = idRessource;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.iduser = iduser;
    }

    public Reservation(int idRessource, Date dateDebut, Date dateFin, int iduser) {
        this.idRessource = idRessource;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.iduser = iduser;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdRessource() { return idRessource; }
    public void setIdRessource(int idRessource) { this.idRessource = idRessource; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public Ressource getRessource() {return ressource;}
    public void setRessource(Ressource ressource) {this.ressource = ressource;}

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", idRessource=" + idRessource +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", iduser='" + iduser + '\'' +
                '}';
    }
}
