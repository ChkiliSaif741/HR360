package tests;

import entities.Personne;
import entities.Projet;
import services.ServicePersonne;
import services.ServiceProjet;
import utils.MyDatabase;

import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public class Main {
    public static void main(String[] args) {
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
        }

    }
}