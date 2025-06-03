package yutgame.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JunitPent {

    private GameModel     model;
    private PentagonBoard board;
    private Player        p1;
    private Piece         piece;

    @BeforeEach
    void setUp() {
        model = new GameModel(new GameConfig(2, 4, Board.Shape.PENTAGON));
        board = (PentagonBoard) model.getBoard();
        p1    = model.getPlayers().get(0);
        piece = p1.getPieces().get(0);
    }

    /*───────────────────────────── 분기점 케이스 ─────────────────────────────*/
    @ParameterizedTest(name = "분기 {0}")
    @MethodSource("branchProvider")
    @DisplayName("분기점 이동 검증")
    void branchTest(String title, int startId, int[] moves, Integer expectedId) {
        place(piece, startId);
        moveSeq(piece, moves);
        if (expectedId == null) {
            assertNull(piece.getPosition(), title + " : 완주 후 위치가 null이어야 합니다.");
            assertTrue(piece.isFinished(),   title + " : isFinished()가 true여야 합니다.");
        } else {
            assertEquals(expectedId.intValue(), piece.getPosition().getId(), title);
        }

    }

    private static Stream<Arguments> branchProvider() {
        return Stream.of(
            /* 분기점1 */
            Arguments.of("5→26"           , 5 , new int[]{1}       , 26),
            Arguments.of("5→35"           , 5 , new int[]{4}       , 35),
            Arguments.of("5→32→33"        , 5 , new int[]{3,1}     , 33),
            Arguments.of("4→6"            , 4 , new int[]{2}       , 6 ),
            /* 분기점2 */
            Arguments.of("8→11"           , 8 , new int[]{3}       , 11),
            Arguments.of("8→10→32→33"    , 8 , new int[]{2,3,1}   , 33),
            Arguments.of("8→10→35"        , 8 , new int[]{2,4}     , 35),
            /* 분기점3 */
            Arguments.of("14→16"          , 14, new int[]{2}       , 16),
            Arguments.of("14→15→32→33"   , 14, new int[]{1,3,1}   , 33),
            Arguments.of("14→15→35"       , 14, new int[]{1,4}     , 35),
            /* 분기점4 */
            Arguments.of("35→20→21"       , 35, new int[]{2,1}     , 21),
            Arguments.of("19→21"          , 19, new int[]{2}       , 21),
            /* 분기점5 */
            Arguments.of("24→25→Goal"     , 24, new int[]{1,1}     , null),
            Arguments.of("34→25→Goal"     , 34, new int[]{1,1}     , null)
        );
    }

    /*───────────────────────────── 백도 케이스 ─────────────────────────────*/
    @ParameterizedTest(name = "백도 {0}")
    @MethodSource("backProvider")
    @DisplayName("백도 로직 검증")
    void backdoTest(String title, int startId, int[] moves, int expectedId) {
        place(piece, startId);
        moveSeq(piece, moves);
        if (expectedId == -1) {
            assertTrue(piece.isFinished(), title + " : 완주하지 않음");
        } else {
            assertEquals(expectedId, piece.getPosition().getId(), title);
        }
    }

    private static Stream<Arguments> backProvider() {
        int B = YutThrowResult.BACKDO.getValue();
        return Stream.of(
            Arguments.of("6→5→26"        , 6 , new int[]{B,1}    , 26),
            Arguments.of("6→5→4"         , 6 , new int[]{B,B}    , 4 ),
            Arguments.of("26→5→4"        , 26, new int[]{B,B}    , 4 ),
            Arguments.of("11→10→28"      , 11, new int[]{B,1}    , 28),
            Arguments.of("11→10→9"       , 11, new int[]{B,B}    , 9 ),
            Arguments.of("28→10→9"       , 28, new int[]{B,B}    , 9 ),
            Arguments.of("16→15→30"      , 16, new int[]{B,1}    , 30),
            Arguments.of("16→15→14"      , 16, new int[]{B,B}    , 14),
            Arguments.of("30→15→14"      , 30, new int[]{B,B}    , 14),
            Arguments.of("20→36"         , 20, new int[]{B}      , 36),
            Arguments.of("33→32→31"      , 33, new int[]{B,B}    , 31),
            Arguments.of("35→32→31"      , 35, new int[]{B,B}    , 31)
        );
    }

    /*───────────────────────── 헬퍼 메서드 ─────────────────────────*/
    /** 특정 셀에 말을 강제 배치 – 스택・캡처 모두 비활성 */
    private void place(Piece pc, int cellId) {
        Cell target = board.get(cellId);
        if (pc.getPosition() != null) pc.getPosition().leave(pc);
        pc.setPosition(target);
        target.enter(pc, false, false);
    }

    /** 이동 시퀀스를 순차 적용 */
    private void moveSeq(Piece pc, int... moves) {
        for (int m : moves) pc.move(m);
    }
}
