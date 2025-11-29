package reversi;

import java.util.List;

public class DFSBot extends BotPlayer {
    
    public DFSBot(Couleurcase color) {
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
        
        // If only one valid move, return it immediately (no need for DFS)
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        // Perform DFS search up to 6 moves ahead
        return dfsSearch(board, validMoves, 6);
    }
    
    /**
     * Performs DFS search to find the best move by evaluating future game states
     * @param currentBoard The current game board state
     * @param validMoves List of valid moves for the current turn
     * @param maxDepth Maximum depth to search (number of moves ahead)
     * @return The best move found
     */
    private Move dfsSearch(ReversiPlateau currentBoard, List<Move> validMoves, int maxDepth) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Evaluate each possible first move using DFS
        for (Move firstMove : validMoves) {
            // Create a copy of the board to simulate the move
            ReversiPlateau boardAfterFirstMove = currentBoard.copy();
            boardAfterFirstMove.placePion(firstMove, this.color);
            
            // Check if this move leads to immediate victory
            if (boardAfterFirstMove.GameOver()) {
                int score = evaluateBoard(boardAfterFirstMove);
                if (score > 0) {
                    // This move leads to victory, return it immediately
                    return firstMove;
                }
            }
            
            // Perform DFS to explore future moves recursively
            int moveScore = performDFS(boardAfterFirstMove, maxDepth - 1, this.color.oppose());
            
            // Update best move if this one has a better score
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMove = firstMove;
            }
        }
        
        // If no move was evaluated (shouldn't happen), return a random valid move
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    
    /**
     * Performs DFS traversal of the game tree recursively
     * @param board The current board state
     * @param depth Remaining depth to search
     * @param currentPlayer The player whose turn it is
     * @return The best score achievable from this board state
     */
    private int performDFS(ReversiPlateau board, int depth, Couleurcase currentPlayer) {
        // Base case 1: If game is over, evaluate the final board state
        if (board.GameOver()) {
            return evaluateBoard(board);
        }
        
        // Base case 2: If we've reached maximum depth, evaluate current board
        if (depth <= 0) {
            return evaluateBoard(board);
        }
        
        // Get valid moves for the current player
        List<Move> validMoves = board.getValidMoves(currentPlayer);
        
        // If no valid moves, the player passes - continue with opposite player
        if (validMoves.isEmpty()) {
            return performDFS(board, depth - 1, currentPlayer.oppose());
        }
        
        int bestScore = Integer.MIN_VALUE;
        
        // Recursively explore all valid moves
        for (Move move : validMoves) {
            // Create a copy of the board for this branch
            ReversiPlateau newBoard = board.copy();
            newBoard.placePion(move, currentPlayer);
            
            // If this move ends the game, evaluate it immediately
            if (newBoard.GameOver()) {
                int score = evaluateBoard(newBoard);
                if (score > bestScore) {
                    bestScore = score;
                }
                // If we found a winning state, we can stop searching this branch
                if (score > 0) {
                    return score;
                }
                continue;
            }
            
            // Recursively search deeper with the opposite player's turn
            int score = performDFS(newBoard, depth - 1, currentPlayer.oppose());
            
            // Update best score for this level
            if (score > bestScore) {
                bestScore = score;
            }
        }
        
        return bestScore;
    }
    
    /**
     * Evaluates a board state and returns a score from the perspective of this bot
     * Positive score is good for the bot, negative is bad
     * @param board The board to evaluate
     * @return Evaluation score
     */
    private int evaluateBoard(ReversiPlateau board) {
        // Simple evaluation: difference in piece count
        int myScore = board.getScore(this.color);
        int opponentScore = board.getScore(this.color.oppose());
        
        // If game is over, assign winning/losing scores
        if (board.GameOver()) {
            if (myScore > opponentScore) {
                return 1000; // Big positive score for win
            } else if (myScore < opponentScore) {
                return -1000; // Big negative score for loss
            } else {
                return 0; // Draw
            }
        }
        
        // For non-terminal states, return simple score difference
        return myScore - opponentScore;
    }
}