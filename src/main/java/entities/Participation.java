package entities;

public class Participation {
    private int id;
    private int idFormation;
    private int idUser;


    public Participation(int idFormation, int idUser) {
        this.idFormation = idFormation;
        this.idUser = idUser;
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
