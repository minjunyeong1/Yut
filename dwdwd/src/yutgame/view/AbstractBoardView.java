package yutgame.view;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import yutgame.controller.GameController;
import yutgame.model.*;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractBoardView extends Pane {

    protected int windowSizeX = 1000;
    protected int windowSizeY = 700;

    protected GameConfig config;
    protected GameModel model;
    protected GameController gameController;

    protected Piece selectedPiece = null;
    protected Map<String, Button> resultButtons = new HashMap<>();
    protected Map<String, Button> yutChoiceButtons = new HashMap<>();

    protected Consumer<YutThrowResult> resultSelectionListener;

    protected Button throwYutButton;
    protected Button deployPieceButton;
    protected Button nextTurnButton;
    
    private final Label[] pieceStatusLabels = new Label[4];
    
    public AbstractBoardView(GameConfig config, GameModel model, GameController controller) {
        this.config = config;
        this.model = model;
        this.gameController = controller;

        // 보드 전체 크기(가로 1000, 세로 700) 설정
        setPrefSize(windowSizeX, windowSizeY);
        setStyle("-fx-background-color: white;");
    }

    protected abstract void buildBoard();

    public void initialize() {
        buildBoard();
        addCommonButtons();
    }

    

    public void initPieceStatusLabelsView(int playerCount, int piecesPerPlayer) {
        for (int i = 0; i < 4; i++) {
            if (pieceStatusLabels[i] == null) {
                pieceStatusLabels[i] = new Label();
                pieceStatusLabels[i].setLayoutX(20);              // X 위치 고정
                pieceStatusLabels[i].setLayoutY(500 + i * 25);    // 아래로 일정 간격
                pieceStatusLabels[i].setFont(Font.font("Arial", 14));
                this.getChildren().add(pieceStatusLabels[i]);     // this는 Pane
            }

            if (i < playerCount) {
                pieceStatusLabels[i].setText("P" + (i + 1) + " 0/" + piecesPerPlayer);
            } else {
                pieceStatusLabels[i].setText("P" + (i + 1) + " 0/0");
            }
        }
    }
    
    public void showFinishedPieceCount(int playerIndex, long finishedPieceCount, int piecesPerPlayer) {
        if (playerIndex >= 0 && playerIndex < pieceStatusLabels.length) {
            pieceStatusLabels[playerIndex].setText(
                "P" + (playerIndex + 1) + " " + finishedPieceCount + "/" + piecesPerPlayer
            );
        }
    }
    
   
    /**
     * 플레이어 아이콘(원 모양) 4개를 보드 상단에 추가하는 메서드
     */
    protected void addPlayerIcons() {
        String[] colors = { "blue", "green", "red", "yellow" };
        int startX = 500;
        int spacing = 60;

        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            Image icon = new Image(getClass().getResource("/yutgame/img/big" + color + ".jpg").toExternalForm());
            ImageView iconView = new ImageView(icon);
            iconView.setLayoutX(startX + i * spacing);
            iconView.setLayoutY(10);
            getChildren().add(iconView);
        }
    }

    /**
     * 공통 버튼(랜덤 윷 던지기 / 수동 윷 결과 / 말 선택 버튼)을 모두 보드 위에 올리는 메서드
     */
    protected void addCommonButtons() {
        // ── 1) “랜덤 윷 던지기” 버튼 ────────────────────────
        throwYutButton = new Button("랜덤 윷 던지기");
        throwYutButton.setLayoutX(windowSizeX - 200);
        throwYutButton.setLayoutY(windowSizeY - 50);
        throwYutButton.setPrefSize(120, 35);
        getChildren().add(throwYutButton);

        // ── 2) 플레이어 아이콘(원) 추가 ─────────────────────
        addPlayerIcons();

        // ── 3) 수동 윷 선택 버튼 (“빽도”, “도”, “개”, “걸”, “윷”, “모”) ────
        String[] labels = {"빽도", "도", "개", "걸", "윷", "모"};
        YutThrowResult[] results = {
            YutThrowResult.BACKDO, YutThrowResult.DO, YutThrowResult.GAE,
            YutThrowResult.GEO, YutThrowResult.YUT, YutThrowResult.MO
        };

        int buttonWidth = 60;
        int buttonHeight = 35;
        int spacing = 10;
        int totalWidth = labels.length * buttonWidth + (labels.length - 1) * spacing;

        int yutStartX = (windowSizeX - totalWidth) / 2;
        int yutY = 650;

        for (int i = 0; i < labels.length; i++) {
            Button yutBtn = new Button(labels[i]);
            yutBtn.setLayoutX(yutStartX + i * (buttonWidth + spacing));
            yutBtn.setLayoutY(yutY);
            yutBtn.setPrefSize(buttonWidth, buttonHeight);
            yutChoiceButtons.put(results[i].name(), yutBtn);
            getChildren().add(yutBtn);
        }

        // ── 4) 말 선택 버튼 (“1번 말”, “2번 말”, …) ─────────────────
        int pieceCount = config != null ? config.getPiecesPerPlayer() : 4;
        int pieceButtonStartY = 600;
        int pieceButtonHeight2 = 35;

        for (int i = 0; i < pieceCount; i++) {
            Button pieceBtn = new Button((i + 1) + "번 말");
            pieceBtn.setLayoutX(windowSizeX / 2 - 205 + i * 90);
            pieceBtn.setLayoutY(pieceButtonStartY);
            pieceBtn.setPrefSize(80, pieceButtonHeight2);

            final int pieceIndex = i;
            pieceBtn.setOnAction(e -> {
                Player p = model.getPlayers().get(model.getCurrentPlayerIndex());
                selectedPiece = p.getPieces().get(pieceIndex);

                // 이미 도착한 말이라면 선택 취소만 처리
                if (selectedPiece.isFinished()) {
                    selectedPiece = null;
                    updatePieceIcons();
                    return;
                }

                // 현재 플레이어의 윷 결과 목록을 복사해서, “빽도” 불가 조건 걸기
                List<YutThrowResult> filteredResults = new ArrayList<>(p.getYutHistory());
                if (selectedPiece.getPosition() != null
                        && selectedPiece.getPosition().getId() == 0) {
                    filteredResults.removeIf(r -> r.getValue() == -1);
                }

                showResultButtons(filteredResults);
                updatePieceIcons();
            });

            getChildren().add(pieceBtn);
        }
    }

    /**
     * 컨트롤러가 “수동 윷 결과 버튼(빽도/도/개/…)”을 클릭했을 때 호출할 리스너를 등록
     */
    public void setResultSelectionListener(Consumer<YutThrowResult> listener) {
        this.resultSelectionListener = listener;
    }

    /**
     * 현재 선택 가능한 윷 결과(빽도/도/개/걸/윷/모) 버튼을 보드 중앙 아래쪽에 표시
     */
    public void showResultButtons(List<YutThrowResult> results) {
        int startX = windowSizeX / 2 - (results.size() * 60) / 2;
        int y = 500;

        // 기존에 있던 버튼 삭제
        resultButtons.values().forEach(this.getChildren()::remove);
        resultButtons.clear();

        for (int i = 0; i < results.size(); i++) {
            YutThrowResult result = results.get(i);
            String key = result.name() + "_" + i;

            Button btn = new Button(result.toString());
            btn.setLayoutX(startX);
            btn.setLayoutY(y);
            btn.setPrefSize(60, 35);
            startX += 70;

            btn.setOnAction(e -> {
                if (resultSelectionListener != null) {
                    resultSelectionListener.accept(result);
                }
            });

            getChildren().add(btn);
            resultButtons.put(key, btn);
        }
    }

    /**
     * 이미 고른 “수동 윷 결과 버튼” 하나를 제거할 때 사용
     */
    public void removeResultButton(YutThrowResult result) {
        Iterator<Map.Entry<String, Button>> it = resultButtons.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Button> entry = it.next();
            if (entry.getValue().getText().equals(result.toString())) {
                getChildren().remove(entry.getValue());
                it.remove();
                break;
            }
        }
    }

    /**
     * 말 아이콘을 화면에 다시 그려야 할 때(이동 후 등) 서브클래스에서 Override하여 처리
     */
    public void updatePieceIcons() {
        // 서브클래스에서 override
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void clearSelectedPiece() {
        this.selectedPiece = null;
    }

    public Button getThrowYutButton() {
        return throwYutButton;
    }

    public Map<String, Button> getYutChoiceButtons() {
        return yutChoiceButtons;
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }
}
