package yutgame.view;

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
        this.config         = config;
        this.model          = model;
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
            Image icon = new Image(getClass().getResource("/yutgame/img/big" + colors[i] + ".jpg").toExternalForm());
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
        YutThrowResult[] results = { YutThrowResult.BACKDO, YutThrowResult.DO, YutThrowResult.GAE,
                                     YutThrowResult.GEO, YutThrowResult.YUT, YutThrowResult.MO };

        int buttonWidth = 60, buttonHeight = 35, spacingBtn = 10;
        int totalWidth = labels.length * buttonWidth + (labels.length - 1) * spacingBtn;
        int yutStartX = (windowSizeX - totalWidth) / 2;
        int yutY = 650;
        for (int i = 0; i < labels.length; i++) {
            Button b = new Button(labels[i]);
            b.setLayoutX(yutStartX + i * (buttonWidth + spacingBtn));
            b.setLayoutY(yutY);
            b.setPrefSize(buttonWidth, buttonHeight);
            yutChoiceButtons.put(results[i].name(), b);
            getChildren().add(b);
        }

        int pieceCount = config != null ? config.getPiecesPerPlayer() : 4;
        int pieceBtnY = 600;
        for (int i = 0; i < pieceCount; i++) {
            Button pBtn = new Button((i+1) + "번 말");
            pBtn.setLayoutX(windowSizeX/2 -200 + i*90);
            pBtn.setLayoutY(pieceBtnY);
            pBtn.setPrefSize(80, 35);
            final int idx = i;
            pBtn.setOnAction(e -> {
                Player p = model.getPlayers().get(model.getCurrentPlayerIndex());
                selectedPiece = p.getPieces().get(idx);
                if (selectedPiece.isFinished()) {
                    selectedPiece = null;
                    updatePieceIcons();
                    return;
                }
                List<YutThrowResult> filtered = new ArrayList<>(p.getYutHistory());
                if (selectedPiece.getPosition()!=null && selectedPiece.getPosition().getId()==0) {
                    filtered.removeIf(r->r.getValue()==-1);
                }
                showResultButtons(filtered);
                updatePieceIcons();
            });
            getChildren().add(pBtn);
        }
    }

    public void setResultSelectionListener(Consumer<YutThrowResult> listener) {
        this.resultSelectionListener = listener;
    }

    public void showResultButtons(List<YutThrowResult> results) {
        int startX = windowSizeX/2 - (results.size()*60)/2, y=500;
        resultButtons.values().forEach(this.getChildren()::remove);
        resultButtons.clear();
        for (int i=0; i<results.size(); i++) {
            YutThrowResult r = results.get(i);
            Button b = new Button(r.toString());
            b.setLayoutX(startX);
            b.setLayoutY(y);
            b.setPrefSize(60,35);
            startX += 70;
            b.setOnAction(e-> { if (resultSelectionListener!=null) resultSelectionListener.accept(r); });
            getChildren().add(b);
            resultButtons.put(r.name()+"_"+i,b);
        }
    }

    public void removeResultButton(YutThrowResult result) {
        for (Iterator<Map.Entry<String,Button>> it = resultButtons.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String,Button> en = it.next();
            if (en.getValue().getText().equals(result.toString())) {
                getChildren().remove(en.getValue()); it.remove(); break;
            }
        }
    }

    public void updatePieceIcons() {}
    public Piece getSelectedPiece() { return selectedPiece; }
    public void clearSelectedPiece() { selectedPiece=null; }
    public Button getThrowYutButton() { return throwYutButton; }
    public Map<String,Button> getYutChoiceButtons() { return yutChoiceButtons; }
    public void setGameController(GameController c) { this.gameController=c; }
}
