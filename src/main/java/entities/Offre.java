package entities;

import java.time.LocalDateTime;

public class Offre {
private int id_offre;
private String titre,description,statut;
    private LocalDateTime datePublication;  // Date et heure de publication
    private LocalDateTime dateExpiration;   // Date et heure d'expiration


    // Constructeurs
    public Offre() {}

    public Offre(String titre, String description, LocalDateTime datePublication, LocalDateTime dateExpiration) {
        this.titre = titre;
        this.description = description;
        this.datePublication = datePublication;
        this.dateExpiration = dateExpiration;
        this.statut = "Publiée"; // Vous pouvez définir un statut par défaut
    }

    public void mettreAJourStatut() {
        if (dateExpiration.isBefore(LocalDateTime.now())) {
            setStatut("Expirée");
        } else {
            setStatut("Publiée");
        }
    }

    // Getters et Setters
    public int getId() {
        return id_offre;
    }

    public void setId(int id) {
        this.id_offre = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // toString()
    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id_offre +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", datePublication=" + datePublication +
                ", dateExpiration=" + dateExpiration +
                ", statut=" + statut +
                '}';
    }



}
