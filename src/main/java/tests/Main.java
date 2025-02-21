package tests;

import entities.Candidat;
import entities.Employe;
import entities.Formation;
import entities.Utilisateur;
import services.ServiceCandidat;
import services.ServiceEmploye;
import services.ServiceFormation;
import services.ServiceUtilisateur;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServiceUtilisateur serviceUtilisateur= new ServiceUtilisateur();
        ServiceEmploye serviceEmploye= new ServiceEmploye();
        ServiceCandidat serviceCandidat= new ServiceCandidat();
        ServiceFormation serviceFormation= new ServiceFormation();
        Utilisateur u1= new Utilisateur("Farhani","Hamza","hamza.farhani@esprit.tn","","Responsable RH");
        Utilisateur u2 = new Utilisateur("Farhani","Yassine","yassine.farhani@esprit.tn","","Employé");
        Employe e1 = new Employe("Farhani","Hamza","hamza.farhani@esprit.tn","","Développeur",3000,"",1);
        Candidat c1 = new Candidat("Farhani","Yassine","yassine.farhani@esprit.tn","","yassine_cv.pdf");
        Formation f1= new Formation("Gestion du temps et Productivité",
                "Apprenez à gérer votre temps de manière plus efficace" +
                " pour améliorer la productivité et réduire le stress.",
                24 ,"12/05/2025" );
        Formation f2 = new Formation("sensibilisation","produire",10,"12/05/2025");

        try {
            //Gestion du temps et Productivité
            //Sensibilisation
            //serviceUtilisateur.ajouter(u1);
            //serviceUtilisateur.ajouter(u2);
            //serviceFormation.ajouter(f1);
            serviceFormation.afficher();
            //serviceFormation.ajouter(f2);
            //serviceUtilisateur.modifier(u2);
            //serviceFormation.modifier(f1);
            //serviceUtilisateur.supprimer(12);
            //System.out.println(serviceUtilisateur.afficher());
                serviceEmploye.ajouter(e1);
            //serviceCandidat.ajouter(c1);
            System.out.println(serviceEmploye.afficher());
            //serviceEmploye.inscrireEmployeFormation(9,6);
            //serviceEmploye.inscrireEmployeFormation(10,7);
            //System.out.println(serviceEmploye.afficher());
            //System.out.println(serviceCandidat.afficher());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}