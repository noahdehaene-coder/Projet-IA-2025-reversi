package reversi;

import java.util.*;

public class AstarBot extends BotPlayer {
    
    public AstarBot(Couleurcase color) {
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
        
        // Use A* search to find the best move with cost + heuristic evaluation
        return aStarSearch(board, validMoves, 4); // Search 4 moves ahead
    }
    
    /**
     * A* search that combines actual cost (g) with heuristic estimate (h)
     * f(n) = g(n) + h(n) where:
     * - g(n) is the actual cost/benefit so far
     * - h(n) is the heuristic estimate of cost to goal
     */
    private Move aStarSearch(ReversiPlateau currentBoard, List<Move> validMoves, int maxDepth) {
        Move bestMove = null;
        double bestFScore = Double.NEGATIVE_INFINITY;
        
        // Evaluate each first move using A* evaluation
        for (Move firstMove : validMoves) {
            // Calculate g-score (actual benefit from this move)
            double gScore = calculateActualBenefit(currentBoard, firstMove);
            
            // Calculate h-score (heuristic estimate of future potential)
            double hScore = calculateHeuristicEstimate(currentBoard, firstMove, maxDepth);
            
            // A* evaluation: f(n) = g(n) + h(n)
            double fScore = gScore + hScore;
            
            if (fScore > bestFScore) {
                bestFScore = fScore;
                bestMove = firstMove;
            }
        }
        
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    
    /**
     * Calculate actual benefit (g-score) of this move
     * This represents the known, immediate advantage
     */
    private double calculateActualBenefit(ReversiPlateau board, Move move) {
        double gScore = 0.0;
        
        // Simulate the move
        ReversiPlateau newBoard = board.copy();
        newBoard.placePion(move, this.color);
        
        // 1. Immediate piece gain (most important actual benefit)
        int piecesGained = calculatePiecesGained(board, newBoard);
        gScore += piecesGained * 2.5;
        
        // 2. Position stability benefit
        gScore += getStabilityBenefit(newBoard, move) * 1.8;
        
        // 3. Actual mobility change
        int mobilityChange = calculateMobilityChange(board, newBoard);
        gScore += mobilityChange * 1.2;
        
        // 4. Corner capture (huge actual benefit)
        if (isCorner(move.x, move.y)) {
            gScore += 15.0;
        }
        
        return gScore;
    }
    
    /**
     * Calculate heuristic estimate (h-score) of future potential
     * This estimates how promising this move is for long-term success
     */
    private double calculateHeuristicEstimate(ReversiPlateau board, Move move, int maxDepth) {
        // Simulate the move
        ReversiPlateau newBoard = board.copy();
        newBoard.placePion(move, this.color);
        
        // Use a combination of strategic factors for heuristic
        double hScore = 0.0;
        
        // 1. Potential for corner control
        hScore += evaluateCornerPotential(newBoard) * 2.0;
        
        // 2. Potential for edge control
        hScore += evaluateEdgePotential(newBoard) * 1.2;
        
        // 3. Potential mobility advantage
        hScore += evaluateMobilityPotential(newBoard) * 1.0;
        
        // 4. Potential for stable pieces
        hScore += evaluateStabilityPotential(newBoard) * 1.5;
        
        // 5. Look ahead a few moves with simplified evaluation
        if (maxDepth > 0) {
            hScore += lookAheadPotential(newBoard, maxDepth - 1) * 0.8;
        }
        
        return hScore;
    }
    
    /**
     * Calculate how many pieces this move would flip
     */
    private int calculatePiecesGained(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalScore = originalBoard.getScore(this.color);
        int newScore = newBoard.getScore(this.color);
        return newScore - originalScore - 1;
    }
    
    /**
     * Evaluate stability benefit of this position
     */
    private double getStabilityBenefit(ReversiPlateau board, Move move) {
        double stability = 0.0;
        int x = move.x, y = move.y;
        
        // Corners are completely stable
        if (isCorner(x, y)) {
            stability += 10.0;
        }
        
        // Edges are relatively stable
        if (x == 0 || x == 7 || y == 0 || y == 7) {
            stability += 4.0;
        }
        
        // Count friendly neighbors for additional stability
        int friendlyNeighbors = countFriendlyNeighbors(board, x, y);
        stability += friendlyNeighbors * 0.5;
        
        return stability;
    }
    
    /**
     * Calculate change in mobility (our moves vs opponent moves)
     */
    private int calculateMobilityChange(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalMyMoves = originalBoard.getValidMoves(this.color).size();
        int originalOpponentMoves = originalBoard.getValidMoves(this.color.oppose()).size();
        int newMyMoves = newBoard.getValidMoves(this.color).size();
        int newOpponentMoves = newBoard.getValidMoves(this.color.oppose()).size();
        
        int myMobilityChange = newMyMoves - originalMyMoves;
        int opponentMobilityChange = newOpponentMoves - originalOpponentMoves;
        
        return myMobilityChange - opponentMobilityChange;
    }
    
    /**
     * Check if position is a corner
     */
    private boolean isCorner(int x, int y) {
        return (x == 0 && y == 0) || (x == 0 && y == 7) || 
               (x == 7 && y == 0) || (x == 7 && y == 7);
    }
    
    /**
     * Count friendly neighboring pieces
     */
    private int countFriendlyNeighbors(ReversiPlateau board, int x, int y) {
        int count = 0;
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};
        
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && 
                board.getEtat(nx, ny) == this.color) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Evaluate potential for corner control
     */
    private double evaluateCornerPotential(ReversiPlateau board) {
        double potential = 0.0;
        int[][] corners = {{0,0}, {0,7}, {7,0}, {7,7}};
        
        for (int[] corner : corners) {
            Couleurcase state = board.getEtat(corner[0], corner[1]);
            if (state == this.color) {
                potential += 5.0; // We control this corner
            } else if (state == Couleurcase.VIDE) {
                // Check if we have access to this corner
                if (board.getValidMoves(this.color).stream()
                    .anyMatch(m -> m.x == corner[0] && m.y == corner[1])) {
                    potential += 3.0; // We can take this corner
                }
            }
        }
        
        return potential;
    }
    
