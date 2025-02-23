package entities;

public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String imgSrc;
    private String poste;
    private float salaire;
    private String competence;

    public float getSalaire() {
        return salaire;
    }

    public void setSalaire(float salaire) {
        this.salaire = salaire;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Utilisateur() {
    }

    public Utilisateur(int id, String nom, String prenom, String email, String password ,String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Utilisateur(String nom, String prenom, String email, String password ,String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Utilisateur(String prenom, String nom, String email, String password, String role, String imgSrc) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imgSrc = imgSrc;
    }

    public Utilisateur(int id, String nom, String prenom, String email, String password, String role, String imgSrc, String poste, float salaire) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imgSrc = imgSrc;
        this.poste = poste;
        this.salaire = salaire;
    }

    public Utilisateur(String nom, String prenom, String email, String password, String role, String imgSrc, String poste, float salaire) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imgSrc = imgSrc;
        this.poste = poste;
        this.salaire = salaire;
    }

    public Utilisateur(int id, String nom, String prenom, String email, String password, String role, String imgSrc, String competence) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imgSrc = imgSrc;
        this.competence = competence;
    }

    public Utilisateur(String nom, String prenom, String email, String password, String role, String imgSrc, String competence) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imgSrc = imgSrc;
        this.competence = competence;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
