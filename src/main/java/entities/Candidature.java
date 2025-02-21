package entities;

import java.time.LocalDateTime;

public class Candidature {
    private int id_candidature;
    private LocalDateTime dateCandidature;
    private String statut;
    private String cv;
    private String lettreMotivation;
    private int id_offre;
    private String description;
    private LocalDateTime dateModification;
    private int id_user;

    public Candidature() {}

    public Candidature(LocalDateTime dateCandidature, String statut, String cv, String lettreMotivation, int id_offre, String description, LocalDateTime dateModification, int id_user) {
        this.dateCandidature = dateCandidature;
        this.statut = statut;
        this.cv = cv;
        this.lettreMotivation = lettreMotivation;
        this.id_offre = id_offre;
        this.description = description;
        this.dateModification = dateModification;
        this.id_user = id_user;
    }

    // Getters and setters
    public int getId_candidature() { return id_candidature; }
    public void setId_candidature(int id_candidature) { this.id_candidature = id_candidature; }

    public LocalDateTime getDateCandidature() { return dateCandidature; }
    public void setDateCandidature(LocalDateTime dateCandidature) { this.dateCandidature = dateCandidature; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }

    public String getLettreMotivation() { return lettreMotivation; }
    public void setLettreMotivation(String lettreMotivation) { this.lettreMotivation = lettreMotivation; }

    public int getId_offre() { return id_offre; }
    public void setId_offre(int id_offre) { this.id_offre = id_offre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public int getId_user() { return id_user; }
    public void setId_user(int id_user) { this.id_user = id_user; }

    @Override
    public String toString() {
        return "Candidature{" +
                "id_candidature=" + id_candidature +
                ", dateCandidature=" + dateCandidature +
                ", statut='" + statut + '\'' +
                ", cv='" + cv + '\'' +
                ", lettreMotivation='" + lettreMotivation + '\'' +
                ", id_offre=" + id_offre +
                ", description='" + description + '\'' +
                ", dateModification=" + dateModification +
                ", id_user=" + id_user +
                '}';
    }
}
