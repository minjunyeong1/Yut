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

public class HexagonBoardView extends AbstractBoardView {
    private JButton[][] panButton = new JButton[1][7];
    private JLabel lineLabel;
    private Map<Integer, Point> cellIdToPosition = new HashMap<>();
    private List<JLabel> pieceIconLabels = new ArrayList<>();

    public HexagonBoardView(GameConfig config, GameModel model, GameController controller) {
        super(config, model, controller);
        addCommonButtons();
    }

    @Override
    protected void buildBoard() {
        setLayout(null);
        int bs = windowSizeX / 20;
        ImageIcon lineIcon = new ImageIcon(getClass().getResource("/yutgame/img/line_hex.png"));
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/yutgame/img/startcircle.jpg"));
        ImageIcon bigCircleIcon = new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg"));
        ImageIcon circleIcon = new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg"));

        int imgX = windowSizeX / 2 - lineIcon.getIconWidth() / 2 - 280;
        int imgY = windowSizeY / 2 - lineIcon.getIconHeight() / 2 - 100;
        int cx = imgX + lineIcon.getIconWidth() / 2;
        int cy = imgY + lineIcon.getIconHeight() / 2;
        int radius = bs * 44 / 10;

        // 셀 ID별 위치 계산
        mapHexagonCellPositions(cx, cy, bs); 

        // 꼭짓점 중앙만 
        Set<Integer> bigCircleIds = Set.of(5, 10, 15, 20, 25, 37);

        for (Map.Entry<Integer, Point> entry : cellIdToPosition.entrySet()) {
            int id = entry.getKey();
            Point pos = entry.getValue();

            ImageIcon icon;
            if (id == 0) icon = startIcon;
            else if (bigCircleIds.contains(id)) icon = bigCircleIcon;
            else icon = circleIcon;

            JButton btn = new JButton(icon);
            btn.setBounds(pos.x - bs / 2, pos.y - bs / 2, bs, bs);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            add(btn);
        }

        lineLabel = new JLabel(lineIcon);
        lineLabel.setBounds(imgX, imgY, lineIcon.getIconWidth(), lineIcon.getIconHeight());
        add(lineLabel, 0);
        setComponentZOrder(lineLabel, getComponentCount() - 1);

        updatePieceIcons();
        repaint();
    }


    private void mapHexagonCellPositions(int cx, int cy, int bs) {
        int radius = bs * 44 / 10;

        // 꼭짓점 (0, 5, 10, 15, 20, 25)
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(330 + i * 60);
            int x = (int) (cx + radius * Math.cos(angle));
            int y = (int) (cy - radius * Math.sin(angle)) + 10;
            cellIdToPosition.put(i * 5, new Point(x, y));
        }

        // 외곽 간 셀들 (1~4, 6~9, ..., 21~24, 26~29)
        for (int i = 0; i < 6; i++) {
            Point from = cellIdToPosition.get(i * 5);
            Point to = cellIdToPosition.get(((i + 1) % 6) * 5);
            for (int j = 1; j <= 4; j++) {
                double ratio = j / 5.0;
                int x = (int) (from.x * (1 - ratio) + to.x * ratio);
                int y = (int) (from.y * (1 - ratio) + to.y * ratio);
                cellIdToPosition.put(i * 5 + j, new Point(x, y));
            }
        }

        // 중심 셀
        cellIdToPosition.put(37, new Point(cx, cy + 20));

        // 대각선 셀 (41~52)
        for (int i = 0; i < 6; i++) {
            Point from = cellIdToPosition.get(i * 5);
            for (int j = 1; j <= 2; j++) {
                double ratio = j / 3.0;
                int x = (int) (from.x * (1 - ratio) + cx * ratio);
                int y = (int) (from.y * (1 - ratio) + (cy + 20) * ratio);
                cellIdToPosition.put(39 + i * 2 + j, new Point(x, y));
            }
        }
    }

    @Override
    public void updatePieceIcons() {
        for (JLabel label : pieceIconLabels) remove(label);
        pieceIconLabels.clear();

        List<Player> players = model.getPlayers();
        String[] colorNames = {"blue", "green", "red", "yellow"};
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

                System.out.printf("\u2705 말 아이콘 표시 → player %d (%s), cell ID %d, 위치: (%d, %d), 스택: %d%n",
                    playerIndex, color, cellId, pos.x, pos.y, stackSize);
            }
        }

        setComponentZOrder(lineLabel, getComponentCount() - 1);
        repaint();
    }
}
