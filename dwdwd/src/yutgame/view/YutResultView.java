package yutgame.view;

import javax.swing.*;
import java.awt.*;

public class YutResultView extends JPanel {
    private final JTextArea resultArea;
    private final StringBuilder history;

    public YutResultView() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createTitledBorder("🎲 게임 결과")); // 테두리 + 제목

        resultArea = new JTextArea();
        resultArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);
        history = new StringBuilder();
    }

    public void setResult(String resultText) {
        history.append(resultText).append("\n");
        resultArea.setText(history.toString());
    }

    public void clearResults() {
        history.setLength(0);
        resultArea.setText("");
    }
}