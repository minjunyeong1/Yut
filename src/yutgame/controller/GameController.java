package yutgame.controller;

import yutgame.model.*;
import yutgame.view.*;
import java.util.List;
import java.util.Optional;

public class GameController {

    @SuppressWarnings("unused")
    private final GameConfig config;
    private final GameModel model;
    private MainView view;

    public GameController(GameConfig config, GameModel model) {
        this.config = config;
        this.model = model;
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

    /** 승리 알림 및 재시작/종료 옵션 */
    private void showVictoryAlert(String winnerName) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + "님이 승리했습니다!\n재시작 혹은 종료를 선택하세요.");

        javafx.scene.control.ButtonType restart = new javafx.scene.control.ButtonType("재시작");
        javafx.scene.control.ButtonType exit    = new javafx.scene.control.ButtonType(
            "종료", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(restart, exit);

        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restart) {
            // 현재 게임 창 닫고 SettingView로 복귀
            javafx.stage.Stage stage = (javafx.stage.Stage)
                view.getBoardView().getScene().getWindow();
            stage.close();
            new SettingController();
        } else {
            // 애플리케이션 종료
            javafx.application.Platform.exit();
        }
    }
}
