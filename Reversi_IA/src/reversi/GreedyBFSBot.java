package reversi;

import java.util.*;

public class GreedyBFSBot extends BotPlayer {
    
    public GreedyBFSBot(Couleurcase color) {
        super(color);
    }
    
    @Override
    public Move getMove(ReversiPlateau board) {
        // Get all valid moves for the current player
        List<Move> validMoves = board.getValidMoves(this.color);
        
        // If no valid moves, return null (pass turn)
        if (validMoves.isEmpty()) {
            return null;
        }
        
        // If only one valid move, return it immediately
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        // Use Greedy Best-First Search to find the best move
        return greedyBFSSearch(board, validMoves);
    }
    
    /**
     * Greedy Best-First Search that always expands the most promising node first
     * Uses a heuristic function to evaluate moves and picks the best immediate option
     */
    private Move greedyBFSSearch(ReversiPlateau currentBoard, List<Move> validMoves) {
        // Priority queue to always get the best move according to our heuristic
        PriorityQueue<ScoredMove> moveQueue = new PriorityQueue<>(
            (a, b) -> Double.compare(b.score, a.score) // Descending order
        );
        
        // Evaluate each move using greedy heuristic
        for (Move move : validMoves) {
            double score = greedyHeuristic(currentBoard, move);
            moveQueue.add(new ScoredMove(move, score));
        }
        
        // Return the move with highest heuristic score
        return moveQueue.poll().move;
    }
    
    /**
     * Greedy heuristic function that evaluates a move based on:
     * 1. Immediate piece gain
     * 2. Position quality
     * 3. Potential to limit opponent's mobility
     */
    private double greedyHeuristic(ReversiPlateau board, Move move) {
        double score = 0.0;
        
        // Simulate the move to see immediate consequences
        ReversiPlateau simulatedBoard = board.copy();
        simulatedBoard.placePion(move, this.color);
        
        // 1. Immediate piece gain (most important for greedy approach)
        int piecesGained = calculatePiecesGained(board, simulatedBoard);
        score += piecesGained * 3.0;
        
        // 2. Position value - greedy for corners and edges
        score += getGreedyPositionValue(move.x, move.y) * 2.5;
        
        // 3. Opponent mobility reduction - greedy for limiting opponent
        int opponentMobilityReduction = calculateOpponentMobilityReduction(board, simulatedBoard);
        score += opponentMobilityReduction * 1.5;
        
        // 4. Immediate win detection - most greedy option
        if (simulatedBoard.GameOver()) {
            int finalScore = simulatedBoard.getScore(this.color) - simulatedBoard.getScore(this.color.oppose());
            if (finalScore > 0) {
                score += 1000; // Huge bonus for immediate win
            }
        }
        
        return score;
    }
    
    /**
     * Calculate how many pieces this move would flip
     */
    private int calculatePiecesGained(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalScore = originalBoard.getScore(this.color);
        int newScore = newBoard.getScore(this.color);
        return newScore - originalScore - 1; // Subtract the placed piece
    }
    
    /**
     * Greedy position valuation - strongly prefers corners and edges
     */
    private double getGreedyPositionValue(int x, int y) {
        // Corners are extremely valuable in greedy approach
        if ((x == 0 && y == 0) || (x == 0 && y == 7) || 
            (x == 7 && y == 0) || (x == 7 && y == 7)) {
            return 20.0; // Very high value for corners
        }
        // Edges are also very valuable
        else if (x == 0 || x == 7 || y == 0 || y == 7) {
            return 8.0; // High value for edges
        }
        // Center control
        else if (x >= 2 && x <= 5 && y >= 2 && y <= 5) {
            return 3.0;
        }
        // Dangerous positions near corners
        else if ((x == 1 || x == 6) && (y == 1 || y == 6)) {
            return -10.0; // Strongly avoid these
        }
        else {
            return 1.0;
        }
    }
    
    /**
     * Calculate how much this move reduces opponent's mobility
     */
    private int calculateOpponentMobilityReduction(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalOpponentMoves = originalBoard.getValidMoves(this.color.oppose()).size();
        int newOpponentMoves = newBoard.getValidMoves(this.color.oppose()).size();
        return originalOpponentMoves - newOpponentMoves; // Positive is good
    }
    
    /**
     * Helper class to store moves with their scores
     */
    private static class ScoredMove {
        Move move;
        double score;
        
        ScoredMove(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }
}