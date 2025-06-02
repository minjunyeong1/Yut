package yutgame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            players.add(new Player(this, "P" + i, config.getPiecesPerPlayer(), start));
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

    // ───── 승리 판정 관련 메서드 ─────────────────────

    /**
     * 현재 플레이어가 모든 말을 도착시켰는지 확인
     */
    public boolean isCurrentPlayerWinner() {
        return getCurrentPlayer().getPieces().stream()
                .allMatch(Piece::isFinished);
    }

    /**
     * 전체 플레이어 중 승자 반환 (있을 경우)
     */
    public Optional<Player> getWinner() {
        return players.stream()
                .filter(p -> p.getPieces().stream().allMatch(Piece::isFinished))
                .findFirst();
    }
    
    public Cell getLastCell() {
        return board.getLastCell();
    }

    // 현재 플레이어의 완주한 말의 개수
    public long getFinishedPieceCountofCurrentPlayer() {
    	return getCurrentPlayer().getFinishedPieceCount();
    }
}