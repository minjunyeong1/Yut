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
    private boolean canAddResult = true;

    public Player(String name, int pieceCount, Cell startCell) {
        this.name = name;
        for (int i = 0; i < pieceCount; i++) {
            Piece p = new Piece(this, startCell);
            pieces.add(p);
        }
    }
    public void addYutResult(YutThrowResult result) {
    	 if (!canAddResult) {
    	        System.out.println("❌ 더 이상 결과를 추가할 수 없습니다.");
    	        return;
    	    }

    	    yutHistory.add(result);

    	    // 윷 or 모가 아니면 더 이상 추가 못 하게
    	    if (result != YutThrowResult.YUT && result != YutThrowResult.MO) {
    	        canAddResult = false;
    	    }
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
