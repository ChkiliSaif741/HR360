package entities;

public class Participation {
    private int id;
    private int idFormation;
    private int idUser;
    private String formationNom; // Ajouter ce champ

    public Participation(int idFormation, int idUser, String formationNom) {
        this.idFormation = idFormation;
        this.idUser = idUser;
        this.formationNom = formationNom;
    }


    public Participation(int idFormation, int idUser) {
        this.idFormation = idFormation;
        this.idUser = idUser;
    }


    public String getFormationNom() {
        return formationNom;
    }





    public int getId() {
        return id;
    }

    public int getIdFormation() {
        return idFormation;
    }

    public int getIdUser() {
        return idUser;
    }


}
