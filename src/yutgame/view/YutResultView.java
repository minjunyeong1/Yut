package yutgame.view;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class YutResultView extends VBox {
    private final TextArea resultArea;
    private final StringBuilder history;

    public YutResultView() {
        setPrefSize(300,300);
        setPadding(new Insets(10));
        setBackground(new Background(new BackgroundFill(Color.rgb(240,240,240),CornerRadii.EMPTY,Insets.EMPTY)));
        resultArea=new TextArea("🎲 윷 결과:\n");
        resultArea.setStyle("-fx-font-family:'SansSerif';-fx-font-weight:bold;-fx-font-size:18px;");
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(280);
        getChildren().add(resultArea);
        history=new StringBuilder();
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
