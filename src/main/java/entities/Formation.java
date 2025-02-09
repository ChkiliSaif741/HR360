package entities;

public class Formation {
    private int id;
    private String titre;
    private String description;
    private int duree;
    private String dateFormation;

    public Formation() {}
    public Formation(int id, String titre, String description, int duree, String dateFormation) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.duree = duree;
        this.dateFormation = dateFormation;
    }

    public Formation(String titre, String description, int duree, String dateFormation) {
        this.titre = titre;
        this.description = description;
        this.duree = duree;
        this.dateFormation = dateFormation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getDateFormation() {
        return dateFormation;
    }

    public void setDateFormation(String dateFormation) {
        this.dateFormation = dateFormation;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", duree=" + duree +
                ", dateFormation='" + dateFormation + '\'' +
                '}';
    }
}
