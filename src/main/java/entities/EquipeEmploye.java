package entities;

public class EquipeEmploye {
    private int idEquipe;
    private int idEmploye;

    public EquipeEmploye() {}

    public EquipeEmploye(int idEquipe, int idEmploye) {
        this.idEquipe = idEquipe;
        this.idEmploye = idEmploye;
    }

    public int getIdEquipe() { return idEquipe; }
    public void setIdEquipe(int idEquipe) { this.idEquipe = idEquipe; }

    public int getIdEmploye() { return idEmploye; }
    public void setIdEmploye(int idEmploye) { this.idEmploye = idEmploye; }
}
