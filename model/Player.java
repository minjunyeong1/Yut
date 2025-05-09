package yutgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player and their pieces.
 */
public class Player {
    private String name;
    private List<Piece> pieces;
    private List<YutThrowResult> yutHistory = new ArrayList<>();
    
    public Player(String name, int pieceCount, Cell startCell) {
        this.name = name;
        this.pieces = new ArrayList<>();
        for (int i = 0; i < pieceCount; i++) {
            pieces.add(new Piece(this, startCell));
        }
    }
    public void addYutResult(YutThrowResult result) {
        yutHistory.add(result);
    }

    public List<YutThrowResult> getYutHistory() {
        return yutHistory;
    }
    public String getName() { return name; }
    public List<Piece> getPieces() { return pieces; }
	public void clearYutHistory() {
	    yutHistory.clear();
		
	}
}
