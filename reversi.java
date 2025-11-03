import java.util.ArrayList;
import java.util.List;

public enum Couleurcase{
    VIDE,
    BLANC,
    NOIR;
    
    public Couleurcase oppose(){
        if (this == BLANC){
            return NOIR;
        }
        else if (this == NOIR){
            return BLANC;
        }
        return VIDE;
    }
}

public class Move {
    public final int x;
    public final int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }
}


public class ReversiPlateau{
    public static final int taille 8;
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
    
    //coller la méthode case_disponible
    
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
            while (isWithinBounds(currentX, currentY) && board[currentX][currentY] == couleurOppose) {
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
    
    public int getScore(Couleurcase couleurcase){
        int Score = 0;
        for (int i=0; i<taille; i++){
            for (int j=0; j<taille; j++){
                if (plateau[i][j]=couleurcase) Score++;
            }
        }
        return Score;
    }
    
    public boolean GameOver(){
        return getValidMoves(Couleurcase.NOIR).isEmpty() && getValidMoves(Couleurcase.BLANC).isEmpty()
    }

}