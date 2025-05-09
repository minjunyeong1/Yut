package yutgame.model;

import java.util.*;

/**
 * Represents a single game piece.
 */
public class Piece {
    private Player owner;
    private Cell position; // 현재 칸 (null == 도착 후 제거)
    private Piece leader;
    private final List<Piece> passengers = new ArrayList<>();


    public Piece(Player owner, Cell startCell) {
        this.owner = owner;
        this.position = startCell;
        if (startCell != null)
        	startCell.enter(this, false, false);
    }

    
    /*--------------- 업기 보조 method------------------*/
    // 리더여부
    boolean isLeader() { return leader == null; }
    // 리더를 지정
    void setLeader(Piece l) { this.leader = l; }
    // 말을 리더 위에 업기
    void addPassenger(Piece p) { passengers.add(p); }
    
    List<Piece> detachGroup(){ // 캡처 및 완주 시 사용
    	List<Piece> grp = new ArrayList<>();
    	grp.add(this);
    	grp.addAll(passengers);
    	passengers.clear();
    	leader = null; // 더 이상 승객이 아님
    	return grp;
    }
    
    
    /*--------------- 이동 ----------------*/
    public List<Piece> move(int steps) {
    	if(!isLeader()) return List.of(); // 승객이면 무시(이동금지)
    	
    	List<Piece> captured = new ArrayList<>();
    	
    	int absSteps = Math.abs(steps);
    	int dir = (steps >= 0) ? 1 : -1; // BACKDO용
    	
    	
    	for(int i = 0; i < steps && position != null; i++) {
    		// 다음 칸 계산
    		Cell next = (dir > 0) ? nextForwardCell(i == 0) : position.next(Cell.Path.OUTER);
    		
    		// 결승선 도착 처리
    		if (next == null) {
    			for (Piece p : detachGroup()) position.leave(p);
    			position = null;
    			return captured;
    		}
    		
    		// 현재 칸에서 떼어내기: 이렇게 짠 이유는 grouping된 말을 모두 해당 칸에서 제거해줘야함
    		for (Piece p : detachGroup()) position.leave(p);
    		position = next;
    		
    		boolean last	= (i == absSteps - 1);
    		
    		// 업기 및 캡처 처리
    		captured.addAll(position.enter(this, last, last));
    	}
    	return captured;
    }
    	
    private Cell nextForwardCell(boolean firstStep) {
    	if (firstStep && position.isBranchEntrance() && position.next(Cell.Path.DIAGONAL) != null) {
			// 첫번째 스탭 + 분기입구(isBranchEntrance + next cell을 DIAGONAL Path로 갈 수 있는지)
			// -> 지름길
    		return position.next(Cell.Path.DIAGONAL);
    	}
    	return position.next(Cell.Path.OUTER);
    }
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public List<Piece> getPassengers() { return passengers; }
    public boolean isFinished() { return position == null; }
}
