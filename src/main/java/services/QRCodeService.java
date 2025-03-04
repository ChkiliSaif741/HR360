package services;

import com.google.zxing.WriterException;
import entities.Ressource;
import utils.QRCodeGenerator;

import java.io.IOException;
import java.util.List;

public class QRCodeService {

    private static final ServiceReservation reservationService = new ServiceReservation();

    public static String generateQRCodeForRessource(Ressource ressource) {
        List<String> unavailableDates = reservationService.getUnavailableDatesForRessource(ressource.getId());
        StringBuilder datesString = new StringBuilder();

        if (unavailableDates.isEmpty()) {
            datesString.append("✅ Aucune date indisponible.");
        } else {
            for (String date : unavailableDates) {
                datesString.append("📌 ").append(date).append("\n");
            }
        }

        String data = "📌 RESSOURCE RÉSERVÉE 📌\n" +
                "----------------------\n" +
                "🆔 ID : " + ressource.getId() + "\n" +
                "📛 Nom : " + ressource.getNom() + "\n" +
                "📂 Type : " + ressource.getType() + "\n" +
                "📌 État : " + ressource.getEtat() + "\n" +
                "💰 Prix : " + ressource.getPrix() + " DT\n" +
                "----------------------\n" +
                "📅 DATES INDISPONIBLES :\n" + datesString +
                "----------------------\n" +
                "✅ Réservez à l'avance !";

        try {
            return QRCodeGenerator.generateQRCode(data, "ressource_" + ressource.getId());
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
