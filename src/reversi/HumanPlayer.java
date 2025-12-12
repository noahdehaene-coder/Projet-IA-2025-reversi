package reversi;

/**
 * Classe représentant un joueur humain dans le jeu Reversi.
 * Hérite de la classe Player et n'ajoute pas de logique supplémentaire,
 * car les coups humains sont gérés via l'interface graphique et le GameController.
 */
public class HumanPlayer extends Player {
    
    /**
     * Constructeur du joueur humain.
     * Initialise le joueur avec une couleur spécifique (NOIR ou BLANC).
     *
     * @param color La couleur des pions du joueur humain.
     */
    public HumanPlayer(Couleurcase color) {
        super(color);
    }
}
