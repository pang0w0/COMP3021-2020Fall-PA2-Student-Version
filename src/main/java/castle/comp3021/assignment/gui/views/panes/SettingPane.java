package castle.comp3021.assignment.gui.views.panes;
import castle.comp3021.assignment.gui.DurationTimer;
import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.AudioManager;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;
import castle.comp3021.assignment.gui.views.NumberTextField;
import castle.comp3021.assignment.gui.views.SideMenuVBox;
import castle.comp3021.assignment.protocol.Configuration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SettingPane extends BasePane {
    @NotNull
    private final Label title = new Label("Jeson Mor <Game Setting>");
    @NotNull
    private final Button saveButton = new BigButton("Save");
    @NotNull
    private final Button returnButton = new BigButton("Return");
    @NotNull
    private final Button isHumanPlayer1Button = new BigButton("Player 1: ");
    @NotNull
    private final Button isHumanPlayer2Button = new BigButton("Player 2: ");
    @NotNull
    private final Button toggleSoundButton = new BigButton("Sound FX: Enabled");

    @NotNull
    private final VBox leftContainer = new SideMenuVBox();

    @NotNull
    private final NumberTextField sizeFiled = new NumberTextField(String.valueOf(globalConfiguration.getSize()));

    @NotNull
    private final BorderPane sizeBox = new BorderPane(null, null, sizeFiled, null, new Label("Board size"));

    @NotNull
    private final NumberTextField durationField = new NumberTextField(String.valueOf(DurationTimer.getDefaultEachRound()));
    @NotNull
    private final BorderPane durationBox = new BorderPane(null, null, durationField, null,
            new Label("Max Duration (s)"));

    @NotNull
    private final NumberTextField numMovesProtectionField =
            new NumberTextField(String.valueOf(globalConfiguration.getNumMovesProtection()));
    @NotNull
    private final BorderPane numMovesProtectionBox = new BorderPane(null, null,
            numMovesProtectionField, null, new Label("Steps of protection"));

    @NotNull
    private final VBox centerContainer = new BigVBox();
    @NotNull
    private final TextArea infoText = new TextArea(ViewConfig.getAboutText());


    public SettingPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * Add components to corresponding containers
     */
    @Override
    void connectComponents() {
        //TODO-DONE
        leftContainer.getChildren().addAll(title, sizeBox, numMovesProtectionBox, durationBox,
                isHumanPlayer1Button, isHumanPlayer2Button, toggleSoundButton, saveButton, returnButton);
        centerContainer.getChildren().add(infoText);
        setLeft(leftContainer);
        setCenter(centerContainer);
    }

    @Override
    void styleComponents() {
        infoText.getStyleClass().add("text-area");
        infoText.setEditable(false);
        infoText.setWrapText(true);
        infoText.setPrefHeight(ViewConfig.HEIGHT);
    }

    /**
     * Add handlers to buttons, textFields.
     * Hint:
     *  - Text of {@link SettingPane#isHumanPlayer1Button}, {@link SettingPane#isHumanPlayer2Button},
     *            {@link SettingPane#toggleSoundButton} should be changed accordingly
     *  - You may use:
     *      - {@link Configuration#isFirstPlayerHuman()},
     *      - {@link Configuration#isSecondPlayerHuman()},
     *      - {@link Configuration#setFirstPlayerHuman(boolean)}
     *      - {@link Configuration#isSecondPlayerHuman()},
     *      - {@link AudioManager#setEnabled(boolean)},
     *      - {@link AudioManager#isEnabled()},
     */
    @Override
    void setCallbacks() {
        //TODO
        isHumanPlayer1Button.setOnAction(e->{
            if (globalConfiguration.isFirstPlayerHuman()){
                isHumanPlayer1Button.setText("Player 1: Computer");
            }else{
                isHumanPlayer1Button.setText("Player 1: Player");
            }
        });

        isHumanPlayer2Button.setOnAction(e->{
            if (globalConfiguration.isSecondPlayerHuman()){
                isHumanPlayer2Button.setText("Player 2: Computer");
            }else{
                isHumanPlayer2Button.setText("Player 2: Player");
            }
        });

        toggleSoundButton.setOnAction(e->{
            if(AudioManager.getInstance().isEnabled()){
                AudioManager.getInstance().setEnabled(false);
                toggleSoundButton.setText("Sound FX: Disabled");
            }else {
                AudioManager.getInstance().setEnabled(true);
                toggleSoundButton.setText("Sound FX: Enabled");
            }
        });

        saveButton.setOnAction(e->{returnToMainMenu(true);});

        returnButton.setOnAction(e->{returnToMainMenu(false);});
    }

    /**
     * Fill in the default values for all editable fields.
     */
    private void fillValues() {
        // TODO
        sizeFiled.setText(""+globalConfiguration.getSize());
        numMovesProtectionField.setText(""+globalConfiguration.getNumMovesProtection());

        if(globalConfiguration.isFirstPlayerHuman()){
            isHumanPlayer1Button.setText("Player 1: Computer");
        }else {
            isHumanPlayer1Button.setText("Player 1: Player");
        }

        if(globalConfiguration.isSecondPlayerHuman()){
            isHumanPlayer2Button.setText("Player 2: Computer");
        }else {
            isHumanPlayer2Button.setText("Player 2: Player");
        }

    }

    /**
     * Switches back to the {@link MainMenuPane}.
     *
     * @param writeBack Whether to save the values present in the text fields to their respective classes.
     */
    private void returnToMainMenu(final boolean writeBack) {
        //TODO
        if(writeBack){
            globalConfiguration.setSize(sizeFiled.getValue());
            globalConfiguration.setNumMovesProtection(numMovesProtectionField.getValue());
            globalConfiguration.setFirstPlayerHuman(isHumanPlayer1Button.getText().equals("Player 1: Player"));
            globalConfiguration.setSecondPlayerHuman(isHumanPlayer2Button.getText().equals("Player 2: Player"));
        }
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }

    /**
     * Validate the text fields
     * The useful msgs are predefined in {@link ViewConfig#MSG_BAD_SIZE_NUM}, etc.
     * @param size number in {@link SettingPane#sizeFiled}
     * @param numProtection number in {@link SettingPane#numMovesProtectionField}
     * @param duration number in {@link SettingPane#durationField}
     * @return If validation failed, {@link Optional} containing the reason message; An empty {@link Optional}
     *      * otherwise.
     */
    public static Optional<String> validate(int size, int numProtection, int duration) {
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

        if(duration <= 0){
            return  Optional.of(ViewConfig.MSG_NEG_DURATION);
        }

        return Optional.empty();
    }
}
