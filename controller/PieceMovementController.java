package yutgame.controller;

import yutgame.model.Piece;
import yutgame.model.YutThrowResult;

/**
 * Moves a piece on board according to throw result.
 */
public class PieceMovementController {
    public void movePiece(Piece piece, YutThrowResult result) {
        int steps;
        switch(result) {
            case BACKDO: steps = -1; break;
            case DO:     steps = 1;  break;
            case GAE:    steps = 2;  break;
            case GEO:    steps = 3;  break;
            case YUT:    steps = 4;  break;
            case MO:     steps = 5;  break;
            default:     steps = 0;
        }
        piece.move(steps);
    }
}
