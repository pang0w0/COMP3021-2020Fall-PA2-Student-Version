package castle.comp3021.assignment.gui.views.panes;

import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.controllers.SceneManager;
import castle.comp3021.assignment.protocol.Configuration;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import castle.comp3021.assignment.gui.views.BigButton;
import castle.comp3021.assignment.gui.views.BigVBox;

public class MainMenuPane extends BasePane {
    @NotNull
    private final VBox container = new BigVBox();
    @NotNull
    private final Label title = new Label("Jeson Mor");
    @NotNull
    private final Button playButton = new BigButton("Play Game");

    @NotNull
    private final Button settingsButton = new BigButton("Settings / About ");
    @NotNull
    private final Button validationButtion = new BigButton("Validation");
    @NotNull
    private final Button quitButton = new BigButton("Quit");

    public MainMenuPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    @Override
    void connectComponents() {
        // TODO-DONE
        //container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(title, playButton, settingsButton, validationButtion, quitButton);
        setCenter(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        title.getStyleClass().add("head-size");
    }

    /**
     * add callbacks to buttons
     * Hint:
     *      - playButton -> {@link GamePane}
     *      - settingsButton -> {@link SettingPane}
     *      - validationButtion -> {@link ValidationPane}
     *      - quitButton -> quit the game
     */
    @Override
    void setCallbacks() {
        //TODO-DOING
        playButton.setOnAction(e->{
            GamePane temp = SceneManager.getInstance().getPane(GamePane.class);
            temp.fillValues();
            SceneManager.getInstance().showPane(GamePane.class);
        });

        settingsButton.setOnAction(e->{
            SettingPane temp = SceneManager.getInstance().getPane(SettingPane.class);

            SceneManager.getInstance().showPane(SettingPane.class);
        });

        validationButtion.setOnAction(e->{
            ValidationPane temp = SceneManager.getInstance().getPane(ValidationPane.class);

            SceneManager.getInstance().showPane(ValidationPane.class);
        });

        quitButton.setOnAction(e->{Platform.exit();});
    }

}
