package tests;

import entities.Entretien;
import services.ServiceEntretien;
import entities.Evaluation;
import services.ServiceEvaluation;
import utils.MyDatabase;

import utils.type;
import utils.statut;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


import java.sql.SQLException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        ServiceEntretien serviceEntretien = new ServiceEntretien();
        ServiceEvaluation serviceEvaluation = new ServiceEvaluation();

        Entretien entretien = new Entretien(LocalDate.of(2000,9,4), LocalTime.of(5,40,27), type.En_ligne, statut.Planifié, "https://meet.google.com/test" ,null,1);
        Entretien entretien2 = new Entretien(LocalDate.of(2004,1,1), LocalTime.of(10,18,0), type.En_ligne, statut.Reporté, "https://meet.google.com/test" ,null,1);

        //Evaluation evaluation = new Evaluation(15.5f, 17.0f, "Bonne prestation", LocalDateTime.now(), 7, 1);
        //Evaluation evaluation2 = new Evaluation(17.5f, 10.0f, "bien", LocalDateTime.now(), 8, 1);

        try {
            //serviceEntretien.ajouter(entretien2);
            //entretien2.setIdEntretien(5);
            //serviceEntretien.modifier(entretien2);
            //System.out.println(serviceEntretien.afficher());
            //serviceEntretien.supprimer(4);

            //serviceEvaluation.ajouter(evaluation);
            //evaluation2.setIdEvaluation(10);
            //serviceEvaluation.modifier(evaluation2);
            //serviceEvaluation.supprimer(12);
            System.out.println(serviceEntretien.afficher());







        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}


/*
ServicePersonne servicePersonne= new ServicePersonne();
Personne p1= new Personne(22,"foulen","BenFoulen");
Personne p2= new Personne(1,25,"Sami","BenFoulen");
        try {
                //servicePersonne.ajouter(p2);
                servicePersonne.modifier(p2);
            System.out.println(servicePersonne.afficher());

        } catch (SQLException e) {
        System.out.println(e.getMessage());
        }

 */