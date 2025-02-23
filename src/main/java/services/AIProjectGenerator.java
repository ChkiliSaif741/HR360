package services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import entities.Projet;
import entities.Tache;
import entities.TacheStatus;

public class AIProjectGenerator {
    private static final String API_KEY = "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"; // Replace with your API Key
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) {
        // Example: Creating a project
        Projet projet = new Projet("Website Redesign", "", Date.valueOf("2025-03-01"), Date.valueOf("2025-03-30"));

        // Generate AI project details with tasks
        Projet generatedProject = generateProjectDetails(projet);

        if (generatedProject != null) {
            System.out.println(generatedProject);
        }
    }

    public static Projet generateProjectDetails(Projet projet) {
        int retries = 3;
        int waitTime = 2000;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                Thread.sleep(1000);

                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // AI Prompt with project start and end dates
                String prompt = "Generate a project description for '" + projet.getNom() + "' with start date " +
                        projet.getDateDebut() + " and end date " + projet.getDateFin() + ". "
                        + "Then provide a list of tasks, each with a name, description, start date, and end date. "
                        + "Ensure that all tasks start and end within the project's time frame.";

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("model", "gpt-4o-mini");
                jsonRequest.put("temperature", 0.7);
                jsonRequest.put("max_tokens", 800);

                JSONArray messages = new JSONArray();
                messages.put(new JSONObject().put("role", "system").put("content", "You are an AI assistant specializing in project management."));
                messages.put(new JSONObject().put("role", "user").put("content", prompt));

                jsonRequest.put("messages", messages);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        return parseAIResponse(response.toString(), projet);
                    }
                } else if (statusCode == 429) {
                    System.out.println("API rate limit exceeded. Retrying in " + (waitTime / 1000) + " seconds...");
                    Thread.sleep(waitTime);
                    waitTime *= 2;
                } else {
                    System.out.println("Error: API request failed with status code " + statusCode);
                    System.out.println("Response Message: " + connection.getResponseMessage());
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Projet parseAIResponse(String jsonResponse, Projet projet) {
        JSONObject responseObject = new JSONObject(jsonResponse);
        JSONArray choices = responseObject.getJSONArray("choices");

        if (choices.length() > 0) {
            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
            System.out.println(content);
            // Extract project description and tasks from AI response
            String[] sections = content.split("\n\n");
            String projectDescription = cleanProjectDescription(sections[2]);
            projet.setDescription(projectDescription);

            List<Tache> tasks = new ArrayList<>();
            for (int i = 1; i < sections.length; i++) {
                String[] taskDetails = sections[i].split("\n");
                if (taskDetails.length >= 4) {
                    String taskName = cleanTaskName(taskDetails[0]);  // Clean task name
                    String taskDescription = cleanTaskDescription(taskDetails[1]);
                    LocalDate startDate = DateParser.parseDate(taskDetails[2]);
                    LocalDate endDate = DateParser.parseDate(taskDetails[3]);
                    if (startDate == null || endDate == null) {
                        System.err.println("❌ Skipping task due to invalid date.");
                        return null;
                    }
                        // Ensure task dates are within project dates
                        LocalDate projectStart = projet.getDateDebut().toLocalDate();
                        LocalDate projectEnd = projet.getDateFin().toLocalDate();

                        if (startDate.isBefore(projectStart)) startDate = projectStart;
                        if (endDate.isAfter(projectEnd)) endDate = projectEnd;

                        tasks.add(new Tache(taskName, taskDescription, Date.valueOf(startDate), Date.valueOf(endDate), TacheStatus.EN_COURS, projet.getId()));
                    }
                }

                projet.setDescription(projectDescription);
                System.out.println("\n=== AI Generated Project Details ===");
                System.out.println(projet);

                for (Tache tache : tasks) {
                    System.out.println(tache);
                }

                return projet;
            }
            return null;
        }
    public static String cleanTaskName(String rawName) {
        if (rawName == null) return "";

        // Remove numbers at the beginning (e.g., "1. ", "2. ", etc.)
        rawName = rawName.replaceAll("^\\d+\\.\\s*", "");

        // Remove "**Task Name:**" and any markdown-style "**"
        rawName = rawName.replace("**Task Name:**", "").replace("**", "").trim();

        return rawName;
    }

    public static String cleanTaskDescription(String rawDescription) {
        if (rawDescription == null) return "";

        // Remove "**Description:**" and markdown-style "**"
        rawDescription = rawDescription.replace("**Description:**", "").replace("**", "").trim();

        return rawDescription;
    }

    public static String cleanProjectDescription(String rawDescription) {
        if (rawDescription == null) return "";

        rawDescription = rawDescription.replace("**Project Overview:**  \n", "").replace("**", "").trim();

        return rawDescription;
    }

}
