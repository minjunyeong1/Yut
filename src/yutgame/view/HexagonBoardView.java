package yutgame.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import yutgame.controller.GameController;
import yutgame.model.*;

import java.util.*;

public class HexagonBoardView extends AbstractBoardView {

    private final Map<Integer, Point2D> cellIdToPosition = new HashMap<>();
    private final List<ImageView> pieceIcons = new ArrayList<>();
    private ImageView lineImageView;

    public HexagonBoardView(GameConfig config, GameModel model, GameController controller) {
        super(config, model, controller);
        initialize();
    }

    @Override
    protected void buildBoard() {
        double bs = windowSizeX / 20.0;

        Image lineImage = new Image(getClass().getResource("/yutgame/img/line_hex.png").toExternalForm());
        Image circle = new Image(getClass().getResource("/yutgame/img/circle.jpg").toExternalForm());
        Image bigCircle = new Image(getClass().getResource("/yutgame/img/bigcircle.jpg").toExternalForm());
        Image startCircle = new Image(getClass().getResource("/yutgame/img/startcircle.jpg").toExternalForm());

        double imgX = windowSizeX / 2.0 - lineImage.getWidth() / 2 - 280;
        double imgY = windowSizeY / 2.0 - lineImage.getHeight() / 2 - 100;
        double cx = imgX + lineImage.getWidth() / 2;
        double cy = imgY + lineImage.getHeight() / 2;

        mapHexagonCellPositions(cx, cy + 20, bs); // 20px shift for vertical offset

        Set<Integer> bigCircleIds = Set.of(5, 10, 15, 20, 25, 37);

        for (Map.Entry<Integer, Point2D> entry : cellIdToPosition.entrySet()) {
            int id = entry.getKey();
            Point2D pos = entry.getValue();

            Image icon = switch (id) {
                case 0 -> startCircle;
                case 5, 10, 15, 20, 25, 30, 37 -> bigCircle;
                default -> circle;
            };

            ImageView iv = new ImageView(icon);
            iv.setFitWidth(bs);
            iv.setFitHeight(bs);
            iv.setLayoutX(pos.getX() - bs / 2);
            iv.setLayoutY(pos.getY() - bs / 2);
            getChildren().add(iv);
        }

        lineImageView = new ImageView(lineImage);
        lineImageView.setLayoutX(imgX);
        lineImageView.setLayoutY(imgY);
        getChildren().add(0, lineImageView); // 맨 뒤
        updatePieceIcons();
    }

    private void mapHexagonCellPositions(double cx, double cy, double bs) {
        int radius = (int)(bs * 4.4);

        // 꼭짓점
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(330 + i * 60);
            double x = cx + radius * Math.cos(angle);
            double y = cy - radius * Math.sin(angle);
            cellIdToPosition.put(i * 5, new Point2D(x, y));
        }

        cellIdToPosition.put(30, cellIdToPosition.get(0));

        // 외곽 셀
        for (int i = 0; i < 6; i++) {
            Point2D from = cellIdToPosition.get(i * 5);
            Point2D to = cellIdToPosition.get(((i + 1) % 6) * 5);
            for (int j = 1; j <= 4; j++) {
                double ratio = j / 5.0;
                double x = from.getX() * (1 - ratio) + to.getX() * ratio;
                double y = from.getY() * (1 - ratio) + to.getY() * ratio;
                cellIdToPosition.put(i * 5 + j, new Point2D(x, y));
            }
        }

        // 중앙 셀
        cellIdToPosition.put(37, new Point2D(cx, cy));

        int[] diagonalIds = {39, 38, 31, 32, 33, 34, 35, 36, 43, 42, 41, 40};
        int idx = 0;
        for (int i = 0; i < 6; i++) {
            Point2D from = cellIdToPosition.get(i * 5);
            for (int j = 1; j <= 2; j++) {
                if (idx >= diagonalIds.length) break;
                double ratio = j / 3.0;
                double x = from.getX() * (1 - ratio) + cx * ratio;
                double y = from.getY() * (1 - ratio) + cy * ratio;
                cellIdToPosition.put(diagonalIds[idx++], new Point2D(x, y));
            }
        }
    }

    @Override
    public void updatePieceIcons() {
        getChildren().removeAll(pieceIcons);
        pieceIcons.clear();

        List<Player> players = model.getPlayers();
        String[] colors = { "blue", "green", "red", "yellow" };
        Set<Integer> drawn = new HashSet<>();

        for (int pIdx = 0; pIdx < players.size(); pIdx++) {
            Player player = players.get(pIdx);
            String color = colors[pIdx % colors.length];

            for (Piece piece : player.getPieces()) {
                Piece leader = piece.isLeader() ? piece : piece.getLeader();
                if (leader == null || leader.getPosition() == null) continue;

                int id = leader.getPosition().getId();
                if (id == 0 || drawn.contains(id)) continue;
                drawn.add(id);

                Point2D pos = cellIdToPosition.get(id);
                if (pos == null) continue;

                int stack = Math.min(leader.getStackSize(), 5);
                String imgPath = String.format("/yutgame/img/big%s%d.jpg", color, stack);
                ImageView icon = new ImageView(new Image(getClass().getResource(imgPath).toExternalForm()));
                icon.setFitWidth(30);
                icon.setFitHeight(30);
                icon.setLayoutX(pos.getX() - 15);
                icon.setLayoutY(pos.getY() - 15);

                if (selectedPiece != null) {
                    Piece selectedLeader = selectedPiece.isLeader() ? selectedPiece : selectedPiece.getLeader();
                    if (selectedLeader != null && selectedLeader.getPosition() != null &&
                        selectedLeader.getPosition().getId() == id &&
                        player.getPieces().contains(selectedLeader)) {
                        icon.setEffect(new DropShadow(10, Color.PURPLE));
                    }
                }

                getChildren().add(icon);
                pieceIcons.add(icon);
            }
        }

        getChildren().remove(lineImageView);
        getChildren().add(0, lineImageView); // always behind
    }
    
}
