package yutgame.view;

import javax.swing.*;
import java.awt.*;

/**
 * Settings UI: choose players, pieces, board shape.
 */
public class SettingView extends JFrame {
    private JSpinner playerCountSpinner;
    private JSpinner piecesPerPlayerSpinner;
    private JComboBox<BoardShapeItem> boardShapeCombo;
    private JButton startButton;

    public SettingView() {
        super("Yut Game 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx=0; c.gridy=0; add(new JLabel("플레이어 수 (2~4):"), c);
        playerCountSpinner = new JSpinner(new SpinnerNumberModel(2,2,4,1));
        c.gridx=1; add(playerCountSpinner, c);

        c.gridx=0; c.gridy=1; add(new JLabel("말 개수 (2~5):"), c);
        piecesPerPlayerSpinner = new JSpinner(new SpinnerNumberModel(2,2,5,1));
        c.gridx=1; add(piecesPerPlayerSpinner, c);

        c.gridx=0; c.gridy=2; add(new JLabel("판 모양:"), c);
        boardShapeCombo = new JComboBox<>(new BoardShapeItem[]{
            new BoardShapeItem("사각형", yutgame.model.Board.Shape.RECTANGLE),
            new BoardShapeItem("오각형",  yutgame.model.Board.Shape.PENTAGON),
            new BoardShapeItem("육각형",  yutgame.model.Board.Shape.HEXAGON)
        });
        c.gridx=1; add(boardShapeCombo, c);

        startButton = new JButton("게임 시작");
        c.gridx=0; c.gridy=3; c.gridwidth=2; c.anchor=GridBagConstraints.CENTER;
        add(startButton, c);

        pack();
        setLocationRelativeTo(null);
    }

    public int getPlayerCount() { return (Integer)playerCountSpinner.getValue(); }
    public int getPiecesPerPlayer() { return (Integer)piecesPerPlayerSpinner.getValue(); }
    public yutgame.model.Board.Shape getSelectedShape() { return ((BoardShapeItem)boardShapeCombo.getSelectedItem()).shape; }
    public void addStartListener(java.awt.event.ActionListener l) { startButton.addActionListener(l); }

    private static class BoardShapeItem {
        final String name; final yutgame.model.Board.Shape shape;
        BoardShapeItem(String name, yutgame.model.Board.Shape shape){ this.name=name; this.shape=shape; }
        @Override public String toString(){ return name; }
    }
}
