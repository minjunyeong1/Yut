package yutgame.view;

import javax.swing.*;

import yutgame.controller.GameController;
import yutgame.controller.PieceMovementController;
import yutgame.model.GameConfig;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;
import yutgame.model.YutThrowResult;

import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
        
        addPlayerIcons();

        nextTurnButton = new JButton("턴 넘기기");
        nextTurnButton.setBounds(windowSizeX - 180, 170, 120, 40);
        add(nextTurnButton);

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
                int playerIndex = model.getCurrentPlayerIndex();
                Player player = model.getPlayers().get(playerIndex);
                selectedPiece = player.getPieces().get(pieceIndex);
                System.out.printf("%d번째 플레이어의 %d번째 말 선택\n", playerIndex, pieceIndex);
                showResultButtons(player.getYutHistory());
            });
            add(pieceBtn);
        }
    }


    public void showResultButtons(List<YutThrowResult> results) {
        System.out.println(">> showResultButtons() 호출됨");
        System.out.println(">> 전달된 결과 수: " + results.size());
        System.out.println(">> 결과 리스트: " + results);

        int startX = windowSizeX / 2 - (results.size() * 60) / 2;
        int y = 500;

        for (JButton btn : resultButtons.values()) {
            remove(btn);
        }
        resultButtons.clear();

        for (YutThrowResult result : results) {
            System.out.println("→ 버튼 생성: " + result.toString());

            JButton btn = new JButton(result.toString());
            btn.setBounds(startX, y, 60, 35);
            startX += 70;

            btn.addActionListener(e -> {
                if (selectedPiece != null) {
                    Piece moveTarget = selectedPiece.isLeader()
                        ? selectedPiece
                        : selectedPiece.getLeader();

                    if (moveTarget == null) return;

                    List<Piece> captured = new PieceMovementController().movePiece(moveTarget, result);
                    gameController.resetCapturedPieces(captured);

                    model.getCurrentPlayer().getYutHistory().remove(result);
                    remove(btn);
                    repaint();
                    revalidate();

                    updatePieceIcons();

                    // 승리 판정
                    boolean allFinished = model.getCurrentPlayer().getPieces().stream().allMatch(Piece::isFinished);
                    if (allFinished) {
                        JOptionPane.showMessageDialog(
                            null,
                            model.getCurrentPlayer().getName() + "님이 승리했습니다!",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    
                    
                    if (moveTarget.getPosition() != null) {
                        int playerIndex = model.getCurrentPlayerIndex();
                        int cellId = moveTarget.getPosition().getId();
                        System.out.printf("▶ 플레이어 %d의 말 이동: 셀 ID %d\n", playerIndex, cellId);
                    }

                    if (model.getCurrentPlayer().getYutHistory().isEmpty()) {
                        gameController.nextTurn();
                        selectedPiece = null;
                    }
                }
            });

            add(btn);
            resultButtons.put(result.name(), btn);
        }

        repaint();
    }


    public JButton getThrowYutButton() {
        return throwYutButton;
    }

    public JButton getDeployPieceButton() {
        return deployPieceButton;
    }

    public JButton getNextTurnButton() {
        return nextTurnButton;
    }

    public Map<String, JButton> getYutChoiceButtons() {
        return yutChoiceButtons;
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    /** 말 UI를 각 보드 타입에 맞게 업데이트 (오버라이드 필요) */
    protected void updatePieceIcons() {
        // 서브 클래스에서 구현
    }
}
