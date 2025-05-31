package yutgame.model;

import java.util.*;

import yutgame.model.Board.Shape;

public class HexagonBoard extends Board{
    private final Map<Integer, Cell> cells = new HashMap<>();

    public HexagonBoard() {
    	super(Shape.HEXAGON);
        createNodes();
        linkOuterRing();
        linkDiagonals();
        this.startCell = cells.get(0);
    }

    /* 전체 셀 읽기 전용 */
    public Collection<Cell> all() {
        return Collections.unmodifiableCollection(cells.values());
    }
    public Cell get(int id)       { return cells.get(id); }
    public Cell getStartCell()    { return cells.get(0); }

    /* 1) 노드 생성 */
    private void createNodes() {
    	for (int i = 0; i <= 5; i++) cells.put(i, new Cell(i, false));   // 바깥 지름길 제외 0-4
    	for (int i = 6; i <= 9; i++) cells.put(i, new Cell(i, false));   // 바깥 지름길 제외 6-9
    	for (int i = 11; i <= 14; i++) cells.put(i, new Cell(i, false));   // 바깥 지름길 제외 11-14
    	for (int i = 16; i <= 19; i++) cells.put(i, new Cell(i, false));   // 바깥 지름길 제외 16-19
    	for (int i = 21; i <= 30; i++) cells.put(i, new Cell(i, false));   // 바깥 지름길 제외 21-30
    	for (int i = 31; i <= 36; i++) cells.put(i, new Cell(i, false));   // 안 지름길 제외 31-36
    	for (int i = 38; i <= 43; i++) cells.put(i, new Cell(i, false));   // 안 지름길 제외 38-43
    	cells.put(5, new Cell(5, true)); 									// 지름길 5
    	cells.put(10, new Cell(10, true)); 									// 지름길 10
    	cells.put(15, new Cell(15, true)); 									// 지름길 15
    	cells.put(20, new Cell(20, true)); 									// 지름길 20
    	cells.put(37, new Cell(37, true)); 									// 지름길 37
        //cells.put(99, new Cell(99, false));        							// 도착(종점)
    }

    /* 2) 외곽 링 연결: 0→1→2…→19→99 */
    private void linkOuterRing() {
        for (int i = 0; i < 30; i++) {
            link(i, Cell.Path.OUTER, i + 1);
        }
        //link(30, Cell.Path.OUTER, 99);
    }

    /* 3) 지름길 연결 */
    private void linkDiagonals() {
        // 5 → 31 → 32 → 37 → 38 → 39 → 30
        link(5,  Cell.Path.DIAGONAL, 31);
        link(31, Cell.Path.OUTER,    32);
        link(32, Cell.Path.OUTER,    37);
        link(37,  Cell.Path.DIAGONAL,38);
        link(38, Cell.Path.OUTER,    39);
        link(39, Cell.Path.OUTER,    30);

        // 5 → 31 → 32 → 37 → 40 → 41 → 25
        link(37, Cell.Path.OUTER,    40);
        link(40, Cell.Path.OUTER,    41);
        link(41, Cell.Path.OUTER,    25);

        // 10 → 33 → 34 → 37
        link(10, Cell.Path.DIAGONAL, 33);
        link(33, Cell.Path.OUTER,    34);
        link(34, Cell.Path.OUTER,    37);

        // 15 → 35 → 36 → 37
        link(15, Cell.Path.DIAGONAL, 35);
        link(35, Cell.Path.OUTER,    36);
        link(36, Cell.Path.OUTER,    37);

        // 20 → 43 → 42 → 37
        link(20, Cell.Path.DIAGONAL, 43);
        link(43, Cell.Path.OUTER,    42);
        link(42, Cell.Path.OUTER,    37);
    }

    /** 헬퍼: fromId 셀의 via 경로로 toId 셀에 연결 */
    private void link(int fromId, Cell.Path via, int toId) {
        cells.get(fromId).setNext(via, cells.get(toId));
    }
    public Cell getLastCell() {
        return cells.get(24); 
    }
}