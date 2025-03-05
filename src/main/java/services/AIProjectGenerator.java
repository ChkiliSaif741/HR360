package services;

import entities.TacheStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import entities.Projet;
import entities.Tache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AIProjectGenerator {

    private static final String API_URL = "https://api.together.xyz/v1/chat/completions";  // Together AI API endpoint
    private static final String API_KEY = "e499094f92e24b0998442f639ba731339642e363798e4b2f96459c8c40725dc7";  // 🔹 Replace with your Together AI API key

    private List<Tache> tasks = new ArrayList<>();

    private String projectDescrip;

    public static String getAIResponse(String prompt) {
        try {
            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "meta-llama/Llama-3.3-70B-Instruct-Turbo");  // Correct model name
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            // Define messages array for chat completion
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);

            requestBody.put("messages", messages);

            // Log the request body
            System.out.println("Request Body: " + requestBody.toString());

            // Send API request
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Log the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Log the error response if the request fails
            if (responseCode != 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.err.println("Error Response: " + errorResponse.toString());
                }
                throw new IOException("Error calling Together AI API: " + responseCode);
            }

            // Read and log the success response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Success Response: " + response.toString());
                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void GenerateTasksAndProjectDescript(String projectName,LocalDate date1,LocalDate date2,int nbrTasks) {
        String prompt = "Generate a structured JSON project with "+nbrTasks+" tasks for a project named "+projectName
                +" with start date: "+date1.toString()+" and end date: "+date2.toString()+"\n"
                + "The JSON must follow this exact format without any extra text: "
                + "{ \"projectName\": \"Project Name\", "
                + "\"description\": \"Project Description\", "
                + "\"startDate\": \"YYYY-MM-DD\", "
                + "\"endDate\": \"YYYY-MM-DD\", "
                + "\"tasks\": [ { \"name\": \"Task Name\", \"description\": \"Task Description\", "
                + "\"startDate\": \"YYYY-MM-DD\", \"endDate\": \"YYYY-MM-DD\" } ] }"
                + " Ensure all task dates are within the project start and end dates.Allow dates overlapping"
                + "The task names should be meaningful and reflect the purpose of the task. "
                + "The task descriptions should be detailed and explain what the task involves. "
                + "For example, instead of 'Task 1', use 'Project Planning and Kickoff' as the task name, "
                + "and provide a detailed description like 'Define project scope, objectives, and deliverables. "
                + "Identify key stakeholders and set up initial project timelines.";

        String aiResponse = getAIResponse(prompt);
        System.out.println("AI Response: " + aiResponse);
        Projet projet=parseAIResponse(aiResponse);
        this.projectDescrip=projet.getDescription();
    }
    public static void main(String[] args) {
        /*
        String projectName="RH application";
        String date1="2025-02-25";
        String date2="2025-04-25";
        int nbrTasks=3;
        String prompt = "Generate a structured JSON project with "+nbrTasks+" tasks for a project named "+projectName
                +" with start date: "+date1+" and end date: "+date2+"\n"
                + "The JSON must follow this exact format without any extra text: "
                + "{ \"projectName\": \"Project Name\", "
                + "\"description\": \"Project Description\", "
                + "\"startDate\": \"YYYY-MM-DD\", "
                + "\"endDate\": \"YYYY-MM-DD\", "
                + "\"tasks\": [ { \"name\": \"Task Name\", \"description\": \"Task Description\", "
                + "\"startDate\": \"YYYY-MM-DD\", \"endDate\": \"YYYY-MM-DD\" } ] }"
                + " Ensure all task dates are within the project start and end dates.Allow dates overlapping"
                + "The task names should be meaningful and reflect the purpose of the task. "
                + "The task descriptions should be detailed and explain what the task involves. "
                + "For example, instead of 'Task 1', use 'Project Planning and Kickoff' as the task name, "
                + "and provide a detailed description like 'Define project scope, objectives, and deliverables. "
                + "Identify key stakeholders and set up initial project timelines.";

        String aiResponse = getAIResponse(prompt);
        System.out.println("AI Response: " + aiResponse);
        Projet projet=parseAIResponse(aiResponse);
        System.out.println(projet);*/
    }

    public  Projet parseAIResponse(String aiResponse) {
        try {
            if (aiResponse == null || aiResponse.isEmpty()) {
                throw new IllegalArgumentException("AI response is empty");
            }

            // Parse AI response JSON
            JSONObject responseObject = new JSONObject(aiResponse);
            JSONArray choices = responseObject.getJSONArray("choices");
            if (choices.length() == 0) {
                throw new IllegalArgumentException("No valid choices in AI response");
            }

            // Extract content and convert to JSON
            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
            JSONObject projectData = new JSONObject(content);

            // Extract project details
            String name = projectData.getString("projectName");
            String description = projectData.getString("description");
            LocalDate startDate = LocalDate.parse(projectData.getString("startDate"));
            LocalDate endDate = LocalDate.parse(projectData.getString("endDate"));

            // Convert to SQL Date
            Date sqlStartDate = Date.valueOf(startDate);
            Date sqlEndDate = Date.valueOf(endDate);

            // Create Project object
            Projet project = new Projet(name, description, sqlStartDate, sqlEndDate);

            // Extract tasks
            List<Tache> taskList = new ArrayList<>();
            JSONArray tasks = projectData.getJSONArray("tasks");
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject taskObj = tasks.getJSONObject(i);

                String taskName = taskObj.getString("name");
                String taskDesc = taskObj.getString("description");
                LocalDate taskStartDate = LocalDate.parse(taskObj.getString("startDate"));
                LocalDate taskEndDate = LocalDate.parse(taskObj.getString("endDate"));

                // Ensure tasks are within project dates
                if (taskStartDate.isBefore(startDate) || taskEndDate.isAfter(endDate)) {
                    System.err.println("❌ Task " + taskName + " has dates outside project range!");
                    continue;
                }

                // Convert to SQL Date
                Date sqlTaskStart = Date.valueOf(taskStartDate);
                Date sqlTaskEnd = Date.valueOf(taskEndDate);

                // Create Task object
                Tache task = new Tache(taskName, taskDesc, sqlTaskStart, sqlTaskEnd, TacheStatus.A_FAIRE, project.getId());
                taskList.add(task);
            }

            // Print tasks
            for (Tache task : taskList) {
                System.out.println(task);
            }
            this.tasks.clear();
            this.tasks.addAll(taskList);
            return project;
        } catch (Exception e) {
            System.err.println("Error parsing AI response: " + e.getMessage());
            return null;
        }
    }

    public List<Tache> getTasks() {
        return tasks;
    }

    public String getProjectDescrip() {
        return projectDescrip;
    }
}