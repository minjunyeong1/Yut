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
    public int getId() { return id; }
    public boolean isBranchEntrance() { return branchEntrance; }

    /* ------------ 말 관리 ------------ */
    public void leave(Piece h) { horses.remove(h); }
    public List<Piece> horses() { return horses; }
    public List<Piece> enter(Piece newcomer, boolean allowCapture, boolean allowStack){
    	List<Piece> captured = new ArrayList<>();
    	
    	// 상대 말 제거(캡쳐)
    	if(allowCapture) {
	    	Iterator<Piece> it = horses.iterator();
	    	while (it.hasNext()) {
	    		Piece p = it.next();
	    		if (p.getOwner() != newcomer.getOwner()) {
	    			captured.addAll(p.detachGroup());
	    			it.remove();
	    		}
	    	}
    	}
    	
    	// 우리 편 업기
    	if (allowStack) {
    		Optional<Piece> myLeader = horses.stream().filter(p -> p.getOwner() == newcomer.getOwner()).findFirst();
    		
    		if (myLeader.isPresent()) {
    			Piece leader = myLeader.get();
    			leader.addPassenger(newcomer);
    			newcomer.setLeader(leader);
    			return captured;
    		}
    	}
    	
    	// 중간 단계 or 합체나 잡기가 없는 경우
    	horses.add(newcomer);
    	return captured;
    }
}
