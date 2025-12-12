package reversi;

import java.util.List;
import java.util.Random;

/**
 * Classe représentant un bot qui joue de manière totalement aléatoire.
 * À chaque tour, choisit un coup valide au hasard parmi tous les coups possibles.
 */
public class RandomBot extends BotPlayer {
    
    /** Générateur de nombres aléatoires pour choisir les coups. */
    private Random rand;

    /**
     * Constructeur du bot aléatoire.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public RandomBot(Couleurcase color) {
        super(color);
        this.rand = new Random();
    }
    
    /**
     * Méthode principale pour obtenir le coup aléatoire du bot.
     * Sélectionne un coup valide au hasard dans la liste des coups possibles.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Un coup valide choisi aléatoirement, ou null si aucun coup n'est possible.
     */
    public Move getMove(ReversiPlateau board) {
        // Récupère tous les coups valides pour le joueur actuel
        List<Move> validMoves = board.getValidMoves(this.color);
        
        // Si aucun coup valide, le bot ne peut pas jouer (passe son tour)
        if (validMoves.isEmpty()) {
            return null;
        }
        
        // Choisit un coup au hasard dans la liste
        return validMoves.get(rand.nextInt(validMoves.size()));
    }
}
