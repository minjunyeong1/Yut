package yutgame.view;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

public class YutResultView extends VBox {

    private final TextArea resultArea;
    private final StringBuilder history;

    public YutResultView() {
        setPrefSize(300, 300);
        setPadding(new Insets(10));

        // 배경색 설정
        setBackground(new Background(
            new BackgroundFill(Color.rgb(240,240,240),
            CornerRadii.EMPTY, Insets.EMPTY)));

        // 결과 텍스트 영역
        resultArea = new TextArea("🎲 윷 결과:\n");
        resultArea.setStyle(
            "-fx-font-family: 'SansSerif';" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 18px;");
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(280);

        getChildren().add(resultArea);
        history = new StringBuilder();
    }

    public void setResult(String resultText) {
        history.append(resultText).append("\n");
        resultArea.setText("🎲 결과:\n" + history);
    }

    public void clearResults() {
        history.setLength(0);
        resultArea.setText("🎲 결과:\n");
    }
}
