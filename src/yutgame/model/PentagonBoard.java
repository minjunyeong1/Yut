package yutgame.model;

import java.util.*;

import yutgame.model.Board.Shape;

public class PentagonBoard extends Board{
    private final Map<Integer, Cell> cells = new HashMap<>();

    public PentagonBoard() {
    	super(Shape.PENTAGON);
        createNodes();
        linkOuterRing();
        linkDiagonals();
        this.startCell = cells.get(0);
    }

    /* 외부 조회 */
    public Collection<Cell> all() { return Collections.unmodifiableCollection(cells.values()); }
    public Cell get(int id)       { return cells.get(id); }
    public Cell getStartCell()    { return cells.get(0); }

    /* 1) 노드 생성 */
    private void createNodes() {
        for (int i = 0; i <= 4; i++) { cells.put(i, new Cell(i, false)); } // 바깥 지름길 제외 0-4
        for (int i = 6; i <= 9; i++) { cells.put(i, new Cell(i, false)); } // 바깥 지름길 제외 6-9
        for (int i = 11; i <= 14; i++) { cells.put(i, new Cell(i, false)); } // 바깥 지름길 제외 11-14
        for (int i = 16; i <= 25; i++) { cells.put(i, new Cell(i, false)); } // 바깥 지름길 제외 16-25
        for (int i = 26; i <= 31; i++) { cells.put(i, new Cell(i, false)); } // 안 지름길 제외 26-31
        for (int i = 33; i <= 36; i++) { cells.put(i, new Cell(i, false)); } // 바깥 지름길 제외 33-36
        cells.put(5, new Cell(5, true));									// 지름길 5
        cells.put(10, new Cell(10, true));									// 지름길 10
        cells.put(15, new Cell(15, true));									// 지름길 15
        cells.put(32, new Cell(32, true));									// 지름길 32
        //cells.put(99, new Cell(99, false));									// 도착(공통 종점)
    }

    /* 2) 외곽 링 연결: 0→1→2…→19→99 */
    private void linkOuterRing() {
        for (int i = 0; i < 25; i++) {
            link(i, Cell.Path.OUTER, i + 1);
        }
        //link(25, Cell.Path.OUTER, 99); // 마지막 칸에서 도착으로
    }

    /* 3) 지름길 연결 */
    private void linkDiagonals() {
        // 입구 5 → 26 → 27 → 32 → 33 → 34 
        link(5, Cell.Path.DIAGONAL, 26);
        link(26, Cell.Path.OUTER,    27);
        link(27, Cell.Path.OUTER,    32);
        link(32, Cell.Path.DIAGONAL, 33);
        link(33, Cell.Path.OUTER,    34);
        link(34, Cell.Path.OUTER,    25);

        // 입구 5 → 26 → 27 → 32 → 35 → 36
        link(32, Cell.Path.OUTER, 	35);
        link(35, Cell.Path.OUTER, 	36);
        link(36, Cell.Path.OUTER, 	20);
        
        // 입구 11 → 29 → 30 → 33
        link(10, Cell.Path.DIAGONAL, 28);
        link(28, Cell.Path.OUTER,    29);
        link(29, Cell.Path.OUTER,    32);

        // 입구 15 → 30 → 31 → 32 
        link(15, Cell.Path.DIAGONAL, 30);
        link(30, Cell.Path.OUTER,    31);
        link(31, Cell.Path.OUTER,    32);
        
        // 도착
        //link(25, Cell.Path.OUTER,    99);
    }

    /** 헬퍼 */
    private void link(int fromId, Cell.Path via, int toId) {
        cells.get(fromId).setNext(via, cells.get(toId));
    }
    public Cell getLastCell() {
        return cells.get(29); 
    }
}
