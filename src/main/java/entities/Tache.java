package entities;

import java.sql.Date;

public class Tache {
    private int id;
    private String nom;
    private String description;
    private Date dateDebut, dateFin;
    private TacheStatus statut;
    private int idProjet;

    public Tache() {}
    public Tache(int id, String nom, String description, Date dateDebut, Date dateFin, TacheStatus statut, int idProjet) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.idProjet = idProjet;
    }

    public Tache(String nom, String description, Date dateDebut, Date dateFin, TacheStatus statut, int idProjet) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.idProjet = idProjet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public TacheStatus getStatut() {
        return statut;
    }

    public void setStatut(TacheStatus statut) {
        this.statut = statut;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    @Override
    public String toString() {
        return "Tache{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut=" + statut.getValue() +
                ", idProjet=" + idProjet +
                '}';
    }
}
