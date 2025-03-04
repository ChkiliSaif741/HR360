package services;

import com.google.zxing.WriterException;
import entities.Ressource;
import utils.QRCodeGenerator;

import java.io.IOException;

public class QRCodeService {

    public static String generateQRCodeForRessource(Ressource ressource) {
        String data = "ID: " + ressource.getId() + "\n" +
                "Nom: " + ressource.getNom() + "\n" +
                "Type: " + ressource.getType() + "\n" +
                "État: " + ressource.getEtat() + "\n" +
                "Prix: " + ressource.getPrix();

        try {
            return QRCodeGenerator.generateQRCode(data, "ressource_" + ressource.getId());
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
