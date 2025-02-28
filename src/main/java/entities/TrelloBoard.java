package entities;

public class TrelloBoard {
    private int idTache;
    private String boardId;

    public TrelloBoard() {}

    public TrelloBoard(int idTache, String boardId) {
        this.idTache = idTache;
        this.boardId = boardId;
    }

    public int getIdTache() { return idTache; }
    public void setIdTache(int idTache) { this.idTache = idTache; }

    public String getBoardId() { return boardId; }
    public void setBoardId(String boardId) { this.boardId = boardId; }
}
