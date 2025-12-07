import java.util.List;
import java.util.Random;

/**
 * Classe représentant un bot utilisant l'algorithme de Monte Carlo
 * pour décider de son coup. Cette méthode est basée sur des simulations
 * aléatoires pour estimer la valeur de chaque coup possible.
 */
public class MonteCarloBot extends BotPlayer {
    
    /** Générateur de nombres aléatoires pour les simulations. */
    private Random random = new Random();
    
    /** Nombre de parties simulées par coup évalué. */
    private static final int SIMULATIONS = 1000;

    /**
     * Constructeur du bot Monte Carlo.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public MonteCarloBot(Couleurcase color) {
        super(color);
    }

    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise la méthode de Monte Carlo : pour chaque coup possible,
     * simule un grand nombre de parties aléatoires et choisit le coup
     * avec le meilleur taux de victoire.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible.
     */
    public Move getMove(ReversiPlateau board) {
        List<Move> validMoves = board.getValidMoves(this.color);
        if (validMoves.isEmpty()) return null;

        Move bestMove = null;
        int bestWins = -1; // Initialise avec une valeur impossible

        // Évalue chaque coup possible
        for (Move move : validMoves) {
            int wins = 0; // Compteur de victoires pour ce coup
            
            // Simule N parties aléatoires pour évaluer ce coup
            for (int i = 0; i < SIMULATIONS; i++) {
                ReversiPlateau clone = board.copy();
                clone.placePion(move, this.color); // Joue le premier coup
                
                // Termine la partie de manière aléatoire et vérifie si on gagne
                if (simulateRandomGame(clone, this.color.oppose())) {
                    wins++;
                }
            }

            // Met à jour le meilleur coup si ce coup a un meilleur taux de victoire
            if (wins > bestWins) {
                bestWins = wins;
                bestMove = move;
            }
        }
        
        return bestMove;
    }

    /**
     * Simule une partie aléatoire à partir d'un état donné.
     * Les joueurs jouent des coups aléatoires jusqu'à la fin de la partie.
     *
     * @param board Le plateau de départ (après le premier coup du bot).
     * @param currentTurn Le joueur dont c'est le tour pour commencer la simulation.
     * @return true si le bot gagne la partie simulée, false sinon.
     */
    private boolean simulateRandomGame(ReversiPlateau board, Couleurcase currentTurn) {
        // Continue jusqu'à ce que la partie soit terminée
        while (!board.GameOver()) {
            List<Move> moves = board.getValidMoves(currentTurn);
            
            if (!moves.isEmpty()) {
                // Choisit un coup aléatoire parmi les coups valides
                Move randomMove = moves.get(random.nextInt(moves.size()));
                board.placePion(randomMove, currentTurn);
            }
            
            // Passe au joueur suivant
            currentTurn = currentTurn.oppose();
        }
        
        // Compare les scores à la fin de la simulation
        int myScore = board.getScore(this.color);
        int oppScore = board.getScore(this.color.oppose());
        return myScore > oppScore; // Victoire si notre score est supérieur
    }
}