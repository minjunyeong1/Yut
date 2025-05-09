package yutgame.view;

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
        setLayout(null); // 절대 좌표 배치 사용

        // ── 중앙: 보드 영역 ───────────────────────────
        boardView.setBounds(10, 10, 950, 700);
        add(boardView);

        // ── 오른쪽: 턴 정보 및 결과 영역 ────────────────
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBounds(810, 10, 260, 700);

        // 플레이어 정보
        PlayerInfoView playerInfoView = new PlayerInfoView();
        playerInfoView.setBounds(0, 0, 260, 150);
        rightPanel.add(playerInfoView);


        // 기존 코드 수정
        turnView = new TurnView();
        turnView.setBounds(500, 10, 235, 80); // boardView 내 위치 조정
        boardView.add(turnView);            


        // 윷 결과 표시
        yutResultView = new YutResultView();
        yutResultView.setBounds(150, 140, 300, 300);
        rightPanel.add(yutResultView);

        add(rightPanel);

        setSize(1100, 750);
        setLocationRelativeTo(null); // 화면 중앙
        setResizable(false);
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
}
