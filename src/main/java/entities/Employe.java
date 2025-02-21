package entities;

public class Employe extends Utilisateur{

    private String poste;
    private float salaire;
    private int idFormation;
    public Employe() {}


    public Employe(String nom, String prenom, String email, String password ,String poste, float salaire) {
        super(nom, prenom, email, password ,"Employe");
        this.poste = poste;
        this.salaire = salaire;
    }

    public Employe(String nom, String prenom, String email, String password ,String poste, float salaire,String imgSrc){
        super(nom, prenom, email, password ,"Employe",imgSrc);
        this.poste = poste;
        this.salaire = salaire;
    }

    public Employe(String nom, String prenom, String email, String password ,String poste, float salaire,String imgSrc,int idFormation){
        super(nom, prenom, email, password ,"Employe",imgSrc);
        this.poste = poste;
        this.salaire = salaire;
        this.idFormation = idFormation;
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
