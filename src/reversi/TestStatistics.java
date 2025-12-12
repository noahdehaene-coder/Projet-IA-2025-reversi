package reversi;

/**
 * Conteneur pour les statistiques des tests entre bots.
 * Stocke les résultats des séries de parties (victoires, égalités, temps d'exécution).
 */
public class TestStatistics {
    /** Nombre de victoires du bot noir (premier bot) */
    public final int blackWins;
    
    /** Nombre de victoires du bot blanc (second bot) */
    public final int whiteWins;
    
    /** Nombre de parties terminées par une égalité */
    public final int draws;
    
    /** Nombre total de parties jouées */
    public final int totalGames;
    
    /** Temps total d'exécution en millisecondes */
    public final long totalTimeMillis;
    
    /** Type du premier bot (joueur noir) */
    public final String bot1Type;
    
    /** Type du second bot (joueur blanc) */
    public final String bot2Type;
    
    /**
     * Constructeur pour initialiser les statistiques des tests.
     *
     * @param blackWins Nombre de victoires du bot noir
     * @param whiteWins Nombre de victoires du bot blanc
     * @param draws Nombre d'égalités
     * @param totalGames Nombre total de parties
     * @param totalTimeMillis Temps total d'exécution en ms
     * @param bot1Type Type du premier bot
     * @param bot2Type Type du second bot
     */
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
