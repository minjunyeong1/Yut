package yutgame.controller;

import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;
import yutgame.view.AbstractBoardView;
import yutgame.view.YutResultView;

import javax.swing.*;

import java.util.List;
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
                System.out.println("수동 선택 결과: " + result);
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
        Player currentPlayer = model.getCurrentPlayer();

        // ➤ 윷/모가 아니라면 더 이상 추가 불가한지 확인
        if (!currentPlayer.canAddMoreResults()) {
            System.out.println("❌ 현재 플레이어는 추가 던지기를 할 수 없습니다.");
            return;
        }

        // 결과 UI에 출력
        resultView.setResult(result.toString());

        // 결과 저장
        currentPlayer.addYutResult(result);

        // 디버그 로그
        System.out.println("▶ 현재 플레이어 윷 결과 저장됨: " + currentPlayer.getYutHistory());
    }

}
