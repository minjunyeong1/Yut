package yutgame.view;

import javax.swing.*;

import yutgame.controller.GameController;
import yutgame.model.GameConfig;
import yutgame.model.GameModel;
import yutgame.model.Piece;
import yutgame.model.Player;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PentagonBoardView extends AbstractBoardView {
    private JLabel lineLabel;
    private final Map<Integer, Point> cellIdToPosition = new HashMap<>();
    private final List<JLabel> pieceIconLabels = new ArrayList<>();

    private final ImageIcon[] pieceIcons = {
        new ImageIcon(getClass().getResource("/yutgame/img/blue1.jpg")),
        new ImageIcon(getClass().getResource("/yutgame/img/green1.jpg")),
        new ImageIcon(getClass().getResource("/yutgame/img/red1.jpg")),
        new ImageIcon(getClass().getResource("/yutgame/img/yellow1.jpg"))
    };

    public PentagonBoardView(GameConfig config, GameModel model, GameController controller) {
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
        setLayout(null);
        int bs = windowSizeX / 20;
        ImageIcon lineIcon = new ImageIcon(getClass().getResource("/yutgame/img/line_pen.png"));
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/yutgame/img/startcircle.jpg"));
        ImageIcon bigCircleIcon = new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg"));
        ImageIcon circleIcon = new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg"));

        int imgX = windowSizeX / 2 - lineIcon.getIconWidth() / 2 - 260;
        int imgY = windowSizeY / 2 - lineIcon.getIconHeight() / 2 - 130;
        int cx = imgX + lineIcon.getIconWidth() / 2;
        int cy = imgY + lineIcon.getIconHeight() / 2;
        int radius = bs * 42 / 10;

        List<Point> outerVertices = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(306 + i * 72);
            int x = (int) (cx + radius * Math.cos(angle));
            int y = (int) (cy - radius * Math.sin(angle)) + 20;
            outerVertices.add(new Point(x, y));
        }

        int cellId = 0;
        // 외곽 셀 0~24
        for (int i = 0; i < 5; i++) {
            Point from = outerVertices.get(i);
            Point to = outerVertices.get((i + 1) % 5);
            for (int j = 0; j < 5; j++) {
                double ratio = j / 5.0;
                int x = (int)(from.x * (1 - ratio) + to.x * ratio);
                int y = (int)(from.y * (1 - ratio) + to.y * ratio);
                JButton btn = new JButton((j == 0 && i == 0) ? startIcon : (j == 0) ? bigCircleIcon : circleIcon);
                btn.setBounds(x - bs / 2, y - bs / 2, bs, bs);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);
                add(btn);
                cellIdToPosition.put(cellId++, new Point(x, y));
            }
        }

        // 중앙 셀 (32)
        JButton centerBtn = new JButton(bigCircleIcon);
        centerBtn.setBounds(cx - bs / 2 + 2, cy - bs / 2 + 20, bs, bs);
        centerBtn.setContentAreaFilled(false);
        centerBtn.setBorderPainted(false);
        centerBtn.setFocusPainted(false);
        centerBtn.setOpaque(false);
        add(centerBtn);
        cellIdToPosition.put(32, new Point(cx, cy + 20));

        // 대각선 셀 26~36
        int diagId = 26;
        for (int i = 0; i < 5; i++) {
            Point from = outerVertices.get(i);
            for (int j = 1; j <= 2; j++) {
                double r = j / 3.0;
                int x = (int)(from.x * (1 - r) + cx * r);
                int y = (int)(from.y * (1 - r) + (cy + 20) * r);
                JButton btn = new JButton(circleIcon);
                btn.setBounds(x - bs / 2, y - bs / 2, bs, bs);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);
                add(btn);
                cellIdToPosition.put(diagId++, new Point(x, y));
            }
        }

        lineLabel = new JLabel(lineIcon);
        lineLabel.setBounds(imgX, imgY, lineIcon.getIconWidth(), lineIcon.getIconHeight());
        add(lineLabel, 0);
        setComponentZOrder(lineLabel, getComponentCount() - 1);
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

                int stackSize = Math.min(leader.getStackSize(), 5);
                String imagePath = String.format("/yutgame/img/big%s%d.jpg", color, stackSize);
                ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));

                JLabel iconLabel = new JLabel(icon);
                iconLabel.setBounds(pos.x - 15, pos.y - 15, 30, 30);
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
