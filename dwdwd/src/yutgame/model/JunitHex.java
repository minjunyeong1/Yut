package yutgame.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JunitHex {

    private GameModel    model;
    private HexagonBoard board;
    private Player       p1;
    private Piece        piece;

    @BeforeEach
    void init() {
        model = new GameModel(new GameConfig(2, 4, Board.Shape.HEXAGON));
        board = (HexagonBoard) model.getBoard();
        p1    = model.getPlayers().get(0);
        piece = p1.getPieces().get(0);
    }

    /*─────────────────────────────── 분기점 ───────────────────────────────*/
    @ParameterizedTest(name = "분기 {0}")
    @MethodSource("branchProvider")
    @DisplayName("분기점 이동")
    void branchTest(String title, int startId, int[] moves, Integer expected) {
        place(piece, startId);
        moveSeq(piece, moves);
        if (expected == null) {
            assertTrue(piece.isFinished(), title + " : 완주하지 않음");
        } else {
            assertEquals(expected.intValue(), piece.getPosition().getId(), title);
        }
    }

    private static Stream<Arguments> branchProvider() {
        return Stream.of(
            /* 분기점1 */
            Arguments.of("5→31",           5 , new int[]{1}       , 31),
            Arguments.of("5→42",           5 , new int[]{4}       , 40),
            Arguments.of("5→37→38",        5 , new int[]{3,1}     , 38),
            Arguments.of("4→6",            4 , new int[]{2}       , 6 ),
            /* 분기점2 */
            Arguments.of("8→11",           8 , new int[]{3}       , 11),
            Arguments.of("8→10→37→38",    8 , new int[]{2,3,1}   , 38),
            Arguments.of("8→10→40",        8 , new int[]{2,4}     , 40),
            /* 분기점3 */
            Arguments.of("14→16",          14, new int[]{2}       , 16),
            Arguments.of("14→15→37→38",   14, new int[]{1,3,1}   , 38),
            Arguments.of("14→15→40",       14, new int[]{1,4}     , 40),
            /* 분기점4 */
            Arguments.of("19→21",          19, new int[]{2}       , 21),
            Arguments.of("19→20→37→38",   19, new int[]{1,3,1}   , 38),
            Arguments.of("19→20→40",       19, new int[]{1,4}     , 40),
            /* 분기점5 */
            Arguments.of("40→25→26",       40, new int[]{2,1}     , 26),
            Arguments.of("24→26",          24, new int[]{2}       , 26),
            /* 분기점6 (골) */
            Arguments.of("29→30→Goal",     29, new int[]{1,1}     , null),
            Arguments.of("39→30→Goal",     39, new int[]{1,1}     , null)
        );
    }

    /*─────────────────────────────── 백도 ────────────────────────────────*/
    @ParameterizedTest(name = "백도 {0}")
    @MethodSource("backProvider")
    @DisplayName("백도 이동")
    void backdoTest(String title, int startId, int[] moves, Integer expected) {
        place(piece, startId);
        moveSeq(piece, moves);
        if (expected == null) {
            assertTrue(piece.isFinished(), title);
        } else {
            assertEquals(expected.intValue(), piece.getPosition().getId(), title);
        }
    }

    private static Stream<Arguments> backProvider() {
        int B = YutThrowResult.BACKDO.getValue();
        return Stream.of(
            Arguments.of("6→5→31",        6 , new int[]{B,1}    , 31 ),
            Arguments.of("6→5→4",         6 , new int[]{B,B}    , 4  ),
            Arguments.of("31→5→4",        31, new int[]{B,B}    , 4  ),
            Arguments.of("11→10→33",      11, new int[]{B,1}    , 33 ),
            Arguments.of("11→10→9",       11, new int[]{B,B}    , 9  ),
            Arguments.of("33→10→9",       33, new int[]{B,B}    , 9  ),
            Arguments.of("16→15→35",      16, new int[]{B,1}    , 35 ),
            Arguments.of("16→15→14",      16, new int[]{B,B}    , 14 ),
            Arguments.of("35→15→14",      35, new int[]{B,B}    , 14 ),
            Arguments.of("21→20→43",      21, new int[]{B,1}    , 43 ),
            Arguments.of("21→20→19",      21, new int[]{B,B}    , 19 ),
            Arguments.of("43→20→19",      43, new int[]{B,B}    , 19 ),
            Arguments.of("26→25",         26, new int[]{B}      , 25 ),
            Arguments.of("26→25→41",      26, new int[]{B,B}    , 41 ),
            Arguments.of("40→37→38",      40, new int[]{B,1}    , 38 )
        );
    }

    /*────────────────────────────── 헬퍼 ───────────────────────────────*/
    private void place(Piece pc, int id) {
        Cell target = board.get(id);
        if (pc.getPosition() != null) pc.getPosition().leave(pc);
        pc.setPosition(target);
        target.enter(pc, false, false);
    }

    private void moveSeq(Piece pc, int... moves) {
        for (int m : moves) pc.move(m);
    }
}