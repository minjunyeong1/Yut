package yutgame.view;

import javax.swing.*;
import java.awt.*;

public class YutResultView extends JPanel {
    private final JTextArea resultArea;
    private final StringBuilder history;

    public YutResultView() {
        setLayout(new BorderLayout());

        setBackground(new Color(240, 240, 240)); 
        setOpaque(true);

        resultArea = new JTextArea("결과:\n");
        resultArea.setFont(new Font("SansSerif", Font.BOLD, 18));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        add(resultArea, BorderLayout.CENTER);
        history = new StringBuilder();
    }

    public void setResult(String resultText) {
        history.append(resultText).append("\n");
        resultArea.setText("결과:\n" + history);
    }

    public void clearResults() {
        history.setLength(0);
        resultArea.setText("결과:\n");
    }
}
