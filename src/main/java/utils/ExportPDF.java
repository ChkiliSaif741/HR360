package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ExportPDF {

    public static void exporterTauxOccupation(Map<String, Double> tauxOccupation, String filePath) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Ajouter un titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("Tableau de Bord - Taux d'Occupation des Ressources", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Créer un tableau avec 2 colonnes
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Ajouter les en-têtes de colonnes
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell cell1 = new PdfPCell(new Phrase("Ressource", headerFont));
        cell1.setBackgroundColor(BaseColor.BLUE);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Taux d'Occupation (%)", headerFont));
        cell2.setBackgroundColor(BaseColor.BLUE);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        // Ajouter les données
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        for (Map.Entry<String, Double> entry : tauxOccupation.entrySet()) {
            PdfPCell cell = new PdfPCell(new Phrase(entry.getKey(), dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(entry.getValue().toString() + "%", dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        document.add(table);
        document.close();
    }
}