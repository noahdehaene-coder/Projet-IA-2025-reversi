package reversi;

/**
 * Conteneur pour les résultats d'une partie individuelle.
 * Stocke le gagnant et les scores d'une seule partie.
 */
public class TestResultat {
    /** Couleur du gagnant (NOIR, BLANC ou VIDE pour égalité) */
    public final Couleurcase winner;
    
    /** Score final du joueur noir */
    public final int blackScore;
    
    /** Score final du joueur blanc */
    public final int whiteScore;
    
    /**
     * Constructeur pour initialiser les résultats d'une partie.
     *
     * @param winner Couleur du gagnant (NOIR, BLANC ou VIDE)
     * @param blackScore Score du joueur noir
     * @param whiteScore Score du joueur blanc
     */
    public TestResultat(Couleurcase winner, int blackScore, int whiteScore) {
        this.winner = winner;
        this.blackScore = blackScore;
        this.whiteScore = whiteScore;
    }
}
