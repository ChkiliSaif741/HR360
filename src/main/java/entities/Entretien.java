package entities;
import utils.type;
import utils.statut;

import java.time.LocalDate;
import java.time.LocalTime;

public class Entretien {
    private int idEntretien;
    private LocalDate date;
    private LocalTime heure;
    private type type;
    private statut statut;
    private String lien_meet;
    private String localisation;
    private int idCandidature;



    public Entretien() {}


    public Entretien(LocalDate date, LocalTime heure, type type, statut statut,String lien_meet,String localisation, int idCandidature) {
        this.date = date;
        this.heure = heure;
        this.type = type;
        this.statut = statut;
        this.lien_meet = lien_meet;
        this.localisation = localisation;
        this.idCandidature = idCandidature;

    }

    public Entretien(int idEntretien, LocalDate date, LocalTime heure, type type, statut statut, String lien_meet, String localisation, int idCandidature) {
        this.idEntretien = idEntretien;
        this.date = date;
        this.heure = heure;
        this.type = type;
        this.statut = statut;
        this.lien_meet = lien_meet;
        this.localisation = localisation;
        this.idCandidature = idCandidature;
    }

    public int getIdEntretien() {
        return idEntretien;
    }
    public void setIdEntretien(int idEntretien) {
        this.idEntretien = idEntretien;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public LocalTime getHeure() {
        return heure;
    }
    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }
    public type getType() {
        return type;
    }

    public void setType(type type) {
        this.type = type;
    }

    public statut getStatut() {
        return statut;
    }

    public void setStatut(statut statut) {
        this.statut = statut;
    }
    public String getLien_meet() {
        return lien_meet;
    }
    public void setLien_meet(String lien_meet) {
        this.lien_meet = lien_meet;
    }
    public String getLocalisation() {
        return localisation;
    }
    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
    public int getIdCandidature() {
        return idCandidature;
    }
    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
    }

    @Override
    public String toString() {
        return "Entretien{" +
                "idEntretien=" + idEntretien +
                ", date=" + date +
                ", heure=" + heure +
                ", type=" + type +
                ", statut=" + statut +
                ", idCandidature=" + idCandidature +
                '}';
    }



}
