package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.FXJesonMor;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.AudioManager;
import castle.comp3021.assignment.gui.controllers.Renderer;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.Configuration;
import castle.comp3021.assignment.protocol.MoveRecord;
import castle.comp3021.assignment.protocol.Place;
import castle.comp3021.assignment.protocol.Player;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;
import castle.comp3021.assignment.protocol.exception.InvalidGameException;
import castle.comp3021.assignment.protocol.io.Deserializer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
//import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class ValidationPane extends BasePane{
    @NotNull
    private final VBox leftContainer = new BigVBox();
    @NotNull
    private final BigVBox centerContainer = new BigVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Label explanation = new Label("Upload and validation the game history.");
    @NotNull
    private final Button loadButton = new BigButton("Load file");
    @NotNull
    private final Button validationButton = new BigButton("Validate");
    @NotNull
    private final Button replayButton = new BigButton("Replay");
    @NotNull
    private final Button returnButton = new BigButton("Return");

    private Canvas gamePlayCanvas = new Canvas();

    /**
     * store the loaded information
     */
    private Configuration loadedConfiguration;
    private Integer[] storedScores;
    private FXJesonMor loadedGame;
    private Place loadedcentralPlace;
    private ArrayList<MoveRecord> loadedMoveRecords = new ArrayList<>();

    private BooleanProperty isValid = new SimpleBooleanProperty(false);//??don't know how to use


    public ValidationPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    @Override
    void connectComponents() {
        // TODO-DONE
        validationButton.setDisable(true);
        replayButton.setDisable(true);
        leftContainer.getChildren().addAll(title, explanation, loadButton, validationButton,
                replayButton, returnButton);

        centerContainer.getChildren().add(gamePlayCanvas);

        setLeft(leftContainer);
        setCenter(centerContainer);
    }

    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
    }

    /**
     * Add callbacks to each buttons.
     * Initially, replay button is disabled, gamePlayCanvas is empty
     * When validation passed, replay button is enabled.
     */
    @Override
    void setCallbacks() {
        //TODO
        loadButton.setOnAction(e->{
            if(loadFromFile()){
                loadedGame.stopCountdown();
                validationButton.setDisable(false);
                replayButton.setDisable(true);
            }
        });

        validationButton.setOnAction(e->{
            onClickValidationButton();
            isValid.setValue(false);
        });

        replayButton.setOnAction(e->{//only active for one time
            if(!isValid.get()){
                isValid.setValue(true);
                onClickReplayButton();
            }
        });

        returnButton.setOnAction(e->{
            returnToMainMenu();
        });

    }

    /**
     * load From File and deserializer the game by two steps:
     *      - {@link ValidationPane#getTargetLoadFile}
     *      - {@link Deserializer}
     * Hint:
     *      - Get file from {@link ValidationPane#getTargetLoadFile}
     *      - Instantiate an instance of {@link Deserializer} using the file's path
     *      - Using {@link Deserializer#parseGame()}
     *      - Initialize {@link ValidationPane#loadedConfiguration}, {@link ValidationPane#loadedcentralPlace},
     *                   {@link ValidationPane#loadedGame}, {@link ValidationPane#loadedMoveRecords}
     *                   {@link ValidationPane#storedScores}
     * @return whether the file and information have been loaded successfully.
     */
    private boolean loadFromFile() {
        //TODO
        try {
            File file = getTargetLoadFile();
            if(file == null){
                return false;
            }
            Deserializer de = new Deserializer(file.toPath());
            try {
                de.parseGame();
            }catch (InvalidGameException gameException) {
                showErrorConfiguration(gameException.getMessage());
                return false;
            }
            try {
                //initialize
                if(loadedGame != null){
                    loadedGame.stopCountdown();
                }
                loadedConfiguration = de.getLoadedConfiguration();
                loadedcentralPlace = de.getLoadedConfiguration().getCentralPlace();
                loadedMoveRecords = de.getMoveRecords();
                storedScores = de.getStoredScores();
                loadedGame = new FXJesonMor(loadedConfiguration);
                return true;
            }catch (InvalidConfigurationError error){
                showErrorConfiguration(error.getMessage());
                return false;
            }
        }catch (FileNotFoundException err){
            showErrorMsg();
            return false;
        }
    }

    /**
     * When click validation button, validate the loaded game configuration and move history
     * Hint:
     *      - if nothing loaded, call {@link ValidationPane#showErrorMsg}
     *      - if loaded, check loaded content by calling {@link ValidationPane#validateHistory}
     *      - When the loaded file has passed validation, the "replay" button is enabled.
     */
    private void onClickValidationButton(){
        //TODO
        if(loadedConfiguration == null){
            showErrorMsg();
            return;
        }
//        //every click refresh the whole game
//        loadedConfiguration = new Configuration(loadedConfiguration.getSize(),
//                new Player[]{new ConsolePlayer(loadedConfiguration.getPlayers()[0].getName()),
//                        new ConsolePlayer(loadedConfiguration.getPlayers()[1].getName())},
//                loadedConfiguration.getNumMovesProtection());
//        loadedConfiguration.setAllInitialPieces();
//        loadedGame = new FXJesonMor(loadedConfiguration);

        if(validateHistory()){
            passValidationWindow();
            replayButton.setDisable(false);
            validationButton.setDisable(true);
        }else{
            //the window have popped
            validationButton.setDisable(true);
        }
    }

    /**
     * Display the history of recorded move.
     * Hint:
     *      - You can add a "next" button to render each move, or
     *      - Or you can refer to {@link Task} for implementation.
     */
    private void onClickReplayButton(){
        //TODO
        gamePlayCanvas.setHeight(loadedConfiguration.getSize() * ViewConfig.PIECE_SIZE);
        gamePlayCanvas.setWidth(loadedConfiguration.getSize() * ViewConfig.PIECE_SIZE);
        gamePlayCanvas.setVisible(true);

        loadedConfiguration = new Configuration(loadedConfiguration.getSize(),
                new Player[]{new ConsolePlayer(loadedConfiguration.getPlayers()[0].getName()),
                        new ConsolePlayer(loadedConfiguration.getPlayers()[1].getName())},
                loadedConfiguration.getNumMovesProtection());
        loadedConfiguration.setAllInitialPieces();
        loadedGame = new FXJesonMor(loadedConfiguration);

        loadedGame.renderBoard(gamePlayCanvas);
        Renderer.renderPieces(gamePlayCanvas, loadedConfiguration.getInitialBoard());

        var ref = new Object() {
            int numOfMove = 0;
        };
        loadedGame.addOnTickHandler(()->{
            if(ref.numOfMove < loadedMoveRecords.size()) {
                if (AudioManager.getInstance().isEnabled()) {
                    AudioManager.getInstance().playSound(AudioManager.SoundRes.PLACE);
                }
                loadedGame.movePiece(loadedMoveRecords.get(ref.numOfMove).getMove());
                ref.numOfMove++;
                loadedGame.renderBoard(gamePlayCanvas);
                Renderer.renderPieces(gamePlayCanvas, loadedConfiguration.getInitialBoard());
            }else {
                loadedGame.stopCountdown();
            }
        });

        loadedGame.startCountdown();

//        var ref = new Object() {
//            int numOfMove = 0;
//        };
//        Timeline animation = new Timeline(
//                new KeyFrame(Duration.millis(500), e->{
//                    if(isValid.get()) {
//                        if (AudioManager.getInstance().isEnabled()) {
//                            AudioManager.getInstance().playSound(AudioManager.SoundRes.PLACE);
//                        }
//                        loadedGame.movePiece(loadedMoveRecords.get(ref.numOfMove).getMove());
//                        ref.numOfMove++;
//                        loadedGame.renderBoard(gamePlayCanvas);
//                        Renderer.renderPieces(gamePlayCanvas, loadedConfiguration.getInitialBoard());
//                    }else {
//                        gamePlayCanvas.setVisible(false);
//                    }
//                }));
//        animation.setCycleCount(loadedMoveRecords.size());
//        animation.play();
    }

    /**
     * Validate the {@link ValidationPane#loadedConfiguration}, {@link ValidationPane#loadedcentralPlace},
     *              {@link ValidationPane#loadedGame}, {@link ValidationPane#loadedMoveRecords}
     *              {@link ValidationPane#storedScores}
     * Hint:
     *      - validate configuration of game
     *      - whether each move is valid
     *      - whether scores are correct
     */
    private boolean validateHistory(){
        //TODO-DONE
//        if(loadedcentralPlace != loadedConfiguration.getCentralPlace()){
//            showErrorConfiguration("Central place incorrect");
//            return false;
//        }
        for (MoveRecord m : loadedMoveRecords) {
            if(loadedGame.getCurrentPlayer().validateMove(loadedGame, m.getMove()) == null){
                if(loadedGame.getCurrentPlayer().equals(m.getPlayer())){
                    loadedGame.movePiece(m.getMove());
                    loadedGame.updateScore(m.getPlayer(),
                            loadedGame.getPiece(m.getMove().getDestination().x(), m.getMove().getDestination().y()),
                            m.getMove());
                }else {
                    showErrorConfiguration("Moved a piece that not belong to current player");
                }
            }else {
                showErrorConfiguration(loadedGame.getCurrentPlayer().validateMove(loadedGame, m.getMove()));
                return false;
            }
        }
        if(loadedConfiguration.getPlayers()[0].getScore() == storedScores[0] &&
                loadedConfiguration.getPlayers()[1].getScore() == storedScores[1]){
            return true;
        }else {
            showErrorConfiguration("Score is incorrect");
            return false;
        }
    }

    /**
     * Popup window show error message
     * Hint:
     *      - title: Invalid configuration or game process!
     *      - HeaderText: Due to following reason(s):
     *      - ContentText: errorMsg
     * @param errorMsg error message
     */
    private void showErrorConfiguration(String errorMsg){
        // TODO-DONE
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid configuration or game process!");
        alert.setHeaderText("Due to following reason(s):");
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }

    /**
     * Pop up window to warn no record has been uploaded.
     * Hint:
     *      - title: Error!
     *      - ContentText: You haven't loaded a record, Please load first.
     */
    private void showErrorMsg(){//????
        //TODO-DONE
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("");
        alert.setContentText("You haven't loaded a record, Please load first.");
        alert.showAndWait();
    }

    /**
     * Pop up window to show pass the validation
     * Hint:
     *     - title: Confirm
     *     - HeaderText: Pass validation!
     */
    private void passValidationWindow(){
        //TODO-DONE
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirm!");
        alert.setHeaderText("Pass validation!");
        alert.setContentText("");
        alert.showAndWait();
    }

    /**
     * Return to Main menu
     * Hint:
     *  - Before return, clear the rendered canvas, and clear stored information
     */
    private void returnToMainMenu(){
        // TODO
        gamePlayCanvas.setVisible(false);
        isValid.setValue(false);
        loadButton.setDisable(false);
        validationButton.setDisable(true);
        replayButton.setDisable(true);
        if(loadedGame != null){
            loadedGame.stopCountdown();
        }
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }


    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        //TODO-DONE
        FileChooser fileChooser = new FileChooser();
        Stage s = new Stage();
        File file = fileChooser.showOpenDialog(s);
        return file;
    }
}
