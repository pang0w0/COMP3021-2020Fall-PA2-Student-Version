package castle.comp3021.assignment.player;

import castle.comp3021.assignment.protocol.Color;
import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.Move;
import castle.comp3021.assignment.protocol.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import castle.comp3021.assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A computer player that makes a move using smart strategy.
 */
public class SmartRandomPlayer extends Player {
    public SmartRandomPlayer(String name, Color color) {
        super(name, color);
    }

    public SmartRandomPlayer(String name) {
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
        //TODO: bonus only

        Move fakeMove = new Move(0,0,0,0);
        Move nearestCentral = fakeMove;
        Place central = game.getCentralPlace();

        for(var m : availableMoves){
            Piece p = game.getPiece(m.getSource());
            if(p.getLabel() == 'K' || p.getLabel() == 'k'){
                if(m.getSource().equals(central)){
                    if(game.getConfiguration().getNumMovesProtection() < game.getNumMoves()) {
                        return m;
                    }
                }
                if(m.getDestination().equals(central)){
                    return m;
                }
                int nearestCentralDistance = Math.abs(nearestCentral.getDestination().x() - central.x()) +
                        Math.abs(nearestCentral.getDestination().y() - central.y());
                int currentMoveDistance = Math.abs(m.getDestination().x() - central.x()) +
                        Math.abs(m.getDestination().y() - central.y());

                if(currentMoveDistance == 3 && !m.getSource().equals(central)){
                    return m;
                }

                if(currentMoveDistance < nearestCentralDistance){
                    if(!m.getSource().equals(central)) {
                        nearestCentral = m;//get the nearest central move
                    }
                }
            }
        }

        if(fakeMove.equals(nearestCentral)){
            int index = new Random().nextInt(availableMoves.length);
            return availableMoves[index];
        }else {
            return nearestCentral;
        }
    }
}
