package reversi;

import java.util.List;

/**
 * Classe représentant un bot Alpha-Beta optimisé utilisant une représentation bit à bit
 * du plateau pour des performances plus rapides.
 */
public class AlphaBetaBotRapide extends BotPlayer {
    /** Profondeur maximale de recherche dans l'arbre des coups. */
    private int maxDepth;
    
    /**
     * Tableau de poids statiques pour chaque case du plateau (8x8 = 64 cases).
     * Les valeurs reflètent l'importance stratégique de chaque position :
     * - Coins (100) : très avantageux
     * - Cases près des coins (-20, -50) : dangereuses (peuvent donner un coin à l'adversaire)
     * - Bordures (10, 5) : avantageuses
     * - Centre (-1, -2) : moins stables
     */
    private static final int[] WEIGHTS = {
         100, -20,  10,   5,   5,  10, -20, 100,
         -20, -50,  -2,  -2,  -2,  -2, -50, -20,
          10,  -2,  -1,  -1,  -1,  -1,  -2,  10,
           5,  -2,  -1,  -1,  -1,  -1,  -2,   5,
           5,  -2,  -1,  -1,  -1,  -1,  -2,   5,
          10,  -2,  -1,  -1,  -1,  -1,  -2,  10,
         -20, -50,  -2,  -2,  -2,  -2, -50, -20,
         100, -20,  10,   5,   5,  10, -20, 100
    };

    /**
     * Constructeur du bot Alpha-Beta optimisé.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     * @param depth Profondeur maximale de recherche pour l'algorithme Alpha-Beta.
     */
    public AlphaBetaBotRapide(Couleurcase color, int depth) {
        super(color);
        this.maxDepth = depth;
    }

    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise une représentation bit à bit du plateau (FastReversiBoard) pour
     * des opérations plus rapides.
     *
     * @param board L'état actuel du plateau de jeu (ReversiPlateau).
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible.
     */
    public Move getMove(ReversiPlateau board) {
        // Convertit le plateau classique en représentation optimisée bit à bit
        FastReversiBoard fastBoard = new FastReversiBoard(board);
        boolean isBlack = (this.color == Couleurcase.NOIR);
        
        // Récupère les coups valides sous forme de masque binaire (64 bits)
        long validMoves = fastBoard.getValidMovesBitmask(isBlack);
        if (validMoves == 0) return null; // Aucun coup possible

        long bestMove = 0; // Masque binaire du meilleur coup
        int maxEval = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Parcourt tous les bits à 1 dans le masque des coups valides
        for (int i = 0; i < 64; i++) {
            long mask = 1L << i; // Crée un masque pour la case i
            if ((validMoves & mask) != 0) { // Si le coup est valide
                FastReversiBoard clone = fastBoard.copy();
                clone.makeMove(i / 8, i % 8, isBlack); // Effectue le coup sur le clone
                
                // Évalue le coup avec l'algorithme Alpha-Beta
                int eval = alphaBeta(clone, maxDepth - 1, alpha, beta, !isBlack);
                
                // Met à jour le meilleur coup si nécessaire
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = mask;
                }
                alpha = Math.max(alpha, eval); // Met à jour alpha
            }
        }

        // Convertit le masque binaire en objet Move (coordonnées ligne/colonne)
        int index = Long.numberOfTrailingZeros(bestMove);
        return new Move(index / 8, index % 8);
    }

    /**
     * Implémentation récursive de l'algorithme Alpha-Beta pour FastReversiBoard.
     * Utilise des masques binaires pour représenter les coups.
     *
     * @param board Le plateau optimisé à évaluer.
     * @param depth Profondeur restante.
     * @param alpha Valeur alpha (meilleure option pour le maximisant).
     * @param beta  Valeur beta (meilleure option pour le minimisant).
     * @param maxPlayer True si c'est le tour du joueur maximisant, false sinon.
     * @return La valeur heuristique du noeud évalué.
     */
    private int alphaBeta(FastReversiBoard board, int depth, int alpha, int beta, boolean maxPlayer) {
        // Condition d'arrêt : profondeur nulle atteinte
        if (depth == 0) return evaluate(board);

        // Détermine quel joueur doit jouer (basé sur maxPlayer et la couleur du bot)
        boolean isCurrentPlayerBlack = (this.color == Couleurcase.NOIR) ? maxPlayer : !maxPlayer;
        long moves = board.getValidMovesBitmask(isCurrentPlayerBlack);
        
        // Vérifie si le joueur courant a des coups possibles
        if (moves == 0) {
            // Vérifie si l'adversaire peut également jouer
            boolean isOpponentBlack = !isCurrentPlayerBlack;
            if (board.getValidMovesBitmask(isOpponentBlack) == 0) {
                // Aucun joueur ne peut jouer : fin de partie
                return evaluate(board) * 10; // Bonus pour les états finaux
            }
            // Passe le tour au joueur suivant
            return alphaBeta(board, depth - 1, alpha, beta, !maxPlayer);
        }

        // Phase de maximisation (tour du joueur courant)
        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 64; i++) {
                if ((moves & (1L << i)) != 0) { // Si le coup i est valide
                    FastReversiBoard clone = board.copy();
                    clone.makeMove(i / 8, i % 8, this.color == Couleurcase.NOIR);
                    int eval = alphaBeta(clone, depth - 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break; // Élagage alpha
                }
            }
            return maxEval;
        } 
        // Phase de minimisation (tour de l'adversaire)
        else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 64; i++) {
                if ((moves & (1L << i)) != 0) { // Si le coup i est valide
                    FastReversiBoard clone = board.copy();
                    clone.makeMove(i / 8, i % 8, this.color != Couleurcase.NOIR);
                    int eval = alphaBeta(clone, depth - 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break; // Élagage beta
                }
            }
            return minEval;
        }
    }

    /**
     * Fonction d'évaluation heuristique pour FastReversiBoard.
     * Combine l'évaluation positionnelle (poids des cases) et la mobilité.
     *
     * @param board Le plateau optimisé à évaluer.
     * @return Un score entier représentant l'avantage du bot.
     */
    private int evaluate(FastReversiBoard board) {
        // Récupère les masques binaires des pions du bot et de l'adversaire
        long myPieces = (this.color == Couleurcase.NOIR) ? board.black : board.white;
        long oppPieces = (this.color == Couleurcase.NOIR) ? board.white : board.black;
        
        int score = 0;
        
        // 1. Évaluation positionnelle : somme des poids des cases occupées
        for (int i = 0; i < 64; i++) {
            if ((myPieces & (1L << i)) != 0) score += WEIGHTS[i]; // Case occupée par le bot
            else if ((oppPieces & (1L << i)) != 0) score -= WEIGHTS[i]; // Case occupée par l'adversaire
        }
        
        // 2. Mobilité : nombre de coups possibles (bits à 1 dans le masque)
        int myMobility = Long.bitCount(board.getValidMovesBitmask(this.color == Couleurcase.NOIR));
        int oppMobility = Long.bitCount(board.getValidMovesBitmask(this.color != Couleurcase.NOIR));
        
        // Ajoute la différence de mobilité pondérée
        score += (myMobility - oppMobility) * 15; // Poids empirique 15
        
        return score;
    }
}
