package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.DurationTimer;
import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.AudioManager;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.GameplayInfoPane;
import castle.comp3021.assignment.gui.views.SideMenuVBox;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.player.RandomPlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.gui.controllers.Renderer;
import castle.comp3021.assignment.protocol.io.Serializer;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

/**
 * This class implements the main playing function of Jeson Mor
 * The necessary components have been already defined (e.g., topBar, title, buttons).
 * Basic functions:
 *      - Start game and play, update scores
 *      - Restart the game
 *      - Return to main menu
 *      - Elapsed Timer (ticking from 00:00 -> 00:01 -> 00:02 -> ...)
 *          - The format is defined in {@link GameplayInfoPane#formatTime(int)}
 * Requirement:
 *      - The game should be initialized by configuration passed from {@link GamePane}, instead of the default configuration
 *      - The information of the game (including scores, current player name, ect.) is implemented in {@link GameplayInfoPane}
 *      - The center canvas (defined as gamePlayCanvas) should be disabled when current player is computer
 * Bonus:
 *      - A countdown timer (if this is implemented, then elapsed timer can be either kept or removed)
 *      - The format of countdown timer is defined in {@link GameplayInfoPane#countdownFormat(int)}
 *      - If one player runs out of time of each round {@link DurationTimer#getDefaultEachRound()}, then the player loses the game.
 * Hint:
 *      - You may find it useful to synchronize javafx UI-thread using {@link javafx.application.Platform#runLater}
 */

public class GamePlayPane extends BasePane {
    @NotNull
    private final HBox topBar = new HBox(20);
    @NotNull
    private final SideMenuVBox leftContainer = new SideMenuVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Text parameterText = new Text();
    @NotNull
    private final BigButton returnButton = new BigButton("Return");
    @NotNull
    private final BigButton startButton = new BigButton("Start");
    @NotNull
    private final BigButton restartButton = new BigButton("Restart");
    @NotNull
    private final BigVBox centerContainer = new BigVBox();
    @NotNull
    private final Label historyLabel = new Label("History");

    @NotNull
    private final Text historyFiled = new Text();
    @NotNull
    private final ScrollPane scrollPane = new ScrollPane();

    /**
     * time passed in seconds
     * Hint:
     *      - Bind it to time passed in {@link GameplayInfoPane}
     */
    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();

    @NotNull
    private final Canvas gamePlayCanvas = new Canvas();

    private GameplayInfoPane infoPane = null;

    /**
     * You can add more necessary variable here.
     * Hint:
     *      - the passed in {@link FXJesonMor}
     *      - other global variable you want to note down.
     */
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // TODO-DONE?
    public FXJesonMor globalJeson = null;
    private Place source;
    private Move tempMove;

    private void createTimeoutPopup(String loserName){
        //TODO-DONE
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sorry! Time's out!");
        alert.setHeaderText("Confirmation");
        alert.setContentText(loserName + " Lose!");

        ButtonType newGame = new ButtonType("Start New Game", ButtonBar.ButtonData.LEFT);
        ButtonType export = new ButtonType("Export Move Records");
        ButtonType returnMain = new ButtonType("Return to Main Menu", ButtonBar.ButtonData.RIGHT);

        alert.getButtonTypes().setAll(newGame, export, returnMain);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == newGame){
            onRestartButtonClick();
        } else if (result.get() == export) {//save file
            try {
                Serializer.getInstance().saveToFile(globalJeson);
            }catch (IOException ex){
                System.out.println(ex);
            }
            //after save, restart
            onRestartButtonClick();
        } else if (result.get() == returnMain) {
            doQuitToMenu();
        }
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public GamePlayPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * Components are added, adjust it by your own choice
     */
    @Override
    void connectComponents() {
        //TODO-DONE
        topBar.setAlignment(Pos.CENTER);
        topBar.getChildren().add(title);
        leftContainer.getChildren().addAll(parameterText, historyLabel, scrollPane,
                startButton, restartButton, returnButton);
        centerContainer.getChildren().add(gamePlayCanvas);

        setTop(topBar);
        setLeft(leftContainer);
        setCenter(centerContainer);
    }

