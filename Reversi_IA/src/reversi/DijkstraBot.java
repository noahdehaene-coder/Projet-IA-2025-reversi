package reversi;

import java.util.*;

public class DijkstraBot extends BotPlayer {
    
    public DijkstraBot(Couleurcase color) {
        super(color);
    }
    
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
     * Dijkstra algorithm adapted for Reversi:
     * - Each board state is a node
     * - Each move is an edge with negative weight (we want to minimize opponent's advantage)
     * - Finds the path that minimizes opponent's maximum potential
     */
    private Move dijkstraSearch(ReversiPlateau startBoard, List<Move> validMoves) {
        // Priority queue for Dijkstra's algorithm
        PriorityQueue<BoardNode> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        Map<String, Integer> distances = new HashMap<>();
        Map<String, Move> firstMoves = new HashMap<>();
        
        // Initialize with all possible first moves
        for (Move firstMove : validMoves) {
            ReversiPlateau newBoard = startBoard.copy();
            newBoard.placePion(firstMove, this.color);
            
            String boardKey = getBoardKey(newBoard);
            // Initial distance is the negative of our advantage (we want to minimize opponent's advantage)
            int initialDistance = -evaluateBoardAdvantage(newBoard);
            
            distances.put(boardKey, initialDistance);
            firstMoves.put(boardKey, firstMove);
            queue.add(new BoardNode(newBoard, initialDistance, 0));
        }
        
        Move bestMove = null;
        int bestFinalDistance = Integer.MAX_VALUE;
        
        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            BoardNode current = queue.poll();
            
            // If we reached maximum depth or game over, evaluate this path
            if (current.depth >= 6 || current.board.GameOver()) {
                if (current.distance < bestFinalDistance) {
                    bestFinalDistance = current.distance;
                    bestMove = firstMoves.get(getBoardKey(current.board));
                }
                continue;
            }
            
            // Explore opponent's moves (this is the key - we consider opponent's best responses)
            Couleurcase currentPlayer = current.depth % 2 == 0 ? this.color.oppose() : this.color;
            List<Move> nextMoves = current.board.getValidMoves(currentPlayer);
            
            if (nextMoves.isEmpty()) {
                // Player passes
                ReversiPlateau passBoard = current.board.copy();
                String boardKey = getBoardKey(passBoard);
                int newDistance = current.distance;
                
                if (!distances.containsKey(boardKey) || newDistance < distances.get(boardKey)) {
                    distances.put(boardKey, newDistance);
                    queue.add(new BoardNode(passBoard, newDistance, current.depth + 1));
                    if (!firstMoves.containsKey(boardKey)) {
                        firstMoves.put(boardKey, firstMoves.get(getBoardKey(current.board)));
                    }
                }
                continue;
            }
            
            for (Move move : nextMoves) {
                ReversiPlateau newBoard = current.board.copy();
                newBoard.placePion(move, currentPlayer);
                
                // Calculate edge weight: negative of the advantage change
                int advantageChange = evaluateBoardAdvantage(newBoard) - evaluateBoardAdvantage(current.board);
                int newDistance = current.distance - advantageChange; // Negative because we want to minimize opponent's advantage
                
                String boardKey = getBoardKey(newBoard);
                
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
     * Evaluates board advantage from this bot's perspective
     * Positive = good for bot, Negative = good for opponent
     */
    private int evaluateBoardAdvantage(ReversiPlateau board) {
        if (board.GameOver()) {
            int myScore = board.getScore(this.color);
            int opponentScore = board.getScore(this.color.oppose());
            if (myScore > opponentScore) return 1000;
            if (myScore < opponentScore) return -1000;
            return 0;
        }
        
        int basicScore = board.getScore(this.color) - board.getScore(this.color.oppose());
        
        // Add positional advantage
        int positionalScore = evaluatePositionalAdvantage(board);
        
        // Add mobility advantage
        int mobilityScore = evaluateMobilityAdvantage(board);
        
        return basicScore + positionalScore + mobilityScore;
    }
    
    /**
     * Evaluate positional advantage using stable piece evaluation
     */
    private int evaluatePositionalAdvantage(ReversiPlateau board) {
        int score = 0;
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
        
        for (int i = 0; i < ReversiPlateau.taille; i++) {
            for (int j = 0; j < ReversiPlateau.taille; j++) {
                Couleurcase cell = board.getEtat(i, j);
                if (cell == this.color) {
                    score += positionWeights[i][j];
                } else if (cell == this.color.oppose()) {
                    score -= positionWeights[i][j];
                }
            }
        }
        
        return score / 10; // Normalize
    }
    
    /**
     * Evaluate mobility advantage
     */
    private int evaluateMobilityAdvantage(ReversiPlateau board) {
        int myMoves = board.getValidMoves(this.color).size();
        int opponentMoves = board.getValidMoves(this.color.oppose()).size();
        
        if (myMoves + opponentMoves == 0) return 0;
        
        return (myMoves - opponentMoves) * 2;
    }
    
    /**
     * Creates a unique key for board state (simplified for this example)
     */
    private String getBoardKey(ReversiPlateau board) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < ReversiPlateau.taille; i++) {
            for (int j = 0; j < ReversiPlateau.taille; j++) {
                key.append(board.getEtat(i, j).ordinal());
            }
        }
        return key.toString();
    }
    
    /**
     * Node class for Dijkstra's algorithm
     */
    private static class BoardNode {
        ReversiPlateau board;
        int distance;
        int depth;
        
        BoardNode(ReversiPlateau board, int distance, int depth) {
            this.board = board;
            this.distance = distance;
            this.depth = depth;
        }
    }
}