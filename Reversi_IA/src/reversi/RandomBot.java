package reversi;

import java.util.List;
import java.util.Random;

public class RandomBot extends BotPlayer {
    
    private Random rand;

    public RandomBot(Couleurcase color) {
        super(color);
        this.rand = new Random();
    }
    
    public Move getMove(ReversiPlateau board) {
        List<Move> validMoves = board.getValidMoves(this.color);
        
        if (validMoves.isEmpty()) {
            return null; // Ne peut pas jouer
        }
        
        // Choisit un coup au hasard dans la liste
        return validMoves.get(rand.nextInt(validMoves.size()));
    }
}
