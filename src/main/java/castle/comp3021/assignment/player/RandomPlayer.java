package castle.comp3021.assignment.player;

import castle.comp3021.assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A computer player that makes a move randomly.
 */
public class RandomPlayer extends Player {
    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    public RandomPlayer(String name) {
        this(name, Color.BLUE);
    }

    /**
     * Bonus:
     * You can implement a smarter way to play the random role
     * Hint:
     *  - E.g., weighted random selection is an option
     *  - You are encouraged to come up with other strategies as long as they involve random (i.e., not fixed rules)
     * @param game           the current game object
     * @param availableMoves available moves for this player to choose from.
     * @return next Move
     */
    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        //TODO: bonus only, it's ok to keep it.
        int index = new Random().nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
