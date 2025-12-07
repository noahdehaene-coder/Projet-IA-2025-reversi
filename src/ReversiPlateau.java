import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant le plateau de jeu Reversi (Othello).
 * Gère l'état du jeu, la logique des coups, les règles et les vérifications.
 */
public class ReversiPlateau {
    
    /** Taille du plateau (8x8 pour le Reversi standard). */
    public static final int taille = 8;
    
    /** Tableau 2D représentant l'état du plateau. */
    private Couleurcase[][] plateau;
    
    /**
     * Constructeur du plateau. Crée un plateau 8x8 vide.
     */
    public ReversiPlateau() {
        plateau = new Couleurcase[taille][taille];
    }
    
    /**
     * Initialise le plateau avec la configuration de départ du Reversi.
     * 4 pions au centre dans la configuration croisée standard.
     */
    public void initialisation() {
        // Initialise toutes les cases comme vides
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                plateau[i][j] = Couleurcase.VIDE;
            }
        }
        
        // Place les 4 pions initiaux au centre
        plateau[3][3] = Couleurcase.BLANC;
        plateau[3][4] = Couleurcase.NOIR;
        plateau[4][3] = Couleurcase.NOIR;
        plateau[4][4] = Couleurcase.BLANC;
    }
    
    /**
     * Retourne l'état d'une case spécifique du plateau.
     *
     * @param x Coordonnée x (ligne) de la case.
     * @param y Coordonnée y (colonne) de la case.
     * @return La couleur de la case (VIDE, BLANC, ou NOIR).
     */
    public Couleurcase getEtat(int x, int y) {
        return plateau[x][y];
    }
    
    /**
     * Vérifie si un coup est valide pour un joueur donné.
     * Un coup est valide s'il respecte les règles du Reversi :
     * 1. La case doit être vide
     * 2. Doit "encadrer" au moins une ligne de pions adverses entre deux pions du joueur
     *
     * @param move Le coup à vérifier.
     * @param couleurcase La couleur du joueur qui veut jouer.
     * @return true si le coup est valide, false sinon.
     */
    public boolean isMoveValid(Move move, Couleurcase couleurcase) {
        
        // Règle 1 : La case doit être vide et dans les limites du plateau.
        if (!isWithinBounds(move.x, move.y) || plateau[move.x][move.y] != Couleurcase.VIDE) {
            return false;
        }

        Couleurcase couleurOppose = couleurcase.oppose();
        
        // Définit les 8 directions (dx, dy)
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Haut, Bas, Gauche, Droite
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonales
        };

        // Règle 2 : Vérifie dans les 8 directions si on "encadre" au moins un pion adverse.
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            int currentX = move.x + dx;
            int currentY = move.y + dy;

            boolean OpposeTrouve = false; // Indique si au moins un pion adverse a été trouvé dans cette direction

            // Avance dans cette direction tant qu'on est sur le plateau et qu'on rencontre des pions adverses.
            while (isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurOppose) {
                OpposeTrouve = true;
                currentX += dx;
                currentY += dy;
            }

            // Après la boucle, on a 3 cas :
            // 1. On est sorti du plateau (currentX/Y hors limites)
            // 2. On a trouvé une case vide
            // 3. On a trouvé un pion de notre couleur

            // Le coup est valide si on a trouvé au moins un pion adverse
            // et si la ligne de pions adverses se termine par un pion de notre couleur.
            if (OpposeTrouve && isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurcase) {
                // Ce coup est valide. Inutile de vérifier les autres directions.
                return true;
            }
        }

        // Si aucune direction n'est valide
        return false;
    }

    
    /**
     * Place un pion sur le plateau et retourne tous les pions adverses capturés.
     * Applique les règles du Reversi pour retourner les pions.
     *
     * @param move Le coup à jouer.
     * @param couleurcase La couleur du joueur qui joue.
     */
    public void placePion(Move move, Couleurcase couleurcase) {
        // 1. Place le pion du joueur
        plateau[move.x][move.y] = couleurcase;

        Couleurcase couleurOppose = couleurcase.oppose();

        // 2. Définit les 8 directions (dx, dy)
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Haut, Bas, Gauche, Droite
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonales
        };

        // 3. Parcourt chaque direction pour trouver des pions à retourner
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            int currentX = move.x + dx;
            int currentY = move.y + dy;

            boolean OpposeTrouve = false;

            // Avance dans cette direction tant qu'on trouve des pions adverses
            while (isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurOppose) {
                OpposeTrouve = true;
                currentX += dx;
                currentY += dy;
            }

            // Si on a trouvé au moins un pion adverse et que la ligne se termine
            // par un pion de notre couleur (on a "pris en sandwich")
            if (OpposeTrouve && isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurcase) {
                
                // 4. Remonte la ligne en arrière pour retourner les pions
                int flipX = move.x + dx;
                int flipY = move.y + dy;

                // S'arrête juste avant d'atteindre le pion de notre couleur
                while (flipX != currentX || flipY != currentY) {
                    plateau[flipX][flipY] = couleurcase; // Retourne le pion
                    flipX += dx;
                    flipY += dy;
                }
            }
        }
    }
    
    /**
     * Retourne la liste de tous les coups valides pour un joueur donné.
     *
     * @param couleurcase La couleur du joueur.
     * @return Une liste de Move représentant tous les coups valides.
     */
    public List<Move> getValidMoves(Couleurcase couleurcase) {
        List<Move> validMoves = new ArrayList<>();
        
        // Parcourt toutes les cases du plateau
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                
                Move potentialMove = new Move(i, j);
                
                // Si le coup (i, j) est valide, l'ajoute à la liste
                if (isMoveValid(potentialMove, couleurcase)) {
                    validMoves.add(potentialMove);
                }
            }
        }
        return validMoves;
    }
    
    /**
     * Vérifie si des coordonnées sont dans les limites du plateau.
     *
     * @param x Coordonnée x (ligne).
     * @param y Coordonnée y (colonne).
     * @return true si les coordonnées sont valides, false sinon.
     */
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < taille && y >= 0 && y < taille;
    }

    
    /**
     * Calcule le score (nombre de pions) pour un joueur donné.
     *
     * @param couleurcase La couleur du joueur.
     * @return Le nombre de pions de cette couleur sur le plateau.
     */
    public int getScore(Couleurcase couleurcase) {
        int Score = 0;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (plateau[i][j] == couleurcase) Score++;
            }
        }
        return Score;
    }
    
    /**
     * Vérifie si la partie est terminée.
     * La partie est terminée quand aucun joueur ne peut jouer de coup valide.
     *
     * @return true si la partie est terminée, false sinon.
     */
    public boolean GameOver() {
        return getValidMoves(Couleurcase.NOIR).isEmpty() && getValidMoves(Couleurcase.BLANC).isEmpty();
    }
    
    /**
     * Crée une copie profonde de ce plateau.
     * Utile pour les simulations et les algorithmes qui explorent des états futurs.
     *
     * @return Une nouvelle instance de ReversiPlateau avec le même état.
     */
    public ReversiPlateau copy() {
        
        ReversiPlateau newBoard = new ReversiPlateau();
        
        // Copie toutes les cases
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                // Copie la valeur (BLANC, NOIR, ou VIDE)
                newBoard.plateau[i][j] = this.plateau[i][j];
            }
        }
        
        return newBoard;
    }
}