package services;

import entities.Equipe;
import entities.Projet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ServiceChargeTravail {

    public Map<LocalDate, Integer> calculerChargeParJour(Equipe equipe) throws SQLException {
        Map<LocalDate, Integer> chargeParJour = new HashMap<>();
        ProjetEquipeService projetEquipeService = new ProjetEquipeService();
        ServiceProjet serviceProjet = new ServiceProjet();

        // Récupérer tous les projets associés à l'équipe
        List<Integer> projetsID = projetEquipeService.getProjetsByEquipe(equipe.getId());
        List<Projet> projets = new ArrayList<>();
        for (Integer projetID : projetsID) {
            projets.add(serviceProjet.getById(projetID));
        }

        for (Projet projet : projets) {
            LocalDate dateDebut = projet.getDateDebut().toLocalDate();
            LocalDate dateFin = projet.getDateFin().toLocalDate();
            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
            if (jours <= 0) continue;  // Vérifier que le projet est valide

            // Calcul de la charge : chaque jour représente un jour de travail
            LocalDate dateActuelle = dateDebut;
            while (!dateActuelle.isAfter(dateFin)) {
                chargeParJour.put(dateActuelle, chargeParJour.getOrDefault(dateActuelle, 0) + 1);
                dateActuelle = dateActuelle.plusDays(1);
            }
        }

        return chargeParJour;
    }

    public void afficherCharge(Equipe equipe) throws SQLException {
        Map<LocalDate, Integer> chargeParJour = calculerChargeParJour(equipe);

        System.out.println("📊 Charge de travail de l'équipe : " + equipe.getNom());
        for (Map.Entry<LocalDate, Integer> entry : chargeParJour.entrySet()) {
            LocalDate date = entry.getKey();
            int joursTravail = entry.getValue();
            System.out.println("📅 " + date + " -> " + joursTravail + " jours de travail");
        }
    }
}
