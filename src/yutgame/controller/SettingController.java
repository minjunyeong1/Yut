package yutgame.controller;

import javafx.stage.Stage;
import yutgame.model.*;
import yutgame.view.*;

/**
 * Coordinates the settings window and game launch.
 */
public class SettingController {
    private SettingView settingView;

    public SettingController(Stage primaryStage) {
        settingView = new SettingView();
        settingView.addStartListener(() -> onStart(primaryStage));
    }

    private void onStart(Stage stageToClose) {
        int players = settingView.getPlayerCount();
        int pieces  = settingView.getPiecesPerPlayer();
        Board.Shape shape = settingView.getSelectedShape();

        stageToClose.close();

        GameConfig config = new GameConfig(players, pieces, shape);
        GameModel model   = new GameModel(config);

        AbstractBoardView boardView;

        switch(shape) {
            case PENTAGON:
                boardView = new PentagonBoardView(config, model, null);
                break;
            case HEXAGON:
                boardView = new HexagonBoardView(config, model, null);
                break;
            default:
                boardView = new RectangleBoardView(config, model, null);
        }

        MainView view = new MainView(boardView); // GameModel 전달
        GameController gc = new GameController(model, view);
        boardView.setGameController(gc);

        view.show();
    }
}
