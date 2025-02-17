package entities;

import java.time.LocalDateTime;

public class Candidature {
    private int id_candidature;
    private LocalDateTime dateCandidature;
    private LocalDateTime dateEntretien; // Nouveau champ ajouté
    private String statut;
    private String cv;
    private String lettreMotivation;
    private int id_offre; // Clé étrangère

    public Candidature() {}

    public Candidature(LocalDateTime dateCandidature, LocalDateTime dateEntretien, String statut, String cv, String lettreMotivation, int id_offre) {
        this.dateCandidature = dateCandidature;
        this.dateEntretien = dateEntretien;
        this.statut = statut;
        this.cv = cv;
        this.lettreMotivation = lettreMotivation;
        this.id_offre = id_offre;
    }

    public int getId_candidature() {
        return id_candidature;
    }

    public LocalDateTime getDateCandidature() {
        return dateCandidature;
    }

    public LocalDateTime getDateEntretien() {
        return dateEntretien;
    }

    public String getStatut() {
        return statut;
    }

    public String getCv() {
        return cv;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public int getId_offre() {
        return id_offre;
    }

    public void setId_candidature(int id_candidature) {
        this.id_candidature = id_candidature;
    }

    public void setDateCandidature(LocalDateTime dateCandidature) {
        this.dateCandidature = dateCandidature;
    }

    public void setDateEntretien(LocalDateTime dateEntretien) {
        this.dateEntretien = dateEntretien;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    public void setId_offre(int id_offre) {
        this.id_offre = id_offre;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "id_candidature=" + id_candidature +
                ", dateCandidature=" + dateCandidature +
                ", dateEntretien=" + dateEntretien +
                ", statut='" + statut + '\'' +
                ", cv='" + cv + '\'' +
                ", lettreMotivation='" + lettreMotivation + '\'' +
                ", id_offre=" + id_offre +
                '}';
    }
}
