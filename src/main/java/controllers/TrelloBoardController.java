package controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TrelloBoardController {
    @FXML
    private WebView webView;

    private String boardid; // Replace with actual Trello board ID
    private static final String API_KEY = "6a7939bb3380aa7016622f79c58cde16";
    private static final String TOKEN = "ATTAd882eb46ac7c80931df7f18f18b551431470e527c82fce94673498351e59116b1A7DD9EE";

    public void loadBoard() {
        WebEngine webEngine = webView.getEngine();
        String boardUrl = "https://trello.com/b/" + boardid;

        // Append API key and token if the board is private
        if (TOKEN != null && !TOKEN.isEmpty()) {
            boardUrl += "?key=" + API_KEY + "&token=" + TOKEN;
        }
        webEngine.load(boardUrl);
    }

    public void setBoardId(String boardid) {
        this.boardid = boardid;
        loadBoard();
    }
}
