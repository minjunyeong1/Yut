package yutgame.controller;

import yutgame.model.Cell;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;
import yutgame.view.*;

import java.util.List;

public class GameController {
    private final GameModel model;
    private final MainView view;
    private final YutThrowController yutThrowController;
    boolean stillCanAdd = true; // 텍스트 로그 중복 방지용 플래그
    private boolean turnHadCapture = false; // 이번 턴 중 말 잡은 적 있는지 여부

    public GameController(GameModel model, MainView view) {
        this.model = model;
        this.view  = view;

        /*  먼저 “순수” 윷 컨트롤러를 만든다 */
        this.yutThrowController = new YutThrowController(model);
        
        //  여기서 백도 스킵 턴 콜백 연결
        this.yutThrowController.setBackdoSkipCallback(this::nextTurn);

        /* 그리고 UI-배선을 건다 */
        setupEventHandlers();
        setupResultButtonHandler();

        updateTurnUI();
    }

    /** 버튼 이벤트 핸들러 등록 */
    private void setupEventHandlers() {
        AbstractBoardView boardView = view.getBoardView();

        // ── 1) 랜덤 던지기 버튼 ─────────────────────────
        boardView.getThrowYutButton()
                 .addActionListener(e -> yutThrowController.throwYut());

        // ── 2) 수동 선택 버튼들(도·개·걸·윷·모·빽도) ───────
        boardView.getYutChoiceButtons().forEach((name, btn) -> {
            YutThrowResult r = YutThrowResult.valueOf(name);
            btn.addActionListener(e -> yutThrowController.handleManualThrow(r));
        });

        // ── 3) 로직 → UI 콜백(결과 버튼 & 텍스트) ───────────
        yutThrowController.setYutThrowCallback(result -> {
            if (stillCanAdd) {
                view.getYutResultView().setResult(result.toString());
            }
            stillCanAdd = model.getCurrentPlayer().canAddResult();
        });
    }

    /** 결과 버튼 클릭 시 동작 정의 (Controller가 처리) */
    private void setupResultButtonHandler() {
        view.getBoardView().setResultSelectionListener(result -> {
            Piece selected = view.getBoardView().getSelectedPiece();
            if (selected == null) return;

            Piece moveTarget = selected.isLeader() ? selected : selected.getLeader();
            if (moveTarget == null || moveTarget.isFinished()) return;

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

            // 2. 시작 셀(1번)에서 빽도일 경우 → 마지막 셀로 이동 (이동만 수행하고 흐름은 그대로 유지)
            if (steps == -1 && moveTarget.getPosition() != null && moveTarget.getPosition().getId() == 1) {
                Cell last = model.getBoard().getLastCell();
                moveTarget.getPosition().leave(moveTarget);
                moveTarget.setPosition(last);
                last.enter(moveTarget, true, true);
                model.getCurrentPlayer().getYutHistory().remove(result);
                view.getBoardView().removeResultButton(result);
                result = YutThrowResult.DO;
            }
            
            // 기본 말 이동
            List<Piece> captured = new PieceMovementController().movePiece(moveTarget, result);

            // 3. 말 잡기 발생하면 플래그 설정
            if (!captured.isEmpty()) {
                turnHadCapture = true;
                stillCanAdd = true;
            }

            // 잡힌 말 처리 (시작점으로 복귀)
            resetCapturedPieces(captured);

            // 결과 제거 및 UI 갱신
            model.getCurrentPlayer().getYutHistory().remove(result);
            view.getBoardView().removeResultButton(result);
            view.getBoardView().updatePieceIcons();

            // 4. 승리 판정
            if (model.isCurrentPlayerWinner()) {
                String winner = model.getCurrentPlayer().getName();
                view.handleWinCondition(winner);  // ✅ model은 Controller에서만 접근
                return;
            }

            // 5. 남은 윷 결과 없으면 턴 종료 판단
            if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
                boolean yutMo = model.getCurrentPlayer().getlastisYutMo();
                boolean extraTurn = turnHadCapture || yutMo; // ✅ 잡았거나 윷/모인 경우 추가 턴

                if (extraTurn) {
                    model.getCurrentPlayer().setCanAddResult(true);
                } else {
                    nextTurn();
                }
                turnHadCapture = false; // 턴 종료 시 초기화
                view.getBoardView().clearSelectedPiece();
            }
            
            Player current = model.getCurrentPlayer();
            List<YutThrowResult> history = current.getYutHistory();

            boolean hasOnlyBackdo = history.size() == 1 && history.get(0).getValue() == -1;
            boolean allAtStartOrFinished = current.getPieces().stream()
            	    .allMatch(p ->
            	        p.getPosition() == null || p.getPosition().getId() == 0
            	    );
            boolean yutMo = current.getlastisYutMo();
            boolean extraTurn = turnHadCapture || yutMo;

            if (hasOnlyBackdo && allAtStartOrFinished && !extraTurn) {
                current.clearYutHistory();
                view.getBoardView().removeResultButton(YutThrowResult.BACKDO);
                view.getBoardView().clearSelectedPiece();
                nextTurn();
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
        view.getBoardView().clearSelectedPiece();  // 말 선택 해제
        view.getBoardView().updatePieceIcons();    // 말 UI 갱신
        stillCanAdd = true;
    }

    /** 턴 UI 업데이트 */
    private void updateTurnUI() {
        int index = model.getCurrentPlayerIndex();
        view.getTurnView().updateTurn(index);
    }
}
