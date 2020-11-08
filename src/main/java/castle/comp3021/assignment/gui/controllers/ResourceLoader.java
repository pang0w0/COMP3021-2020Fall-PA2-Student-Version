package castle.comp3021.assignment.gui.controllers;

import castle.comp3021.assignment.protocol.exception.ResourceNotFoundException;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

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
        // TODO: Initialize RES_PATH-DONE
        // replace null to the actual path
        RES_PATH = Paths.get("C:\\Users\\pang\\IdeaProjects\\COMP3021-2020Fall-PA2-Student-Version\\src\\main\\resources").toAbsolutePath();
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
        // TODO-DONE
        try {
            Paths.get(RES_PATH.resolve(relativePath).toString());
        } catch (InvalidPathException e){
            throw new ResourceNotFoundException("the file cannot be found under the resource directory");
        }

        //System.out.println(RES_PATH.resolve(relativePath).toString());
        return RES_PATH.resolve(relativePath).toFile().toURI().toString();
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
        Image image;
        String loc;
        switch (typeChar){
            case 'K':
                loc = getResource("assets/images/whiteK.png");
                image = new Image(loc);
                break;
            case 'A':
                loc = getResource("assets/images/whiteA.png");
                image = new Image(loc);
                break;
            case 'k':
                loc = getResource("assets/images/blackK.png");
                image = new Image(loc);
                break;
            case 'a':
                loc = getResource("assets/images/blackA.png");
                image = new Image(loc);
                break;
            case 'c':
                loc = getResource("assets/images/center.png");
                image = new Image(loc);
                break;
            case 'l':
                loc = getResource("assets/images/lightBoard.png");
                image = new Image(loc);
                break;
            default:
            case 'd':
                loc = getResource("assets/images/darkBoard.png");
                image = new Image(loc);
                break;
        }
        return image;
    }

}