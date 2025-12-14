package reversi;

import java.util.*;

/**
 * Classe représentant un bot utilisant l'algorithme de Dijkstra optimisé
 * avec représentation bit à bit pour des performances plus rapides.
 * Version rapide de DijkstraBot utilisant FastReversiBoard.
 */
public class DijkstraBotRapide extends BotPlayer {
    
    /**
     * Tableau de poids positionnels aplatis (1D) pour une lecture rapide.
     * Mêmes valeurs que dans AlphaBetaBotRapide :
     * - Coins : 100 (très avantageux)
     * - Cases dangereuses près des coins : -20, -50
     * - Bordures : 10, 5
     * - Centre : -1, -2
     */
    private static final int[] WEIGHTS = {
        100, -20, 10, 5, 5, 10, -20, 100,
        -20, -50, -2, -2, -2, -2, -50, -20,
        10, -2, -1, -1, -1, -1, -2, 10,
        5, -2, -1, -1, -1, -1, -2, 5,
        5, -2, -1, -1, -1, -1, -2, 5,
        10, -2, -1, -1, -1, -1, -2, 10,
        -20, -50, -2, -2, -2, -2, -50, -20,
        100, -20, 10, 5, 5, 10, -20, 100
    };

    /**
     * Constructeur du bot Dijkstra optimisé.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public DijkstraBotRapide(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Convertit d'abord le plateau en représentation optimisée (FastReversiBoard)
     * puis applique l'algorithme de Dijkstra adapté.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible.
     */
    public Move getMove(ReversiPlateau board) {
        // 1. Conversion immédiate en représentation bit à bit (Bitboard)
        FastReversiBoard startBoard = new FastReversiBoard(board);
        boolean isBlack = (this.color == Couleurcase.NOIR);
        
        // Vérification rapide s'il y a des coups possibles
        long validMoves = startBoard.getValidMovesBitmask(isBlack);
        if (validMoves == 0) return null;
        
        // Optimisation : s'il n'y a qu'un seul coup possible, le joue immédiatement
        if (Long.bitCount(validMoves) == 1) {
            int i = Long.numberOfTrailingZeros(validMoves);
            return new Move(i / 8, i % 8);
        }
        
        return dijkstraSearch(startBoard, isBlack);
    }
    
    /**
     * Implémentation optimisée de l'algorithme de Dijkstra pour FastReversiBoard.
     *
     * @param startBoard Le plateau de départ en représentation optimisée.
     * @param myColorIsBlack true si le bot joue les noirs, false pour les blancs.
     * @return Le meilleur coup selon l'algorithme de Dijkstra.
     */
    private Move dijkstraSearch(FastReversiBoard startBoard, boolean myColorIsBlack) {
        // File de priorité : explore d'abord les états qui nous avantagent le plus (distance faible)
        PriorityQueue<BoardNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        
        // Map pour stocker la distance minimale pour atteindre un état donné
        Map<String, Integer> distances = new HashMap<>();
        
        // Map pour retenir quel premier coup a mené à quel état
        Map<String, Move> firstMoves = new HashMap<>();
        
        // Initialisation avec les premiers coups possibles
        long validMoves = startBoard.getValidMovesBitmask(myColorIsBlack);
        
        // Explore chaque coup initial possible
        for (int i = 0; i < 64; i++) {
            if ((validMoves & (1L << i)) != 0) {
                FastReversiBoard nextBoard = startBoard.copy();
                nextBoard.makeMove(i / 8, i % 8, myColorIsBlack);
                
                String key = getKey(nextBoard);
                // La distance est négative car on veut maximiser notre avantage
                // Dijkstra cherche le minimum, donc min(-Score) = max(Score)
                int dist = -evaluateBoardAdvantage(nextBoard, myColorIsBlack);
                
                distances.put(key, dist);
                Move moveObj = new Move(i / 8, i % 8);
                firstMoves.put(key, moveObj);
                
                queue.add(new BoardNode(nextBoard, dist, 1));
            }
        }
        
        Move bestMove = null;
        int bestFinalDistance = Integer.MAX_VALUE;
        long startTime = System.currentTimeMillis();
        
        // Exploration avec limite de temps
        while (!queue.isEmpty()) {
            // Sécurité temps : arrête si la recherche prend plus de 1,5 secondes
            if (System.currentTimeMillis() - startTime > 1500) break;

            BoardNode current = queue.poll();
            String currentKey = getKey(current.board);
            
            // Si on a déjà trouvé un chemin plus court vers cet état, on ignore
            if (distances.containsKey(currentKey) && distances.get(currentKey) < current.distance) {
                continue;
            }
            
            // Conditions d'arrêt : profondeur maximale ou fin de partie
            if (current.depth >= 5 || (current.board.getValidMovesBitmask(true) == 0 && 
                                       current.board.getValidMovesBitmask(false) == 0)) {
                if (current.distance < bestFinalDistance) {
                    bestFinalDistance = current.distance;
                    bestMove = firstMoves.get(currentKey); // Récupère le coup initial qui a mené ici
                }
                continue;
            }
            
            // Détermine à qui c'est le tour dans cet état simulé
            // Alternance basée sur la profondeur : 0 = nous, 1 = adversaire, 2 = nous, etc
            boolean isBlackTurnNow = ((current.depth % 2) == 0) ? myColorIsBlack : !myColorIsBlack;
            
            long nextMovesMask = current.board.getValidMovesBitmask(isBlackTurnNow);
            
            if (nextMovesMask == 0) {
                // Le joueur passe son tour
                // Continue avec le même plateau, profondeur +1
                BoardNode passNode = new BoardNode(current.board.copy(), current.distance, current.depth + 1);
                String passKey = getKey(passNode.board) + ":" + (current.depth + 1); // Clé unique incluant profondeur pour éviter les boucles
                
                if (!distances.containsKey(passKey) || current.distance < distances.get(passKey)) {
                    distances.put(passKey, current.distance);
                    // Propage le "premier coup" responsable de cette branche
                    firstMoves.putIfAbsent(passKey, firstMoves.get(currentKey)); 
                    queue.add(passNode);
                }
                continue;
            }

            // Génération des états voisins (coups possibles)
            // On récupère les indices des coups valides dans une liste
            List<Integer> initialIndices = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                if ((validMoves & (1L << i)) != 0) {
                    initialIndices.add(i);
                }
            }
            
            // 2. On mélange cette liste
            Collections.shuffle(initialIndices);
            
            // 3. On itère sur la liste mélangée
            for (int i : initialIndices) {
                // Le reste du code reste IDENTIQUE à avant (copie, move, calcul distance...)
                FastReversiBoard nextBoard = startBoard.copy();
                nextBoard.makeMove(i / 8, i % 8, myColorIsBlack);
                
                String key = getKey(nextBoard);
                int dist = -evaluateBoardAdvantage(nextBoard, myColorIsBlack);
                
                distances.put(key, dist);
                Move moveObj = new Move(i / 8, i % 8);
                firstMoves.put(key, moveObj);
                
                queue.add(new BoardNode(nextBoard, dist, 1));
            }
        }
        
