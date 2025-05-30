package yutgame.view;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.*;

public class TurnView extends Pane {
    private ImageView turnArrowImageView;
    private List<Point2D> playerIconPositions;

    public TurnView() {
        setPrefSize(200,100);
        Image arrowImage = new Image(getClass().getResource("/yutgame/img/player_turn.png").toExternalForm());
        turnArrowImageView=new ImageView(arrowImage);
        getChildren().add(turnArrowImageView);
        playerIconPositions=new ArrayList<>();
        playerIconPositions.add(new Point2D(-15,-3));  // 파랑
        playerIconPositions.add(new Point2D(45,-3));   // 초록
        playerIconPositions.add(new Point2D(105,-3));  // 빨강
        playerIconPositions.add(new Point2D(165,-3));  // 노랑
        Point2D p = playerIconPositions.get(0);
        turnArrowImageView.setLayoutX(p.getX());
        turnArrowImageView.setLayoutY(p.getY());
    }
    public void updateTurn(int turnIndex) {
        if (turnIndex>=0 && turnIndex<playerIconPositions.size()) {
            Point2D p = playerIconPositions.get(turnIndex);
            turnArrowImageView.setLayoutX(p.getX());
            turnArrowImageView.setLayoutY(p.getY());
        }
    }
}