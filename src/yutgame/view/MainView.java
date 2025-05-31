package yutgame.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import yutgame.controller.SettingController;

import java.util.Optional;

public class MainView extends BorderPane {
    private TurnView turnView;
    private AbstractBoardView boardView;
    private YutResultView yutResultView;
    private final Stage stage;

    public MainView(AbstractBoardView boardView) {
        this.boardView = boardView;
        this.stage = new Stage(); // 새 창 생성

        // ── 중앙 보드 영역 ─────────────────────────────
        StackPane boardPane = new StackPane();
        boardPane.getChildren().add(boardView);
        boardPane.setPadding(new Insets(10));
        boardPane.setPrefSize(950, 700);

        // ── TurnView: 보드 위 오른쪽 위에 배치 ─────────────
        turnView = new TurnView();
        StackPane.setMargin(turnView, new Insets(0, 0, 600, 450));
        boardPane.getChildren().add(turnView);

        // ── 오른쪽 결과창 영역 ─────────────────────────
        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(200);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setSpacing(10);
        rightPanel.setStyle("-fx-background-color: #f5f5f5;");

        yutResultView = new YutResultView();
        VBox.setVgrow(yutResultView, Priority.ALWAYS);
        rightPanel.getChildren().add(yutResultView);

        // ── 전체 배치 ───────────────────────────────
        setLeft(boardPane);
        setRight(rightPanel);

        // ── Scene 및 Stage 구성 ─────────────────────
        Scene scene = new Scene(this, 1180, 750);
        stage.setTitle("Yut Play");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    public TurnView getTurnView() {
        return turnView;
    }

    public AbstractBoardView getBoardView() {
        return boardView;
    }

    public YutResultView getYutResultView() {
        return yutResultView;
    }

    public void show() {
        stage.show();
        stage.centerOnScreen();
    }

    public void handleWinCondition(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + "님이 승리했습니다!\n다시 시작하시겠습니까?");
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            stage.close();
            Platform.runLater(() -> new SettingController(new Stage()));
        } else {
            Platform.exit();
        }
    }
}
