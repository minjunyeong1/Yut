package yutgame.view;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import yutgame.controller.GameController;
import yutgame.model.*;

import java.util.*;

public class PentagonBoardView extends AbstractBoardView {
    private final Map<Integer, Point2D> cellIdToPosition = new HashMap<>();
    private final List<ImageView> pieceIcons = new ArrayList<>();
    private ImageView lineImageView;

    public PentagonBoardView(GameConfig config, GameModel model, GameController controller) {
        super(config, model, controller);
        initialize(); // AbstractBoardView의 버튼 및 아이콘 구성
    }

    @Override
    protected void buildBoard() {
        double bs = windowSizeX / 20.0;
        Image lineImg = new Image(getClass().getResource("/yutgame/img/line_pen.png").toExternalForm());
        Image circle = new Image(getClass().getResource("/yutgame/img/circle.jpg").toExternalForm());
        Image bigCircle = new Image(getClass().getResource("/yutgame/img/bigcircle.jpg").toExternalForm());
        Image startCircle = new Image(getClass().getResource("/yutgame/img/startcircle.jpg").toExternalForm());

        double imgX = windowSizeX / 2.0 - lineImg.getWidth() / 2 - 260;
        double imgY = windowSizeY / 2.0 - lineImg.getHeight() / 2 - 130;
        double cx = imgX + lineImg.getWidth() / 2;
        double cy = imgY + lineImg.getHeight() / 2;
        double radius = bs * 4.2;

        List<Point2D> outerVertices = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(306 + i * 72);
            double x = cx + radius * Math.cos(angle);
            double y = cy - radius * Math.sin(angle) + 20;
            outerVertices.add(new Point2D(x, y));
        }

        int cellId = 0;
        for (int i = 0; i < 5; i++) {
            Point2D from = outerVertices.get(i);
            Point2D to = outerVertices.get((i + 1) % 5);
            for (int j = 0; j < 5; j++) {
                double ratio = j / 5.0;
                double x = from.getX() * (1 - ratio) + to.getX() * ratio;
                double y = from.getY() * (1 - ratio) + to.getY() * ratio;

                Image img = (j == 0 && i == 0) ? startCircle : (j == 0) ? bigCircle : circle;
                ImageView iv = new ImageView(img);
                iv.setFitWidth(bs);
                iv.setFitHeight(bs);
                iv.setLayoutX(x - bs / 2);
                iv.setLayoutY(y - bs / 2);

                getChildren().add(iv);
                cellIdToPosition.put(cellId++, new Point2D(x, y));
            }
        }

        cellIdToPosition.put(25, cellIdToPosition.get(0));

        // 중앙 셀 (32)
        ImageView center = new ImageView(bigCircle);
        center.setFitWidth(bs);
        center.setFitHeight(bs);
        center.setLayoutX(cx - bs / 2 + 2);
        center.setLayoutY(cy - bs / 2 + 20);
        getChildren().add(center);
        cellIdToPosition.put(32, new Point2D(cx, cy + 20));

        // 대각선 셀
        int[] diagIds = {34, 33, 26, 27, 28, 29, 30, 31, 36, 35};
        int diagIndex = 0;
        for (int i = 0; i < 5; i++) {
            Point2D from = outerVertices.get(i);
            for (int j = 1; j <= 2; j++) {
                int id = diagIds[diagIndex++];
                double r = j / 3.0;
                double x = from.getX() * (1 - r) + cx * r;
                double y = from.getY() * (1 - r) + cy * r;

                ImageView diag = new ImageView(circle);
                diag.setFitWidth(bs);
                diag.setFitHeight(bs);
                diag.setLayoutX(x - bs / 2);
                diag.setLayoutY(y - bs / 2);

                getChildren().add(diag);
                cellIdToPosition.put(id, new Point2D(x, y));
            }
        }

        // 선 이미지 맨 뒤로
        lineImageView = new ImageView(lineImg);
        lineImageView.setLayoutX(imgX);
        lineImageView.setLayoutY(imgY);
        getChildren().add(0, lineImageView);

        updatePieceIcons();
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
                        icon.setStyle("-fx-effect: dropshadow(gaussian, purple, 10, 0.5, 0, 0);");
                    }
                }

                getChildren().add(icon);
                pieceIcons.add(icon);
            }
        }

        getChildren().remove(lineImageView);
        getChildren().add(0, lineImageView); // 항상 맨 뒤로
    }
}