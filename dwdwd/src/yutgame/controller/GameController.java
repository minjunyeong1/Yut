package yutgame.controller;

import yutgame.model.Cell;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.view.*;

import java.util.List;

import javax.swing.*;

public class GameController {
    private final GameModel model;
    private final MainView view;

    public GameController(GameModel model, MainView view) {
        this.model = model;
        this.view = view;
        setupEventHandlers();
        updateTurnUI();  // 게임 시작 시 턴 표시 초기화
    }

    /** 버튼 이벤트 핸들러 등록 */
    private void setupEventHandlers() {
        AbstractBoardView boardView = view.getBoardView();

        // 턴 넘기기 버튼
        JButton nextTurnButton = boardView.getNextTurnButton();
        if (nextTurnButton != null) {
            nextTurnButton.addActionListener(e -> nextTurn());
        }

        JButton throwBtn = view.getBoardView().getThrowYutButton();
        YutResultView resultView = view.getYutResultView();


        new YutThrowController(
            view.getBoardView().getThrowYutButton(),
            view.getYutResultView(),
            model,
            this,
            view.getBoardView()
        );
    }

    public void resetCapturedPieces(List<Piece> captured) {
        Cell startCell = model.getBoard().getStartCell();

        for (Piece capturedPiece : captured) {
            for (Piece grouped : capturedPiece.detachGroup()) {
                grouped.setLeader(null);
                grouped.getPassengers().clear();
                
                System.out.println(">> 말 복귀 전: 위치 = " + grouped.getPosition());
                
                grouped.setPosition(startCell);
                startCell.enter(grouped, false, false);  // 업기·잡기 비활성화                
                System.out.println("복귀 대상 말: " + grouped);
                System.out.println("말의 현재 위치 ID: " + grouped.getPosition().getId());
            }
        }
    }



    /** 현재 턴을 다음 플레이어로 변경 */
    public void nextTurn() {
        // 이전 플레이어 기록 초기화
        Player previous = model.getCurrentPlayer();
        previous.clearYutHistory();
        previous.setCanAddResult(true);
        view.getYutResultView().clearResults(); // 결과창 초기화

        // 턴 전환
        int totalPlayers = model.getPlayers().size();
        int next = (model.getCurrentPlayerIndex() + 1) % totalPlayers;
        model.setCurrentPlayerIndex(next);
        updateTurnUI();
    }


    /** 턴 표시 UI 업데이트 */
    private void updateTurnUI() {
        int index = model.getCurrentPlayerIndex();
        view.getTurnView().updateTurn(index);
    }

}
