package yutgame.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Modality;
import yutgame.model.Board;

public class SettingView extends Stage {

    private Spinner<Integer> playerCountSpinner;
    private Spinner<Integer> piecesPerPlayerSpinner;
    private ComboBox<BoardShapeItem> boardShapeCombo;
    private Button startButton;

    public SettingView() {
        setTitle("윷놀이 게임 설정");

        // Main container with simple background
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #f5f5fa;");

        // Title section
        VBox titleSection = new VBox(10);
        titleSection.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("윷놀이 게임 설정");
        titleLabel.setFont(Font.font("맑은 고딕", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.rgb(102, 126, 234));
        
        Label subtitleLabel = new Label("게임 옵션을 선택해주세요");
        subtitleLabel.setFont(Font.font("맑은 고딕", 14));
        subtitleLabel.setTextFill(Color.gray(0.5));
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);

        // Settings card
        VBox settingsCard = new VBox(20);
        settingsCard.setAlignment(Pos.CENTER);
        settingsCard.setPadding(new Insets(30));
        settingsCard.setStyle("-fx-background-color: white;" +
                             "-fx-background-radius: 20;");
        settingsCard.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.1)));
        settingsCard.setMaxWidth(400);

        // Player count setting
        VBox playerSection = createSettingSection(
            "플레이어 수",
            "2~4명이 함께 플레이할 수 있습니다"
        );
        playerCountSpinner = new Spinner<>(2, 4, 2);
        playerCountSpinner.setPrefWidth(150);
        styleSpinner(playerCountSpinner);
        playerSection.getChildren().add(playerCountSpinner);

        // Pieces per player setting
        VBox piecesSection = createSettingSection(
            "말 개수",
            "각 플레이어가 사용할 말의 개수입니다"
        );
        piecesPerPlayerSpinner = new Spinner<>(2, 5, 2);
        piecesPerPlayerSpinner.setPrefWidth(150);
        styleSpinner(piecesPerPlayerSpinner);
        piecesSection.getChildren().add(piecesPerPlayerSpinner);

        // Board shape setting
        VBox shapeSection = createSettingSection(
            "게임판 모양",
            "원하는 게임판 모양을 선택하세요"
        );
        boardShapeCombo = new ComboBox<>();
        boardShapeCombo.getItems().addAll(
            new BoardShapeItem("사각형", Board.Shape.RECTANGLE),
            new BoardShapeItem("오각형", Board.Shape.PENTAGON),
            new BoardShapeItem("육각형", Board.Shape.HEXAGON)
        );
        boardShapeCombo.getSelectionModel().selectFirst();
        boardShapeCombo.setPrefWidth(250);
        styleComboBox(boardShapeCombo);
        
        shapeSection.getChildren().add(boardShapeCombo);

        // Start button
        startButton = new Button("게임 시작");
        startButton.setPrefSize(200, 45);
        startButton.setStyle("-fx-background-color: #667eea;" +
                           "-fx-text-fill: white;" +
                           "-fx-font-size: 16px;" +
                           "-fx-font-weight: bold;" +
                           "-fx-background-radius: 25;" +
                           "-fx-cursor: hand;");
        startButton.setEffect(new DropShadow(10, Color.rgb(102, 126, 234, 0.3)));
        
        // Hover effect
        startButton.setOnMouseEntered(e -> 
            startButton.setStyle("-fx-background-color: #764ba2;" +
                               "-fx-text-fill: white;" +
                               "-fx-font-size: 16px;" +
                               "-fx-font-weight: bold;" +
                               "-fx-background-radius: 25;" +
                               "-fx-cursor: hand;")
        );
        startButton.setOnMouseExited(e -> 
            startButton.setStyle("-fx-background-color: #667eea;" +
                               "-fx-text-fill: white;" +
                               "-fx-font-size: 16px;" +
                               "-fx-font-weight: bold;" +
                               "-fx-background-radius: 25;" +
                               "-fx-cursor: hand;")
        );

        // Add all sections to settings card
        settingsCard.getChildren().addAll(
            playerSection,
            new Separator(),
            piecesSection,
            new Separator(),
            shapeSection,
            new Region(), // Spacer
            startButton
        );
        VBox.setVgrow(settingsCard.getChildren().get(6), Priority.ALWAYS);

        // Add to main container
        mainContainer.getChildren().addAll(titleSection, settingsCard);

        // Scene setup
        Scene scene = new Scene(mainContainer, 500, 600);
        setScene(scene);
        initModality(Modality.APPLICATION_MODAL);
        centerOnScreen();
        show();
    }

    private VBox createSettingSection(String title, String description) {
        VBox section = new VBox(5);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("맑은 고딕", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.gray(0.2));
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("맑은 고딕", 12));
        descLabel.setTextFill(Color.gray(0.6));
        
        section.getChildren().addAll(titleLabel, descLabel);
        return section;
    }

    private void styleSpinner(Spinner<?> spinner) {
        spinner.setStyle("-fx-font-size: 14px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #e0e0e0;");
        spinner.setEditable(false);
    }

    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle("-fx-font-size: 14px;" +
                         "-fx-background-radius: 10;" +
                         "-fx-border-radius: 10;" +
                         "-fx-border-color: #e0e0e0;");
    }

    // Getters remain the same
    public int getPlayerCount() {
        return playerCountSpinner.getValue();
    }

    public int getPiecesPerPlayer() {
        return piecesPerPlayerSpinner.getValue();
    }

    public Board.Shape getSelectedShape() {
        return boardShapeCombo.getValue().shape;
    }

    public void addStartListener(Runnable handler) {
        startButton.setOnAction(e -> handler.run());
    }

    // Keep the BoardShapeItem class exactly as in the original
    private static class BoardShapeItem {
        final String name;
        final Board.Shape shape;

        BoardShapeItem(String name, Board.Shape shape) {
            this.name = name;
            this.shape = shape;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}