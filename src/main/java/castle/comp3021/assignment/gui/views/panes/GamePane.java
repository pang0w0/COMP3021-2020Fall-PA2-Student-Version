package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.NumberTextField;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.player.RandomPlayer;
import castle.comp3021.assignment.player.SmartRandomPlayer;
import castle.comp3021.assignment.protocol.Configuration;
import castle.comp3021.assignment.protocol.Player;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GamePane extends BasePane {
    @NotNull
    private final VBox container = new BigVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Button playButton = new BigButton("Play");
    @NotNull
    private final Button returnButton = new BigButton("Return");
    @NotNull
    private final Button useDefaultButton = new BigButton("Use Default");
    @NotNull
    private final Button isHumanPlayer1Button = new BigButton("");
    @NotNull
    private final Button isHumanPlayer2Button = new BigButton("");

    @NotNull
    private final NumberTextField sizeFiled = new NumberTextField("");


    private final BorderPane sizeBox = new BorderPane(null, null,
            sizeFiled, null, new Label("Size of Board:"));

    @NotNull
    private final NumberTextField numMovesProtectionField = new NumberTextField("");

    @NotNull
    private final BorderPane numMovesProtectionBox = new BorderPane(null, null, numMovesProtectionField,
            null, new Label("Protection Moves:"));


    private FXJesonMor fxJesonMor = null;

    public GamePane() {
        fillValues();
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    @Override
    void connectComponents() {
        //TODO-DONE
//        container.setAlignment(Pos.CENTER);
//        sizeBox.setPadding(new Insets(0,20,0,20));
//        numMovesProtectionBox.setPadding(new Insets(0,20,0,20));
        container.getChildren().addAll(title, sizeBox, numMovesProtectionBox, isHumanPlayer1Button,
                isHumanPlayer2Button, useDefaultButton, playButton, returnButton);
        setCenter(container);
    }

    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
    }

    /**
     * Set callbacks to buttons
     * Hint:
     * -  When fill in board size and step of protections, numbers need to validate
     * -  useDefaultButton: use default value for {@link GamePane#sizeFiled}, {@link GamePane#numMovesProtectionField}, and two players
     *    as they are saved in {@link SettingPane}
     * -  The current configuration (including {@link GamePane#sizeFiled}, {@link GamePane#numMovesProtectionField} and two players role)
     *    should not affect the default settings.
     * -  After clicking "play" button, the handler is implemented in {@link GamePane#startGame(FXJesonMor)},
     *    which links to {@link GamePlayPane} using the current configuration.
     */
    @Override
    void setCallbacks() {
        //TODO-DONE
        isHumanPlayer1Button.setOnAction(e->{
            if (isHumanPlayer1Button.getText().equals("Player 1: Player")){
                isHumanPlayer1Button.setText("Player 1: Computer");
            }else if(isHumanPlayer1Button.getText().equals("Player 1: Computer")){
                isHumanPlayer1Button.setText("Player 1: Smart Computer");
            }else {
                isHumanPlayer1Button.setText("Player 1: Player");
            }
        });

        isHumanPlayer2Button.setOnAction(e->{
            if (isHumanPlayer2Button.getText().equals("Player 2: Player")){
                isHumanPlayer2Button.setText("Player 2: Computer");
            }else if(isHumanPlayer2Button.getText().equals("Player 2: Computer")){
                isHumanPlayer2Button.setText("Player 2: Smart Computer");
            }else {
                isHumanPlayer2Button.setText("Player 2: Player");
            }
        });

        useDefaultButton.setOnAction(e->{
            //sizeFiled.setText(""+globalConfiguration.getSize());
            //numMovesProtectionField.setText(""+globalConfiguration.getNumMovesProtection());
            fillValues();
        });

        playButton.setOnAction(e->{
            Optional o;
            try {
                o = validate(sizeFiled.getValue(), numMovesProtectionField.getValue());
            }catch (NumberFormatException err){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Validation Failed");
                alert.setContentText("Some filed is null or invalid format");
                alert.showAndWait();
                return;
            }
            if(!o.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Validation Failed");
                alert.setContentText(o.get().toString());
                alert.showAndWait();
                return;
            }

            Player whitePlayer;
            Player blackPlayer;

            //only able to use smart player in game pane (restart not work)
            if (isHumanPlayer1Button.getText().equals("Player 1: Player")){
                whitePlayer = new ConsolePlayer("White");
            }else if(isHumanPlayer1Button.getText().equals("Player 1: Computer")){
                whitePlayer = new RandomPlayer("White");
            }else {
                whitePlayer = new SmartRandomPlayer("White");
            }

            if (isHumanPlayer2Button.getText().equals("Player 2: Player")){
                blackPlayer = new ConsolePlayer("Black");
            }else if(isHumanPlayer1Button.getText().equals("Player 2: Computer")){
                blackPlayer = new RandomPlayer("Black");
            }else {
                blackPlayer = new SmartRandomPlayer("Black");
            }

            Player[] players = new Player[]{whitePlayer, blackPlayer};

            try {
                globalConfiguration = new Configuration(sizeFiled.getValue(), players, numMovesProtectionField.getValue());
            }catch (InvalidConfigurationError err){
                System.out.println(err);
                return;
            }

//            for (int i = 0; i < globalConfiguration.getSize(); i++) {
//                if (i % 2 == 0) {
//                    globalConfiguration.addInitialPiece(
//                            new Knight(blackPlayer), i, globalConfiguration.getSize() - 1);
//                } else {
//                    globalConfiguration.addInitialPiece(
//                            new Archer(blackPlayer), i, globalConfiguration.getSize() - 1);
//                }
//            }
//            for (int i = 0; i < globalConfiguration.getSize(); i++) {
//                if (i % 2 == 0) {
//                    globalConfiguration.addInitialPiece(new Knight(whitePlayer), i, 0);
//                } else {
//                    globalConfiguration.addInitialPiece(new Archer(whitePlayer), i, 0);
//                }
//            }
            globalConfiguration.setAllInitialPieces();
            fxJesonMor = new FXJesonMor(globalConfiguration);
            startGame(fxJesonMor);
        });

        returnButton.setOnAction(e->{
            SceneManager.getInstance().showPane(MainMenuPane.class);
        });
    }

    /**
     * Handler when clicking "play" button, using the current configuration to pass a {@link FXJesonMor} instance
     * Hint:
     *      - You may need to initialize and set up {@link GamePlayPane} by passing {@link FXJesonMor}
     * @param fxJesonMor an instance of {@link FXJesonMor}
     */
    void startGame(@NotNull FXJesonMor fxJesonMor) {
        final var gameplayPane = SceneManager.getInstance().<GamePlayPane>getPane(GamePlayPane.class);
        gameplayPane.initializeGame(fxJesonMor);
        SceneManager.getInstance().showPane(GamePlayPane.class);
    }

    /**
     * Fill in the default values for all editable fields.
     */
    void fillValues(){
        // TODO-DONE
        if(globalConfiguration.isFirstPlayerHuman()){
            isHumanPlayer1Button.setText("Player 1: Player");
        }else {
            isHumanPlayer1Button.setText("Player 1: Computer");
        }

        if(globalConfiguration.isSecondPlayerHuman()){
            isHumanPlayer2Button.setText("Player 2: Player");
        }else {
            isHumanPlayer2Button.setText("Player 2: Computer");
        }

        sizeFiled.setText(""+globalConfiguration.getSize());
        numMovesProtectionField.setText(""+globalConfiguration.getNumMovesProtection());
//        if(globalConfiguration.isFirstPlayerHuman()){
//            isHumanPlayer1Button.setText("Player 1: Computer");
//        }else {
//            isHumanPlayer1Button.setText("Player 1: Player");
//        }
//
//        if(globalConfiguration.isSecondPlayerHuman()){
//            isHumanPlayer2Button.setText("Player 2: Computer");
//        }else {
//            isHumanPlayer2Button.setText("Player 2: Player");
//        }
    }

    /**
     * Validate the text fields
     * The useful msgs are predefined in {@link ViewConfig#MSG_BAD_SIZE_NUM}, etc.
     * @param size number in {@link GamePane#sizeFiled}
     * @param numProtection number in {@link GamePane#numMovesProtectionField}
     * @return If validation failed, {@link Optional} containing the reason message; An empty {@link Optional}
     *      * otherwise.
     */
    public static Optional<String> validate(int size, int numProtection) {
        //TODO-DONE
        if(size % 2 == 0){
            return Optional.of(ViewConfig.MSG_ODD_SIZE_NUM);
        }else if(size < 3){
            return Optional.of(ViewConfig.MSG_BAD_SIZE_NUM);
        }else if(size > 26){
            return Optional.of(ViewConfig.MSG_UPPERBOUND_SIZE_NUM);
        }

        if(numProtection < 0){
            return Optional.of(ViewConfig.MSG_NEG_PROT);
        }

        return Optional.empty();
    }
}
