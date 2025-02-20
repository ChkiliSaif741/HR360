package entities;

public class Candidat extends Utilisateur{

    private String cv;

    public Candidat() {
    }

    public Candidat(String nom, String prenom, String email, String password ,String cv) {
        super(nom, prenom, email, password ,"Candidat"); // Appelle le constructeur de Utilisateur
        this.cv = cv;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    @Override
    public String toString() {
        return super.toString() + ", CV: " + cv;
    }
}
