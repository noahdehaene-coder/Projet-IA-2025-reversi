/**
 * Container for test statistics.
 */
public class TestStatistics {
    public final int blackWins;
    public final int whiteWins;
    public final int draws;
    public final int totalGames;
    public final long totalTimeMillis;
    public final String bot1Type;
    public final String bot2Type;
    
    public TestStatistics(int blackWins, int whiteWins, int draws, int totalGames, 
                         long totalTimeMillis, String bot1Type, String bot2Type) {
        this.blackWins = blackWins;
        this.whiteWins = whiteWins;
        this.draws = draws;
        this.totalGames = totalGames;
        this.totalTimeMillis = totalTimeMillis;
        this.bot1Type = bot1Type;
        this.bot2Type = bot2Type;
    }
}