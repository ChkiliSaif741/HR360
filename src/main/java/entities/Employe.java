package entities;

public class Employe extends Utilisateur{

    private String poste;
    private float salaire;
    private int idFormation;
    public Employe() {}


    public Employe(String nom, String prenom, String email, String poste, float salaire) {
        super(nom, prenom, email, "Employe");
        this.poste = poste;
        this.salaire = salaire;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public float getSalaire() {
        return salaire;
    }

    public void setSalaire(float salaire) {
        this.salaire = salaire;
    }

    public int getIdFormation() {
        return idFormation;
    }

    public void setIdFormation(int idFormation) {
        this.idFormation = idFormation;
    }

    @Override
    public String toString() {
        return "Employe{" + "poste=" + poste + ", salaire=" + salaire +
                ", idFormation=" + idFormation + '}';
    }
}
