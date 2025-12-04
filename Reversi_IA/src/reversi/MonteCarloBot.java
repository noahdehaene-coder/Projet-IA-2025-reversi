package reversi;

import java.util.List;
import java.util.Random;

public class MonteCarloBot extends BotPlayer {
    private Random random = new Random();
    private static final int SIMULATIONS = 1000; // Nombre de parties simulées par coup

    public MonteCarloBot(Couleurcase color) {
        super(color);
    }

    public Move getMove(ReversiPlateau board) {
        List<Move> validMoves = board.getValidMoves(this.color);
        if (validMoves.isEmpty()) return null;

        Move bestMove = null;
        int bestWins = -1;

        // Pour chaque coup possible...
        for (Move move : validMoves) {
            int wins = 0;
            
            // On simule N parties aléatoires
            for (int i = 0; i < SIMULATIONS; i++) {
                ReversiPlateau clone = board.copy();
                clone.placePion(move, this.color); // On joue le premier coup
                
                // On termine la partie au hasard
                if (simulateRandomGame(clone, this.color.oppose())) {
                    wins++;
                }
            }

            if (wins > bestWins) {
                bestWins = wins;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // Retourne true si NOTRE bot a gagné
    private boolean simulateRandomGame(ReversiPlateau board, Couleurcase currentTurn) {
        while (!board.GameOver()) {
            List<Move> moves = board.getValidMoves(currentTurn);
            if (!moves.isEmpty()) {
                Move randomMove = moves.get(random.nextInt(moves.size()));
                board.placePion(randomMove, currentTurn);
            }
            currentTurn = currentTurn.oppose();
            
            // Si l'autre joueur ne peut pas jouer non plus, on re-check GameOver au prochain tour de boucle
        }
        
        int myScore = board.getScore(this.color);
        int oppScore = board.getScore(this.color.oppose());
        return myScore > oppScore;
    }
}
