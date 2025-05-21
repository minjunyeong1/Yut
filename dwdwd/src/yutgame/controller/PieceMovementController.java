package yutgame.controller;

import java.util.List;
import yutgame.model.Piece;
import yutgame.model.YutThrowResult;

public class PieceMovementController {
    public List<Piece> movePiece(Piece piece, YutThrowResult result) {
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
        return piece.move(steps);  // ✅ 잡힌 말들 반환
    }
}
