package yutgame.controller;

import java.util.*;
import yutgame.model.Piece;
import yutgame.model.YutThrowResult;

/**
 * Moves a piece on board according to throw result.
 */
public class PieceMovementController {
    public boolean movePiece(Piece piece, YutThrowResult result) {
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
        
        // 말 이동 + 잡기
        List<Piece> captured = piece.move(steps);
        
        // 추가 턴 조건
        boolean extraThrowFromResult = (result == YutThrowResult.YUT || result == YutThrowResult.MO);
        boolean extraThrowFromCapture = !captured.isEmpty();
        
        return extraThrowFromResult || extraThrowFromCapture;
    }
}