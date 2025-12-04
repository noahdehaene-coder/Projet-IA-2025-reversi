package reversi;

import java.util.List;

public class AlphaBetaBotRapide extends BotPlayer {
    private int maxDepth;
    // Poids statiques pour les cases (Coins = très bien, cases X = danger)
    private static final int[] WEIGHTS = {
         100, -20,  10,   5,   5,  10, -20, 100,
         -20, -50,  -2,  -2,  -2,  -2, -50, -20,
          10,  -2,  -1,  -1,  -1,  -1,  -2,  10,
           5,  -2,  -1,  -1,  -1,  -1,  -2,   5,
           5,  -2,  -1,  -1,  -1,  -1,  -2,   5,
          10,  -2,  -1,  -1,  -1,  -1,  -2,  10,
         -20, -50,  -2,  -2,  -2,  -2, -50, -20,
         100, -20,  10,   5,   5,  10, -20, 100
    };

    public AlphaBetaBotRapide(Couleurcase color, int depth) {
        super(color);
        this.maxDepth = depth;
    }

    public Move getMove(ReversiPlateau board) {
        FastReversiBoard fastBoard = new FastReversiBoard(board);
        boolean isBlack = (this.color == Couleurcase.NOIR);
        
        long validMoves = fastBoard.getValidMovesBitmask(isBlack);
        if (validMoves == 0) return null;

        long bestMove = 0;
        int maxEval = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // On itère sur les bits à 1 (les coups valides)
        for (int i = 0; i < 64; i++) {
            long mask = 1L << i;
            if ((validMoves & mask) != 0) {
                FastReversiBoard clone = fastBoard.copy();
                clone.makeMove(i / 8, i % 8, isBlack);
                
                int eval = alphaBeta(clone, maxDepth - 1, alpha, beta, !isBlack);
                
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = mask;
                }
                alpha = Math.max(alpha, eval);
            }
        }

        int index = Long.numberOfTrailingZeros(bestMove);
        return new Move(index / 8, index % 8);
    }

    private int alphaBeta(FastReversiBoard board, int depth, int alpha, int beta, boolean maxPlayer) {
        if (depth == 0) return evaluate(board);

        // Vérification fin de partie simplifiée : si aucun coup ni pour l'un ni pour l'autre
        long moves = board.getValidMovesBitmask(this.color == Couleurcase.NOIR ? maxPlayer : !maxPlayer);
        if (moves == 0) {
            // Si l'adversaire ne peut pas jouer non plus, c'est fini
            if (board.getValidMovesBitmask(this.color == Couleurcase.NOIR ? !maxPlayer : maxPlayer) == 0) {
                return evaluate(board) * 10; // Bonus pour victoire finale
            }
            // Sinon on passe le tour
            return alphaBeta(board, depth - 1, alpha, beta, !maxPlayer);
        }

        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 64; i++) {
                if ((moves & (1L << i)) != 0) {
                    FastReversiBoard clone = board.copy();
                    clone.makeMove(i / 8, i % 8, this.color == Couleurcase.NOIR);
                    int eval = alphaBeta(clone, depth - 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 64; i++) {
                if ((moves & (1L << i)) != 0) {
                    FastReversiBoard clone = board.copy();
                    clone.makeMove(i / 8, i % 8, this.color != Couleurcase.NOIR);
                    int eval = alphaBeta(clone, depth - 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;
                }
            }
            return minEval;
        }
    }

    private int evaluate(FastReversiBoard board) {
        long myPieces = (this.color == Couleurcase.NOIR) ? board.black : board.white;
        long oppPieces = (this.color == Couleurcase.NOIR) ? board.white : board.black;
        
        int score = 0;
        // 1. Évaluation positionnelle (Coins > Bords > Centre)
        for (int i = 0; i < 64; i++) {
            if ((myPieces & (1L << i)) != 0) score += WEIGHTS[i];
            else if ((oppPieces & (1L << i)) != 0) score -= WEIGHTS[i];
        }
        
        // 2. Mobilité (Nombre de coups disponibles)
        // C'est souvent plus important que le score en début de partie
        int myMobility = Long.bitCount(board.getValidMovesBitmask(this.color == Couleurcase.NOIR));
        int oppMobility = Long.bitCount(board.getValidMovesBitmask(this.color != Couleurcase.NOIR));
        
        score += (myMobility - oppMobility) * 15; // Poids arbitraire 15
        
        return score;
    }
}
