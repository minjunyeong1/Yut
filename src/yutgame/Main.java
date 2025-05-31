package yutgame;

import javafx.application.Application;
import javafx.stage.Stage;
import yutgame.controller.GameController;
import yutgame.model.GameConfig;
import yutgame.model.GameModel;
import yutgame.view.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SettingView settingView = new SettingView(); // show()는 내부에서 자동 호출됨

        settingView.addStartListener(() -> {
            // 설정값 기반 config, model 생성
            GameConfig config = new GameConfig(
                    settingView.getPlayerCount(),
                    settingView.getPiecesPerPlayer(),
                    settingView.getSelectedShape()
            );
            GameModel model = new GameModel(config);

            // 보드 뷰 생성 (controller는 아직 null로 둠)
            AbstractBoardView board = switch (config.getBoardShape()) {
                case RECTANGLE -> new RectangleBoardView(config, model, null);
                case PENTAGON  -> new PentagonBoardView(config, model, null);
                case HEXAGON   -> new HexagonBoardView(config, model, null);
            };

            // MainView 생성 (보드 뷰 주입)
            MainView main = new MainView(board);

            // GameController 생성 후 view 연결
            GameController controller = new GameController(model, main);
            board.setGameController(controller); // board에도 controller 주입

            // 메인 창 띄우기
            main.show();

            // 설정 창 닫기
            settingView.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
