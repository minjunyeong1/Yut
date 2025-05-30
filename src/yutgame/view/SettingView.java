package yutgame.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import yutgame.model.Board;

public class SettingView extends Stage {
    private Spinner<Integer> playerCountSpinner;
    private Spinner<Integer> piecesPerPlayerSpinner;
    private ComboBox<BoardShapeItem> boardShapeCombo;
    private Button startButton;

    public SettingView() {
        setTitle("Yut Game 설정");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15)); grid.setHgap(10); grid.setVgap(10);
        Label pLbl = new Label("플레이어 수 (2~4):");
        playerCountSpinner = new Spinner<>(2,4,2);
        grid.add(pLbl,0,0); grid.add(playerCountSpinner,1,0);
        Label pcLbl = new Label("말 개수 (2~5):");
        piecesPerPlayerSpinner = new Spinner<>(2,5,2);
        grid.add(pcLbl,0,1); grid.add(piecesPerPlayerSpinner,1,1);
        Label sLbl = new Label("판 모양:");
        boardShapeCombo = new ComboBox<>();
        boardShapeCombo.getItems().addAll(
            new BoardShapeItem("사각형", Board.Shape.RECTANGLE),
            new BoardShapeItem("오각형", Board.Shape.PENTAGON),
            new BoardShapeItem("육각형", Board.Shape.HEXAGON)
        );
        boardShapeCombo.getSelectionModel().selectFirst();
        grid.add(sLbl,0,2); grid.add(boardShapeCombo,1,2);
        startButton = new Button("게임 시작");
        grid.add(startButton,0,3,2,1);
        GridPane.setMargin(startButton,new Insets(10,0,0,0));
        Scene scene = new Scene(grid);
        setScene(scene); initModality(Modality.APPLICATION_MODAL);
        sizeToScene(); centerOnScreen(); show();
    }

    public int getPlayerCount() { return playerCountSpinner.getValue(); }
    public int getPiecesPerPlayer() { return piecesPerPlayerSpinner.getValue(); }
    public Board.Shape getSelectedShape() { return boardShapeCombo.getValue().shape; }
    public void addStartListener(Runnable handler) { startButton.setOnAction(e->handler.run()); }
    private static class BoardShapeItem { final String name; final Board.Shape shape;
        BoardShapeItem(String name, Board.Shape shape) { this.name=name; this.shape=shape; }
        @Override public String toString() { return name; }
    }
}