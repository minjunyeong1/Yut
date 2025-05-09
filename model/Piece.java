package yutgame.model;

/**
 * Represents a single game piece.
 */
public class Piece {
    private Player owner;
    private Cell position; // 현재 칸 (null == 도착 후 제거)


    public Piece(Player owner, Cell startCell) {
        this.owner = owner;
        this.position = startCell;
    }

    public void move(int steps) {
    	for(int i = 0; i < steps && position != null; i++) {
    		Cell next;
    		boolean firstStep = (i == 0);
    		
    		if (firstStep && position.isBranchEntrance() && position.next(Cell.Path.DIAGONAL) != null) {
    			// 첫번째 스탭 + 분기입구(isBranchEntrance + next cell을 DIAGONAL Path로 갈 수 있는지)
    			// -> 지름길
    			next = position.next(Cell.Path.DIAGONAL);
    		}
    		else {
    			// 그 외 -> 가던 길 계속
    			next = position.next(Cell.Path.OUTER);
    		}
    		
    		// 결승선 도착 처리
    		if (next == null) {
    			position.leave(this);
    			position = null;
    			return;
    		}
    		
    		//실제 말 이동
    		position.leave(this);
    		position = next;
    		position.enter(this);
    	}
    }	
    
    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
}
