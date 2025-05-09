package yutgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player and their pieces.
 */
public class Player {
    private String name;
    private List<Piece> pieces = new ArrayList<>();
    private List<YutThrowResult> yutHistory = new ArrayList<>();
    
    public Player(String name, int pieceCount, Cell startCell) {
        this.name = name;
        for (int i = 0; i < pieceCount; i++) {
            Piece p = new Piece(this, startCell);
            pieces.add(p);
        }
    }
    public void addYutResult(YutThrowResult result) { yutHistory.add(result); }
    public void clearYutHistory() { yutHistory.clear(); }
    public String getName() { return name; }
    public List<Piece> getPieces() { return pieces; }
    public List<YutThrowResult> getYutHistory() { return yutHistory; }
}
