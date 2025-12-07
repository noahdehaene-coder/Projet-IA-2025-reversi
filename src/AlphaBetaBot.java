import java.util.List;

/**
 * Classe représentant un bot qui utilise l'algorithme Alpha-Beta pour choisir son coup.
 * Hérite de la classe abstraite BotPlayer et implémente une stratégie de recherche
 * avec élagage alpha-beta pour optimiser l'exploration de l'arbre des coups possibles.
 */
public class AlphaBetaBot extends BotPlayer {

    /** Profondeur maximale de recherche dans l'arbre des coups. */
    private int maxDepth;

    /**
     * Constructeur du bot AlphaBeta.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     * @param depth Profondeur maximale de recherche pour l'algorithme Alpha-Beta.
     */
    public AlphaBetaBot(Couleurcase color, int depth) {
        super(color);
        this.maxDepth = depth;
    }

    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Appelle l'algorithme Alpha-Beta à la racine de l'arbre de décision.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible.
     */
    public Move getMove(ReversiPlateau board) {
        return alphaBetaRoot(board, maxDepth);
    }

    /**
     * Point d'entrée de l'algorithme Alpha-Beta.
     * Évalue tous les coups possibles à la profondeur donnée et retourne le meilleur.
     *
     * @param board L'état actuel du plateau.
     * @param depth Profondeur de recherche restante.
     * @return Le meilleur coup pour le joueur actuel.
     */
    private Move alphaBetaRoot(ReversiPlateau board, int depth) {
        // Récupère tous les coups valides pour le bot
        List<Move> moves = board.getValidMoves(this.color);
        if (moves.isEmpty()) return null; // Aucun coup possible

        Move bestMove = moves.get(0); // Initialisation avec le premier coup
        int alpha = Integer.MIN_VALUE; // Meilleure valeur pour le maximisant (bot)
        int beta = Integer.MAX_VALUE;  // Meilleure valeur pour le minimisant (adversaire)

        // Parcourt tous les coups possibles pour trouver le meilleur
        for (Move move : moves) {
            ReversiPlateau clone = board.copy();
            clone.placePion(move, this.color);
            
            // Appel récursif pour évaluer le coup du point de vue de l'adversaire (minimisation)
            int score = alphaBeta(clone, depth - 1, alpha, beta, false);
            
            // Met à jour le meilleur coup si un score supérieur est trouvé
            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    /**
     * Implémentation récursive de l'algorithme Alpha-Beta.
     * Alterne entre maximisation (pour le bot) et minimisation (pour l'adversaire).
     *
     * @param board L'état du plateau à évaluer.
     * @param depth Profondeur restante.
     * @param alpha Valeur alpha (meilleure option pour le maximisant).
     * @param beta  Valeur bêta (meilleure option pour le minimisant).
     * @param maximizingPlayer True si c'est le tour du joueur maximisant (le bot), false sinon.
     * @return La valeur heuristique du noeud évalué.
     */
    private int alphaBeta(ReversiPlateau board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        // Condition d'arrêt : profondeur nulle ou partie terminée
        if (depth == 0 || board.GameOver()) {
            return evaluateComplex(board);
        }

        // Détermine le joueur courant (bot ou adversaire)
        Couleurcase currentPlayer = maximizingPlayer ? this.color : this.color.oppose();
        List<Move> moves = board.getValidMoves(currentPlayer);

        // Si le joueur courant n'a aucun coup valide, on passe son tour
        if (moves.isEmpty()) {
            return alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer);
        }

        // Phase de maximisation (tour du bot)
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                ReversiPlateau clone = board.copy();
                clone.placePion(move, currentPlayer);
                int eval = alphaBeta(clone, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Élagage alpha : coupe les branches inutiles
            }
            return maxEval;
        } 
        // Phase de minimisation (tour de l'adversaire)
        else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                ReversiPlateau clone = board.copy();
                clone.placePion(move, currentPlayer);
                int eval = alphaBeta(clone, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Élagage bêta : coupe les branches inutiles
            }
            return minEval;
        }
    }

    /**
     * Fonction d'évaluation heuristique avancée.
     * Combine plusieurs critères pour attribuer un score à l'état du plateau :
     * - Mobilité (nombre de coups possibles)
     * - Contrôle des coins
     * - Score brut (nombre de pions)
     * 
     * @param board Le plateau à évaluer.
     * @return Un score entier représentant l'avantage du bot sur l'adversaire.
     */
    private int evaluateComplex(ReversiPlateau board) {
        // 1. Mobilité (capacité à jouer des coups)
        int myMobility = board.getValidMoves(this.color).size();
        int opMobility = board.getValidMoves(this.color.oppose()).size();
        
        // 2. Contrôle des coins (positions stratégiques stables)
        int myCorners = countCorners(board, this.color);
        int opCorners = countCorners(board, this.color.oppose());

        // Pondération des critères (valeurs à ajuster empiriquement)
        return (myMobility - opMobility) * 10      // Mobilité × 10
             + (myCorners - opCorners) * 100       // Coins × 100
             + (board.getScore(this.color) - board.getScore(this.color.oppose())); // Différence de pions
    }
    
    /**
     * Compte le nombre de coins occupés par un joueur donné.
     * Les coins sont les cases (0,0), (0,7), (7,0), (7,7) sur un plateau 8×8.
     *
     * @param board Le plateau de jeu.
     * @param c     La couleur du joueur à vérifier.
     * @return Le nombre de coins contrôlés par le joueur.
     */
    private int countCorners(ReversiPlateau board, Couleurcase c) {
        int count = 0;
        if(board.getEtat(0,0) == c) count++;
        if(board.getEtat(0,7) == c) count++;
        if(board.getEtat(7,0) == c) count++;
        if(board.getEtat(7,7) == c) count++;
        return count;
    }
}