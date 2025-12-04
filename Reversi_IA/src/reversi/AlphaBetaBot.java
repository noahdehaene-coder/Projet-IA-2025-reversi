package reversi;

import java.util.List;

public class AlphaBetaBot extends BotPlayer {

    private int maxDepth;

    public AlphaBetaBot(Couleurcase color, int depth) {
        super(color);
        this.maxDepth = depth;
    }

    public Move getMove(ReversiPlateau board) {
        return alphaBetaRoot(board, maxDepth);
    }

    private Move alphaBetaRoot(ReversiPlateau board, int depth) {
        List<Move> moves = board.getValidMoves(this.color);
        if (moves.isEmpty()) return null;

        Move bestMove = moves.get(0);
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : moves) {
            ReversiPlateau clone = board.copy();
            clone.placePion(move, this.color);
            
            // On appelle la récursion pour l'adversaire (min)
            int score = alphaBeta(clone, depth - 1, alpha, beta, false);
            
            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int alphaBeta(ReversiPlateau board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || board.GameOver()) {
            return evaluateComplex(board);
        }

        Couleurcase currentPlayer = maximizingPlayer ? this.color : this.color.oppose();
        List<Move> moves = board.getValidMoves(currentPlayer);

        if (moves.isEmpty()) {
            // Si le joueur ne peut pas jouer, on passe le tour
            return alphaBeta(board, depth - 1, alpha, beta, !maximizingPlayer);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                ReversiPlateau clone = board.copy();
                clone.placePion(move, currentPlayer);
                int eval = alphaBeta(clone, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Élagage
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                ReversiPlateau clone = board.copy();
                clone.placePion(move, currentPlayer);
                int eval = alphaBeta(clone, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Élagage
            }
            return minEval;
        }
    }

    // Une fonction d'évaluation plus poussée
    private int evaluateComplex(ReversiPlateau board) {
        int myScore = 0;
        int opScore = 0;
        
        // 1. Mobilité (très important)
        int myMobility = board.getValidMoves(this.color).size();
        int opMobility = board.getValidMoves(this.color.oppose()).size();
        
        // 2. Coins (Corners)
        int myCorners = countCorners(board, this.color);
        int opCorners = countCorners(board, this.color.oppose());

        // Poids arbitraires à ajuster
        return (myMobility - opMobility) * 10 
             + (myCorners - opCorners) * 100 
             + (board.getScore(this.color) - board.getScore(this.color.oppose())); 
    }
    
    private int countCorners(ReversiPlateau board, Couleurcase c) {
        int count = 0;
        if(board.getEtat(0,0) == c) count++;
        if(board.getEtat(0,7) == c) count++;
        if(board.getEtat(7,0) == c) count++;
        if(board.getEtat(7,7) == c) count++;
        return count;
    }
}