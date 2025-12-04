package reversi;

import java.util.*;

public class DijkstraBotRapide extends BotPlayer {
    
    // Poids positionnels aplatis (1D) pour lecture rapide
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

    public DijkstraBotRapide(Couleurcase color) {
        super(color);
    }
    
    public Move getMove(ReversiPlateau board) {
        // 1. Conversion immédiate en Bitboard
        FastReversiBoard startBoard = new FastReversiBoard(board);
        boolean isBlack = (this.color == Couleurcase.NOIR);
        
        // Vérification rapide s'il y a des coups
        long validMoves = startBoard.getValidMovesBitmask(isBlack);
        if (validMoves == 0) return null;
        
        // S'il n'y a qu'un seul coup, on le joue tout de suite (opti)
        if (Long.bitCount(validMoves) == 1) {
            int i = Long.numberOfTrailingZeros(validMoves);
            return new Move(i / 8, i % 8);
        }
        
        return dijkstraSearch(startBoard, isBlack);
    }
    
    private Move dijkstraSearch(FastReversiBoard startBoard, boolean myColorIsBlack) {
        // Queue prioritaire : on explore d'abord les états qui nous avantagent le plus (distance faible)
        PriorityQueue<BoardNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        
        // Map pour stocker la distance minimale pour atteindre un état donné
        // Clé = "BlackBitboard:WhiteBitboard" (très rapide à générer)
        Map<String, Integer> distances = new HashMap<>();
        
        // Map pour se souvenir quel premier coup a mené à quel état
        Map<String, Move> firstMoves = new HashMap<>();
        
        // Initialisation avec les premiers coups possibles
        long validMoves = startBoard.getValidMovesBitmask(myColorIsBlack);
        
        for (int i = 0; i < 64; i++) {
            if ((validMoves & (1L << i)) != 0) {
                FastReversiBoard nextBoard = startBoard.copy();
                nextBoard.makeMove(i / 8, i % 8, myColorIsBlack);
                
                String key = getKey(nextBoard);
                // La distance est négative car on veut maximiser notre avantage
                // (Dijkstra cherche le min, donc min(-Score) = max(Score))
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
        
        while (!queue.isEmpty()) {
            // Sécurité temps : on arrête si ça prend plus de 1.5 secondes
            if (System.currentTimeMillis() - startTime > 1500) break;

            BoardNode current = queue.poll();
            String currentKey = getKey(current.board);
            
            // Si on a déjà trouvé un chemin plus court vers cet état (meilleur score), on ignore
            if (distances.containsKey(currentKey) && distances.get(currentKey) < current.distance) {
                continue;
            }
            
            // Limite de profondeur ou fin de partie
            if (current.depth >= 5 || (current.board.getValidMovesBitmask(true) == 0 && current.board.getValidMovesBitmask(false) == 0)) {
                if (current.distance < bestFinalDistance) {
                    bestFinalDistance = current.distance;
                    bestMove = firstMoves.get(currentKey); // On récupère le coup initial qui a mené ici
                }
                continue;
            }
            
            // C'est à qui de jouer dans cet état simulé ?
            // depth 0 = nous, depth 1 = adversaire, depth 2 = nous...
            boolean isBlackTurnNow = ((current.depth % 2) == 0) ? myColorIsBlack : !myColorIsBlack;
            
            long nextMovesMask = current.board.getValidMovesBitmask(isBlackTurnNow);
            
            if (nextMovesMask == 0) {
                // Le joueur passe son tour
                // On continue avec le même plateau, profondeur +1, tour inversé implicitement par la parité
                BoardNode passNode = new BoardNode(current.board.copy(), current.distance, current.depth + 1);
                String passKey = getKey(passNode.board) + ":" + (current.depth + 1); // Clé unique incluant profondeur pour éviter boucles
                
                if (!distances.containsKey(passKey) || current.distance < distances.get(passKey)) {
                    distances.put(passKey, current.distance);
                    // On propage le "premier coup" responsable de cette branche
                    firstMoves.putIfAbsent(passKey, firstMoves.get(currentKey)); 
                    queue.add(passNode);
                }
                continue;
            }

            // Génération des voisins
            for (int i = 0; i < 64; i++) {
                if ((nextMovesMask & (1L << i)) != 0) {
                    FastReversiBoard nextBoard = current.board.copy();
                    nextBoard.makeMove(i / 8, i % 8, isBlackTurnNow);
                    
                    int newAdvantage = evaluateBoardAdvantage(nextBoard, myColorIsBlack);
                    // Le coût de l'arête est la variation d'avantage. 
                    // On veut minimiser le score final (qui est -Avantage).
                    int newDistance = -newAdvantage; 
                    
                    String key = getKey(nextBoard);
                    
                    if (!distances.containsKey(key) || newDistance < distances.get(key)) {
                        distances.put(key, newDistance);
                        firstMoves.putIfAbsent(key, firstMoves.get(currentKey));
                        queue.add(new BoardNode(nextBoard, newDistance, current.depth + 1));
                    }
                }
            }
        }
        
        // Fallback : si on a timeout ou rien trouvé, on prend le meilleur coup immédiat
        if (bestMove == null) {
             long m = startBoard.getValidMovesBitmask(myColorIsBlack);
             int i = Long.numberOfTrailingZeros(m);
             return new Move(i/8, i%8);
        }
        
        return bestMove;
    }
    
    // Génère une clé unique très rapide pour la HashMap
    private String getKey(FastReversiBoard b) {
        return b.black + ":" + b.white;
    }
    
    private int evaluateBoardAdvantage(FastReversiBoard b, boolean amIBlack) {
        long myPieces = amIBlack ? b.black : b.white;
        long oppPieces = amIBlack ? b.white : b.black;
        
        int myScore = Long.bitCount(myPieces);
        int oppScore = Long.bitCount(oppPieces);
        
        // Fin de partie : victoire absolue
        long validMe = b.getValidMovesBitmask(amIBlack);
        long validOpp = b.getValidMovesBitmask(!amIBlack);
        
        if (validMe == 0 && validOpp == 0) {
            if (myScore > oppScore) return 10000;
            if (myScore < oppScore) return -10000;
            return 0;
        }
        
        int score = 0;
        
        // 1. Matrice de poids (optimisée boucle simple)
        for (int i = 0; i < 64; i++) {
            if ((myPieces & (1L << i)) != 0) score += WEIGHTS[i];
            else if ((oppPieces & (1L << i)) != 0) score -= WEIGHTS[i];
        }
        
        // 2. Mobilité (x2)
        score += (Long.bitCount(validMe) - Long.bitCount(validOpp)) * 5;
        
        // 3. Différence de pions (faible poids en début de partie)
        score += (myScore - oppScore);
        
        return score;
    }

    private static class BoardNode {
        FastReversiBoard board;
        int distance; // Score inversé
        int depth;
        
        BoardNode(FastReversiBoard board, int distance, int depth) {
            this.board = board;
            this.distance = distance;
            this.depth = depth;
        }
    }
}
