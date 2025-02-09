package tests;

import entities.Formation;
import entities.Utilisateur;
import services.ServiceFormation;
import services.ServiceUtilisateur;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServiceUtilisateur serviceUtilisateur= new ServiceUtilisateur();
        ServiceFormation serviceFormation= new ServiceFormation();
        Utilisateur u1= new Utilisateur("Farhani","Hamza","hamza.farhani@esprit.tn","Responsable RH");
        Utilisateur u2 = new Utilisateur("Farhani","Yassine","yassine.farhani@esprit.tn","Employé");
        Formation f1= new Formation("Gestion du temps et Productivité","Apprenez à gérer votre temps de manière plus efficace pour améliorer la productivité et réduire le stress." , 24 ,"12/05/2025" );
        Formation f2 = new Formation("sensibilisation","produire",10,"12/05/2025");

        try {
            //Gestion du temps et Productivité
            //Sensibilisation
//          serviceUtilisateur.ajouter(u1);
//          serviceUtilisateur.ajouter(u2);
            //serviceFormation.ajouter(f1);
            //serviceFormation.ajouter(f2);
            //serviceUtilisateur.modifier(u2);
            //serviceFormation.modifier(f1);
            //serviceUtilisateur.supprimer(12);
            System.out.println(serviceUtilisateur.afficher());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}