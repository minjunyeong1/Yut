package yutgame.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class TurnView extends Pane {

    private ImageView turnArrowImageView;
    private List<Point2D> playerIconPositions;

    public TurnView() {
        setPrefSize(200, 100);

        // 화살표 이미지 로딩
        Image arrowImage = new Image(getClass()
            .getResource("/yutgame/img/player_turn.png")
            .toExternalForm());
        turnArrowImageView = new ImageView(arrowImage);
        getChildren().add(turnArrowImageView);

        // 플레이어 아이콘 상대 위치 리스트
        playerIconPositions = new ArrayList<>();
        playerIconPositions.add(new Point2D(35, 7));   // 파랑
        playerIconPositions.add(new Point2D(95, 7));    // 초록
        playerIconPositions.add(new Point2D(155, 7));   // 빨강
        playerIconPositions.add(new Point2D(215, 7));   // 노랑

        // 초기 위치 설정 (첫 번째 플레이어)
        Point2D p = playerIconPositions.get(0);
        turnArrowImageView.setLayoutX(p.getX());
        turnArrowImageView.setLayoutY(p.getY());
    }

    /**
     * @param turnIndex 0=파랑, 1=초록, 2=빨강, 3=노랑
     */
    public void updateTurn(int turnIndex) {
        if (turnIndex >= 0 && turnIndex < playerIconPositions.size()) {
            Point2D p = playerIconPositions.get(turnIndex);
            turnArrowImageView.setLayoutX(p.getX());
            turnArrowImageView.setLayoutY(p.getY());
        }
    }
}
