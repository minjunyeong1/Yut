package yutgame.model;

/**
 * Holds game configuration such as number of players, pieces per player, and board shape.
 */
public class GameConfig {
    private int numPlayers;
    private int piecesPerPlayer;
    private Board.Shape boardShape;

    public GameConfig(int numPlayers, int piecesPerPlayer, Board.Shape boardShape) {
        this.numPlayers = numPlayers;
        this.piecesPerPlayer = piecesPerPlayer;
        this.boardShape = boardShape;
    }

    public int getNumPlayers() { return numPlayers; }
    public int getPiecesPerPlayer() { return piecesPerPlayer; }
    public Board.Shape getBoardShape() { return boardShape; }
}
