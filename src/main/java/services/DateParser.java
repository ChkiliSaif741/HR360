package services;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateParser {

    public static LocalDate parseDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            System.err.println("❌ Error: Empty date string.");
            return null;
        }

        // Remove unwanted characters
        String cleanDate = rawDate.replaceAll("[^a-zA-Z0-9, ]", "").trim(); // Removes special characters
        cleanDate = cleanDate.replace("Start Date", "").replace("End Date", "").trim(); // Remove labels

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        try {
            return LocalDate.parse(cleanDate, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("❌ Error parsing date: " + cleanDate);
            return null;
        }
    }

}


