package castle.comp3021.assignment.gui.controllers;

import castle.comp3021.assignment.protocol.exception.ResourceNotFoundException;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class for loading resources from the filesystem.
 */
public class ResourceLoader {
    /**
     * Path to the resources directory.
     */
    @NotNull
    private static final Path RES_PATH;

    static {
        // TODO: Initialize RES_PATH-DONE?
        // replace null to the actual path
        RES_PATH = Paths.get("C:\\Users\\pang\\IdeaProjects\\COMP3021-2020Fall-PA2-Student-Version\\" +
                "src\\main\\resources\\assets\\");
    }

    /**
     * Retrieves a resource file from the resource directory.
     *
     * @param relativePath Path to the resource file, relative to the root of the resource directory.
     * @return Absolute path to the resource file.
     * @throws ResourceNotFoundException If the file cannot be found under the resource directory.
     */
    @NotNull
    public static String getResource(@NotNull final String relativePath){
        // TODO-?????
        try {
            Paths.get(RES_PATH.toString() + relativePath);
        } catch (InvalidPathException e){
            throw new ResourceNotFoundException("the file cannot be found under the resource directory");
        }

        return RES_PATH.toString() + relativePath;
    }

    /**
     * Return an image {@link Image} object
     * @param typeChar a character represents the type of image needed.
     *                 - 'K': white knight (whiteK.png)
     *                 - 'A': white archer (whiteA.png)
     *                 - 'k': black knight (blackK.png)
     *                 - 'a': black archer (blackA.png)
     *                 - 'c': central x (center.png)
     *                 - 'l': light board (lightBoard.png)
     *                 - 'd': dark board (darkBoard.png)
     * @return an image
     */
    @NotNull
    public static Image getImage(char typeChar) {
        // TODO-DONE?
        Image image = null;
        String loc;
        switch (typeChar){
            case 'K':
                loc = getResource("images/whiteK.png");
                image = new Image(loc);
                break;
            case 'A':
                loc = getResource("images/whiteA.png");
                image = new Image(loc);
                break;
            case 'k':
                loc = getResource("images/blackK.png");
                image = new Image(loc);
                break;
            case 'a':
                loc = getResource("images/blackA.png");
                image = new Image(loc);
                break;
            case 'c':
                loc = getResource("images/center.png");
                image = new Image(loc);
                break;
            case 'l':
                loc = getResource("images/lightBoard.png");
                image = new Image(loc);
                break;
            case 'd':
                loc = getResource("images/darkBoard.png");
                image = new Image(loc);
                break;
            default:
        }

        assert image != null;
        return image;
    }


}