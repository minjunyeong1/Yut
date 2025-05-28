package yutgame;

import javafx.application.Application;
import javafx.stage.Stage;
import yutgame.controller.GameController;
import yutgame.model.GameConfig;
import yutgame.model.GameModel;
import yutgame.view.AbstractBoardView;
import yutgame.view.MainView;
import yutgame.view.RectangleBoardView;
import yutgame.view.PentagonBoardView;
import yutgame.view.HexagonBoardView;
import yutgame.view.SettingView;
import yutgame.view.TurnView;
import yutgame.view.YutResultView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SettingView settingView = new SettingView();

        settingView.addStartListener(() -> {
            // 1) 모델·컨트롤러 생성
            GameConfig config = new GameConfig(
                settingView.getPlayerCount(),
                settingView.getPiecesPerPlayer(),
                settingView.getSelectedShape()
            );
            GameModel model = new GameModel(config);
            GameController controller = new GameController(config, model);

            // 2) 보드 뷰 생성 및 컨트롤러 주입
            AbstractBoardView board = switch (config.getBoardShape()) {
                case RECTANGLE -> new RectangleBoardView(config, model, controller);
                case PENTAGON  -> new PentagonBoardView(config, model, controller);
                case HEXAGON   -> new HexagonBoardView(config, model, controller);
            };
            board.setGameController(controller);

            // 3) MainView 세팅
            MainView main = new MainView();
            main.setBoardView(board);
            main.setTurnView(new TurnView());
            main.setYutResultView(new YutResultView());

            // 4) 뷰를 먼저 초기화해서 버튼들이 생성되도록
            Stage gameStage = new Stage();
            main.start(gameStage);

            // 5) 컨트롤러에 뷰를 연결하여 이벤트 핸들러 등록
            controller.setView(main);

            // 6) 설정 창 닫기
            settingView.close();
        });

        settingView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
