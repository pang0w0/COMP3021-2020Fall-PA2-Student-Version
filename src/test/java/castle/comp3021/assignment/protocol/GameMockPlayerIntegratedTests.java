package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.textversion.JesonMor;
import castle.comp3021.assignment.mock.MockPiece;
import castle.comp3021.assignment.mock.MockPlayer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.util.IntegratedTest;
import castle.comp3021.assignment.util.SampleTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameMockPlayerIntegratedTests {
    private Configuration config;
    private MockPlayer player1;
    private MockPlayer player2;

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = new MockPlayer(Color.YELLOW);
        this.config = new Configuration(3, new Player[]{player1, player2});
    }

    @AfterEach
    public void clear() {
        this.player1 = null;
        this.player2 = null;
        this.config = null;
    }


    @Test
    @IntegratedTest
    public void testCaptureAllWinSingle() {
        this.config.addInitialPiece(new MockPiece(player1), 0, 0);
        this.config.addInitialPiece(new MockPiece(player2), 2, 2);

        player1.setNextMoves(new Move[]{
                new Move(0, 0, 0, 1),
                new Move(0, 1, 0, 2),
                new Move(0, 2, 1, 2),
        });
        player2.setNextMoves(new Move[]{
                new Move(2, 2, 1, 2),
                new Move(1, 2, 0, 2),
                new Move(0, 2, 0, 1),
        });
        var game = new JesonMor(this.config);
        var winner = game.start();
        assertEquals(player2, winner);
        assertEquals(4, game.getNumMoves());
        assertEquals(2, player1.getScore());
        assertEquals(2, player2.getScore());
    }

    @Test
    @IntegratedTest
    public void testCaptureAllWinMultiple() {
        this.config.addInitialPiece(new MockPiece(player1), 0, 0);
        this.config.addInitialPiece(new MockPiece(player1), 1, 0);
        this.config.addInitialPiece(new MockPiece(player2), 2, 2);

        player1.setNextMoves(new Move[]{
                new Move(0, 0, 0, 1),
                new Move(0, 1, 0, 2),
                new Move(1, 0, 0, 0),
                new Move(0, 0, 0, 1),
        });
        player2.setNextMoves(new Move[]{
                new Move(2, 2, 1, 2),
                new Move(1, 2, 0, 2),
                new Move(0, 2, 0, 1),
                new Move(0, 1, 0, 0),
        });
        var game = new JesonMor(this.config);
        var winner = game.start();
        assertEquals(player1, winner);
        assertEquals(7, game.getNumMoves());
        assertEquals(4, player1.getScore());
        assertEquals(3, player2.getScore());
    }
}
