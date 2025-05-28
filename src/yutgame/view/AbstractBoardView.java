package yutgame.view;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

    public AbstractBoardView(GameConfig config, GameModel model, GameController controller) {
        this.config = config;
        this.model = model;
        this.gameController = controller;

        setPrefSize(windowSizeX, windowSizeY);
        setStyle("-fx-background-color: white;");
    }

    protected abstract void buildBoard();

    public void initialize() {
        buildBoard();
        addCommonButtons();
    }

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

    protected void addCommonButtons() {
        throwYutButton = new Button("윷 던지기");
        throwYutButton.setLayoutX(windowSizeX - 180);
        throwYutButton.setLayoutY(50);
        throwYutButton.setPrefSize(120, 40);
        getChildren().add(throwYutButton);

        addPlayerIcons();

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

        int pieceCount = config != null ? config.getPiecesPerPlayer() : 4;
        int pieceButtonStartY = 600;
        int pieceButtonHeight = 35;

        for (int i = 0; i < pieceCount; i++) {
            Button pieceBtn = new Button((i + 1) + "번 말");
            pieceBtn.setLayoutX(windowSizeX / 2 - 200 + i * 90);
            pieceBtn.setLayoutY(pieceButtonStartY);
            pieceBtn.setPrefSize(80, pieceButtonHeight);

            final int pieceIndex = i;
            pieceBtn.setOnAction(e -> {
                Player p = model.getPlayers().get(model.getCurrentPlayerIndex());
                selectedPiece = p.getPieces().get(pieceIndex);

                if (selectedPiece.isFinished()) {
                    selectedPiece = null;
                    updatePieceIcons();
                    return;
                }

                List<YutThrowResult> filteredResults = new ArrayList<>(p.getYutHistory());

                if (selectedPiece.getPosition() != null &&
                        selectedPiece.getPosition().getId() == 0) {
                    filteredResults.removeIf(r -> r.getValue() == -1); // 빽도 제거
                }

                showResultButtons(filteredResults);
                updatePieceIcons();
            });

            getChildren().add(pieceBtn);
        }
    }

    public void setResultSelectionListener(Consumer<YutThrowResult> listener) {
        this.resultSelectionListener = listener;
    }

    public void showResultButtons(List<YutThrowResult> results) {
        int startX = windowSizeX / 2 - (results.size() * 60) / 2;
        int y = 500;

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

    public void updatePieceIcons() {
        // override in subclass if using Canvas
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void clearSelectedPiece() {
        selectedPiece = null;
    }

    public Button getThrowYutButton() {
        return throwYutButton;
    }

    public Button getDeployPieceButton() {
        return deployPieceButton;
    }

    public Map<String, Button> getYutChoiceButtons() {
        return yutChoiceButtons;
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }
}
