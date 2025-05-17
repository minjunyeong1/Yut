package yutgame.model;

import java.util.ArrayList;
import java.util.List;
import yutgame.model.RectangleBoard;
import yutgame.model.PentagonBoard;
import yutgame.model.HexagonBoard;

/**
 * Manages game state: players, board, turn progression.
 */
public class GameModel {
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;

    public GameModel(GameConfig config) {
        // 보드 생성
    	this.board = switch (config.getBoardShape()) {
        case RECTANGLE -> new RectangleBoard();
        case PENTAGON  -> new PentagonBoard();
        case HEXAGON   -> new HexagonBoard();
    };
        this.players = new ArrayList<>();
        
        Cell start = board.getStartCell();  // 시작 위치 셀 가져오기

        for (int i = 0; i < config.getNumPlayers(); i++) {
            players.add(new Player("P" + i, config.getPiecesPerPlayer(), start));
        }

        this.currentPlayerIndex = 0;
    }


    // ───── 게임 정보 접근자 ─────────────────────
    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int index) {
            this.currentPlayerIndex = index;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}
