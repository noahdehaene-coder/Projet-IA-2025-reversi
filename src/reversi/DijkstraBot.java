package reversi;

import java.util.*;

/**
 * Classe représentant un bot utilisant l'algorithme de Dijkstra adapté pour le Reversi.
 * L'algorithme traite les états du plateau comme des noeuds et les coups comme des arêtes,
 * cherchant à minimiser l'avantage maximal de l'adversaire.
 */
public class DijkstraBot extends BotPlayer {
    
    /**
     * Constructeur du bot Dijkstra.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public DijkstraBot(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise une version adaptée de l'algorithme de Dijkstra pour évaluer
     * les coups possibles.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible (passe le tour).
     */
    public Move getMove(ReversiPlateau board) {
        List<Move> validMoves = board.getValidMoves(this.color);
        
        if (validMoves.isEmpty()) {
            return null;
        }
        
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        return dijkstraSearch(board, validMoves);
    }
    
    /**
     * Algorithme de Dijkstra adapté pour le Reversi :
     * - Chaque état du plateau est un noeud
     * - Chaque coup est une arête avec un poids négatif (on veut minimiser l'avantage de l'adversaire)
     * - Trouve le chemin qui minimise le potentiel maximal de l'adversaire
     *
     * @param startBoard Le plateau de départ.
     * @param validMoves Liste des coups valides pour le premier mouvement.
     * @return Le meilleur coup selon l'algorithme de Dijkstra.
     */
    private Move dijkstraSearch(ReversiPlateau startBoard, List<Move> validMoves) {
        // File de priorité pour l'algorithme de Dijkstra (triée par distance)
        PriorityQueue<BoardNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        
        // Map pour stocker les distances minimales connues pour chaque état de plateau
        Map<String, Integer> distances = new HashMap<>();
        
        // Map pour associer le premier coup à chaque état de plateau
        Map<String, Move> firstMoves = new HashMap<>();
        
        // Initialise avec tous les premiers coups possibles
        for (Move firstMove : validMoves) {
            ReversiPlateau newBoard = startBoard.copy();
            newBoard.placePion(firstMove, this.color);
            
            String boardKey = getBoardKey(newBoard);
            
            // Distance initiale : négatif de notre avantage (on veut minimiser l'avantage de l'adversaire)
            int initialDistance = -evaluateBoardAdvantage(newBoard);
            
            distances.put(boardKey, initialDistance);
            firstMoves.put(boardKey, firstMove);
            queue.add(new BoardNode(newBoard, initialDistance, 0));
        }
        
        Move bestMove = null;
        int bestFinalDistance = Integer.MAX_VALUE;
        
        // Algorithme de Dijkstra
        while (!queue.isEmpty()) {
            BoardNode current = queue.poll();
            
            // Si profondeur maximale atteinte ou partie terminée, évalue ce chemin
            if (current.depth >= 6 || current.board.GameOver()) {
                if (current.distance < bestFinalDistance) {
                    bestFinalDistance = current.distance;
                    bestMove = firstMoves.get(getBoardKey(current.board));
                }
                continue;
            }
            
            // Explore les coups de l'adversaire (on considère les meilleures réponses de l'adversaire)
            // Alternance des joueurs selon la profondeur
            Couleurcase currentPlayer = current.depth % 2 == 0 ? this.color.oppose() : this.color;
            List<Move> nextMoves = current.board.getValidMoves(currentPlayer);
            
            if (nextMoves.isEmpty()) {
                // Le joueur passe son tour
                ReversiPlateau passBoard = current.board.copy();
                String boardKey = getBoardKey(passBoard);
                int newDistance = current.distance;
                
                // Met à jour si un chemin plus court est trouvé
                if (!distances.containsKey(boardKey) || newDistance < distances.get(boardKey)) {
                    distances.put(boardKey, newDistance);
                    queue.add(new BoardNode(passBoard, newDistance, current.depth + 1));
                    if (!firstMoves.containsKey(boardKey)) {
                        firstMoves.put(boardKey, firstMoves.get(getBoardKey(current.board)));
                    }
                }
                continue;
            }
            
            // Explore tous les coups possibles du joueur actuel
            for (Move move : nextMoves) {
                ReversiPlateau newBoard = current.board.copy();
                newBoard.placePion(move, currentPlayer);
                
                // Calcule le poids de l'arête : négatif du changement d'avantage
                int advantageChange = evaluateBoardAdvantage(newBoard) - evaluateBoardAdvantage(current.board);
                int newDistance = current.distance - advantageChange; // Négatif car on veut minimiser l'avantage de l'adversaire
                
                String boardKey = getBoardKey(newBoard);
                
                // Met à jour si un chemin plus court est trouvé
                if (!distances.containsKey(boardKey) || newDistance < distances.get(boardKey)) {
                    distances.put(boardKey, newDistance);
                    queue.add(new BoardNode(newBoard, newDistance, current.depth + 1));
                    if (!firstMoves.containsKey(boardKey)) {
                        firstMoves.put(boardKey, firstMoves.get(getBoardKey(current.board)));
                    }
                }
            }
        }
        
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    
    /**
     * Évalue l'avantage sur le plateau du point de vue de ce bot.
     * Positif = bon pour le bot, Négatif = bon pour l'adversaire.
     *
     * @param board Le plateau à évaluer.
     * @return Le score d'avantage.
     */
    private int evaluateBoardAdvantage(ReversiPlateau board) {
        // Si la partie est terminée, évalue le résultat final
        if (board.GameOver()) {
            int myScore = board.getScore(this.color);
            int opponentScore = board.getScore(this.color.oppose());
            if (myScore > opponentScore) return 1000;
            if (myScore < opponentScore) return -1000;
            return 0;
        }
        
        // Score basique : différence du nombre de pions
        int basicScore = board.getScore(this.color) - board.getScore(this.color.oppose());
        
        // Ajoute l'avantage positionnel
        int positionalScore = evaluatePositionalAdvantage(board);
        
        // Ajoute l'avantage de mobilité
        int mobilityScore = evaluateMobilityAdvantage(board);
        
        return basicScore + positionalScore + mobilityScore;
    }
    
    /**
     * Évalue l'avantage positionnel en utilisant une grille de poids.
     * Les coins ont un poids élevé, les cases "dangereuses" près des coins ont un poids négatif.
     *
     * @param board Le plateau à évaluer.
     * @return Le score positionnel.
     */
    private int evaluatePositionalAdvantage(ReversiPlateau board) {
        int score = 0;
        
        // Grille de poids pour chaque position 
        int[][] positionWeights = {
            {100, -20, 10, 5, 5, 10, -20, 100},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {10, -2, -1, -1, -1, -1, -2, 10},
            {5, -2, -1, -1, -1, -1, -2, 5},
            {5, -2, -1, -1, -1, -1, -2, 5},
            {10, -2, -1, -1, -1, -1, -2, 10},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {100, -20, 10, 5, 5, 10, -20, 100}
        };
        
        // Parcourt toutes les cases du plateau
        for (int i = 0; i < ReversiPlateau.taille; i++) {
            for (int j = 0; j < ReversiPlateau.taille; j++) {
                Couleurcase cell = board.getEtat(i, j);
                if (cell == this.color) {
                    score += positionWeights[i][j]; // Case occupée par le bot
                } else if (cell == this.color.oppose()) {
                    score -= positionWeights[i][j]; // Case occupée par l'adversaire
                }
            }
        }
        
        return score / 10; // Normalise le score
    }
    
    /**
     * Évalue l'avantage de mobilité (nombre de coups disponibles).
     *
     * @param board Le plateau à évaluer.
     * @return Le score de mobilité.
     */
    private int evaluateMobilityAdvantage(ReversiPlateau board) {
        int myMoves = board.getValidMoves(this.color).size();
        int opponentMoves = board.getValidMoves(this.color.oppose()).size();
        
        if (myMoves + opponentMoves == 0) return 0;
        
        return (myMoves - opponentMoves) * 2;
    }
    
    /**
     * Crée une clé unique pour un état de plateau (version simplifiée).
     * Convertit l'état du plateau en chaîne de caractères pour l'utiliser comme clé dans les Maps.
     *
     * @param board Le plateau à encoder.
     * @return Une chaîne représentant l'état du plateau.
     */
    private String getBoardKey(ReversiPlateau board) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < ReversiPlateau.taille; i++) {
            for (int j = 0; j < ReversiPlateau.taille; j++) {
                // Utilise la valeur ordinale de l'énumération (0, 1, 2)
                key.append(board.getEtat(i, j).ordinal());
            }
        }
        return key.toString();
    }
    
    /**
     * Classe interne représentant un noeud pour l'algorithme de Dijkstra.
     */
    private static class BoardNode {
        /** L'état du plateau à ce noeud. */
        ReversiPlateau board;
        
        /** La distance (coût) pour atteindre cet état. */
        int distance;
        
        /** La profondeur dans l'arbre de recherche. */
        int depth;
        
        /**
         * Constructeur pour créer un nouveau noeud.
         *
         * @param board L'état du plateau.
         * @param distance La distance pour atteindre cet état.
         * @param depth La profondeur dans l'arbre.
         */
        BoardNode(ReversiPlateau board, int distance, int depth) {
            this.board = board;
            this.distance = distance;
            this.depth = depth;
        }
    }
}
