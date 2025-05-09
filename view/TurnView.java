package yutgame.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TurnView extends JPanel {
    private JLabel turnArrowLabel;
    private List<Point> playerIconPositions;

    public TurnView() {
        setLayout(null);
        setOpaque(false);
        
        // 화살표 아이콘
        ImageIcon arrowIcon = new ImageIcon(getClass().getResource("/yutgame/img/player_turn.png"));
        turnArrowLabel = new JLabel(arrowIcon);
        add(turnArrowLabel);

        // 플레이어 말 아이콘들의 위치 저장 (예시 좌표, 실제 아이콘 위치 기준으로 조정 필요)
        playerIconPositions = new ArrayList<>();
        playerIconPositions.add(new Point(-15, -3)); // 파랑
        playerIconPositions.add(new Point(45, -3)); // 초록
        playerIconPositions.add(new Point(105, -3)); // 빨강
        playerIconPositions.add(new Point(165, -3)); // 노랑

        // 초기 위치
        Point p = playerIconPositions.get(0);
        turnArrowLabel.setBounds(p.x, p.y, arrowIcon.getIconWidth(), arrowIcon.getIconHeight());
    }

    // 호출 시 턴에 맞게 화살표 위치 이동
    public void updateTurn(int turnIndex) {
        if (turnIndex >= 0) {
        	System.out.print("턴"+ turnIndex+"\n");
            Point p = playerIconPositions.get(turnIndex);
            turnArrowLabel.setLocation(p.x, p.y); // 아이콘 위쪽에 배치
            repaint();
        }
    }
}


