package yutgame.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainView {

    private TurnView turnView;
    private AbstractBoardView boardView;
    private YutResultView yutResultView;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Yut Play");

        // 루트 크기를 씬 크기와 동일하게 명시
        Pane root = new Pane();
        root.setPrefSize(1200, 750);

        // ── 보드 영역 ───────────────────────────
        if (boardView != null) {
            boardView.setLayoutX(10);
            boardView.setLayoutY(10);
            boardView.setPrefSize(950, 700);
            boardView.initialize();
            root.getChildren().add(boardView);
        }

        // ── 보드 위: 턴 표시(화살표) ─────────────
        if (turnView != null && boardView != null) {
            boardView.getChildren().add(turnView);
            double iconStartX = 500;
            turnView.setLayoutX(iconStartX);
            turnView.setLayoutY(10);
        }

        // ── 오른쪽: 결과 영역 ────────────────────
        Pane rightPanel = new Pane();
        // 1200px 넓이에 맞춰, 260px 폭의 패널이 잘리지 않도록 X를 930으로 설정
        rightPanel.setLayoutX(930);
        rightPanel.setLayoutY(10);
        rightPanel.setPrefSize(260, 700);
        if (yutResultView != null) {
            yutResultView.setLayoutX(0);
            yutResultView.setLayoutY(0);
            rightPanel.getChildren().add(yutResultView);
        }
        root.getChildren().add(rightPanel);

        // 씬에 폭·높이를 명시적으로 지정
        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void setBoardView(AbstractBoardView boardView) {
        this.boardView = boardView;
    }

    public void setTurnView(TurnView turnView) {
        this.turnView = turnView;
    }

    public void setYutResultView(YutResultView yutResultView) {
        this.yutResultView = yutResultView;
    }

    public AbstractBoardView getBoardView() {
        return boardView;
    }

    public TurnView getTurnView() {
        return turnView;
    }

    public YutResultView getYutResultView() {
        return yutResultView;
    }
}
