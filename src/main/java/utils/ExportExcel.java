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

        // Créer une police pour l'en-tête
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        // Créer un style pour l'en-tête
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        // Créer l'en-tête
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Ressource");
        headerCell.setCellStyle(headerCellStyle);

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Taux d'Occupation (%)");
        headerCell.setCellStyle(headerCellStyle);

        // Remplir les données
        int rowNum = 1;
        for (Map.Entry<String, Double> entry : tauxOccupation.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        // Ajuster la largeur des colonnes
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Écrire le fichier
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}