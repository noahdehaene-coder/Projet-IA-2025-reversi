package reversi;

import java.util.ArrayList;
import java.util.List;

public class ReversiPlateau{
    public static final int taille = 8;
    private Couleurcase[][] plateau;
    
    public ReversiPlateau(){
        plateau = new Couleurcase[taille][taille];
    }
    
    public void initialisation(){
        for (int i=0; i<taille; i++){
            for (int j=0; j<taille; j++){
                plateau[i][j]=Couleurcase.VIDE;
            }
        }
        
        plateau[3][3]=Couleurcase.BLANC;
        plateau[3][4]=Couleurcase.NOIR;
        plateau[4][3]=Couleurcase.NOIR;
        plateau[4][4]=Couleurcase.BLANC;
    }
    
    public Couleurcase getEtat(int x, int y){
        return plateau[x][y];
    }
    
    public boolean isMoveValid(Move move, Couleurcase couleurcase) {
        
        // Règle 1 : La case doit être vide et dans les limites du plateau.
        if (!isWithinBounds(move.x, move.y) || plateau[move.x][move.y] != Couleurcase.VIDE) {
            return false;
        }

        Couleurcase couleurOppose = couleurcase.oppose();
        
        // Définir les 8 directions (dx, dy)
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Haut, Bas, Gauche, Droite
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonales
        };

        boolean isMoveValidInAnyDirection = false;

        // Règle 2 : Vérifier dans les 8 directions si on "encadre" au moins un pion adverse.
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            int currentX = move.x + dx;
            int currentY = move.y + dy;

            boolean OpposeTrouve = false; // A-t-on trouvé au moins 1 pion adverse ?

            // Avancer dans cette direction...
            while (isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurOppose) {
                // ...tant qu'on est sur le plateau et qu'on rencontre des pions adverses.
                OpposeTrouve = true;
                currentX += dx;
                currentY += dy;
            }

            // Après la boucle, on a 3 cas :
            // 1. On est sorti du plateau (currentX/Y hors limites)
            // 2. On a trouvé une case vide (board[...][...] == EMPTY)
            // 3. On a trouvé un pion de notre couleur (board[...][...] == playerColor)

            // Le coup est valide si on a trouvé au moins un pion adverse (foundOpponent == true)
            // ET si la ligne de pions adverses se termine par un pion de notre couleur.
            if (OpposeTrouve && isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurcase) {
                // Ce coup est valide. Inutile de vérifier les autres directions.
                return true;
            }
        }

        // Si, après avoir vérifié les 8 directions, aucune n'était valide
        return false;
    }

    
    public void placePion(Move move, Couleurcase couleurcase) {
        // 1. Placer le pion du joueur
        plateau[move.x][move.y] = couleurcase;

        Couleurcase couleurOppose = couleurcase.oppose();

        // 2. Définir les 8 directions (dx, dy)
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Haut, Bas, Gauche, Droite
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonales
        };

        // 3. Parcourir chaque direction pour trouver des pions à retourner
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            int currentX = move.x + dx;
            int currentY = move.y + dy;

            boolean OpposeTrouve = false;

            // Avancer dans cette direction tant qu'on trouve des pions adverses
            while (isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurOppose) {
                OpposeTrouve = true;
                currentX += dx;
                currentY += dy;
            }

            // Si on a trouvé au moins un pion adverse ET que la ligne se termine
            // par un pion de notre couleur (on a "pris en sandwich")
            if (OpposeTrouve && isWithinBounds(currentX, currentY) && plateau[currentX][currentY] == couleurcase) {
                
                // 4. Remonter la ligne en arrière pour retourner les pions
                int flipX = move.x + dx;
                int flipY = move.y + dy;

                // On s'arrête juste avant d'atteindre le pion de notre couleur
                while (flipX != currentX || flipY != currentY) {
                    plateau[flipX][flipY] = couleurcase; // Retourner le pion
                    flipX += dx;
                    flipY += dy;
                }
            }
        }
    }
    
    public List<Move> getValidMoves(Couleurcase couleurcase) {
        List<Move> validMoves = new ArrayList<>();
        
        // Parcourir toutes les cases du plateau
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                
                Move potentialMove = new Move(i, j);
                
                // Si le coup (i, j) est valide, l'ajouter à la liste
                if (isMoveValid(potentialMove, couleurcase)) {
                    validMoves.add(potentialMove);
                }
            }
        }
        return validMoves;
    }
    
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < taille && y >= 0 && y < taille;
    }

    
    public int getScore(Couleurcase couleurcase){
        int Score = 0;
        for (int i=0; i<taille; i++){
            for (int j=0; j<taille; j++){
                if (plateau[i][j]==couleurcase) Score++;
            }
        }
        return Score;
    }
    
    public boolean GameOver(){
        return getValidMoves(Couleurcase.NOIR).isEmpty() && getValidMoves(Couleurcase.BLANC).isEmpty();
    }
    
    public ReversiPlateau copy() {
        
        ReversiPlateau newBoard = new ReversiPlateau();
        
        
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                // Copie la valeur (BLACK, WHITE, ou EMPTY)
                newBoard.plateau[i][j] = this.plateau[i][j];
            }
        }
        
        
        return newBoard;
    }

}
