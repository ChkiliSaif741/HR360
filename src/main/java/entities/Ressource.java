package entities;

public class Ressource {
    private int id;
    private String nom;
    private String type;
    private String etat;
    private String utilisateur;

    public Ressource(){};
    public Ressource(int id, String nom, String type, String etat, String utilisateur) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.utilisateur = utilisateur;
    }

    public Ressource(String nom, String type, String etat, String utilisateur) {
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.utilisateur = utilisateur;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }

    @Override
    public String toString() {
        return "Ressource{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", etat='" + etat + '\'' +
                ", utilisateur='" + utilisateur + '\'' +
                '}';
    }
}
