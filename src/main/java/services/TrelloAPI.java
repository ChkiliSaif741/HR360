package services;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.List;

public class TrelloAPI {
    private static final String API_KEY = "6a7939bb3380aa7016622f79c58cde16";
    private static final String API_TOKEN = "ATTAd882eb46ac7c80931df7f18f18b551431470e527c82fce94673498351e59116b1A7DD9EE";
    private static final String TRELLO_URL = "https://api.trello.com/1";

    public static String createBoard(String boardName) {
        String url = TRELLO_URL + "/boards/";
        HttpResponse<JsonNode> response = Unirest.post(url)
                .queryString("name", boardName)
                .queryString("key", API_KEY)
                .queryString("token", API_TOKEN)
                .queryString("defaultLists", "false") // No default lists
                .asJson();

        if (response.getStatus() == 200) {
            return response.getBody().getObject().getString("id");
        } else {
            System.out.println("Error creating Trello board: " + response.getBody());
            return null;
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
}