    /**
     * style of title and scrollPane have been set up, no need to add more
     */
    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(ViewConfig.WIDTH / 4.0, ViewConfig.HEIGHT / 3.0 );
        scrollPane.setContent(historyFiled);
    }

    /**
     * The listeners are added here.
     */
    @Override
    void setCallbacks() {
        //TODO-DONE
        startButton.setOnAction(e->{
            startButton.setDisable(true);
            restartButton.setDisable(false);
            startGame();
        });

        restartButton.setOnAction(e->{
            onRestartButtonClick();
        });

        returnButton.setOnAction(e->{
            doQuitToMenuAction();
            });

        gamePlayCanvas.setOnMousePressed(e->{
            onCanvasPressed(e);
        });
        gamePlayCanvas.setOnMouseDragged(e->{
            onCanvasDragged(e);
        });
        gamePlayCanvas.setOnMouseReleased(e->{
            onCanvasReleased(e);
        });

    }

    /**
     * Set up necessary initialization.
     * Hint:
     *      - Set buttons enable/disable
     *          - Start button: enable
     *          - restart button: disable
     *      - This function can be invoked before {@link GamePlayPane#startGame()} for setting up
     *
     * @param fxJesonMor pass in an instance of {@link FXJesonMor}
     */
    void initializeGame(@NotNull FXJesonMor fxJesonMor) {
        //TODO-DONE
        globalJeson = fxJesonMor;
        startButton.setDisable(false);
        restartButton.setDisable(true);
        disnableCanvas();
        ticksElapsed.setValue(DurationTimer.getDefaultEachRound());
        //create a new container that have new configuration
        if(infoPane != null){
            centerContainer.getChildren().remove(1);
        }
        infoPane = new GameplayInfoPane(globalJeson.getPlayer1Score(), globalJeson.getPlayer2Score(),
                globalJeson.getCurPlayerName(), ticksElapsed);
        centerContainer.getChildren().add(infoPane);
        //
        String s = "Parameters:"+"\n"+
                "\n"+
                "Size of board: "+globalConfiguration.getSize()+"\n"+
                "Num of protection moves: "+globalConfiguration.getNumMovesProtection()+"\n";
        if(globalConfiguration.getPlayers()[0] instanceof ConsolePlayer){
            s += "Player White(human)\n";
        }else{
            s += "Player White(computer)\n";
        }
        if(globalConfiguration.getPlayers()[1] instanceof ConsolePlayer){
            s += "Player Black(human)\n";
        }else{
            s += "Player Black(computer)\n";
        }
        parameterText.setText(s);

        gamePlayCanvas.setHeight(globalConfiguration.getSize() * ViewConfig.PIECE_SIZE);
        gamePlayCanvas.setWidth(globalConfiguration.getSize() * ViewConfig.PIECE_SIZE);
        fxJesonMor.renderBoard(gamePlayCanvas);
    }

    /**
     * enable canvas clickable
     */
    private void enableCanvas(){
        gamePlayCanvas.setDisable(false);
    }

    /**
     * disable canvas clickable
     */
    private void disnableCanvas(){
        gamePlayCanvas.setDisable(true);
    }

    /**
     * After click "start" button, everything will start from here
     * No explicit skeleton is given here.
     * Hint:
     *      - Give a carefully thought to how to activate next round of play
     *      - When a new {@link Move} is acquired, it needs to be check whether this move is valid.
     *          - If is valid, make the move, render the {@link GamePlayPane#gamePlayCanvas}
     *          - If is invalid, abort the move
     *          - Update score, add the move to {@link GamePlayPane#historyFiled}, also record the move
     *          - Move forward to next player
     *      - The player can be either computer or human, when the computer is playing, disable {@link GamePlayPane#gamePlayCanvas}
     *      - You can add a button to enable next move once current move finishes.
     *          - or you can add handler when mouse is released
     *          - or you can take advantage of timer to automatically change player. (Bonus)
     */
    public void startGame() {
        //TODO-DONE
        globalJeson.startCountdown();
        globalJeson.addOnTickHandler(()->{//add this event to the timer
            ticksElapsed.set(ticksElapsed.get() - 1);
            if(globalJeson.getCurrentPlayer() instanceof ConsolePlayer){
                enableCanvas();
            }else {
                disnableCanvas();

                Move moved = globalJeson.getCurrentPlayer().nextMove(globalJeson,
                        globalJeson.getAvailableMoves(globalJeson.getCurrentPlayer()));
                Piece movedPiece = globalJeson.getPiece(moved.getSource());

                if(AudioManager.getInstance().isEnabled()){
                    AudioManager.getInstance().playSound(AudioManager.SoundRes.PLACE);
                }
                globalJeson.movePiece(moved);
                tempMove = moved;//refresh the global variable
                ticksElapsed.set(DurationTimer.getDefaultEachRound());

                //after move, refresh board
                globalJeson.renderBoard(gamePlayCanvas);
                Renderer.renderPieces(gamePlayCanvas, globalConfiguration.getInitialBoard());
                //update history
                updateHistoryField(moved);
                //update score (+numOfMove+next player)
                globalJeson.updateScore(globalJeson.getCurrentPlayer(), movedPiece, moved);
                //get winner
                checkWinner();
            }

            if (ticksElapsed.get() < 0){
                if(AudioManager.getInstance().isEnabled()){
                    AudioManager.getInstance().playSound(AudioManager.SoundRes.LOSE);
                }
                globalJeson.stopCountdown();
                createTimeoutPopup(globalJeson.getCurrentPlayer().getName());

            }
        });



    }

    /**
     * Restart the game
     * Hint: end the current game and start a new game
     */
    private void onRestartButtonClick(){
        //TODO-DONE
        startButton.setDisable(false);
        restartButton.setDisable(true);
        endGame();

        Player whitePlayer;
        Player blackPlayer;

        if (globalConfiguration.isFirstPlayerHuman()){
            whitePlayer = new ConsolePlayer("White");
        }else{
            whitePlayer = new RandomPlayer("White");
        }
        if (globalConfiguration.isSecondPlayerHuman()){
            blackPlayer = new ConsolePlayer("Black");
        }else{
            blackPlayer = new RandomPlayer("Black");
        }
        Player[] players = new Player[]{whitePlayer, blackPlayer};
        globalConfiguration = new Configuration(globalConfiguration.getSize(), players,
                globalConfiguration.getNumMovesProtection());
        globalConfiguration.setAllInitialPieces();
//        for (int i = 0; i < globalConfiguration.getSize(); i++) {
//            if (i % 2 == 0) {
//                globalConfiguration.addInitialPiece(
//                        new Knight(globalConfiguration.getPlayers()[1]), i, globalConfiguration.getSize() - 1);
//            } else {
//                globalConfiguration.addInitialPiece(
//                        new Archer(globalConfiguration.getPlayers()[1]), i, globalConfiguration.getSize() - 1);
//            }
//        }
//        for (int i = 0; i < globalConfiguration.getSize(); i++) {
//            if (i % 2 == 0) {
//                globalConfiguration.addInitialPiece(new Knight(globalConfiguration.getPlayers()[0]), i, 0);
//            } else {
//                globalConfiguration.addInitialPiece(new Archer(globalConfiguration.getPlayers()[0]), i, 0);
//            }
//        }

        globalJeson = new FXJesonMor(globalConfiguration);
        initializeGame(globalJeson);
    }

    /**
     * Add mouse pressed handler here.
     * Play click.mp3
     * draw a rectangle at clicked board tile to show which tile is selected
     * Hint:
     *      - Highlight the selected board cell using {@link Renderer#drawRectangle(GraphicsContext, double, double)}
     *      - Refer to {@link GamePlayPane#toBoardCoordinate(double)} for help
     * @param event mouse click
     */
    private void onCanvasPressed(MouseEvent event){
        // TODO-DONE
        Renderer.drawRectangle(gamePlayCanvas.getGraphicsContext2D(),
                toBoardCoordinate(event.getX()), toBoardCoordinate(event.getY()));
        if(AudioManager.getInstance().isEnabled()){
            AudioManager.getInstance().playSound(AudioManager.SoundRes.CLICK);
        }
        source = new Place(toBoardCoordinate(event.getX()), toBoardCoordinate(event.getY()));
    }

    /**
     * When mouse dragging, draw a path
     * Hint:
     *      - When mouse dragging, you can use {@link Renderer#drawOval(GraphicsContext, double, double)} to show the path
     *      - Refer to {@link GamePlayPane#toBoardCoordinate(double)} for help
     * @param event mouse position
     */
    private void onCanvasDragged(MouseEvent event){
        //TODO-DONE
        Renderer.drawOval(gamePlayCanvas.getGraphicsContext2D(), event.getX(), event.getY());
    }

    /**
     * Mouse release handler
     * Hint:
     *      - When mouse released, a {@link Move} is completed, you can either validate and make the move here, or somewhere else.
     *      - Refer to {@link GamePlayPane#toBoardCoordinate(double)} for help
     *      - If the piece has been successfully moved, play place.mp3 here (or somewhere else)
     * @param event mouse release
     */
    private void onCanvasReleased(MouseEvent event){
        // TODO-DONE
        //clear the shape first
        globalJeson.renderBoard(gamePlayCanvas);
        Renderer.renderPieces(gamePlayCanvas, globalConfiguration.getInitialBoard());
        //check whether the move is valid
        tempMove = new Move(source, new Place(toBoardCoordinate(event.getX()), toBoardCoordinate(event.getY())));
        Piece pieceMoved = globalConfiguration.getInitialBoard()[tempMove.getSource().x()][tempMove.getSource().y()];

        if(globalJeson.getCurrentPlayer().validateMove(globalJeson, tempMove) == null){
            if(globalJeson.getCurrentPlayer().equals(pieceMoved.getPlayer())){
                if(AudioManager.getInstance().isEnabled()){
                    AudioManager.getInstance().playSound(AudioManager.SoundRes.PLACE);
                }
                globalJeson.movePiece(tempMove);
                ticksElapsed.set(DurationTimer.getDefaultEachRound());
            } else {
                showInvalidMoveMsg("The piece you moved does not belong to you!");
                return;
            }
        }else{
            showInvalidMoveMsg(globalJeson.getCurrentPlayer().validateMove(globalJeson, tempMove));
            return;
        }

        //after successfully moved a piece
        //render board
        globalJeson.renderBoard(gamePlayCanvas);
        Renderer.renderPieces(gamePlayCanvas, globalConfiguration.getInitialBoard());
        //update history
        updateHistoryField(tempMove);
        //update score (+numOfMove+next player)
        globalJeson.updateScore(globalJeson.getCurrentPlayer(), pieceMoved, tempMove);
        //get winner
        checkWinner();
        //

    }

    /**
     * Creates a popup which tells the winner
     */
    private void createWinPopup(String winnerName){
        //TODO-DONE
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("Confirmation");
        alert.setContentText(winnerName + " win!");

        ButtonType newGame = new ButtonType("Start New Game", ButtonBar.ButtonData.LEFT);
        ButtonType export = new ButtonType("Export Move Records");
        ButtonType returnMain = new ButtonType("Return to Main Menu", ButtonBar.ButtonData.RIGHT);

        alert.getButtonTypes().setAll(newGame, export, returnMain);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == newGame){
            onRestartButtonClick();
        } else if (result.get() == export) {//save file
            try {
                Serializer.getInstance().saveToFile(globalJeson);
            }catch (IOException ex){
                System.out.println(ex);
            }
            //after save, restart
            onRestartButtonClick();
        } else if (result.get() == returnMain) {
            doQuitToMenu();
        }
    }


    /**
     * check winner, if winner comes out, then play the win.mp3 and popup window.
     * The window has three options:
     *      - Start New Game: the same function as clicking "restart" button
     *      - Export Move Records: Using {@link castle.comp3021.assignment.protocol.io.Serializer} to write game's configuration to file
     *      - Return to Main menu, using {@link GamePlayPane#doQuitToMenuAction()}
     */
    private void checkWinner(){
        //TODO-DONE
        //createWinPopup
        int lastX = tempMove.getDestination().x(), lastY = tempMove.getDestination().y();
        Piece lastPiece = globalConfiguration.getInitialBoard()[lastX][lastY];
        Player winner = globalJeson.getWinner(lastPiece.getPlayer(), lastPiece, tempMove);
        if(winner != null){
            if(AudioManager.getInstance().isEnabled()){
                AudioManager.getInstance().playSound(AudioManager.SoundRes.WIN);
            }
            globalJeson.stopCountdown();
            createWinPopup(winner.getName());
        }
    }

    /**
     * Popup a window showing invalid move information
     * @param errorMsg error string stating why this move is invalid
     */
    private void showInvalidMoveMsg(String errorMsg){
        //TODO-DONE
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Move");
        alert.setHeaderText("Your movement is invalid due to following reason(s):");
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }

    /**
     * Before actually quit to main menu, popup a alert window to double check
     * Hint:
     *      - title: Confirm
     *      - HeaderText: Return to menu?
     *      - ContentText: Game progress will be lost.
     *      - Buttons: CANCEL and OK
     *  If click OK, then refer to {@link GamePlayPane#doQuitToMenu()}
     *  If click Cancle, than do nothing.
     */
    private void doQuitToMenuAction() {
        // TODO-DONE
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Return to menu?");
        alert.setContentText("Game progress will be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            doQuitToMenu();
        }
            //cancel and do nothing
    }

    /**
     * Update the move to the historyFiled
     * @param move the last move that has been made
     */
    private void updateHistoryField(Move move){
        //TODO-DONE
        historyFiled.setText(historyFiled.getText() +
                String.format("[%d, %d]", move.getSource().x(), move.getSource().y()) + " -> " +
                String.format("[%d, %d]", move.getDestination().x(), move.getDestination().y()) + "\n");
    }

    /**
     * Go back to main menu
     * Hint: before quit, you need to end the game
     */
    private void doQuitToMenu() {
        // TODO-DONE
        endGame();
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }

    /**
     * Converting a vertical or horizontal coordinate x to the coordinate in board
     * Hint:
     *      The pixel size of every piece is defined in {@link ViewConfig#PIECE_SIZE}
     * @param x coordinate of mouse click
     * @return the coordinate on board
     */
    private int toBoardCoordinate(double x){
        // TODO-DONE
        return (int) (x / ViewConfig.PIECE_SIZE);
    }

    /**
     * Handler of ending a game
     * Hint:
     *      - Clear the board, history text field
     *      - Reset buttons
     *      - Reset timer
     *
     */
    private void endGame() {
        //TODO-DONE
        globalJeson.stopCountdown();
        historyFiled.setText("");
    }
}
