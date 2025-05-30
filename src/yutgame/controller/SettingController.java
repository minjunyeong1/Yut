package yutgame.controller;

import yutgame.model.Board;
import yutgame.model.GameConfig;
import yutgame.model.GameModel;
import yutgame.view.AbstractBoardView;
import yutgame.view.HexagonBoardView;
import yutgame.view.MainView;
import yutgame.view.PentagonBoardView;
import yutgame.view.RectangleBoardView;
import yutgame.view.TurnView;
import yutgame.view.YutResultView;
import yutgame.view.SettingView;

public class SettingController {

    private final SettingView settingView;

    public SettingController() {
        settingView = new SettingView();
        settingView.addStartListener(this::onStart);
        settingView.show();
    }

    private void onStart() {
        int players = settingView.getPlayerCount();
        int pieces  = settingView.getPiecesPerPlayer();
        Board.Shape shape = settingView.getSelectedShape();

        settingView.close();

        // MVC 생성
        GameConfig config = new GameConfig(players, pieces, shape);
        GameModel model = new GameModel(config);
        GameController controller = new GameController(config, model);

        AbstractBoardView boardView = switch (shape) {
            case PENTAGON -> new PentagonBoardView(config, model, controller);
            case HEXAGON  -> new HexagonBoardView(config, model, controller);
            default       -> new RectangleBoardView(config, model, controller);
        };

        // MainView 세팅
        MainView view = new MainView();
        view.setBoardView(boardView);
        view.setTurnView(new TurnView());
        view.setYutResultView(new YutResultView());

        // 새 게임 창 표시 (fully-qualified Stage)
        view.start(new javafx.stage.Stage());

        // 컨트롤러에 뷰 연결
        controller.setView(view);

        // 보드 뷰에도 컨트롤러 설정
        boardView.setGameController(controller);
    }
}
