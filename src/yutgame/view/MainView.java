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

        Pane root = new Pane();
        root.setPrefSize(1100, 750);

        // ── 보드 영역 ───────────────────────────
        if (boardView != null) {
            boardView.setLayoutX(10);
            boardView.setLayoutY(10);
            boardView.setPrefSize(950, 700);
            boardView.initialize();
            root.getChildren().add(boardView);
        }

        // ── 오른쪽: 턴 정보 + 결과 ───────────────
        Pane rightPanel = new Pane();
        rightPanel.setLayoutX(810);
        rightPanel.setLayoutY(10);
        rightPanel.setPrefSize(260, 700);

        if (turnView != null) {
            turnView.setLayoutX(0);
            turnView.setLayoutY(10);
            turnView.setPrefSize(235, 80);
            rightPanel.getChildren().add(turnView);
        }

        if (yutResultView != null) {
            yutResultView.setLayoutX(0);
            yutResultView.setLayoutY(140);
            yutResultView.setPrefSize(260, 300);
            rightPanel.getChildren().add(yutResultView);
        }

        root.getChildren().add(rightPanel);

        Scene scene = new Scene(root);
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
