package yutgame.controller;

import yutgame.model.GameModel;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;
import yutgame.view.AbstractBoardView;
import yutgame.view.YutResultView;

import javax.swing.*;

import java.util.Map;
import java.util.Random;

/**
 * 윷 던지기 버튼 클릭을 처리하는 컨트롤러
 */
public class YutThrowController {
    private final JButton throwYutButton;
    private final YutResultView resultView;
    private final GameModel model;
    private final GameController gameController;
    private final AbstractBoardView boardView; 
    private final Random random;

    public YutThrowController(
            JButton throwYutButton,
            YutResultView resultView,
            GameModel model,
            GameController gameController,
            AbstractBoardView boardView) {
        this.throwYutButton = throwYutButton;
        this.resultView = resultView;
        this.model = model;
        this.gameController = gameController;
        this.boardView = boardView;
        this.random = new Random();
        setup();
    }

    private void setup() {
        // 윷 던지기 버튼: 랜덤 처리
        throwYutButton.addActionListener(e -> {
            YutThrowResult result = throwYut();
            System.out.println("던진 결과: " + result);
            handleManualThrow(result);  // 공통 로직으로 정리
        });

        // 수동 버튼들 처리
        Map<String, JButton> yutButtons = boardView.getYutChoiceButtons();

        for (Map.Entry<String, JButton> entry : yutButtons.entrySet()) {
            String resultKey = entry.getKey();  // "DO", "YUT", ...
            JButton btn = entry.getValue();

            // 내부 이벤트 변수명은 evt 등으로 변경
            btn.addActionListener(evt -> {
                YutThrowResult result = YutThrowResult.valueOf(resultKey);
                handleManualThrow(result);
            });
        }
    }

    private YutThrowResult throwYut() {
        // 랜덤 던지기
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
        System.out.println("수동 선택 결과: " + result);
        resultView.setResult(result.toString());

        Player currentPlayer = model.getCurrentPlayer();
        currentPlayer.addYutResult(result);

        if (result != YutThrowResult.YUT && result != YutThrowResult.MO) {
        	// 말 움직이는 함수 
            gameController.nextTurn();
        }
    }

}
