package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ExportExcel {

    public static void exporterTauxOccupation(Map<String, Double> tauxOccupation, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Taux d'Occupation");

        // Créer l'en-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Ressource");
        headerRow.createCell(1).setCellValue("Taux d'Occupation (%)");

        // Remplir les données
        int rowNum = 1;
        for (Map.Entry<String, Double> entry : tauxOccupation.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        // Écrire le fichier
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}