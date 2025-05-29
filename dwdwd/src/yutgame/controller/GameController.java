package yutgame.controller;

import yutgame.model.Cell;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;
import yutgame.view.*;

import javax.swing.*;
import java.util.List;

public class GameController {
    private final GameModel model;
    private final MainView view;

    public GameController(GameModel model, MainView view) {
        this.model = model;
        this.view = view;
        setupEventHandlers();
        setupResultButtonHandler();  // ✅ 윷 버튼 처리 콜백 등록
        updateTurnUI();              // 게임 시작 시 턴 표시 초기화
    }

    /** 버튼 이벤트 핸들러 등록 */
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

    /** 결과 버튼 클릭 시 동작 정의 (Controller가 처리) */
    private void setupResultButtonHandler() {
        view.getBoardView().setResultSelectionListener(result -> {
            Piece selected = view.getBoardView().getSelectedPiece();
            if (selected == null) return;

            Piece moveTarget = selected.isLeader() ? selected : selected.getLeader();
            if (moveTarget == null) return;

            int steps = result.getValue();

         // 1. 빽도인데 모든 말이 0번 셀에 있으면 → 턴 넘김
            if (steps == -1 && moveTarget.getPosition().getId() == 0) {
                boolean allAtStartCell = model.getCurrentPlayer().getPieces().stream()
                    .allMatch(p -> p.getPosition() != null && p.getPosition().getId() == 0);

                if (allAtStartCell) {
                    model.getCurrentPlayer().getYutHistory().remove(result);
                    nextTurn();
                    return;
                }
            }

            // 2. 시작 셀(1번)에서 빽도일 경우 → 마지막 셀로 이동
            if (steps == -1 && moveTarget.getPosition() != null && moveTarget.getPosition().getId() == 1) {
                Cell last = model.getBoard().getLastCell();  // Board 구현체에 getLastCell() 필요
                moveTarget.getPosition().leave(moveTarget);
                moveTarget.setPosition(last);
                last.enter(moveTarget, true, true);
                model.getCurrentPlayer().getYutHistory().remove(result);
                view.getBoardView().updatePieceIcons();
                view.getBoardView().clearSelectedPiece();

                // 턴 종료 처리
                if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
                    nextTurn();
                }
                return;
            }

            // 기본 말 이동
            List<Piece> captured = new PieceMovementController().movePiece(moveTarget, result);

            // 추가 턴 판정
            boolean tookPiece = !captured.isEmpty();
            // boolean yutMo = (result == YutThrowResult.YUT || result == YutThrowResult.MO);
            boolean extraTurn = /* yutMo || */ tookPiece;

            if (extraTurn) {
            		model.getCurrentPlayer().setCanAddResult(true);
            }
            else if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
            	nextTurn();
            }

            // 말 잡기
            resetCapturedPieces(captured);

            // 결과 제거 및 UI 갱신
            model.getCurrentPlayer().getYutHistory().remove(result);
            view.getBoardView().removeResultButton(result);
            view.getBoardView().updatePieceIcons();

            // 승리 판정
            if (model.isCurrentPlayerWinner()) {
                view.handleWinCondition();
                return;
            }

            // 남은 윷 결과 없으면 턴 종료
            if (!extraTurn && model.getCurrentPlayer().getYutHistory().isEmpty()) {
                nextTurn();
                view.getBoardView().clearSelectedPiece();
            }
        });
    }

    /** 잡힌 말 복귀 처리 */
    public void resetCapturedPieces(List<Piece> captured) {
        Cell startCell = model.getBoard().getStartCell();

        for (Piece capturedPiece : captured) {
            for (Piece grouped : capturedPiece.detachGroup()) {
                grouped.setLeader(null);
                grouped.getPassengers().clear();
                grouped.setPosition(startCell);
                startCell.enter(grouped, false, false);
            }
        }
    }

    /** 현재 턴을 다음 플레이어로 변경 */
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
        int index = model.getCurrentPlayerIndex();
        view.getTurnView().updateTurn(index);
    }
}