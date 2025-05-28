package yutgame;

import javafx.application.Application;
import javafx.stage.Stage;
import yutgame.controller.GameController;
import yutgame.model.*;
import yutgame.view.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SettingView settingView = new SettingView();

        settingView.addStartListener(() -> {
            GameConfig config = new GameConfig(
                settingView.getPlayerCount(),
                settingView.getPiecesPerPlayer(),
                settingView.getSelectedShape()
            );
            GameModel model = new GameModel(config);
            GameController controller = new GameController(config, model);

            AbstractBoardView board = switch (config.getBoardShape()) {
                case RECTANGLE -> new RectangleBoardView(config, model, controller);
                case PENTAGON  -> new PentagonBoardView(config, model, controller);
                case HEXAGON   -> new HexagonBoardView(config, model, controller);
            };

            MainView main = new MainView();
            main.setBoardView(board);
            main.setTurnView(new TurnView());
            main.setYutResultView(new YutResultView());

            controller.setView(main);

            Stage gameStage = new Stage();
            main.start(gameStage);

            settingView.close();
        });

        settingView.show();
    }

    public static void main(String[] args) {
        launch(args); // JavaFX 시작
    }
}


