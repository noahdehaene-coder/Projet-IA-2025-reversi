import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Classe représentant un bot utilisant l'algorithme BFS (Breadth-First Search)
 * pour choisir son coup. Le BFS explore les états futurs du jeu de manière
 * large d'abord, évaluant les coups possibles jusqu'à une certaine profondeur.
 */
public class BFSBot extends BotPlayer {
    
    /**
     * Constructeur du bot BFS.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public BFSBot(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise l'algorithme BFS pour évaluer les coups possibles jusqu'à une
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
        
        // Si un seul coup possible, le retourne immédiatement (pas besoin de BFS)
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        // Effectue une recherche BFS jusqu'à 6 coups d'avance
        return bfsSearch(board, validMoves, 6);
    }
    
    /**
     * Effectue une recherche BFS pour trouver le meilleur coup en évaluant
     * les états futurs du jeu.
     *
     * @param currentBoard L'état actuel du plateau de jeu.
     * @param validMoves Liste des coups valides pour le tour actuel.
     * @param maxDepth Profondeur maximale de recherche (nombre de coups d'avance).
     * @return Le meilleur coup trouvé.
     */
    private Move bfsSearch(ReversiPlateau currentBoard, List<Move> validMoves, int maxDepth) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE; // Initialise avec la plus petite valeur
        
        // Évalue chaque coup possible comme premier mouvement
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
            
            // Effectue BFS pour explorer les coups futurs
            int moveScore = performBFS(boardAfterFirstMove, maxDepth - 1);
            
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
     * Effectue un parcours BFS de l'arbre de jeu.
     * Explore les états du jeu de manière large d'abord.
     *
     * @param startBoard L'état du plateau à partir duquel commencer la recherche.
     * @param maxDepth Profondeur maximale à rechercher à partir de ce point.
     * @return Le meilleur score réalisable à partir de cet état du plateau.
     */
    private int performBFS(ReversiPlateau startBoard, int maxDepth) {
        // File (linkedlist) pour BFS, stocke les états du plateau et leur profondeur
        Queue<BoardState> queue = new LinkedList<>();
        queue.add(new BoardState(startBoard, 0, this.color.oppose()));
        
        int bestScore = Integer.MIN_VALUE;
        
        // Parcourt tous les états accessibles
        while (!queue.isEmpty()) {
            BoardState currentState = queue.poll();
            ReversiPlateau currentBoard = currentState.board;
            int currentDepth = currentState.depth;
            Couleurcase currentPlayer = currentState.currentPlayer;
            
            // Vérifie si la partie est terminée à cet état
            if (currentBoard.GameOver()) {
                int score = evaluateBoard(currentBoard);
                if (score > bestScore) {
                    bestScore = score;
                }
                // Si on trouve un état gagnant à une faible profondeur, on peut arrêter la recherche
                if (score > 0 && currentDepth <= maxDepth) {
                    return score;
                }
                continue;
            }
            
            // Si la profondeur maximale est atteinte, évalue ce plateau et continue
            if (currentDepth >= maxDepth) {
                int score = evaluateBoard(currentBoard);
                if (score > bestScore) {
                    bestScore = score;
                }
                continue;
            }
            
            // Récupère les coups valides pour le joueur actuel
            List<Move> validMoves = currentBoard.getValidMoves(currentPlayer);
            
            // Si aucun coup valide, le joueur passe son tour
            if (validMoves.isEmpty()) {
                // Crée un nouvel état où le tour passe à l'autre joueur
                queue.add(new BoardState(currentBoard.copy(), currentDepth + 1, currentPlayer.oppose()));
            } else {
                // Explore tous les coups valides
                for (Move move : validMoves) {
                    ReversiPlateau newBoard = currentBoard.copy();
                    newBoard.placePion(move, currentPlayer);
                    
                    // Ajoute le nouvel état à la file avec profondeur augmentée et joueur opposé
                    queue.add(new BoardState(newBoard, currentDepth + 1, currentPlayer.oppose()));
                }
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
    
    /**
     * Classe interne pour stocker les informations d'état du plateau pour BFS.
     * Utilisée pour maintenir le contexte pendant le parcours BFS.
     */
    private static class BoardState {
        /** L'état du plateau à ce noeud. */
        ReversiPlateau board;
        /** La profondeur dans l'arbre de recherche. */
        int depth;
        /** Le joueur dont c'est le tour à cet état. */
        Couleurcase currentPlayer;
        
        /**
         * Constructeur pour créer un nouvel état de plateau.
         *
         * @param board L'état du plateau.
         * @param depth La profondeur dans l'arbre de recherche.
         * @param currentPlayer Le joueur dont c'est le tour.
         */
        BoardState(ReversiPlateau board, int depth, Couleurcase currentPlayer) {
            this.board = board;
            this.depth = depth;
            this.currentPlayer = currentPlayer;
        }
    }
}