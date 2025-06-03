package yutgame.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * • 보드: {@link RectangleBoard}
 * • 보조 메서드 {@link #placePiece(Piece, int)} / {@link #moveSequence(Piece, int...)} 로 경로를 간결히 기술한다.
 */
public class JunitRect {

    /*───────────────────────── 공용 필드 ─────────────────────────*/
    private GameModel       model;
    private RectangleBoard  board;
    private Player          p1;
    private Player          p2;      // 잡기/업기 전용
    private Piece           piece;   // p1 의 첫 번째 말

    /*───────────────────────── 초기화 ───────────────────────────*/
    @BeforeEach
    void setup() {
        model  = new GameModel(new GameConfig(2, 4, Board.Shape.RECTANGLE));
        board  = (RectangleBoard) model.getBoard();
        p1     = model.getPlayers().get(0);
        p2     = model.getPlayers().get(1);
        piece  = p1.getPieces().get(0);
    }

    /*====================================================================*/
    /* 1. 기본 이동 (Do · Gae · Geo · Yut · Mo)                           */
    /*====================================================================*/
    @ParameterizedTest(name = "기본 이동 – {0}")
    @MethodSource("basicMoveProvider")
    @DisplayName("기본 전진 (도/개/걸/윷/모)")
    void testBasicForward(YutThrowResult res, int expectedId, boolean expectExtraTurn) {
        boolean added = p1.addYutResult(res);               // extra‑turn 로직 검증용
        piece.move(res.getValue());                          // 실제 전진

        // 위치 확인
        assertEquals(expectedId, piece.getPosition().getId(),
                "시작칸(0)에서 " + expectedId + "번 노드에 서야 합니다.");
        // 추가턴 확인
        assertEquals(expectExtraTurn, p1.canAddMoreResults(),
                "YUT/MO 인 경우에만 추가턴이 허용되어야 합니다.");
        // addYutResult 성공 여부 (YUT/MO 는 true, 나머지 false 가 아님)
        assertTrue(added, "addYutResult 가 false 를 리턴하면 안 됩니다.");
    }

    /* 기본 이동 시나리오 */
    static Stream<Arguments> basicMoveProvider() {
        return Stream.of(
                Arguments.of(YutThrowResult.DO , 1, false),
                Arguments.of(YutThrowResult.GAE, 2, false),
                Arguments.of(YutThrowResult.GEO, 3, false),
                Arguments.of(YutThrowResult.YUT, 4, true ),
                Arguments.of(YutThrowResult.MO , 5, true )
        );
    }

    /*====================================================================*/
    /* 2. 분기점 (Branch) 케이스                                           */
    /*====================================================================*/
    @ParameterizedTest(name = "분기 {0}: {1} → {2}")
    @MethodSource("branchCaseProvider")
    @DisplayName("분기점/지름길 이동")
    void testBranchMoves(String title, int startId, int[] steps, Integer expectedId) {
        placePiece(piece, startId, false);
        moveSequence(piece, steps);
        if (expectedId == null) {
            assertTrue(piece.isFinished(), title + " → 완주했어야 함");
            assertNull(piece.getPosition(), title + " → 완주한 말의 위치는 null이어야 함");
        } else {
            assertNotNull(piece.getPosition(), title + " → 말의 위치가 null이면 안 됨");
            assertEquals(expectedId.intValue(),piece.getPosition().getId(),title + " → 최종 위치 불일치");
        }

    }

    /** 분기점 이동 목록.
     *  • steps 는 순차 이동 값 배열이다.
     *  • −1 은 빽도, 나머지는 전진 칸 수. */
    static Stream<Arguments> branchCaseProvider() {
        return Stream.of(
            /* 분기점1 (4·5·6) */
            Arguments.of("4→6"          , 4 , new int[]{2}     , 6  ),
            Arguments.of("5→30"         , 5 , new int[]{1}     , 30 ),
            Arguments.of("5→35"         , 5 , new int[]{4}     , 35 ),
            Arguments.of("5→32→37"      , 5 , new int[]{3,1}   , 37 ),
            /* 분기점2 (8·10·11) */
            Arguments.of("8→11"         , 8 , new int[]{3}     , 11 ),
            Arguments.of("8→10→32→37"  , 8 , new int[]{2,3,1} , 37 ),
            Arguments.of("8→10→37"      , 8 , new int[]{2,4}   , 37 ),
            /* 분기점3 (14·15·16) */
            Arguments.of("35→15→16"     , 35, new int[]{2,1} , 16 ),
            Arguments.of("14→16"        , 14, new int[]{2}     , 16 ),
            /* 분기점4 (20) */
            Arguments.of("19→20→Goal"   , 19, new int[]{1,1}   , null ),
            Arguments.of("38→20→Goal"   , 38, new int[]{1,1} , null )
        );
    }

    /*====================================================================*/
    /* 3. 백도 (Back‑Do) 케이스                                            */
    /*====================================================================*/
    @ParameterizedTest(name = "백도 {0}: {1} → …")
    @MethodSource("backdoProvider")
    @DisplayName("백도(뒤로 이동) 로직")
    void testBackdo(String title, int startId, int[] moves, int expectedId) {
        placePiece(piece, startId, false);
        moveSequence(piece, moves);
        if (expectedId == -1) {
            assertTrue(piece.isFinished(), title + " : 완주하지 않았습니다.");
        } else {
            assertEquals(expectedId, piece.getPosition().getId(), title + " : 위치 불일치");
        }
    }

    static Stream<Arguments> backdoProvider() {
        return Stream.of(
            Arguments.of("6→5→30"             , 6 , new int[]{YutThrowResult.BACKDO.getValue(), 1}                         , 30 ),
            Arguments.of("6→5→4"               , 6 , new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 4  ),
            Arguments.of("30→5→4"              , 30, new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 4  ),
            Arguments.of("11→10→33"            , 11, new int[]{YutThrowResult.BACKDO.getValue(), 1}                     , 33 ),
            Arguments.of("11→10→9"             , 11, new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 9  ),
            Arguments.of("33→10→9"             , 33, new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 9  ),
            Arguments.of("16→15→36"            , 16, new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 36 ),
            Arguments.of("20→38"               , 20, new int[]{YutThrowResult.BACKDO.getValue()}                           , 38 ),
            Arguments.of("37→32→34"            , 37, new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 34 ),
            Arguments.of("35→32→34"            , 35, new int[]{YutThrowResult.BACKDO.getValue(), YutThrowResult.BACKDO.getValue()} , 34 )
        );
    }

    /*====================================================================*/
    /* 4. 잡기 (Capture) 케이스                                            */
    /*====================================================================*/
    @Test
    @DisplayName("업힌 스택 전체 잡기 & 추가턴 부여")
    void testCaptureStack() {
        // 적군 2‑말 스택 생성 (p2 말 두 개를 5번 칸에 쌓음)
        Piece enemy1 = p2.getPieces().get(0);
        Piece enemy2 = p2.getPieces().get(1);
        placePiece(enemy1, 5, false);
        placePiece(enemy2, 5, true);          // 동일 칸 → 스택

        // p1 말: 4 → 5 (1칸 전진하여 캡처)
        placePiece(piece, 4, false);
        List<Piece> caught = piece.move(1);

        assertTrue(caught.containsAll(List.of(enemy1, enemy2)), "두 말 모두 캡처돼야 합니다.");
        assertTrue(p1.canAddMoreResults(), "캡처 후 추가 턴이 주어져야 합니다.");
    }

    /*====================================================================*/
    /* 5. 업기 (Stack) 케이스                                              */
    /*====================================================================*/
    @Test
    @DisplayName("다중 스택 업기 & 이동")
    void testStackingAndMove() {
        Piece mule1 = p1.getPieces().get(1);  // 리더 외 추가 말
        placePiece(piece, 3, false);                 // 리더 3번 칸
        placePiece(mule1, 3, true);                 // 승객 3번 칸 – 업기 발생

        // 스택 이동: 3 → 6 (GEO)
        piece.move(YutThrowResult.GEO.getValue());
        assertAll("StackMove",
                () -> assertEquals(6, piece.getPosition().getId(), "리더 위치가 6번이어야 합니다."),
                () -> assertEquals(6, mule1.getPosition().getId(), "승객도 함께 이동해야 합니다.")
            );
    }

    /*====================================================================*/
    /* 6. 승리 (Finish) 케이스                                             */
    /*====================================================================*/
    @Test
    @DisplayName("모든 말 완주 → 승리")
    void testVictory() {
        // p1 의 모든 말을 19 번 칸에 두고 2칸 이동해 완주 처리
        for (Piece pc : p1.getPieces()) {
            placePiece(pc, 19, false);
            pc.move(2);                       // 19 → 20 → Goal
            assertTrue(pc.isFinished());
        }
        assertEquals(4, p1.getFinishedPieceCount());
        assertTrue(model.isCurrentPlayerWinner());
    }

    /*============================== 헬퍼 ==============================*/
    /** 지정 ID 칸으로 말 강제 배치 (캡처/이동 허용 X) */
    private void placePiece(Piece pc, int cellId, boolean allowStack) {
        Cell target = board.get(cellId);
        if (pc.getPosition() != null) pc.getPosition().leave(pc);
        pc.setPosition(target);
        target.enter(pc, false, allowStack);
    }

    /** 주어진 이동값 시퀀스를 순차 적용 */
    private void moveSequence(Piece pc, int... moves) {
        for (int mv : moves) pc.move(mv);
    }
}
