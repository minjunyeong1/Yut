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
    private int stackSize = 1;

    public Piece(Player owner, Cell startCell) {
        this.owner = owner;
        this.position = startCell;
        if (startCell != null)
            startCell.enter(this, false, false);
    }

    /*--------------- 업기 보조 method------------------*/
    public boolean isLeader() { return leader == null; }
    public void setLeader(Piece l) { this.leader = l; }

    void addPassenger(Piece p) {
        passengers.add(p);
        stackSize += 1; // 스택 증가
    }

    public List<Piece> detachGroup() { // 캡처 및 완주 시 사용
        List<Piece> grp = new ArrayList<>();
        grp.add(this);
        grp.addAll(passengers);
        for (Piece p : passengers) {
            p.leader = null; // 승객의 리더 해제
        }
        passengers.clear(); 
        leader = null;
        stackSize = 1; // 스택 초기화
        return grp;
    }

    /*--------------- 이동 ----------------*/
    public List<Piece> move(int steps) {
        if (!isLeader()) return List.of(); // 승객이면 이동 금지

        List<Piece> captured = new ArrayList<>();
        int absSteps = Math.abs(steps);
        int dir = (steps >= 0) ? 1 : -1;

        List<Piece> group = new ArrayList<>();
        group.add(this);
        group.addAll(passengers);  // ✅ passengers 유지한 채 이동

        for (int i = 0; i < absSteps && position != null; i++) {
            Cell next = (dir > 0) ? nextForwardCell(i == 0) : position.next(Cell.Path.OUTER);

            if (next == null) {
                for (Piece p : group) position.leave(p);
                for (Piece p : group) p.setPosition(null);
                return captured;
            }

            for (Piece p : group) position.leave(p);
            position = next;

            for (Piece p : group) p.setPosition(position);

            boolean last = (i == absSteps - 1);
            captured.addAll(position.enter(this, last, last));
        }

        return captured;
    }


    private Cell nextForwardCell(boolean firstStep) {
        if (firstStep && position.isBranchEntrance() && position.next(Cell.Path.DIAGONAL) != null) {
            return position.next(Cell.Path.DIAGONAL);
        }
        return position.next(Cell.Path.OUTER);
    }

    public Player getOwner() { return owner; }
    public Cell getPosition() { return position; }
    public List<Piece> getPassengers() { return passengers; }
    public boolean isFinished() { return position == null; }
    public void setPosition(Cell position) { this.position = position; }
    public Piece getLeader() { return leader; }
    public int getStackSize() { return stackSize; }
    public void resetStackSize() { stackSize = 1; }
}
