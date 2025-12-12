/**
 * Container for single game result.
 */
public class TestResultat {
    public final Couleurcase winner;
    public final int blackScore;
    public final int whiteScore;
    
    public TestResultat(Couleurcase winner, int blackScore, int whiteScore) {
        this.winner = winner;
        this.blackScore = blackScore;
        this.whiteScore = whiteScore;
    }
}