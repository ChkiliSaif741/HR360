package tests;

import entities.Utilisateur;
import services.ServiceUtilisateur;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServiceUtilisateur serviceUtilisateur= new ServiceUtilisateur();
        Utilisateur u1= new Utilisateur("Farhani","Hamza","hamza.farhani@esprit.tn","Responsable RH");
        Utilisateur u2 = new Utilisateur("Farhani","Yassine","yassine.farhani@esprit.tn","Employé");


        try {
//            serviceUtilisateur.ajouter(u1);
//            serviceUtilisateur.ajouter(u2);
            serviceUtilisateur.modifier(u2);
            //serviceUtilisateur.supprimer(12);
            System.out.println(serviceUtilisateur.afficher());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}