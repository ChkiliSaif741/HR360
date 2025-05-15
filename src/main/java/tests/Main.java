
package tests;

import entities.Tache;
import entities.TacheStatus;
import services.ServiceTache;

import entities.Entretien;
import services.ServiceEntretien;
import entities.Evaluation;
import services.ServiceEvaluation;
import utils.CryptageUtil;
import utils.MyDatabase;
import entities.Formation;
import entities.Utilisateur;
import services.ServiceFormation;
import services.ServiceUtilisateur;


import utils.type;
import utils.statut;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import java.sql.SQLException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        String mdp = "Hamza123@";
        String hashJavaFX = CryptageUtil.crypterMotDePasse(mdp);
        System.out.println("JavaFX: " + hashJavaFX);

        // Simule un hash Symfony
        String hashSymfony = "$2y$13$IhB0Ni9SDHGQX1lRqmZlsuzG9XVZklflHHeSYDWCKH6v/bQTykXkO";

        System.out.println("Vérification: " +
                CryptageUtil.verifierMotDePasse(mdp, hashSymfony));
    }

}

