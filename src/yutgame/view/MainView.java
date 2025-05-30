package yutgame.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import yutgame.controller.SettingController;
import java.util.Optional;

public class MainView implements GameView {
    private TurnView turnView;
    private AbstractBoardView boardView;
    private YutResultView yutResultView;

    @Override
    public void updateTurn(int turnIndex) {
        if (turnView != null) turnView.updateTurn(turnIndex);
    }

    @Override
    public void showYutResult(String text) {
        if (yutResultView != null) yutResultView.setResult(text);
    }

    @Override
    public void clearResults() {
        if (yutResultView != null) yutResultView.clearResults();
    }

    @Override
    public void showVictory(String playerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(null);
        alert.setContentText(playerName + "님이 승리했습니다!");
        alert.showAndWait();
    }

    @Override
    public void refreshBoard() {
        if (boardView != null) boardView.updatePieceIcons();
    }

    @Override
    public void showGameEndOptions(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + "님이 승리했습니다!\n재시작 혹은 종료" + "를 선택하세요.");

        ButtonType restart = new ButtonType("재시작");
        ButtonType exit    = new ButtonType("종료", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(restart, exit);

        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == restart) {
            Stage stage = (Stage) boardView.getScene().getWindow();
            stage.close();
            new SettingController();
        } else {
            Platform.exit();
        }
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Yut Play");
        Pane root = new Pane();
        root.setPrefSize(1200, 750);

        if (boardView != null) {
            boardView.setLayoutX(10);
            boardView.setLayoutY(10);
            boardView.setPrefSize(950, 700);
            boardView.initialize();
            root.getChildren().add(boardView);
        }

        if (turnView != null && boardView != null) {
            boardView.getChildren().add(turnView);
            turnView.setLayoutX(500);
            turnView.setLayoutY(10);
        }

        Pane rightPanel = new Pane();
        rightPanel.setLayoutX(930);
        rightPanel.setLayoutY(10);
        rightPanel.setPrefSize(260, 700);
        if (yutResultView != null) {
            yutResultView.setLayoutX(0);
            yutResultView.setLayoutY(0);
            rightPanel.getChildren().add(yutResultView);
        }
        root.getChildren().add(rightPanel);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void setBoardView(AbstractBoardView boardView) { this.boardView = boardView; }
    public void setTurnView(TurnView turnView)       { this.turnView = turnView; }
    public void setYutResultView(YutResultView yutResultView) { this.yutResultView = yutResultView; }

    public AbstractBoardView getBoardView()         { return boardView; }
    public TurnView getTurnView()                   { return turnView; }
    public YutResultView getYutResultView()         { return yutResultView; }
}