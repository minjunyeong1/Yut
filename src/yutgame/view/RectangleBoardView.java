package yutgame.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import yutgame.controller.GameController;
import yutgame.model.*;

import java.util.*;

public class RectangleBoardView extends AbstractBoardView {

    private final Map<Integer, javafx.geometry.Point2D> cellIdToPosition = new HashMap<>();
    private final List<ImageView> pieceIcons = new ArrayList<>();
    private ImageView lineImageView;

    public RectangleBoardView(GameConfig config, GameModel model, GameController controller) {
        super(config, model, controller);
        initialize();
        addCommonButtons();
    }

    @Override
    protected void buildBoard() {
        double bsx = windowSizeX / 20.0;
        double bsy = bsx;
        double interval = bsx * 1.25;

        // 선 이미지
        Image lineImage = new Image(getClass().getResource("/yutgame/img/line.png").toExternalForm());
        lineImageView = new ImageView(lineImage);
        lineImageView.setLayoutX(52);
        lineImageView.setLayoutY(50);
        getChildren().add(lineImageView);

        double xpos = bsx * 7;
        double ypos = bsy * 7;

        // ── 외곽 셀 (1~20) ─────────────────────
        for (int i = 1; i <= 20; i++) {
            if (i < 6) ypos -= interval;
            else if (i < 11) xpos -= interval;
            else if (i < 16) ypos += interval;
            else xpos += interval;

            String imgName = switch (i) {
                case 5, 10, 15 -> "bigcircle.jpg";
                case 20       -> "startcircle.jpg";
                default       -> "circle.jpg";
            };

            Image img = new Image(getClass().getResource("/yutgame/img/" + imgName).toExternalForm());
            ImageView cell = new ImageView(img);
            cell.setFitWidth(bsx);
            cell.setFitHeight(bsy);
            cell.setLayoutX(xpos);
            cell.setLayoutY(ypos);

            getChildren().add(cell);
            cellIdToPosition.put(i, new javafx.geometry.Point2D(xpos + bsx / 2, ypos + bsy / 2));
            if (i == 20) {
                cellIdToPosition.put(0, new javafx.geometry.Point2D(xpos + bsx / 2, ypos + bsy / 2));
            }
        }

        // ── 대각선 우측 셀 ─────────────────────
        int[] diagRightIds = {5, 30, 31, 32, 35, 36, 15};
        xpos = bsx * 7 - 10;
        ypos = bsy - 10;

        for (int i = 0; i < diagRightIds.length; i++) {
            if (i != 0) {
                xpos -= bsx;
                ypos += bsy;
            }

            int id = diagRightIds[i];
            if (cellIdToPosition.containsKey(id)) continue;

            Image img = new Image(getClass().getResource("/yutgame/img/circle.jpg").toExternalForm());
            ImageView cell = new ImageView(img);
            cell.setFitWidth(bsx);
            cell.setFitHeight(bsy);
            cell.setLayoutX(xpos);
            cell.setLayoutY(ypos);

            getChildren().add(cell);
            cellIdToPosition.put(id, new javafx.geometry.Point2D(xpos + bsx / 2, ypos + bsy / 2));
        }

        // ── 대각선 좌측 셀 ─────────────────────
        int[] diagLeftIds = {10, 33, 34, 32, 37, 38, 20};
        xpos = bsx - 10;
        ypos = bsy - 10;

        for (int i = 0; i < diagLeftIds.length; i++) {
            if (i != 0) {
                xpos += bsx;
                ypos += bsy;
            }

            int id = diagLeftIds[i];
            if (cellIdToPosition.containsKey(id)) continue;

            String imgName = (i == 3) ? "bigcircle.jpg" : "circle.jpg";
            Image img = new Image(getClass().getResource("/yutgame/img/" + imgName).toExternalForm());
            ImageView cell = new ImageView(img);
            cell.setFitWidth(bsx);
            cell.setFitHeight(bsy);
            cell.setLayoutX(xpos);
            cell.setLayoutY(ypos);

            getChildren().add(cell);
            cellIdToPosition.put(id, new javafx.geometry.Point2D(xpos + bsx / 2, ypos + bsy / 2));
        }

     // ── 중심 셀(32) bigcircle로 덮어쓰기 ─────────────────────
        javafx.geometry.Point2D centerPos = cellIdToPosition.get(32);
        if (centerPos != null) {
            double centerX = centerPos.getX() - bsx / 2;
            double centerY = centerPos.getY() - bsy / 2;

            ImageView bigCenter = new ImageView(new Image(
                getClass().getResource("/yutgame/img/bigcircle.jpg").toExternalForm()));
            bigCenter.setFitWidth(bsx);
            bigCenter.setFitHeight(bsy);
            bigCenter.setLayoutX(centerX);
            bigCenter.setLayoutY(centerY);

            getChildren().add(bigCenter);  // 마지막에 추가되므로 위에 렌더됨
        }

        
        updatePieceIcons();
    }



    @Override
    public void updatePieceIcons() {
        getChildren().removeAll(pieceIcons);
        pieceIcons.clear();

        List<Player> players = model.getPlayers();
        String[] colors = { "blue", "green", "red", "yellow" };
        Set<Integer> drawn = new HashSet<>();

        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            Player player = players.get(playerIndex);
            String color = colors[playerIndex % colors.length];

            for (Piece piece : player.getPieces()) {
                Piece leader = piece.isLeader() ? piece : piece.getLeader();
                if (leader == null || leader.getPosition() == null) continue;

                int cellId = leader.getPosition().getId();
                if (cellId == 0 || drawn.contains(cellId)) continue;
                drawn.add(cellId);

                javafx.geometry.Point2D pos = cellIdToPosition.get(cellId);
                if (pos == null) continue;

                int stack = Math.min(leader.getStackSize(), 5);
                String imagePath = String.format("/yutgame/img/big%s%d.jpg", color, stack);
                ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
                icon.setFitWidth(30);
                icon.setFitHeight(30);
                icon.setLayoutX(pos.getX() - 15);
                icon.setLayoutY(pos.getY() - 15);

                if (selectedPiece != null) {
                    Piece selectedLeader = selectedPiece.isLeader() ? selectedPiece : selectedPiece.getLeader();
                    if (selectedLeader != null && selectedLeader.getPosition() != null &&
                        selectedLeader.getPosition().getId() == cellId &&
                        player.getPieces().contains(selectedLeader)) {
                        icon.setStyle("-fx-effect: dropshadow(gaussian, purple, 10, 0.5, 0, 0);");
                    }
                }

                getChildren().add(icon);
                pieceIcons.add(icon);
            }
        }

        getChildren().remove(lineImageView);
        getChildren().add(0, lineImageView);
    }
}
