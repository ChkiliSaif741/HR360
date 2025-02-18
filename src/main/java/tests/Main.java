package tests;

import entities.Tache;
import entities.TacheStatus;
import services.ServiceTache;

import java.sql.SQLException;
import java.sql.Date;

public class Main {
    public static void main(String[] args) {
        /*
        ServiceProjet serviceP= new ServiceProjet();
        Projet p1= new Projet("projeet1","projet important",Date.valueOf("2003-12-25"),Date.valueOf("2004-02-25"));
        Projet p2= new Projet(2,"projeet2","projet wow",Date.valueOf("2009-12-25"),Date.valueOf("2010-02-25"));
        try {
            serviceP.ajouter(p1);
            serviceP.modifier(p2);
            serviceP.supprimer(3);
            System.out.println(serviceP.afficher());


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
        ServiceTache serviceTache = new ServiceTache();
        Tache t1=new Tache("GestionUser","Pour users",Date.valueOf("2004-01-01"),Date.valueOf("2004-02-1"), TacheStatus.A_FAIRE,1);
        Tache t2=new Tache(2,"GestionUser","Pour users",Date.valueOf("2004-01-01"),Date.valueOf("2004-02-1"), TacheStatus.TERMINEE,1);
        try {
            serviceTache.ajouter(t1);
            serviceTache.modifier(t2);
            serviceTache.supprimer(1);
            System.out.println(serviceTache.afficher());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}