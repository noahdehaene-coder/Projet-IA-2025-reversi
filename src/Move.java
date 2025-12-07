/**
 * Classe immuable représentant un coup dans le jeu Reversi.
 * Un coup est défini par ses coordonnées (x, y) sur le plateau 8x8.
 * Cette classe est utilisée pour passer des informations de position
 * entre les différentes parties du programme.
 */
public class Move {
    
    /** Coordonnée x (ligne) du coup, de 0 à 7. */
    public final int x;
    
    /** Coordonnée y (colonne) du coup, de 0 à 7. */
    public final int y;

    /**
     * Constructeur d'un coup.
     *
     * @param x La coordonnée x (ligne) où jouer le coup.
     * @param y La coordonnée y (colonne) où jouer le coup.
     */
    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }
}