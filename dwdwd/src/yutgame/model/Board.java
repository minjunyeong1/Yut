package yutgame.model;

/**
 * Represents the game board and its shape.
 */
public class Board {
    public enum Shape { RECTANGLE, PENTAGON, HEXAGON }
    private Shape shape;
    protected Cell startCell;
    protected Cell lastCell;
    

    public Board(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() { return shape; }
    public Cell getStartCell() { return startCell; }
    public Cell getLastCell() {return lastCell;}
}
