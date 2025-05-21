package yutgame.view;

import javax.swing.*;
import java.awt.*;

public class SettingView extends JFrame {
    private JSpinner playerCountSpinner;
    private JSpinner piecesPerPlayerSpinner;
    private JComboBox<BoardShapeItem> boardShapeCombo;
    private JButton startButton;

    public SettingView() {
        super("Yut Game 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false); // ⛔ 창 크기 조절 방지

        Font font = new Font("Dialog", Font.PLAIN, 16);

        // ───── 중앙 패널 ─────
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.EAST;

        JLabel playerLabel = new JLabel("플레이어 수 (2~4):");
        playerLabel.setFont(font);
        playerCountSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));
        playerCountSpinner.setFont(font);
        ((JSpinner.DefaultEditor) playerCountSpinner.getEditor()).getTextField().setFont(font);

        JLabel pieceLabel = new JLabel("말 개수 (2~5):");
        pieceLabel.setFont(font);
        piecesPerPlayerSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 5, 1));
        piecesPerPlayerSpinner.setFont(font);
        ((JSpinner.DefaultEditor) piecesPerPlayerSpinner.getEditor()).getTextField().setFont(font);

        JLabel shapeLabel = new JLabel("판 모양:");
        shapeLabel.setFont(font);
        boardShapeCombo = new JComboBox<>(new BoardShapeItem[]{
            new BoardShapeItem("사각형", yutgame.model.Board.Shape.RECTANGLE),
            new BoardShapeItem("오각형", yutgame.model.Board.Shape.PENTAGON),
            new BoardShapeItem("육각형", yutgame.model.Board.Shape.HEXAGON)
        });
        boardShapeCombo.setFont(font);

        // Row 0
        c.gridx = 0; c.gridy = 0;
        centerPanel.add(playerLabel, c);
        c.gridx = 1;
        centerPanel.add(playerCountSpinner, c);

        // Row 1
        c.gridx = 0; c.gridy = 1;
        centerPanel.add(pieceLabel, c);
        c.gridx = 1;
        centerPanel.add(piecesPerPlayerSpinner, c);

        // Row 2
        c.gridx = 0; c.gridy = 2;
        centerPanel.add(shapeLabel, c);
        c.gridx = 1;
        centerPanel.add(boardShapeCombo, c);

        // ───── 버튼 ─────
        startButton = new JButton("게임 시작");
        startButton.setFont(font);
        startButton.setPreferredSize(new Dimension(160, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        buttonPanel.add(startButton);

        // 조립
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // 중앙 정렬
    }

    public int getPlayerCount() { return (Integer) playerCountSpinner.getValue(); }
    public int getPiecesPerPlayer() { return (Integer) piecesPerPlayerSpinner.getValue(); }
    public yutgame.model.Board.Shape getSelectedShape() {
        return ((BoardShapeItem) boardShapeCombo.getSelectedItem()).shape;
    }
    public void addStartListener(java.awt.event.ActionListener l) {
        startButton.addActionListener(l);
    }

    private static class BoardShapeItem {
        final String name;
        final yutgame.model.Board.Shape shape;

        BoardShapeItem(String name, yutgame.model.Board.Shape shape) {
            this.name = name;
            this.shape = shape;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
