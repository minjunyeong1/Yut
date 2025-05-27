package yutgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player and their pieces.
 */
public class Player {
	private final GameModel model;
    private String name;
    private List<Piece> pieces = new ArrayList<>();
    private List<YutThrowResult> yutHistory = new ArrayList<>();
    private boolean canAddResult = true;

    public Player(GameModel model,String name, int pieceCount, Cell startCell) {
        this.model = model;
    	this.name = name;
        for (int i = 0; i < pieceCount; i++) {
            Piece p = new Piece(this, startCell);
            pieces.add(p);
        }
    }
    public void addYutResult(YutThrowResult result) {
    	 if (!canAddResult) {
    	        return;
    	    }

    	    yutHistory.add(result);

    	    if (result != YutThrowResult.YUT && result != YutThrowResult.MO) {
    	        canAddResult = false;
    	    }
    }

    public GameModel getModel() {
        return model;
    }
    
    public void clearYutHistory() {
        yutHistory.clear();
    }

    public String getName() {
        return name;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public List<YutThrowResult> getYutHistory() {
        return yutHistory;
    }
    public boolean canAddMoreResults() {
        return canAddResult;
    }

    public void setCanAddResult(boolean value) {
        this.canAddResult = value;
    }

}
