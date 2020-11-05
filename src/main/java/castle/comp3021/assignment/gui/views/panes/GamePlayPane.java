package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.DurationTimer;
import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.GameplayInfoPane;
import castle.comp3021.assignment.gui.views.SideMenuVBox;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.gui.controllers.Renderer;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;

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
    // TODO
    public FXJesonMor globalJeson = null;
    public static  IntegerProperty time = new SimpleIntegerProperty(0);
    private ArrayList<String> moveRecords = new ArrayList<>();
    private Place source;

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
        globalJeson = new FXJesonMor(globalConfiguration);
        time.bindBidirectional(ticksElapsed);
        infoPane = new GameplayInfoPane(globalJeson.getPlayer1Score(), globalJeson.getPlayer2Score(),
                globalJeson.getCurPlayerName(), ticksElapsed);

        topBar.setAlignment(Pos.CENTER);
        topBar.getChildren().add(title);
        leftContainer.getChildren().addAll(parameterText, historyLabel, scrollPane,
                startButton, restartButton, returnButton);
        centerContainer.getChildren().addAll(gamePlayCanvas, infoPane);
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

        restartButton.setOnAction(e->{ onRestartButtonClick(); });

        returnButton.setOnAction(e->{SceneManager.getInstance().showPane(MainMenuPane.class);});
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
        ticksElapsed.setValue(DurationTimer.getDefaultEachRound());
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
        //TODO
        globalJeson.startCountdown();

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
     * Restart the game
     * Hint: end the current game and start a new game
     */
    private void onRestartButtonClick(){
        //TODO

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
        // TODO
        Renderer.drawRectangle(gamePlayCanvas.getGraphicsContext2D(),
                toBoardCoordinate(event.getX()), toBoardCoordinate(event.getY()));
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
        //TODO
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
        // TODO
        Move tempMove = new Move(source, new Place(toBoardCoordinate(event.getX()), toBoardCoordinate(event.getY())));

        Move[] avaMove = globalJeson.getAvailableMoves(globalJeson.getCurrentPlayer());
        for(int i=0;i<avaMove.length;i++){
            if(avaMove[i].equals(tempMove)){
                globalJeson.movePiece(tempMove);
            }else if(i == avaMove.length - 1){
                //error
            }
        }

        globalJeson.renderBoard(gamePlayCanvas);
        Renderer.renderPieces(gamePlayCanvas, globalConfiguration.getInitialBoard());
    }

    /**
     * Creates a popup which tells the winner
     */
    private void createWinPopup(String winnerName){
        //TODO
    }


    /**
     * check winner, if winner comes out, then play the win.mp3 and popup window.
     * The window has three options:
     *      - Start New Game: the same function as clicking "restart" button
     *      - Export Move Records: Using {@link castle.comp3021.assignment.protocol.io.Serializer} to write game's configuration to file
     *      - Return to Main menu, using {@link GamePlayPane#doQuitToMenuAction()}
     */
    private void checkWinner(){
        //TODO
    }

    /**
     * Popup a window showing invalid move information
     * @param errorMsg error string stating why this move is invalid
     */
    private void showInvalidMoveMsg(String errorMsg){
        //TODO
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
        // TODO
    }

    /**
     * Update the move to the historyFiled
     * @param move the last move that has been made
     */
    private void updateHistoryField(Move move){
        //TODO
    }

    /**
     * Go back to main menu
     * Hint: before quit, you need to end the game
     */
    private void doQuitToMenu() {
        // TODO
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
        //TODO
    }
}
