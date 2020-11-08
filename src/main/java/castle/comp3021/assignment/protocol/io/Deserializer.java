package castle.comp3021.assignment.protocol.io;

import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;
import castle.comp3021.assignment.protocol.exception.InvalidGameException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class Deserializer {
    @NotNull
    private Path path;

    private Configuration configuration;

    private Integer[] storedScores;

    Place centralPlace;

    private ArrayList<MoveRecord> moveRecords = new ArrayList<>();



    public Deserializer(@NotNull final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }

        this.path = path;
    }

    /**
     * Returns the first non-empty and non-comment (starts with '#') line from the reader.
     *
     * @param br {@link BufferedReader} to read from.
     * @return First line that is a parsable line, or {@code null} there are no lines to read.
     * @throws IOException if the reader fails to read a line
     * @throws InvalidGameException if unexpected end of file
     */
    @Nullable
    private String getFirstNonEmptyLine(@NotNull final BufferedReader br) throws IOException {
        // TODO-DONE
        String s = br.readLine();
        if(s == null){
            throw new IOException("fail to read a line");
        }
        while (s.equals("") || s.charAt(0) == '#') {
            s = br.readLine();
            if(s == null){
                throw new InvalidGameException("unexpected end of file");
            }
        }
        return s;
    }

    public void parseGame() {
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            int size;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                // TODO: get size here-DONE
                try {
                    line = line.replace("size:", "");
                    size = Integer.parseInt(line);
                }catch (NumberFormatException ex){
                    throw new InvalidGameException("Unexpected EOF when parsing number of board size");
                }
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing number of board size");
            }

            int numMovesProtection;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                // TODO: get numMovesProtection here-DONE
                line = line.replace("numMovesProtection:", "");
                numMovesProtection = Integer.parseInt(line);
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing number of columns");
            }

            //TODO-DONE
            /**
             *  read central place here
             *  If success, assign to {@link Deserializer#centralPlace}
             *  Hint: You may use {@link Deserializer#parsePlace(String)}
             */
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                centralPlace = parsePlace(line);
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing central place");
            }

            int numPlayers;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                //TODO: get number of players here-DONE
                line = line.replace("numPlayers:", "");
                numPlayers = Integer.parseInt(line);
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing number of players");
            }


            // TODO:-DONE
            /**
             * create an array of players {@link Player} with length of numPlayers, and name it by the read-in name
             * Also create an array representing scores {@link Deserializer#storedScores} of players with length of numPlayers
             */
            storedScores = new Integer[numPlayers];
            String[] tempString;
            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                line = line.replace("name:", "");
                tempString = line.split("; score:");
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing players");
            }
            Player firstPlayer = new ConsolePlayer(tempString[0]);
            storedScores[0] = Integer.parseInt(tempString[1]);

            line = getFirstNonEmptyLine(reader);
            if (line != null) {
                line = line.replace("name:", "");
                tempString = line.split("; score:");
            } else {
                throw new InvalidGameException("Unexpected EOF when parsing players");
            }
            Player secondPlayer = new ConsolePlayer(tempString[0]);
            storedScores[1] = Integer.parseInt(tempString[1]);

            Player[] players = new Player[]{firstPlayer, secondPlayer};

            // TODO-DONE
            /**
             * try to initialize a configuration object  with the above read-in variables
             * if fail, throw InvalidConfigurationError exception
             * if success, assign to {@link Deserializer#configuration}
             */
            try {
                configuration = new Configuration(size, players, numMovesProtection);
                configuration.setAllInitialPieces();
            }catch (InvalidConfigurationError err){
                throw err;
            }
            // TODO-DONE
            /**
             * Parse the string of move records into an array of {@link MoveRecord}
             * Assign to {@link Deserializer#moveRecords}
             * You should first implement the following methods:
             * - {@link Deserializer#parseMoveRecord(String)}}
             * - {@link Deserializer#parseMove(String)} ()}
             * - {@link Deserializer#parsePlace(String)} ()}
             */
            line = getFirstNonEmptyLine(reader);
            if(line == null){
                throw new InvalidGameException("Unexpected EOF when parsing move record");
            }
            while (!line.equals("END")){
                moveRecords.add(parseMoveRecord(line));
                line = getFirstNonEmptyLine(reader);
                if(line == null){
                    throw new InvalidGameException("Unexpected EOF when parsing move record");
                }
            }

        } catch (IOException ioe) {
            throw new InvalidGameException(ioe);
        }
    }

    public Configuration getLoadedConfiguration(){
        return configuration;
    }

    public Integer[] getStoredScores(){
        return storedScores;
    }

    public ArrayList<MoveRecord> getMoveRecords(){
        return moveRecords;
    }

    /**
     * Parse the string into a {@link MoveRecord}
     * Handle InvalidConfigurationError if the parse fails.
     * @param moveRecordString a string of a move record
     * @return a {@link MoveRecord}
     */
    private MoveRecord parseMoveRecord(String moveRecordString){
        // TODO-DONE
        moveRecordString = moveRecordString.replace("player:", "");
        var temp = moveRecordString.split("; ");
        if(configuration.getPlayers()[0].getName().equals(temp[0])){
            return new MoveRecord(configuration.getPlayers()[0], parseMove(temp[1]));
        }else {
            return new MoveRecord(configuration.getPlayers()[1], parseMove(temp[1]));
        }

    }

    /**
     * Parse a string of move to a {@link Move}
     * Handle InvalidConfigurationError if the parse fails.
     * @param moveString given string
     * @return {@link Move}
     */
    private Move parseMove(String moveString) {
        // TODO-DONE
        moveString = moveString.replace("move:", "").trim();
        var temp = moveString.split("->");
        return new Move(parsePlace(temp[0]), parsePlace(temp[1]));
    }

    /**
     * Parse a string of move to a {@link Place}
     * Handle InvalidConfigurationError if the parse fails.
     * @param placeString given string
     * @return {@link Place}
     */
    private Place parsePlace(String placeString) {
        //TODO-DONE
        placeString = placeString.replace("centralPlace:", "");
        placeString = placeString.replace("(", "");
        placeString = placeString.replace(")", "");
        var temp = placeString.split(",");
        return new Place(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
    }
}
