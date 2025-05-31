package yutgame.controller;

import yutgame.model.GameModel;
import yutgame.model.YutThrowResult;

import java.util.Random;
import java.util.function.Consumer;

/**
 * 윷 던지기 동작을 처리하는 컨트롤러
 */
public class YutThrowController {
    private final GameModel model;
    private final Random random;
    private Consumer<YutThrowResult> yutThrowCallback; // 윷 던지기 결과 처리 콜백
    private Runnable backdoSkipCallback;
    
    public YutThrowController(GameModel model) {
        this.model = model;
        this.random = new Random();
    }

    /**
     * 윷 던지기 버튼 클릭 시 호출될 콜백 함수를 설정
     *callback 윷 던지기 결과를 처리할 콜백 함수
     */
    public void setYutThrowCallback(Consumer<YutThrowResult> callback) {
        this.yutThrowCallback = callback;
    }
    
    public void setBackdoSkipCallback(Runnable callback) {
        this.backdoSkipCallback = callback;
    }

    /**
     * 윷을 던지는 동작을 수행하고, 결과를 콜백 함수를 통해 알림
     */
    public void throwYut() {
        YutThrowResult result = getRandomYutResult();
        model.getCurrentPlayer().addYutResult(result); // Model 업데이트
        if (yutThrowCallback != null) {
            yutThrowCallback.accept(result); // View에 결과 전달
        }
        boolean hasOnlyBackdo = model.getCurrentPlayer().getYutHistory().size() == 1 &&
                model.getCurrentPlayer().getYutHistory().get(0).getValue() == -1;

        boolean allAtStartCell = model.getCurrentPlayer().getPieces().stream()
                .allMatch(p -> p.getPosition() != null && p.getPosition().getId() == 0);

        if (hasOnlyBackdo && allAtStartCell) {
            	model.getCurrentPlayer().getYutHistory().remove(result);
                if (backdoSkipCallback != null) backdoSkipCallback.run();  //  턴 넘김 요청
            }
    }

    /**
     * 랜덤하게 윷 결과를 생성
     */
    private YutThrowResult getRandomYutResult() {
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
        model.getCurrentPlayer().addYutResult(result);

        if (yutThrowCallback != null) {
            yutThrowCallback.accept(result);
        }

        boolean hasOnlyBackdo = model.getCurrentPlayer().getYutHistory().size() == 1 &&
            model.getCurrentPlayer().getYutHistory().get(0).getValue() == -1;

        boolean allAtStartCell = model.getCurrentPlayer().getPieces().stream()
            .allMatch(p -> p.getPosition() != null && p.getPosition().getId() == 0);

        if (hasOnlyBackdo && allAtStartCell) {
        	model.getCurrentPlayer().getYutHistory().remove(result);
            if (backdoSkipCallback != null) backdoSkipCallback.run(); 
        }
    }
}