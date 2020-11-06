package castle.comp3021.assignment.protocol.io;


import castle.comp3021.assignment.gui.FXJesonMor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class exports the entire game configuration and procedure to file
 * You need to overwrite .toString method for the class that will be serialized
 * Hint:
 *      - The output folder should be selected in a popup window {@link javafx.stage.FileChooser}
 *      - Read file with {@link java.io.BufferedWriter}
 */
public class Serializer {
    @NotNull
    private static final Serializer INSTANCE = new Serializer();

    /**
     * @return Singleton instance of this class.
     */
    @NotNull
    public static Serializer getInstance() {
        return INSTANCE;
    }


    /**
     * Save a {@link castle.comp3021.assignment.textversion.JesonMor} to file.
     *
     * @param fxJesonMor a fxJesonMor instance under export
     * @throws IOException if an I/O exception has occurred.
     */
    public void saveToFile(FXJesonMor fxJesonMor) throws IOException {
        //TODO-DONE
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage s = new Stage();
        File file = fileChooser.showSaveDialog(s);
        if (file != null) {
            try {
                PrintWriter writer;
                writer = new PrintWriter(file);
                writer.write(fxJesonMor.toString());
                writer.close();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
            }
        }
    }
}
