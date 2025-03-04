package entities;

public class Ressource {
    private int id;
    private String nom;
    private String type;
    private String etat;
    private double prix;


    public Ressource(String nom, String type, String etat, double prix) {
        System.out.println("Création Ressource : " + nom + ", " + type + ", " + etat + ", " + prix);
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.prix = prix;
    }
    public Ressource(int id, String nom, String type, String etat, double prix) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.prix = prix;

    }

    public Ressource(String nom, String type, String etat) {
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.prix = prix;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Ressource{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", etat='" + etat + '\'' +
                ", prix='" + prix + '\'' +
                '}';
    }
}
