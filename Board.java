package yutgame.model;

/**
 * Represents the game board and its shape.
 */
public class Board {
    public enum Shape { RECTANGLE, PENTAGON, HEXAGON }
    private Shape shape;
    private Cell startCell;

    public Board(Shape shape) {
        this.shape = shape;
        // TODO: initialize paths based on shape
    }

    public Shape getShape() { return shape; }
    public Cell getStartCell() { return startCell; }
}
