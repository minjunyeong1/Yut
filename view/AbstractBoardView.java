package yutgame.view;

import javax.swing.*;

import yutgame.model.YutThrowResult;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all board views (rectangle, pentagon, hexagon).
 */
public abstract class AbstractBoardView extends JPanel {
    protected int windowSizeX = 1000;
    protected int windowSizeY = 700;

    protected JButton throwYutButton;
    protected JButton deployPieceButton;
    protected JButton nextTurnButton;
    protected Map<String, JButton> yutChoiceButtons = new HashMap<>();

    public AbstractBoardView() {
        setPreferredSize(new Dimension(windowSizeX, windowSizeY));
        setLayout(null);
        setBackground(Color.WHITE);
    }

    /** Build board layout */
    protected abstract void buildBoard();
    
    @Override
    public void addNotify() {
        super.addNotify();
        buildBoard();
        addCommonButtons();
    }

    private void addPlayerIcons() {
        String[] colors = { "blue", "green", "red", "yellow" };
        int startX = 500;
        int spacing = 60;

        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            ImageIcon icon = new ImageIcon(getClass().getResource("/yutgame/img/big" + color + ".jpg"));
            JLabel label = new JLabel(icon);
            label.setBounds(startX + i * spacing, 10, icon.getIconWidth(), icon.getIconHeight());
            add(label);
        }
    }


    
    /** 공통 버튼 추가 */
    protected void addCommonButtons() {
        // ── 윷 던지기 버튼 ─────────────────
        throwYutButton = new JButton("윷 던지기");
        throwYutButton.setBounds(windowSizeX - 180, 50, 120, 40);
        add(throwYutButton);

        // ── 말 꺼내기 버튼 ─────────────────
        deployPieceButton = new JButton("말 꺼내기");
        deployPieceButton.setBounds(windowSizeX - 180, 110, 120, 40);
        add(deployPieceButton);
        addPlayerIcons();
        
        // ── 턴 넘기기 버튼 ─────────────────
        nextTurnButton = new JButton("턴 넘기기");
        nextTurnButton.setBounds(windowSizeX - 180, 170, 120, 40);
        add(nextTurnButton);
        
     // ── 수동 윷 선택 버튼들 ─────────────────
        String[] labels = {"빽도", "도", "개", "걸", "윷", "모"};
        YutThrowResult[] results = {
            YutThrowResult.BACKDO, YutThrowResult.DO, YutThrowResult.GAE,
            YutThrowResult.GEO, YutThrowResult.YUT, YutThrowResult.MO
        };

        int startX = 100;
        int y = 600;
        int width = 80;

        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];
            YutThrowResult result = results[i];

            JButton btn = new JButton(label);
            btn.setBounds(startX + i * width, y, width - 10, 30);
            btn.setActionCommand(result.name()); // 식별용
            add(btn);

            // 버튼 저장 or 컨트롤러에서 나중에 연결할 수 있도록 필드에 모아둘 수도 있음
            yutChoiceButtons.put(result.name(), btn); 

    }
   }
    
    // getter (필요 시 controller 에서 버튼 접근 가능하게)
    public JButton getThrowYutButton() {
        return throwYutButton;
    }

    public JButton getDeployPieceButton() {
        return deployPieceButton;
    }
    
    public JButton getNextTurnButton() {
        return nextTurnButton;
    }

    public Map<String, JButton> getYutChoiceButtons() {
        return yutChoiceButtons;
    }
}
