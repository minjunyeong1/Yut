package yutgame.controller;

import javafx.stage.Stage;
import yutgame.model.*;
import yutgame.view.*;

public class SettingController {

    private final SettingView settingView;

    public SettingController() {
        settingView = new SettingView();
        settingView.addStartListener(this::onStart);
        settingView.show();
    }

    private void onStart() {
        int players = settingView.getPlayerCount();
        int pieces = settingView.getPiecesPerPlayer();
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

        // 1) 뷰 먼저 초기화해서 모든 버튼 인스턴스를 생성
        Stage gameStage = new Stage();
        view.start(gameStage);

        // 2) 컨트롤러에 뷰를 연결 (이 안에서 YutThrowController 가 붙습니다)
        controller.setView(view);

        // 3) 이제 보드 뷰에도 컨트롤러를 알려 줍니다.
        boardView.setGameController(controller);
    }
}
