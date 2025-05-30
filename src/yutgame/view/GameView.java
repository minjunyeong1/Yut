package yutgame.view;

public interface GameView {
    void updateTurn(int turnIndex);
    void showYutResult(String text);
    void clearResults();
    void showVictory(String playerName);
    void refreshBoard();
    void showGameEndOptions(String winnerName);
}
