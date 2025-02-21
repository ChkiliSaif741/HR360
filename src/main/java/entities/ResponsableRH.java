package entities;

public class ResponsableRH {
    private String departement;

    public ResponsableRH() {}

    public ResponsableRH(String departement) {
        this.departement = departement;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String toString() {
        return "ResponsableRH [departement=" + departement + "]";
    }
}
