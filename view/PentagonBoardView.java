package yutgame.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PentagonBoardView extends AbstractBoardView {
    private JButton[][] panButton = new JButton[1][6];
    private JLabel lineLabel;
    public PentagonBoardView() {
        setLayout(null);
        addCommonButtons();  // ← 이거 반드시 있어야 함!
    }
    
    @Override
    protected void buildBoard() {
        setLayout(null);
        int bs = windowSizeX / 20;
        int index = 1;  // 버튼 생성 순서 카운터     
        ImageIcon lineIcon = new ImageIcon(getClass().getResource("/yutgame/img/line_pen_(4).png"));
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/yutgame/img/startcircle.jpg"));
        ImageIcon bigCircleIcon = new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg"));
        ImageIcon circleIcon = new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg"));

        // 오각형 중심 좌표
        int imgX = windowSizeX / 2 - lineIcon.getIconWidth() / 2 - 260;
        int imgY = windowSizeY / 2 - lineIcon.getIconHeight() / 2 - 130;
        int cx = imgX + lineIcon.getIconWidth() / 2;
        int cy = imgY + lineIcon.getIconHeight() / 2;

        int radius = bs * 42 / 10;

        //외곽 위치
        java.util.List<Point> outerPoints = new ArrayList<>();

        // 외곽
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(306 + i * 72);
            int x = (int) (cx + radius * Math.cos(angle));
            int y = (int) (cy - radius * Math.sin(angle)) + 20;

            ImageIcon icon = (i == 0) ? startIcon : bigCircleIcon;

            JButton btn = new JButton(icon);
            btn.setBounds(x - bs / 2, y - bs / 2, bs, bs);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);

            panButton[0][i] = btn;
            btn.setToolTipText("생성 순서: " + index++);
            add(btn);

            outerPoints.add(new Point(x, y));
        }

        //중심 
        JButton centerBtn = new JButton(bigCircleIcon);
        centerBtn.setBounds(cx - bs / 2 + 2, cy - bs / 2 + 20, bs, bs);
        centerBtn.setContentAreaFilled(false);
        centerBtn.setBorderPainted(false);
        centerBtn.setFocusPainted(false);
        centerBtn.setOpaque(false);

        panButton[0][5] = centerBtn;
        centerBtn.setToolTipText("생성 순서: " + index++);
        add(centerBtn);

        //외곽-외곽 원 
        for (int i = 0; i < 5; i++) {
            Point p1 = outerPoints.get(i);
            Point p2 = outerPoints.get((i + 1) % 5);

            for (int j = 1; j <= 4; j++) {
                double ratio = j / 5.0;
                int x = (int) (p1.x * (1 - ratio) + p2.x * ratio);
                int y = (int) (p1.y * (1 - ratio) + p2.y * ratio);

                JButton btn = new JButton(circleIcon);
                btn.setBounds(x - bs / 2, y - bs / 2, bs, bs);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);
                btn.setToolTipText("생성 순서: " + index++);
                add(btn);
            }
        }

        // 외곽-중앙 원
        for (int i = 0; i < 5; i++) {
            Point p = outerPoints.get(i);

            for (int j = 1; j <= 2; j++) {
                double ratio = j / 3.0;
                int x = (int) (p.x * (1 - ratio) + cx * ratio);
                int y = (int) (p.y * (1 - ratio) + (cy + 20) * ratio);

                JButton btn = new JButton(circleIcon);
                btn.setBounds(x - bs / 2, y - bs / 2, bs, bs);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);
                btn.setToolTipText("생성 순서: " + index++);
                add(btn);
            }
        }

        
        lineLabel = new JLabel(lineIcon);
        lineLabel.setBounds(imgX, imgY, lineIcon.getIconWidth(), lineIcon.getIconHeight());
        add(lineLabel, 0);

        repaint();
        setComponentZOrder(lineLabel, getComponentCount() - 1);
    }
}
