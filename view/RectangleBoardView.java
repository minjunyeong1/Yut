package yutgame.view;

import javax.swing.*;
import java.awt.*;

public class RectangleBoardView extends AbstractBoardView {
    private JButton[][] panButton = new JButton[3][21];
    private JLabel lineLabel;

    public RectangleBoardView() {
        setLayout(null);
        addCommonButtons();  // ← 이거 반드시 있어야 함!
    }
    
    @Override
    protected void buildBoard() {
        int bsx = windowSizeX / 20;
        int bsy = bsx;
        double interval = bsx * 1.25;

        
        // 대각선 출력
        lineLabel = new JLabel(new ImageIcon(getClass().getResource("/yutgame/img/line.png")));
        lineLabel.setBounds(52, 50, 330, 332);
        add(lineLabel);

        // 바깥쪽 20개
        int xpos = bsx * 7;
        int ypos = bsy * 7;

        for (int i = 1; i <= 20; i++) {
            if      (i <  6) ypos -= interval;
            else if (i < 11) xpos -= interval;
            else if (i < 16) ypos += interval;
            else              xpos += interval;

            ImageIcon icon = (i==5||i==10||i==15)
                ? new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg"))
                : (i==20)
                  ? new ImageIcon(getClass().getResource("/yutgame/img/startcircle.jpg"))
                  : new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg"));

            JButton btn = new JButton(icon);
            btn.setBounds(xpos, ypos, bsx, bsy);
            // → 버튼 배경/테두리 제거
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);

            panButton[0][i] = btn;
            add(btn);
        }

        // 오른쪽 대각선
        xpos = bsx * 7 - 10;
        ypos = bsy - 10;

        for (int p = 0; p < 6; p++) {
            if(p == 3){
                xpos -= bsx;
				ypos += bsy;
            }

            else{
                if(p == 0){
                    xpos -= bsx;
					ypos += bsy;
                }

                else{
                    JButton btn = new JButton(new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg")));
                    btn.setBounds(xpos, ypos, bsx, bsy);
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                    btn.setFocusPainted(false);
                    btn.setOpaque(false);

                    panButton[1][p] = btn;
                    add(btn);

                    xpos -= bsx;
                    ypos += bsy;    
                }
            }
        }

        // 왼쪽 대각선
        xpos = bsx - 10;
        ypos = bsy - 10;

        for (int p = 0; p < 7; p++) {
            if(p == 0){
                xpos += bsx;
                ypos += bsy;
            }

            else{
                if(p == 3){
                    JButton btn = new JButton(new ImageIcon(getClass().getResource("/yutgame/img/bigcircle.jpg")));
                    btn.setBounds(xpos, ypos, bsx, bsy);
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                    btn.setFocusPainted(false);
                    btn.setOpaque(false);

                    panButton[2][p] = btn;
                    add(btn);
                }

                else{
                    JButton btn = new JButton(new ImageIcon(getClass().getResource("/yutgame/img/circle.jpg")));
                    btn.setBounds(xpos, ypos, bsx, bsy);
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                    btn.setFocusPainted(false);
                    btn.setOpaque(false);

                    panButton[2][p] = btn;
                    add(btn);

                    xpos += bsx;
                    ypos += bsy;
                }
            }
        }

        
        setComponentZOrder(lineLabel, getComponentCount() - 1);
        repaint();
    }
}