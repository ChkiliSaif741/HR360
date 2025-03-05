package entities;

public class ProjetEquipe {
    private int idProjet;
    private int idEquipe;

    public ProjetEquipe() {}

    public ProjetEquipe(int idProjet, int idEquipe) {
        this.idProjet = idProjet;
        this.idEquipe = idEquipe;
    }

    public int getIdProjet() { return idProjet; }
    public void setIdProjet(int idProjet) { this.idProjet = idProjet; }

    public int getIdEquipe() { return idEquipe; }
    public void setIdEquipe(int idEquipe) { this.idEquipe = idEquipe; }
}
