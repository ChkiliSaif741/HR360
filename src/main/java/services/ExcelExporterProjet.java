package services;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.VerticalAlign;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporterProjet {

    public void exportToExcel(Projet projet, List<Tache> taches, Stage stage) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tasks");

        // 🔹 Style for project name row (Blue Background)
        CellStyle projectStyle = workbook.createCellStyle();
        projectStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        projectStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        projectStyle.setAlignment(HorizontalAlignment.CENTER);

        // 🔹 Style for wrapped text in descriptions
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        // 🔹 Style for task status
        CellStyle redStyle = workbook.createCellStyle();
        redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        redStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle orangeStyle = workbook.createCellStyle();
        orangeStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        orangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        orangeStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle greenStyle = workbook.createCellStyle();
        greenStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        greenStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 🔹 First row: Project name (merged across columns)
        Row projectRow = sheet.createRow(0);
        Cell projectCell = projectRow.createCell(0);
        projectCell.setCellValue(projet.getNom());
        projectCell.setCellStyle(projectStyle);

        // Merge first row across all columns
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        // 🔹 Headers
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("Nom");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Date Début");
        headerRow.createCell(3).setCellValue("Date Fin");
        headerRow.createCell(4).setCellValue("Statut");

        CellStyle cellcenter=workbook.createCellStyle();
        cellcenter.setVerticalAlignment(VerticalAlignment.CENTER);

        // 🔹 Fill Task Data
        int rowNum = 2;
        for (Tache tache : taches) {
            Row row = sheet.createRow(rowNum++);
            Cell TaskCell=row.createCell(0);
            TaskCell.setCellValue(tache.getNom());
            TaskCell.setCellStyle(cellcenter);

            // 📌 Set description with auto-wrap
            Cell descCell = row.createCell(1);
            descCell.setCellValue(insertLineBreaks(tache.getDescription(), 50));
            descCell.setCellStyle(wrapStyle);

            Cell DDcell=row.createCell(2);
            DDcell.setCellValue(tache.getDateDebut().toString());
            DDcell.setCellStyle(cellcenter);

            Cell DFcell=row.createCell(3);
            DFcell.setCellValue(tache.getDateFin().toString());
            DFcell.setCellStyle(cellcenter);

            // 📌 Set Status Color
            Cell statusCell = row.createCell(4);
            statusCell.setCellValue(tache.getStatut().getValue());

            if (tache.getStatut() == TacheStatus.A_FAIRE) {
                statusCell.setCellStyle(redStyle);
            } else if (tache.getStatut() == TacheStatus.EN_COURS) {
                statusCell.setCellStyle(orangeStyle);
            } else if (tache.getStatut() == TacheStatus.TERMINEE) {
                statusCell.setCellStyle(greenStyle);
            }
        }

        // 🔹 Auto-size columns
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }

        // 🔹 Ask user where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName(projet.getNom() + ".xlsx");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                System.out.println("Excel file saved: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String insertLineBreaks(String text, int maxCharsPerLine) {
        StringBuilder formattedText = new StringBuilder();
        int count = 0;

        for (char c : text.toCharArray()) {
            formattedText.append(c);
            count++;

            // Insert a line break every maxCharsPerLine
            if (count >= maxCharsPerLine && c == ' ') {
                formattedText.append("\n");
                count = 0;
            }
        }
        return formattedText.toString();
    }

}

