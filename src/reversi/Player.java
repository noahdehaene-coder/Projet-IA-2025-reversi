package reversi;

/**
 * Classe abstraite représentant un joueur dans le jeu Reversi.
 * Tous les types de joueurs (humains ou bots) héritent de cette classe.
 * Fournit des fonctionnalités de base communes à tous les joueurs.
 */
public abstract class Player {
    
    /** La couleur des pions du joueur (NOIR ou BLANC). */
    protected Couleurcase color;

    /**
     * Constructeur de la classe Player.
     * Initialise le joueur avec une couleur spécifique.
     *
     * @param color La couleur des pions du joueur (NOIR ou BLANC).
     */
    public Player(Couleurcase color) {
        this.color = color;
    }

    /**
     * Retourne la couleur du joueur.
     *
     * @return La couleur des pions du joueur (NOIR ou BLANC).
     */
    public Couleurcase getColor() {
        return color;
    }
    
    /**
     * Retourne le nom du joueur.
     * Par défaut, retourne le nom simple de la classe.
     *
     * @return Le nom du joueur (ex: "HumanPlayer", "RandomBot", etc.).
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
