package yutgame.view;

import javax.swing.*;

import yutgame.controller.GameController;
import yutgame.model.GameConfig;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base class for all board views (rectangle, pentagon, hexagon).
 */
public abstract class AbstractBoardView extends JPanel {
    protected int windowSizeX = 1000;
    protected int windowSizeY = 700;
    protected GameConfig config;
    protected GameModel model;
    protected GameController gameController;

    protected Piece selectedPiece = null;
    protected Map<String, JButton> resultButtons = new HashMap<>();
    private Consumer<YutThrowResult> resultSelectionListener;
    private final JLabel[] pieceStatusLabels = new JLabel[4];

    protected JButton throwYutButton;
    protected JButton deployPieceButton;
    protected JButton nextTurnButton;
    protected Map<String, JButton> yutChoiceButtons = new HashMap<>();

    public AbstractBoardView(GameConfig config, GameModel model, GameController controller) {
        this.config = config;
        this.model = model;
        this.gameController = controller;

        setPreferredSize(new Dimension(windowSizeX, windowSizeY));
        setLayout(null);
        setBackground(Color.WHITE);
    }

    /** Build board layout */
    protected abstract void buildBoard();

    @Override
    public void addNotify() {
        super.addNotify();
        buildBoard();
        addCommonButtons();
        addPlayerIcons();
    }

    public void initPieceStatusLabelsView(int playerCount, int piecesPerPlayer) {
        for (int i = 0; i < 4; i++) {
            if (pieceStatusLabels[i] == null) {
                pieceStatusLabels[i] = new JLabel();
                pieceStatusLabels[i].setBounds(20, 500 + i * 25, 100, 20);
                pieceStatusLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
                this.add(pieceStatusLabels[i]);
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
            pieceStatusLabels[playerIndex].setText("P" + (playerIndex + 1) + " " + finishedPieceCount + "/" + piecesPerPlayer);
        }
    }
    
    private void addPlayerIcons() {
        String[] colors = { "blue", "green", "red", "yellow" };
        int startX = 500;
        int spacing = 60;

        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            ImageIcon icon = new ImageIcon(getClass().getResource("/yutgame/img/big" + color + ".jpg"));
            JLabel label = new JLabel(icon);
            label.setBounds(startX + i * spacing, 10, icon.getIconWidth(), icon.getIconHeight());
            add(label);
        }
    }

    /** 공통 버튼 추가 */
    protected void addCommonButtons() {
        throwYutButton = new JButton("윷 던지기");
        throwYutButton.setBounds(windowSizeX - 180, 50, 120, 40);
        add(throwYutButton);

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
            JButton yutBtn = new JButton(labels[i]);
            yutBtn.setBounds(yutStartX + i * (buttonWidth + spacing), yutY, buttonWidth, buttonHeight);
            yutBtn.setActionCommand(results[i].name());
            add(yutBtn);
            yutChoiceButtons.put(results[i].name(), yutBtn);
        }

        int pieceButtonStartY = 600;
        int pieceButtonHeight = 35;
        int pieceCount = (config != null) ? config.getPiecesPerPlayer() : 4;

        for (int i = 0; i < pieceCount; i++) {
            JButton pieceBtn = new JButton((i + 1) + "번 말");
            pieceBtn.setBounds(windowSizeX / 2 - 200 + i * 90, pieceButtonStartY, 80, pieceButtonHeight);
            int pieceIndex = i;
            pieceBtn.addActionListener(e -> {
                Player p = model.getPlayers().get(model.getCurrentPlayerIndex());
                selectedPiece = p.getPieces().get(pieceIndex);

                if (selectedPiece.isFinished()) {
                    selectedPiece = null;
                    updatePieceIcons();
                    return;
                }

                // 출발 전 상태일 경우 빽도 제외
                List<YutThrowResult> filteredResults = new ArrayList<>(p.getYutHistory());

                if (selectedPiece.getPosition() != null &&
                    selectedPiece.getPosition().getId() == 0) {
                    filteredResults.removeIf(r -> r.getValue() == -1); // 빽도 제거
                }

                showResultButtons(filteredResults); // 필터링된 결과만 보여줌
                updatePieceIcons();
            });

            add(pieceBtn);
        }
    }

    /** 윷 결과 버튼 클릭 리스너 등록 */
    public void setResultSelectionListener(Consumer<YutThrowResult> listener) {
        this.resultSelectionListener = listener;
    }

    /** 윷 결과 버튼 출력 */
    public void showResultButtons(List<YutThrowResult> results) {
        int startX = windowSizeX / 2 - (results.size() * 60) / 2;
        int y = 500;

        // 기존 버튼 제거
        for (JButton btn : resultButtons.values()) {
            remove(btn);
        }
        resultButtons.clear();

        for (int i = 0; i < results.size(); i++) {
            YutThrowResult result = results.get(i);  // 고유 참조
            String key = result.name() + "_" + i;    // 고유 키: 예) "MO_0", "MO_1"

            JButton btn = new JButton(result.toString());
            btn.setBounds(startX, y, 60, 35);
            startX += 70;

            btn.addActionListener(e -> {
                if (resultSelectionListener != null) {
                    resultSelectionListener.accept(result);
                }
            });

            add(btn);
            resultButtons.put(key, btn);
        }

        repaint();
    }


    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void clearSelectedPiece() {
        selectedPiece = null;
    }

    public void updatePieceIcons() {
        repaint();
    }

    public void removeResultButton(YutThrowResult result) {
        Iterator<Map.Entry<String, JButton>> it = resultButtons.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JButton> entry = it.next();
            if (entry.getValue().getText().equals(result.toString())) {
                remove(entry.getValue());
                it.remove();
                revalidate();
                repaint();
                break; 
            }
        }
    }


    public JButton getThrowYutButton() {
        return throwYutButton;
    }

    public JButton getDeployPieceButton() {
        return deployPieceButton;
    }


    public Map<String, JButton> getYutChoiceButtons() {
        return yutChoiceButtons;
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }
}
