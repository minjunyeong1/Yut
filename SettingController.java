package yutgame.controller;

import yutgame.model.*;
import yutgame.view.*;

/**
 * Coordinates the settings window and game launch.
 */
public class SettingController {
    private SettingView settingView;

    public SettingController() {
        settingView = new SettingView();
        settingView.addStartListener(e -> onStart());
        settingView.setVisible(true);
    }

    private void onStart() {
        int players = settingView.getPlayerCount();
        int pieces  = settingView.getPiecesPerPlayer();
        Board.Shape shape = settingView.getSelectedShape();

        settingView.dispose();

        GameConfig config = new GameConfig(players, pieces, shape);
        GameModel model   = new GameModel(config);

        AbstractBoardView boardView;
        switch(shape) {
            case PENTAGON: boardView = new PentagonBoardView(); break;
            case HEXAGON:  boardView = new HexagonBoardView();  break;
            default:       boardView = new RectangleBoardView();
        }
        
        MainView view = new MainView(boardView);
        GameController gc = new GameController(model, view);
        gc.startGame();
        view.setVisible(true);
    }
}