    /**
     * Evaluate potential for edge control
     */
    private double evaluateEdgePotential(ReversiPlateau board) {
        double potential = 0.0;
        
        // Count our pieces on edges vs opponent pieces
        for (int i = 0; i < 8; i++) {
            // Top edge
            if (board.getEtat(0, i) == this.color) potential += 1.0;
            else if (board.getEtat(0, i) == this.color.oppose()) potential -= 1.0;
            
            // Bottom edge
            if (board.getEtat(7, i) == this.color) potential += 1.0;
            else if (board.getEtat(7, i) == this.color.oppose()) potential -= 1.0;
            
            // Left edge
            if (board.getEtat(i, 0) == this.color) potential += 1.0;
            else if (board.getEtat(i, 0) == this.color.oppose()) potential -= 1.0;
            
            // Right edge
            if (board.getEtat(i, 7) == this.color) potential += 1.0;
            else if (board.getEtat(i, 7) == this.color.oppose()) potential -= 1.0;
        }
        
        return potential;
    }
    
    /**
     * Evaluate potential mobility advantage
     */
    private double evaluateMobilityPotential(ReversiPlateau board) {
        int myMobility = board.getValidMoves(this.color).size();
        int opponentMobility = board.getValidMoves(this.color.oppose()).size();
        return (myMobility - opponentMobility) * 0.5;
    }
    
    /**
     * Evaluate potential for stable pieces
     */
    private double evaluateStabilityPotential(ReversiPlateau board) {
        double stability = 0.0;
        
        // Simple stability evaluation based on corner connections
        // Pieces connected to corners are more stable
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getEtat(i, j) == this.color) {
                    // Check if this piece is connected to a corner
                    if (isConnectedToCorner(board, i, j)) {
                        stability += 0.3;
                    }
                }
            }
        }
        
        return stability;
    }
    
    /**
     * Check if a piece is connected to a corner (directly or through friendly pieces)
     */
    private boolean isConnectedToCorner(ReversiPlateau board, int x, int y) {
        // Simple implementation - check if on same row/column as a corner we control
        int[][] corners = {{0,0}, {0,7}, {7,0}, {7,7}};
        
        for (int[] corner : corners) {
            if (board.getEtat(corner[0], corner[1]) == this.color) {
                if (x == corner[0] || y == corner[1]) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Simplified look-ahead for potential evaluation
     */
    private double lookAheadPotential(ReversiPlateau board, int depth) {
        if (depth <= 0 || board.GameOver()) {
            return evaluateBoardSimple(board);
        }
        
        List<Move> moves = board.getValidMoves(this.color);
        if (moves.isEmpty()) {
            return evaluateBoardSimple(board);
        }
        
        double bestPotential = Double.NEGATIVE_INFINITY;
        
        // Look at a sample of moves to estimate potential
        for (int i = 0; i < Math.min(3, moves.size()); i++) { // Sample 3 moves
            Move move = moves.get(i);
            ReversiPlateau newBoard = board.copy();
            newBoard.placePion(move, this.color);
            
            double potential = evaluateBoardSimple(newBoard) + lookAheadPotential(newBoard, depth - 1) * 0.7;
            if (potential > bestPotential) {
                bestPotential = potential;
            }
        }
        
        return bestPotential;
    }
    
    /**
     * Simple board evaluation for look-ahead
     */
    private double evaluateBoardSimple(ReversiPlateau board) {
        int myScore = board.getScore(this.color);
        int opponentScore = board.getScore(this.color.oppose());
        return (myScore - opponentScore) * 0.1;
    }
}