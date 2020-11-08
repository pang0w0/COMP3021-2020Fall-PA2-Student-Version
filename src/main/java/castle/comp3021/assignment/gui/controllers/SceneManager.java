package castle.comp3021.assignment.gui.controllers;

import castle.comp3021.assignment.gui.ViewConfig;
import castle.comp3021.assignment.gui.views.panes.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//import castle.comp3021.assignment.gui.views.panes.*;


import java.util.Map;

/**
 * Singleton class for managing scenes.
 */
public class SceneManager {
    /**
     * Singleton instance.
     */
    private static final SceneManager INSTANCE = new SceneManager();


    /**
     * Main menu scene.
     */
    @NotNull
    private final Scene mainMenuScene = new Scene(new MainMenuPane(), ViewConfig.WIDTH / 2.0, ViewConfig.HEIGHT);

//    private final Scene mainMenuScene = new Scene(new MainMenuPane(), Configuration.WIDTH / 2.0, Configuration.HEIGHT);
    /**
     * Settings scene.
     */
    @NotNull
    private final Scene settingsScene = new Scene(new GamePane(), ViewConfig.WIDTH / 2.0, ViewConfig.HEIGHT);

    @NotNull
    private final Scene validationScene = new Scene(new ValidationPane(), ViewConfig.WIDTH, ViewConfig.HEIGHT);

    /**
     * Gameplay scene.
     */
    @NotNull
    private final Scene gameplayScene = new Scene(new GamePlayPane(), ViewConfig.WIDTH, ViewConfig.HEIGHT);
    /**
     * Level editor scene.
     */
    @NotNull
    private final Scene settingEditorScene = new Scene(new SettingPane(), ViewConfig.WIDTH, ViewConfig.HEIGHT);
    /**
     * Map for fast lookup of {@link BasePane} to their respective {@link Scene}.
     */
    @NotNull
    private final Map<Class<? extends BasePane>, Scene> scenes = Map.ofEntries(
            Map.entry(MainMenuPane.class, mainMenuScene),
            Map.entry(GamePane.class, settingsScene),
            Map.entry(GamePlayPane.class, gameplayScene),
            Map.entry(SettingPane.class, settingEditorScene),
            Map.entry(ValidationPane.class, validationScene)
    );
    /**
     * Primary stage.
     */
    @Nullable
    private Stage stage;

    /**
     * Add CSS styles to every scene
     */
    private SceneManager() {
        //TODO-DONE
        String path = "file:src/main/resources/assets/styles/styles.css";
        //String path = ViewConfig.CSS_STYLES_PATH;
        mainMenuScene.getStylesheets().add(path);
        settingsScene.getStylesheets().add(path);
        gameplayScene.getStylesheets().add(path);
        settingEditorScene.getStylesheets().add(path);
        validationScene.getStylesheets().add(path);
    }

    /**
     * Sets the primary stage.
     *
     * @param stage Primary stage.
     */
    public void setStage(@NotNull final Stage stage) {
        if (this.stage != null) {
            throw new IllegalStateException("Primary stage is already initialized!");
        }

        this.stage = stage;
    }

    /**
     * Replaces the currently active {@link Scene} with another one.
     *
     * @param scene New scene to display.
     */
    private void showScene(@NotNull final Scene scene) {
        if (stage == null) {
            return;
        }
        stage.hide();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Replaces the current {@link BasePane} with another one.
     *
     * @param pane New pane to display.
     * @throws IllegalArgumentException If the {@code pane} is not known.
     * Hint:
     * showPane works with help of two parts: {@link SceneManager#scenes} and {@link SceneManager#showScene(Scene)}
     * The logic is: get scene of pane by {@link SceneManager#scenes}
     *      if the corresponding scene exists, then call showScene to show the corresponding scene.
     *      else throw IllegalArgumentException
     *
     */
    public void showPane(@NotNull final Class<? extends BasePane> pane) {
        //TODO-DONE
        if(getPane(pane) != null){
            if(getPane(pane).getClass() == MainMenuPane.class){
                showScene(mainMenuScene);
            }else if(getPane(pane).getClass() == GamePane.class){
                showScene(settingsScene);
            }else if(getPane(pane).getClass() == GamePlayPane.class){
                showScene(gameplayScene);
            }else if(getPane(pane).getClass() == SettingPane.class){
                showScene(settingEditorScene);
            }else if(getPane(pane).getClass() == ValidationPane.class){
                showScene(validationScene);
            }else{
                throw new IllegalArgumentException();
            }
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Retrieves the underlying singleton {@link BasePane} object.
     *
     * @param pane {@link Class} type of pane to retrieve.
     * @param <T>  Actual type of the {@link BasePane} object.
     * @return Handle to the singleton {@link BasePane} object.
     */
    public <T> T getPane(@NotNull final Class<? extends BasePane> pane) {
        //noinspection unchecked
        return (T) scenes.get(pane).getRoot();
    }

    @NotNull
    public static SceneManager getInstance() {
        return INSTANCE;
    }

}
