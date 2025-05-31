package yutgame.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import yutgame.model.*;
import yutgame.view.*;

import java.util.List;

public class GameController {
    private final GameModel model;
    private final MainView view;
    private final YutThrowController yutThrowController;
    private boolean stillCanAdd = true;
    private boolean turnHadCapture = false;

    public GameController(GameModel model, MainView view) {
        this.model = model;
        this.view = view;

        this.yutThrowController = new YutThrowController(model);
        this.yutThrowController.setBackdoSkipCallback(this::nextTurn);

        setupEventHandlers();
        setupResultButtonHandler();

        updateTurnUI();
    }

    private void setupEventHandlers() {
        AbstractBoardView boardView = view.getBoardView();

        // 🔁 랜덤 던지기 버튼
        boardView.getThrowYutButton().setOnAction(e -> yutThrowController.throwYut());

        // 🔁 수동 윷 버튼들
        boardView.getYutChoiceButtons().forEach((name, btn) -> {
            YutThrowResult r = YutThrowResult.valueOf(name);
            btn.setOnAction(e -> yutThrowController.handleManualThrow(r));
        });

        // 윷 던지기 결과 처리
        yutThrowController.setYutThrowCallback(result -> {
            if (stillCanAdd) {
                view.getYutResultView().setResult(result.toString());
            }
            stillCanAdd = model.getCurrentPlayer().canAddResult();
        });
    }

    private void setupResultButtonHandler() {
        view.getBoardView().setResultSelectionListener(result -> {
            Piece selected = view.getBoardView().getSelectedPiece();
            if (selected == null) return;

            Piece moveTarget = selected.isLeader() ? selected : selected.getLeader();
            if (moveTarget == null || moveTarget.isFinished()) return;

            int steps = result.getValue();

            if (steps == -1 && moveTarget.getPosition().getId() == 0) {
                boolean allAtStart = model.getCurrentPlayer().getPieces().stream()
                        .allMatch(p -> p.getPosition() != null && p.getPosition().getId() == 0);
                if (allAtStart) {
                    model.getCurrentPlayer().getYutHistory().remove(result);
                    nextTurn();
                    return;
                }
            }

            if (steps == -1 && moveTarget.getPosition().getId() == 1) {
                Cell last = model.getBoard().getLastCell();
                moveTarget.getPosition().leave(moveTarget);
                moveTarget.setPosition(last);
                last.enter(moveTarget, true, true);
                model.getCurrentPlayer().getYutHistory().remove(result);
                view.getBoardView().removeResultButton(result);
                result = YutThrowResult.DO;
            }

            List<Piece> captured = new PieceMovementController().movePiece(moveTarget, result);

            if (!captured.isEmpty()) {
                turnHadCapture = true;
                stillCanAdd = true;
            }

            resetCapturedPieces(captured);

            model.getCurrentPlayer().getYutHistory().remove(result);
            view.getBoardView().removeResultButton(result);
            view.getBoardView().updatePieceIcons();

            if (model.isCurrentPlayerWinner()) {
            	String winner = model.getCurrentPlayer().getName();
                view.handleWinCondition(winner);
                return;
            }

            if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
                boolean yutMo = result == YutThrowResult.YUT || result == YutThrowResult.MO;
                boolean extraTurn = turnHadCapture || yutMo;
                if (extraTurn) {
                    model.getCurrentPlayer().setCanAddResult(true);
                } else {
                    nextTurn();
                }
                turnHadCapture = false;
                view.getBoardView().clearSelectedPiece();
            }
        });
    }

    public void resetCapturedPieces(List<Piece> captured) {
        Cell start = model.getBoard().getStartCell();
        for (Piece capturedPiece : captured) {
            for (Piece p : capturedPiece.detachGroup()) {
                p.setLeader(null);
                p.getPassengers().clear();
                p.setPosition(start);
                start.enter(p, false, false);
            }
        }
    }

    public void nextTurn() {
        Player current = model.getCurrentPlayer();
        current.clearYutHistory();
        current.setCanAddResult(true);
        view.getYutResultView().clearResults();

        int total = model.getPlayers().size();
        int next = (model.getCurrentPlayerIndex() + 1) % total;
        model.setCurrentPlayerIndex(next);
        updateTurnUI();
        view.getBoardView().clearSelectedPiece();
        view.getBoardView().updatePieceIcons();
        stillCanAdd = true;
    }

    private void updateTurnUI() {
        view.getTurnView().updateTurn(model.getCurrentPlayerIndex());
    }
}
