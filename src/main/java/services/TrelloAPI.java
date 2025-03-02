package services;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class TrelloAPI {
    private static final String API_KEY = "6a7939bb3380aa7016622f79c58cde16";
    private static final String API_TOKEN = "ATTAd882eb46ac7c80931df7f18f18b551431470e527c82fce94673498351e59116b1A7DD9EE";
    private static final String TRELLO_URL = "https://api.trello.com/1";

    public static String createBoardWithLists(String boardName, LocalDate dateDebut, LocalDate dateFin) {
        // Step 1: Create the Trello Board
        String boardId = createBoard(boardName);
        if (boardId == null) {
            System.out.println("Board creation failed!");
            return null;
        }

        // Step 2: Generate lists for each day in the range
        LocalDate currentDate = dateDebut;
        while (currentDate.isAfter(dateFin)) {
            String formattedDate = formatDate(currentDate);
            createList(boardId, formattedDate);
            currentDate = currentDate.minusDays(1);
        }

        return boardId; // Return the board ID if everything is successful
    }

    private static String createBoard(String boardName) {
        String url = TRELLO_URL + "/boards/";
        HttpResponse<JsonNode> response = Unirest.post(url)
                .queryString("name", boardName)
                .queryString("key", API_KEY)
                .queryString("token", API_TOKEN)
                .queryString("defaultLists", "false") // No default lists
                .queryString("prefs_permissionLevel", "public") // Make board public
                .asJson();

        if (response.getStatus() == 200) {
            return response.getBody().getObject().getString("id");
        } else {
            System.out.println("Error creating Trello board: " + response.getBody());
            return null;
        }
    }

    private static void createList(String boardId, String listName) {
        String url = TRELLO_URL + "/lists";
        HttpResponse<JsonNode> response = Unirest.post(url)
                .queryString("name", listName)
                .queryString("idBoard", boardId)
                .queryString("key", API_KEY)
                .queryString("token", API_TOKEN)
                .asJson();

        if (response.getStatus() != 200) {
            System.out.println("Error creating list: " + response.getBody());
        }
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE – dd/MM/yyyy", Locale.FRENCH);
        return "🗓️ " + date.format(formatter);
    }

    public static void getUserInfo() {
        Preferences prefs = Preferences.userNodeForPackage(TrelloAPI.class);
        String token = prefs.get("trello_token", null);

        if (token != null) {
            HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/members/me")
                    .queryString("key", API_KEY)
                    .queryString("token", token)
                    .asJson();

            System.out.println(response.getBody().toString());
        } else {
            System.out.println("User not authenticated.");
        }
    }

    public static void addMembersToBoard(String boardId, List<String> memberEmails) {
        for (String email : memberEmails) {
            String url = TRELLO_URL + "/boards/" + boardId + "/members";
            Unirest.put(url)
                    .queryString("email", email)
                    .queryString("key", API_KEY)
                    .queryString("token", API_TOKEN)
                    .asJson();
        }
    }

    public static boolean deleteBoard(String boardId) {
        String url = TRELLO_URL + "/boards/" + boardId;
        HttpResponse<JsonNode> response = Unirest.delete(url)
                .queryString("key", API_KEY)
                .queryString("token", API_TOKEN)
                .asJson();

        if (response.getStatus() == 200) {
            System.out.println("Board deleted successfully: " + boardId);
            return true;
        } else {
            System.out.println("Error deleting Trello board: " + response.getBody());
            return false;
        }
    }

    public static boolean isBoardStillOnTrello(String boardId) {
        String url = TRELLO_URL + "/boards/" + boardId;
        HttpResponse<JsonNode> response = Unirest.get(url)
                .queryString("key", API_KEY)
                .queryString("token", API_TOKEN)
                .asJson();

        int statusCode = response.getStatus();
        System.out.println("🔍 Checking board: " + boardId + " | Status: " + statusCode);
        System.out.println("📢 API Raw Response: " + response.getBody());

        // ✅ If API returns 404, the board is deleted
        if (statusCode == 404) {
            System.out.println("✅ Board " + boardId + " was deleted!");
            return false;
        }

        // ❌ Fix: Do NOT assume 'null' = deleted, only delete if 404!
        if (statusCode == 200) {
            System.out.println("✅ Board " + boardId + " still exists.");
            return true;
        }

        // 🟡 Handle unexpected errors (e.g., Trello server issues)
        System.out.println("⚠️ Unexpected response from Trello API: " + statusCode);
        return true; // Assume board still exists if Trello is unclear
    }


}
