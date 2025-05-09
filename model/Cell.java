package yutgame.model;

import java.util.*;

public class Cell {
    public enum Path { OUTER, DIAGONAL }

    private final int id;                        // 0 = 출발, 20 = 도착 …
    private final Map<Path, Cell> next = new EnumMap<>(Path.class);
    private final List<Piece> horses = new ArrayList<>();
    private final boolean branchEntrance;

    public Cell(int id, boolean branchEntrance) {
    	this.id = id;
    	this.branchEntrance = branchEntrance;
    	}

    /* ------------ 링크/조회 ------------ */
    public void setNext(Path p, Cell target) { next.put(p, target); }
    public Cell next(Path p) { return next.get(p); }
    public boolean isBranch() { return next.size() > 1; }
    public int getId() { return id; }
    public boolean isBranchEntrance() { return branchEntrance; }

    /* ------------ 말 관리 ------------ */
    public void enter(Piece h) { horses.add(h); }
    public void leave(Piece h) { horses.remove(h); }
    public List<Piece> horses() { return horses; }
}
