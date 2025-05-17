package yutgame.model;

import java.util.*;

public class RectangleBoard extends Board{

    /* === 노드 테이블 ==================================================== */
    private final Map<Integer, Cell> cells = new HashMap<>();

    /** 외부에서 읽기 전용으로 전체 셀을 가져올 때 */
    public Collection<Cell> all() { return Collections.unmodifiableCollection(cells.values()); }
    public Cell get(int id)       { return cells.get(id); }

    /* === 생성자: 노드 생성 → 링크 연결 =================================== */
    public RectangleBoard() {
    	super(Shape.RECTANGLE);
        createNodes();
        linkOuterRing();
        linkDiagonals();
        this.startCell = cells.get(0);
        System.out.println("startCell = " + (startCell == null ? "null" : startCell.getId()));
    }

    /* -------------------------------------------------------------------- */
    /* 1) 노드 생성 : 0-20(바깥), 30-38(안), 99(도착) */
    private void createNodes() {
        for (int i = 0; i <= 20; i++) {
            cells.put(i, new Cell(i, false));
        }

        for (int i = 30; i <= 31; i++) cells.put(i, new Cell(i, false));
        for (int i = 33; i <= 38; i++) cells.put(i, new Cell(i, false));

        cells.put(5,  new Cell(5, true));   // 분기점
        cells.put(10, new Cell(10, true));  // 분기점
        cells.put(32, new Cell(32, true));  // 중앙
        //cells.put(99, new Cell(99, false)); // 도착점
    }


    /* 2) 외곽 링 연결 : 0→1→2 … 20→99(도착) */
    private void linkOuterRing() {
        for (int i = 0; i < 20; i++)
            link(i, Cell.Path.OUTER, i + 1);
        //link(20, Cell.Path.OUTER, 99);  // 마지막 칸에서 도착으로
    }

    /* 3) 지름길 연결 : 분기점 5·10에서 각각 중앙(32)으로 합류          *
     *  ── 5 → 30 → 31 → 32 → 35 → 36 → 15
     *  					→ 37 → 38 → 20 → 99
     *  ──10 → 33 → 34 → 32 → 37 → 38 → 20 → 99
     */
    private void linkDiagonals() {
        // 5번 분기 첫번째
        link(5,  Cell.Path.DIAGONAL, 30);
        link(30, Cell.Path.OUTER,    31);
        link(31, Cell.Path.OUTER,    32);
        link(32, Cell.Path.DIAGONAL, 37);
        link(37, Cell.Path.OUTER,    38);
        link(38, Cell.Path.OUTER,    20);

        // 5번 분기 두번째
        link(5,  Cell.Path.DIAGONAL, 30);
        link(30, Cell.Path.OUTER,    31);
        link(31, Cell.Path.OUTER,    32);
        link(32, Cell.Path.OUTER,    35);
        link(35, Cell.Path.OUTER,    36);
        link(36, Cell.Path.OUTER,    15);
        
        // 10번 분기
        link(10, Cell.Path.DIAGONAL, 33);
        link(33, Cell.Path.OUTER,    34);
        link(34, Cell.Path.OUTER,    32);
        //link(32, Cell.Path.OUTER,    37);
        //link(37, Cell.Path.OUTER,    38);
        //link(38, Cell.Path.OUTER,    20);
    }

    /* -------------------------------------------------------------------- */
    /** 셀 두 개를 경로(Path)로 연결하는 헬퍼 */
    private void link(int fromId, Cell.Path via, int toId) {
        cells.get(fromId).setNext(via, cells.get(toId));
    }
    // 말 초기 위치 설정 게터
    public Cell getStartCell() {
    	return cells.get(0);
    }
}
