package reversi;

import java.util.List;

/**
 * Classe représentant un bot utilisant l'algorithme DFS (Depth-First Search)
 * pour choisir son coup. Le DFS explore les états futurs du jeu de manière
 * profonde d'abord, évaluant récursivement les coups possibles jusqu'à une
 * certaine profondeur.
 */
public class DFSBot extends BotPlayer {
    
    /**
     * Constructeur du bot DFS.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public DFSBot(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise l'algorithme DFS pour évaluer les coups possibles jusqu'à une
     * profondeur de 6 coups.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible (passe le tour).
     */
    @Override
    public Move getMove(ReversiPlateau board) {
        // Récupère tous les coups valides pour le joueur actuel
        List<Move> validMoves = board.getValidMoves(this.color);
        
        // Si aucun coup valide, passe le tour
        if (validMoves.isEmpty()) {
            return null;
        }
        
        // Si un seul coup possible, le retourne immédiatement (pas besoin de DFS)
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        // Effectue une recherche DFS jusqu'à 6 coups d'avance
        return dfsSearch(board, validMoves, 6);
    }
    
    /**
     * Effectue une recherche DFS pour trouver le meilleur coup en évaluant
     * les états futurs du jeu.
     *
     * @param currentBoard L'état actuel du plateau de jeu.
     * @param validMoves Liste des coups valides pour le tour actuel.
     * @param maxDepth Profondeur maximale de recherche (nombre de coups d'avance).
     * @return Le meilleur coup trouvé.
     */
    private Move dfsSearch(ReversiPlateau currentBoard, List<Move> validMoves, int maxDepth) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE; // Initialise avec la plus petite valeur
        
        // Évalue chaque coup possible comme premier mouvement en utilisant DFS
        for (Move firstMove : validMoves) {
            // Crée une copie du plateau pour simuler le coup
            ReversiPlateau boardAfterFirstMove = currentBoard.copy();
            boardAfterFirstMove.placePion(firstMove, this.color);
            
            // Vérifie si ce coup mène à une victoire immédiate
            if (boardAfterFirstMove.GameOver()) {
                int score = evaluateBoard(boardAfterFirstMove);
                if (score > 0) {
                    // Ce coup mène à la victoire, le retourne immédiatement
                    return firstMove;
                }
            }
            
            // Effectue DFS pour explorer récursivement les coups futurs
            int moveScore = performDFS(boardAfterFirstMove, maxDepth - 1, this.color.oppose());
            
            // Met à jour le meilleur coup si celui-ci a un meilleur score
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMove = firstMove;
            }
        }
        
        // Si aucun coup n'a été évalué (ne devrait pas arriver), retourne un coup valide aléatoire
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    
    /**
     * Effectue un parcours DFS récursif de l'arbre de jeu.
     * Explore les états du jeu de manière profonde d'abord.
     *
     * @param board L'état actuel du plateau.
     * @param depth Profondeur restante à explorer.
     * @param currentPlayer Le joueur dont c'est le tour.
     * @return Le meilleur score réalisable à partir de cet état du plateau.
     */
    private int performDFS(ReversiPlateau board, int depth, Couleurcase currentPlayer) {
        // Cas de base 1 : Si la partie est terminée, évalue l'état final du plateau
        if (board.GameOver()) {
            return evaluateBoard(board);
        }
        
        // Cas de base 2 : Si la profondeur maximale est atteinte, évalue le plateau actuel
        if (depth <= 0) {
            return evaluateBoard(board);
        }
        
        // Récupère les coups valides pour le joueur actuel
        List<Move> validMoves = board.getValidMoves(currentPlayer);
        
        // Si aucun coup valide, le joueur passe, continue avec le joueur opposé
        if (validMoves.isEmpty()) {
            return performDFS(board, depth - 1, currentPlayer.oppose());
        }
        
        int bestScore = Integer.MIN_VALUE;
        
        // Explore récursivement tous les coups valides
        for (Move move : validMoves) {
            // Crée une copie du plateau pour cette branche
            ReversiPlateau newBoard = board.copy();
            newBoard.placePion(move, currentPlayer);
            
            // Si ce coup termine la partie, l'évalue immédiatement
            if (newBoard.GameOver()) {
                int score = evaluateBoard(newBoard);
                if (score > bestScore) {
                    bestScore = score;
                }
                // Si on trouve un état gagnant, on peut arrêter la recherche sur cette branche
                if (score > 0) {
                    return score;
                }
                continue;
            }
            
            // Recherche récursive plus profonde avec le tour du joueur opposé
            int score = performDFS(newBoard, depth - 1, currentPlayer.oppose());
            
            // Met à jour le meilleur score pour ce niveau
            if (score > bestScore) {
                bestScore = score;
            }
        }
        
        return bestScore;
    }
    
    /**
     * Évalue un état du plateau et retourne un score du point de vue de ce bot.
     * Un score positif est bon pour le bot, négatif est mauvais.
     *
     * @param board Le plateau à évaluer.
     * @return Le score d'évaluation.
     */
    private int evaluateBoard(ReversiPlateau board) {
        // Évaluation simple : différence du nombre de pions
        int myScore = board.getScore(this.color);
        int opponentScore = board.getScore(this.color.oppose());
        
        // Si la partie est terminée, attribue des scores de victoire/défaite
        if (board.GameOver()) {
            if (myScore > opponentScore) {
                return 1000; // Gros score positif pour une victoire
            } else if (myScore < opponentScore) {
                return -1000; // Gros score négatif pour une défaite
            } else {
                return 0; // Égalité
            }
        }
        
        // Pour les états non terminaux, retourne la simple différence de score
        return myScore - opponentScore;
    }
}
