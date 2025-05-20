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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RectangleBoardView extends AbstractBoardView {
    private JButton[][] panButton = new JButton[3][21];
    private JLabel lineLabel;
    private Map<Integer, Point> cellIdToPosition = new HashMap<>();
    private List<JLabel> pieceIconLabels = new ArrayList<>();
    private JButton highlightedCellButton = null;

    private final ImageIcon[] pieceIcons = {
        new ImageIcon(getClass().getResource("/yutgame/img/blue1.jpg")),
        new ImageIcon(getClass().getResource("/yutgame/img/green1.jpg")),
        new ImageIcon(getClass().getResource("/yutgame/img/red1.jpg")),
        new ImageIcon(getClass().getResource("/yutgame/img/yellow1.jpg"))
    };

    public RectangleBoardView(GameConfig config, GameModel model, GameController controller) {
        super(config, model, controller);
        addCommonButtons();

        for (int i = 0; i < pieceIcons.length; i++) {
            if (pieceIcons[i].getImageLoadStatus() != MediaTracker.COMPLETE) {
                System.out.println("❌ 이미지 로딩 실패: index " + i);
            } else {
                System.out.println("✔ 이미지 로딩 성공: index " + i);
            }
        }
    }

    @Override
    protected void buildBoard() {
        int bsx = windowSizeX / 20;
        int bsy = bsx;
        double interval = bsx * 1.25;

        lineLabel = new JLabel(new ImageIcon(getClass().getResource("/yutgame/img/line.png")));
        lineLabel.setBounds(52, 50, 330, 332);
        add(lineLabel);

        int xpos = bsx * 7;
        int ypos = bsy * 7;

        for (int i = 1; i <= 20; i++) {
            if (i < 6) ypos -= interval;
            else if (i < 11) xpos -= interval;
            else if (i < 16) ypos += interval;
            else xpos += interval;

            ImageIcon icon = (i==5||i==10||i==15)
                ? new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg"))
                : (i==20)
                  ? new ImageIcon(getClass().getResource("/yutgame/img/startcircle.jpg"))
                  : new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg"));

            JButton btn = new JButton(icon);
            btn.setBounds(xpos, ypos, bsx, bsy);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);

            panButton[0][i] = btn;
            add(btn);
            cellIdToPosition.put(i, new Point(xpos + bsx / 2, ypos + bsy / 2));

            if (i == 20) {
                cellIdToPosition.put(0, new Point(xpos + bsx / 2, ypos + bsy / 2));
            }
        }

        // 오른쪽 대각선
        int[] diagRightIds = {5, 30, 31, 32, 35, 36, 15};
        xpos = bsx * 7 - 10;
        ypos = bsy - 10;

        for (int i = 0; i < diagRightIds.length; i++) {
            if (i != 0) {
                xpos -= bsx;
                ypos += bsy;
            }

            JButton btn = new JButton(new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg")));
            btn.setBounds(xpos, ypos, bsx, bsy);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);

            panButton[1][i] = btn;
            add(btn);
            cellIdToPosition.put(diagRightIds[i], new Point(xpos + bsx / 2, ypos + bsy / 2));
        }

        // 왼쪽 대각선
        int[] diagLeftIds = {10, 33, 34, 32, 37, 38, 20};
        xpos = bsx - 10;
        ypos = bsy - 10;

        for (int i = 0; i < diagLeftIds.length; i++) {
            if (i != 0) {
                xpos += bsx;
                ypos += bsy;
            }

            ImageIcon icon = (i == 3)
                ? new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg"))
                : new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg"));

            JButton btn = new JButton(icon);
            btn.setBounds(xpos, ypos, bsx, bsy);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);

            panButton[2][i] = btn;
            add(btn);
            cellIdToPosition.put(diagLeftIds[i], new Point(xpos + bsx / 2, ypos + bsy / 2));
        }
        updatePieceIcons();
        repaint();
    }

    @Override
    public void updatePieceIcons() {
        for (JLabel label : pieceIconLabels) {
            remove(label);
        }
        pieceIconLabels.clear();

        List<Player> players = model.getPlayers();
        String[] colorNames = { "blue", "green", "red", "yellow" };

        Set<Integer> drawnCellIds = new HashSet<>();

        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            Player player = players.get(playerIndex);
            String color = colorNames[playerIndex % colorNames.length];

            for (Piece piece : player.getPieces()) {
                Piece leader = piece.isLeader() ? piece : piece.getLeader();

                if (leader == null || leader.getPosition() == null) continue;

                int cellId = leader.getPosition().getId();
                if (cellId == 0 || drawnCellIds.contains(cellId)) continue;

                drawnCellIds.add(cellId);

                Point pos = cellIdToPosition.get(cellId);
                if (pos == null) continue;

                int stackSize = leader.getStackSize();
                stackSize = Math.min(stackSize, 5);

                String imagePath = String.format("/yutgame/img/big%s%d.jpg", color, stackSize);
                ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));

                JLabel iconLabel = new JLabel(icon);
                iconLabel.setBounds(pos.x - 15, pos.y - 15, 30, 30);
                
                if (selectedPiece != null) {
                    Piece selectedLeader = selectedPiece.isLeader() ? selectedPiece : selectedPiece.getLeader();
                    Player currentPlayer = model.getCurrentPlayer();

                    if (selectedLeader != null && selectedLeader.getPosition() != null &&
                        currentPlayer.getPieces().contains(selectedLeader)) {

                        int selectedId = selectedLeader.getPosition().getId();
                        if (selectedId == cellId) {
                            iconLabel.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 128), 3));
                        }
                    }
                }

                
                add(iconLabel);
                setComponentZOrder(iconLabel, 0);
                pieceIconLabels.add(iconLabel);

                System.out.printf("✅ 말 아이콘 표시 → player %d (%s), cell ID %d, 위치: (%d, %d), 스택: %d%n",
                        playerIndex, color, cellId, pos.x, pos.y, stackSize);
            }
        }

        setComponentZOrder(lineLabel, getComponentCount() - 1);
        repaint();
    }


}