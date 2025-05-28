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

        settingView.close(); // JavaFX에서는 dispose()가 아니라 close()

        GameConfig config = new GameConfig(players, pieces, shape);
        GameModel model = new GameModel(config);

        AbstractBoardView boardView = switch (shape) {
            case PENTAGON -> new PentagonBoardView(config, model, null);
            case HEXAGON  -> new HexagonBoardView(config, model, null);
            default       -> new RectangleBoardView(config, model, null);
        };

        // MainView 생성 및 설정
        MainView view = new MainView();
        view.setBoardView(boardView);
        view.setTurnView(new TurnView());
        view.setYutResultView(new YutResultView());

        // GameController 생성 및 연결
        GameController controller = new GameController(config, model);
        controller.setView(view);
        boardView.setGameController(controller);

        // 게임 화면 표시
        Stage gameStage = new Stage();
        view.start(gameStage);
    }
}
