package yutgame.controller;

import java.util.Map;
import java.util.Random;
import yutgame.model.GameModel;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;
import yutgame.view.AbstractBoardView;
import yutgame.view.YutResultView;

public class YutThrowController {

    private final javafx.scene.control.Button throwYutButton;
    private final YutResultView resultView;
    private final GameModel model;
    private final GameController gameController;
    private final AbstractBoardView boardView;
    private final Random random;

    public YutThrowController(
            javafx.scene.control.Button throwYutButton,
            YutResultView resultView,
            GameModel model,
            GameController gameController,
            AbstractBoardView boardView
    ) {
        this.throwYutButton = throwYutButton;
        this.resultView     = resultView;
        this.model          = model;
        this.gameController = gameController;
        this.boardView      = boardView;
        this.random         = new Random();
        setup();
    }

    @SuppressWarnings("unused")
    private void setup() {
        // 자동 윷 던지기 버튼
        throwYutButton.setOnAction(e -> {
            YutThrowResult result = throwYut();
            handleManualThrow(result);
        });

        // 수동 윷 결과 버튼들
        Map<String, javafx.scene.control.Button> yutButtons = boardView.getYutChoiceButtons();
        for (Map.Entry<String, javafx.scene.control.Button> entry : yutButtons.entrySet()) {
            String resultKey = entry.getKey();
            javafx.scene.control.Button btn = entry.getValue();
            btn.setOnAction(evt -> handleManualThrow(YutThrowResult.valueOf(resultKey)));
        }
    }

    /** 윷 결과 랜덤 생성 */
    private YutThrowResult throwYut() {
        int[] values = {-1, 1, 2, 3, 4, 5};
        int value = values[random.nextInt(values.length)];
        return switch (value) {
            case -1 -> YutThrowResult.BACKDO;
            case 1  -> YutThrowResult.DO;
            case 2  -> YutThrowResult.GAE;
            case 3  -> YutThrowResult.GEO;
            case 4  -> YutThrowResult.YUT;
            case 5  -> YutThrowResult.MO;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public void handleManualThrow(YutThrowResult result) {
        Player currentPlayer = model.getCurrentPlayer();

        // ➤ 윷/모가 아니라면 더 이상 추가 불가한지 확인
        if (!currentPlayer.canAddMoreResults()) {
            return;
        }

        // 결과 UI에 출력
        resultView.setResult(result.toString());

        // 결과 저장
        currentPlayer.addYutResult(result);

        boolean hasOnlyBackdo = currentPlayer.getYutHistory().size() == 1
            && currentPlayer.getYutHistory().get(0).getValue() == -1;
        boolean allAtStartCell = currentPlayer.getPieces().stream()
            .allMatch(p -> p.getPosition() != null && p.getPosition().getId() == 0);

        if (hasOnlyBackdo && allAtStartCell) {
            currentPlayer.getYutHistory().clear();
            boardView.clearSelectedPiece();
            boardView.updatePieceIcons();
            resultView.clearResults();
            gameController.nextTurn();
        }
    }
}
