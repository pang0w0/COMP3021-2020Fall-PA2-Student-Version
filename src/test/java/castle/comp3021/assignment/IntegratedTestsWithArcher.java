package castle.comp3021.assignment;

import castle.comp3021.assignment.textversion.JesonMor;
import castle.comp3021.assignment.mock.MockPlayer;
import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.util.IntegratedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import castle.comp3021.assignment.util.OptionalArcherImplementation;

import static org.junit.jupiter.api.Assertions.assertEquals;

@OptionalArcherImplementation
@IntegratedTest
public class IntegratedTestsWithArcher {
    private Game game;
    private MockPlayer player1;
    private MockPlayer player2;

    @BeforeEach
    public void setUpGame() {
        var size = 5;
        var numMovesProtection = 0;
        this.player1 = new MockPlayer(Color.GREEN);
        this.player2 = new MockPlayer();
        Configuration configuration = new Configuration(size, new Player[]{player1, player2}, numMovesProtection);
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(new Knight(player2), i, size - 1);
            } else {
                configuration.addInitialPiece(new Archer(player2), i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(new Knight(player1), i, 0);
            } else {
                configuration.addInitialPiece(new Archer(player1), i, 0);
            }
        }
        this.game = new JesonMor(configuration);
    }

    @Test
    public void testCaptureAllWin() {
        player1.setNextMoves(
                new Move[]{
                        new Move(0, 0, 1, 2),
                        new Move(1, 0, 1, 4),
                        new Move(1, 4, 4, 4),
                        new Move(4, 0, 3, 2),
                        new Move(2, 0, 0, 1),
                        new Move(0, 1, 1, 3),
                        new Move(3, 2, 1, 1),
                }
        );
        player2.setNextMoves(
                new Move[]{
                        new Move(2, 4, 3, 2),
                        new Move(3, 4, 2, 4),
                        new Move(2, 4, 2, 1),
                        new Move(2, 1, 0, 1),
                        new Move(0, 4, 2, 3),
                        new Move(2, 3, 1, 1)
                }
        );
        var winner = this.game.start();
        assertEquals(player1, winner);
        assertEquals(22, player1.getScore());
        assertEquals(15, player2.getScore());
        assertEquals(13, game.getNumMoves());
    }
}
