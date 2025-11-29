package reversi;

import java.util.*;

public class DijkstraBot extends BotPlayer {
    
    public DijkstraBot(Couleurcase color) {
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
        
        // Use Dijkstra-inspired search to find the best move
        return dijkstraSearch(board, validMoves);
    }
    
    /**
     * Dijkstra-inspired search that prioritizes moves with immediate high value
     * Unlike traditional Dijkstra for pathfinding, we use it to evaluate move quality
     * based on immediate and short-term gains
     */
    private Move dijkstraSearch(ReversiPlateau currentBoard, List<Move> validMoves) {
        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        // Evaluate each possible move using Dijkstra-inspired evaluation
        for (Move move : validMoves) {
            // Create a copy of the board to simulate the move
            ReversiPlateau boardAfterMove = currentBoard.copy();
            boardAfterMove.placePion(move, this.color);
            
            // Calculate the "cost" (inverse of benefit) of this move
            double moveScore = evaluateMoveWithDijkstra(currentBoard, boardAfterMove, move);
            
            // Update best move if this one has a better score
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMove = move;
            }
        }
        
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    
    /**
     * Dijkstra-inspired evaluation that considers:
     * 1. Immediate piece gain (flips)
     * 2. Strategic position value (corners, edges)
     * 3. Mobility (number of future moves)
     */
    private double evaluateMoveWithDijkstra(ReversiPlateau originalBoard, ReversiPlateau newBoard, Move move) {
        double score = 0.0;
        
        // 1. Immediate piece difference (primary factor)
        int immediateGain = calculateImmediateGain(originalBoard, newBoard, move);
        score += immediateGain * 2.0; // Weight immediate gains heavily
        
        // 2. Position value - Dijkstra would prioritize stable positions
        score += getPositionValue(move.x, move.y) * 1.5;
        
        // 3. Mobility - Dijkstra values paths that maintain options
        int myMobility = newBoard.getValidMoves(this.color).size();
        int opponentMobility = newBoard.getValidMoves(this.color.oppose()).size();
        score += (myMobility - opponentMobility) * 0.5;
        
        // 4. Stability - Dijkstra prefers stable, low-risk positions
        score += evaluateStability(newBoard, move) * 1.2;
        
        return score;
    }
    
    /**
     * Calculate how many pieces were flipped by this move
     */
    private int calculateImmediateGain(ReversiPlateau originalBoard, ReversiPlateau newBoard, Move move) {
        int originalCount = originalBoard.getScore(this.color);
        int newCount = newBoard.getScore(this.color);
        return newCount - originalCount - 1; // Subtract 1 for the placed piece
    }
    
    /**
     * Assign values to board positions (corners are most valuable, etc.)
     */
    private double getPositionValue(int x, int y) {
        // Corner positions are most stable and valuable
        if ((x == 0 || x == 7) && (y == 0 || y == 7)) {
            return 10.0; // Corners
        }
        // Edge positions are good but not as good as corners
        else if (x == 0 || x == 7 || y == 0 || y == 7) {
            return 3.0; // Edges
        }
        // Center positions are moderately valuable
        else if (x >= 2 && x <= 5 && y >= 2 && y <= 5) {
            return 1.5; // Center
        }
        // Positions adjacent to corners are dangerous
        else if ((x == 0 || x == 7) && (y == 1 || y == 6) ||
                 (y == 0 || y == 7) && (x == 1 || x == 6)) {
            return -5.0; // Avoid positions that give opponent corner access
        }
        else {
            return 0.5; // Neutral positions
        }
    }
    
    /**
     * Evaluate how stable this position is (less likely to be flipped back)
     */
    private double evaluateStability(ReversiPlateau board, Move move) {
        double stability = 0.0;
        int x = move.x, y = move.y;
        
        // Check if piece is protected in multiple directions
        int protectedDirections = 0;
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};
        
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && 
                board.getEtat(nx, ny) == this.color) {
                protectedDirections++;
            }
        }
        
        stability = protectedDirections * 0.3;
        
        // Additional stability for corner pieces (they can never be flipped)
        if ((x == 0 || x == 7) && (y == 0 || y == 7)) {
            stability += 5.0;
        }
        
        return stability;
    }
}