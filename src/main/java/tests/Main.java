package tests;

import entities.Ressource;
import entities.Reservation;
import services.ServiceRessource;
import services.ServiceReservation;
import utils.MyDatabase;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*
        MyDatabase.getInstance();

        ServiceRessource serviceRessource = new ServiceRessource();
        ServiceReservation serviceReservation = new ServiceReservation();
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.println("\n=== MENU GESTION RESSOURCES & RESERVATIONS ===");
                System.out.println("1. Ajouter une Ressource");
                System.out.println("2. Lister les Ressources");
                System.out.println("3. Modifier une Ressource");
                System.out.println("4. Supprimer une Réservation");
                System.out.println("5. Ajouter une Réservation");
                System.out.println("6. Lister les Réservation avec Ressources");
                System.out.println("7. Modifier une Réservation");
                System.out.println("8. Supprimer une Réservation");
                System.out.println("9. Quitter");
                System.out.print("Choix : ");

                int choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1:
                        System.out.print("Nom du Ressource : ");
                        String nom = scanner.nextLine();
                        System.out.print("Type du Ressource : ");
                        String type = scanner.nextLine();
                        System.out.print("Etat du Ressource : ");
                        String etat = scanner.nextLine();
                        System.out.print("Propriétaire du Ressource : ");
                        String utilisateur = scanner.nextLine();

                        Ressource ressource = new Ressource(nom, type, etat, utilisateur);
                        serviceRessource.ajouter(ressource);
                        System.out.println("Ressource ajoutée !");
                        break;

                    case 2:
                        List<Ressource> ressources = serviceRessource.afficher();
                        for (Ressource r : ressources) {
                            System.out.println(r);
                        }
                        break;


                    case 3:
                        System.out.print("ID de la ressource à modifier : ");
                        int idRessource = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Nom du Ressource : ");
                        String nomModif = scanner.nextLine();
                        System.out.print("Type du Ressource : ");
                        String typeModif = scanner.nextLine();
                        System.out.print("Etat du Ressource : ");
                        String etatModif = scanner.nextLine();
                        System.out.print("Propriétaire du Ressource : ");
                        String utilisateurModif = scanner.nextLine();

                        Ressource ressourceModif = new Ressource(idRessource, nomModif, typeModif, etatModif, utilisateurModif);
                        serviceRessource.modifier(ressourceModif);
                        break;


                    case 4:
                        System.out.print("ID de la ressource à supprimer : ");
                        idRessource = scanner.nextInt();
                        serviceRessource.supprimer(idRessource);
                        break;


                    case 5:
                        System.out.print("ID Ressource : ");
                        int idRes = scanner.nextInt();
                        System.out.print("Date Début (YYYY-MM-DD) : ");
                        Date dateDebut = Date.valueOf(scanner.next());
                        System.out.print("Date Fin (YYYY-MM-DD) : ");
                        Date dateFin = Date.valueOf(scanner.next());
                        scanner.nextLine();
                        System.out.print("Utilisateur : ");
                        String userRes = scanner.nextLine();

                        Reservation reservation = new Reservation(idRes, dateDebut, dateFin, userRes);
                        serviceReservation.ajouter(reservation);
                        System.out.println("Réservation ajoutée !");
                        break;


                    case 6:
                        List<Reservation> reservations = serviceReservation.afficher();
                        System.out.println("\n=== Liste des Réservations avec Ressources ===");
                        for (Reservation r : reservations) {
                            System.out.println(" Réservation ID: " + r.getId());
                            System.out.println("    Date Début: " + r.getDateDebut());
                            System.out.println("    Date Fin: " + r.getDateFin());
                            System.out.println("    Réservée par: " + r.getUtilisateur());
                            System.out.println("    Ressource: " + r.getRessource().getNom() + " (ID: " + r.getRessource().getId() + ")");
                            System.out.println("    Type: " + r.getRessource().getType());
                            System.out.println("    État: " + r.getRessource().getEtat());
                            System.out.println("    Propriétaire: " + r.getRessource().getUtilisateur());
                            System.out.println("--------------------------------------------------");
                        }
                        break;

                    case 7:
                        System.out.print("ID de la réservation à modifier : ");
                        int idResv = scanner.nextInt();
                        scanner.nextLine(); // Pour consommer le retour à la ligne

                        if (idResv > 0) {
                            System.out.print("Date Début (YYYY-MM-DD) : ");
                            Date newDateDebut = Date.valueOf(scanner.next());
                            System.out.print("Date Fin (YYYY-MM-DD) : ");
                            Date newDateFin = Date.valueOf(scanner.next());
                            scanner.nextLine();  // Consommer le retour à la ligne
                            System.out.print("Utilisateur : ");
                            String newUtilisateur = scanner.nextLine();

                            Reservation reservationM = new Reservation(idResv, 0, newDateDebut, newDateFin, newUtilisateur);
                            serviceReservation.modifier(reservationM);
                            System.out.println(" Réservation modifiée !");
                        } else {
                            System.out.println(" ID invalide !");
                        }
                        break;




                    case 8:
                        System.out.print("ID de la réservation à supprimer : ");
                        idResv = scanner.nextInt();
                        serviceReservation.supprimer(idResv);
                        break;


                    case 9:
                        System.out.println(" Au revoir !");
                        scanner.close();
                        System.exit(0);

                    default:
                        System.out.println(" Choix invalide !");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

         */
    }
}
