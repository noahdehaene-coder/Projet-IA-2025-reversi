package reversi;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFSBot extends BotPlayer {
    
    public BFSBot(Couleurcase color) {
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
        
        // If only one valid move, return it immediately (no need for BFS)
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        // Perform BFS search up to 6 moves ahead
        return bfsSearch(board, validMoves, 6);
    }
    
    /**
     * Performs BFS search to find the best move by evaluating future game states
     * @param currentBoard The current game board state
     * @param validMoves List of valid moves for the current turn
     * @param maxDepth Maximum depth to search (number of moves ahead)
     * @return The best move found
     */
    private Move bfsSearch(ReversiPlateau currentBoard, List<Move> validMoves, int maxDepth) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Evaluate each possible first move
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
            
            // Perform BFS to explore future moves
            int moveScore = performBFS(boardAfterFirstMove, maxDepth - 1);
            
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
     * Performs BFS traversal of the game tree
     * @param startBoard The board state to start from
     * @param maxDepth Maximum depth to search from this point
     * @return The best score achievable from this board state
     */
    private int performBFS(ReversiPlateau startBoard, int maxDepth) {
        // Queue for BFS - stores board states and their depth
        Queue<BoardState> queue = new LinkedList<>();
        queue.add(new BoardState(startBoard, 0, this.color.oppose()));
        
        int bestScore = Integer.MIN_VALUE;
        
        while (!queue.isEmpty()) {
            BoardState currentState = queue.poll();
            ReversiPlateau currentBoard = currentState.board;
            int currentDepth = currentState.depth;
            Couleurcase currentPlayer = currentState.currentPlayer;
            
            // Check if game is over at this state
            if (currentBoard.GameOver()) {
                int score = evaluateBoard(currentBoard);
                if (score > bestScore) {
                    bestScore = score;
                }
                // If we found a winning state at early depth, we can stop searching further
                if (score > 0 && currentDepth <= maxDepth) {
                    return score;
                }
                continue;
            }
            
            // If we've reached maximum depth, evaluate this board and continue
            if (currentDepth >= maxDepth) {
                int score = evaluateBoard(currentBoard);
                if (score > bestScore) {
                    bestScore = score;
                }
                continue;
            }
            
            // Get valid moves for the current player
            List<Move> validMoves = currentBoard.getValidMoves(currentPlayer);
            
            // If no valid moves, the player passes
            if (validMoves.isEmpty()) {
                // Create a new state where the turn passes to the other player
                queue.add(new BoardState(currentBoard.copy(), currentDepth + 1, currentPlayer.oppose()));
            } else {
                // Explore all valid moves
                for (Move move : validMoves) {
                    ReversiPlateau newBoard = currentBoard.copy();
                    newBoard.placePion(move, currentPlayer);
                    
                    // Add the new state to the queue with increased depth and opposite player
                    queue.add(new BoardState(newBoard, currentDepth + 1, currentPlayer.oppose()));
                }
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
    
    /**
     * Helper class to store board state information for BFS
     */
    private static class BoardState {
        ReversiPlateau board;
        int depth;
        Couleurcase currentPlayer;
        
        BoardState(ReversiPlateau board, int depth, Couleurcase currentPlayer) {
            this.board = board;
            this.depth = depth;
            this.currentPlayer = currentPlayer;
        }
    }
}