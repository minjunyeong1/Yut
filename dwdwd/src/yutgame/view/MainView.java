package yutgame.view;

import yutgame.model.GameModel;
import yutgame.controller.SettingController;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private TurnView turnView;
    private AbstractBoardView boardView;
    private YutResultView yutResultView;

    public MainView(AbstractBoardView boardView) {
        super("Yut Play");
        this.boardView = boardView;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);               // 절대좌표
        setResizable(false);           // 크기 고정

        // ── 보드 영역 ───────────────────────────────
        boardView.setBounds(10, 10, 950, 700);
        add(boardView);

        // TurnView: 보드 위에 배치
        turnView = new TurnView();
        turnView.setBounds(500, 10, 235, 80);
        boardView.add(turnView);

        // ── 오른쪽 패널 (결과창 전용) ─────────────────
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBounds(970, 10, 200, 700); 
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // YutResultView: 패널 전체를 채움
        yutResultView = new YutResultView();
        rightPanel.add(yutResultView, BorderLayout.CENTER);

        add(rightPanel);

        // ── 전체 창 크기 ─────────────────────────────
        setSize(1180, 750);
        setLocationRelativeTo(null);
    }

    public TurnView getTurnView() {
        return turnView;
    }

    public AbstractBoardView getBoardView() {
        return boardView;
    }

    public YutResultView getYutResultView() {
        return yutResultView;
    }
    
    public void handleWinCondition(String winnerName) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                winnerName + "님이 승리했습니다!\n 다시 시작하시겠습니까?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new SettingController());
        } else {
            System.exit(0);
        }
    }
}