        // Fallback : si timeout ou aucun coup trouvé, prend le meilleur coup immédiat
        if (bestMove == null) {
             long m = startBoard.getValidMovesBitmask(myColorIsBlack);
             int i = Long.numberOfTrailingZeros(m);
             return new Move(i / 8, i % 8);
        }
        
        return bestMove;
    }
    
    /**
     * Génère une clé unique rapide pour la HashMap.
     * Combine les masques binaires des noirs et blancs.
     *
     * @param b Le plateau FastReversiBoard.
     * @return Une chaîne "blackMask:whiteMask".
     */
    private String getKey(FastReversiBoard b) {
        return b.black + ":" + b.white;
    }
    
    /**
     * Évalue l'avantage sur le plateau du point de vue du bot.
     *
     * @param b Le plateau FastReversiBoard à évaluer.
     * @param amIBlack true si le bot joue les noirs.
     * @return Un score d'avantage (positif = bon pour le bot).
     */
    private int evaluateBoardAdvantage(FastReversiBoard b, boolean amIBlack) {
        long myPieces = amIBlack ? b.black : b.white;
        long oppPieces = amIBlack ? b.white : b.black;
        
        int myScore = Long.bitCount(myPieces);
        int oppScore = Long.bitCount(oppPieces);
        
        // Vérifie les coups possibles
        long validMe = b.getValidMovesBitmask(amIBlack);
        long validOpp = b.getValidMovesBitmask(!amIBlack);
        
        // Fin de partie : victoire absolue
        if (validMe == 0 && validOpp == 0) {
            if (myScore > oppScore) return 10000;
            if (myScore < oppScore) return -10000;
            return 0;
        }
        
        int score = 0;
        
        // 1. Évaluation positionnelle (matrice de poids)
        for (int i = 0; i < 64; i++) {
            if ((myPieces & (1L << i)) != 0) score += WEIGHTS[i];
            else if ((oppPieces & (1L << i)) != 0) score -= WEIGHTS[i];
        }
        
        // 2. Mobilité (poids 5)
        score += (Long.bitCount(validMe) - Long.bitCount(validOpp)) * 5;
        
        // 3. Différence de pions (faible poids en début de partie)
        score += (myScore - oppScore);
        
        return score;
    }

    /**
     * Classe interne représentant un noeud pour l'algorithme de Dijkstra.
     */
    private static class BoardNode {
        /** Le plateau en représentation optimisée. */
        FastReversiBoard board;
        
        /** La distance (score inversé). */
        int distance;
        
        /** La profondeur dans l'arbre de recherche. */
        int depth;
        
        /**
         * Constructeur pour créer un nouveau noeud.
         *
         * @param board Le plateau.
         * @param distance La distance (score inversé).
         * @param depth La profondeur.
         */
        BoardNode(FastReversiBoard board, int distance, int depth) {
            this.board = board;
            this.distance = distance;
            this.depth = depth;
        }
    }
}
