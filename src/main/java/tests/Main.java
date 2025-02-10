package tests;

import entities.Candidature;
import entities.Offre;
import services.ServiceCandidature;
import services.ServiceOffre;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        ServiceOffre service = new ServiceOffre();
//
//        try {
//            // Ajouter une offre pour le test si nécessaire
//            Offre newOffre = new Offre("Développeur Java", "Poste CDI", LocalDateTime.now(), LocalDateTime.now().plusDays(30), "OUVERTE");
//            //service.ajouter(newOffre);
//            System.out.println("Offre ajoutée !");
//
//            // Récupérer toutes les offres pour choisir une à supprimer
//            List<Offre> offres = service.afficher();
//            if (!offres.isEmpty()) {
//                // Suppression de la première offre trouvée
//                Offre offreToDelete = offres.get(1);
//                int idToDelete = offreToDelete.getId();
//                System.out.println("Offre à supprimer : " + offreToDelete);
//
//                // Appel de la méthode supprimer
//                service.supprimer(idToDelete);
//                System.out.println("Offre supprimée avec succès !");
//
//                // Vérifier en affichant les offres après suppression
//                offres = service.afficher();
//                System.out.println("Liste des offres après suppression :");
//                for (Offre o : offres) {
//                    System.out.println(o);
//                }
//            } else {
//                System.out.println("Aucune offre trouvée en base de données !");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
        ServiceCandidature serviceCandidature = new ServiceCandidature();

        try {
            // 🔹 Ajouter une candidature liée à une offre existante
            Candidature candidature = new Candidature(
                    LocalDateTime.now(), "EN ATTENTE", "cv.pdf", "Lettre de motivation", 7 // ID de l'offre associée
            );
            serviceCandidature.ajouter(candidature);

            // 🔹 Afficher toutes les candidatures avec les offres associées
            List<Candidature> candidatures = serviceCandidature.afficher();
            for (Candidature c : candidatures) {
                System.out.println(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
