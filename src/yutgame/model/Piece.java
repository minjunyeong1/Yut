package yutgame.model;

import java.util.*;

/**
 * Represents a single game piece.
 */
public class Piece {
    private Player owner;
    private Cell position; // 현재 칸 (null == 도착 후 제거)
    private Cell.Path lastPath = Cell.Path.OUTER; // 마지막으로 진행한 경로
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
        int dir = (steps >= 0) ? 1 : -1;
        int absSteps = Math.abs(steps);

        List<Piece> group = new ArrayList<>();
        group.add(this);
        group.addAll(passengers);  // ✅ passengers 유지한 채 이동

        for (int i = 0; i < absSteps && position != null; i++) {
            Cell from = position;
            Cell next;

            if (dir > 0) {
                if (i == 0) {
                    int consumed = applySpecialRoute(absSteps);
                    if (consumed > 0) {
                        i += consumed - 1;  // ✅ consumed만큼 이미 이동했다고 간주
                        continue;
                    }
                }

                next = nextForwardCell(i == 0);

                // 경로 기록: 대각선 진입 시 기록해 둠
                lastPath = (from.next(Cell.Path.DIAGONAL) == next)
                    ? Cell.Path.DIAGONAL
                    : Cell.Path.OUTER;

            } else {
                // 역방향 이동 시: lastPath 기준 prev 시도
                Cell prev = position.prev(lastPath);

                // fallback: DIAGONAL → OUTER 순으로 시도
                if (prev == null) {
                    prev = position.prev(Cell.Path.DIAGONAL);
                    if (prev != null) lastPath = Cell.Path.DIAGONAL;
                    else {
                        prev = position.prev(Cell.Path.OUTER);
                        if (prev != null) lastPath = Cell.Path.OUTER;
                    }
                }
                next = prev;
            }

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

    private int applySpecialRoute(int steps) {
        Board board = owner.getModel().getBoard();
        if (board.getShape() != Board.Shape.RECTANGLE) return 0;
        if (!(board instanceof RectangleBoard rectBoard)) return 0;

        int pid = position.getId();

        int consumed = 0;
        if (pid == 10 && steps >= 4) consumed = 4;
        else if (pid == 33 && steps >= 3) consumed = 3;
        else if (pid == 34 && steps >= 2) consumed = 2;

        if (consumed > 0) {
            Cell target = rectBoard.getCell(37);
            if (target != null) {
                for (Piece p : passengers) position.leave(p);
                position.leave(this);
                position = target;
                for (Piece p : passengers) p.setPosition(target);
                setPosition(target);
                return consumed;
            }
        }

        return 0;
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