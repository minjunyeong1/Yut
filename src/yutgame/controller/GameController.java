package yutgame.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import yutgame.model.*;
import yutgame.view.*;

import java.util.List;

public class GameController {

    private final GameConfig config;
    private final GameModel model;
    private MainView view;

    public GameController(GameConfig config, GameModel model) {
        this.config = config;
        this.model = model;
        // View는 나중에 setView()로 주입
    }

    /** View 연결 및 이벤트 핸들러 초기화 */
    public void setView(MainView view) {
        this.view = view;
        setupEventHandlers();
        setupResultButtonHandler();
        updateTurnUI();
    }

    /** 윷 던지기 등 버튼 이벤트 핸들러 연결 */
    private void setupEventHandlers() {
        AbstractBoardView boardView = view.getBoardView();

        
        new YutThrowController(
            boardView.getThrowYutButton(),
            view.getYutResultView(),
            model,
            this,
            boardView
        );
    }

    /** 윷 결과 선택 처리 */
    private void setupResultButtonHandler() {
        view.getBoardView().setResultSelectionListener(result -> {
            Piece selected = view.getBoardView().getSelectedPiece();
            if (selected == null) return;

            Piece moveTarget = selected.isLeader() ? selected : selected.getLeader();
            if (moveTarget == null) return;

            int steps = result.getValue();

            // 빽도인데 모든 말이 시작 셀에 있는 경우 → 턴 넘김
            if (steps == -1 && moveTarget.getPosition().getId() == 0) {
                boolean allAtStartCell = model.getCurrentPlayer().getPieces().stream()
                    .allMatch(p -> p.getPosition() != null && p.getPosition().getId() == 0);

                if (allAtStartCell) {
                    model.getCurrentPlayer().getYutHistory().remove(result);
                    nextTurn();
                    return;
                }
            }

            // 1번 셀에서 빽도 → 마지막 셀로 이동
            if (steps == -1 && moveTarget.getPosition().getId() == 1) {
                Cell last = model.getBoard().getLastCell();
                moveTarget.getPosition().leave(moveTarget);
                moveTarget.setPosition(last);
                last.enter(moveTarget, true, true);
                model.getCurrentPlayer().getYutHistory().remove(result);
                view.getBoardView().updatePieceIcons();
                view.getBoardView().clearSelectedPiece();

                if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
                    nextTurn();
                }
                return;
            }

            // 일반 이동 처리
            List<Piece> captured = new PieceMovementController().movePiece(moveTarget, result);
            resetCapturedPieces(captured);

            model.getCurrentPlayer().getYutHistory().remove(result);
            view.getBoardView().removeResultButton(result);
            view.getBoardView().updatePieceIcons();

            if (model.isCurrentPlayerWinner()) {
                showVictoryAlert(model.getCurrentPlayer().getName());
                return;
            }

            if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
                nextTurn();
                view.getBoardView().clearSelectedPiece();
            }
        });
    }

    /** 잡힌 말 복귀 처리 */
    public void resetCapturedPieces(List<Piece> captured) {
        Cell start = model.getBoard().getStartCell();
        for (Piece capturedPiece : captured) {
            for (Piece grouped : capturedPiece.detachGroup()) {
                grouped.setLeader(null);
                grouped.getPassengers().clear();
                grouped.setPosition(start);
                start.enter(grouped, false, false);
            }
        }
    }

    /** 턴 넘기기 */
    public void nextTurn() {
        Player previous = model.getCurrentPlayer();
        previous.clearYutHistory();
        previous.setCanAddResult(true);
        view.getYutResultView().clearResults();

        int totalPlayers = model.getPlayers().size();
        int next = (model.getCurrentPlayerIndex() + 1) % totalPlayers;
        model.setCurrentPlayerIndex(next);

        updateTurnUI();
        view.getBoardView().clearSelectedPiece();
        view.getBoardView().updatePieceIcons();
    }

    /** 턴 UI 업데이트 */
    private void updateTurnUI() {
        int currentIndex = model.getCurrentPlayerIndex();
        view.getTurnView().updateTurn(currentIndex);
    }

    /** 승리 알림 */
    private void showVictoryAlert(String winnerName) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + "님이 승리했습니다!");
        alert.showAndWait();
    }
}
