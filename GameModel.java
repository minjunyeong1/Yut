package yutgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages game state: players, board, turn progression.
 */
public class GameModel {
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;

    public GameModel(GameConfig config) {
        this.board = new Board(config.getBoardShape());
        this.players = new ArrayList<>();
        Cell start = board.getStartCell();
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
