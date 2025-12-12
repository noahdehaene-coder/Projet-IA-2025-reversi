package reversi;

/**
 * Classe optimisée pour représenter un plateau de Reversi en utilisant
 * des masques binaires (bitboards) pour des opérations rapides.
 * Utilise deux long (64 bits) : un pour les pions noirs, un pour les pions blancs.
 * Chaque bit représente une case (1 = occupée par la couleur, 0 = vide ou autre couleur).
 */
public class FastReversiBoard {
    /** Masque binaire des pions noirs. Chaque bit à 1 représente une case occupée par un pion noir. */
    public long black;
    
    /** Masque binaire des pions blancs. Chaque bit à 1 représente une case occupée par un pion blanc. */
    public long white;

    /**
     * Constructeur direct avec masques binaires.
     *
     * @param black Masque binaire des pions noirs.
     * @param white Masque binaire des pions blancs.
     */
    public FastReversiBoard(long black, long white) {
        this.black = black;
        this.white = white;
    }

    /**
     * Convertit un plateau ReversiPlateau standard en représentation binaire optimisée.
     *
     * @param p Le plateau ReversiPlateau à convertir.
     */
    public FastReversiBoard(ReversiPlateau p) {
        black = 0L;
        white = 0L;
        
        // Parcourt toutes les cases du plateau 8x8
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Calcule l'index du bit (0-63)
                int bitIndex = i * 8 + j;
                
                // Met à jour les masques selon la couleur de la case
                if (p.getEtat(i, j) == Couleurcase.NOIR) {
                    black |= (1L << bitIndex);  // Met le bit correspondant à 1 pour les noirs
                } else if (p.getEtat(i, j) == Couleurcase.BLANC) {
                    white |= (1L << bitIndex);  // Met le bit correspondant à 1 pour les blancs
                }
            }
        }
    }

    /**
     * Crée une copie profonde de ce plateau.
     *
     * @return Une nouvelle instance avec les mêmes masques binaires.
     */
    public FastReversiBoard copy() {
        return new FastReversiBoard(black, white);
    }

    /**
     * Calcule les coups valides pour un joueur donné sous forme de masque binaire.
     * Chaque bit à 1 dans le résultat représente une case où un coup est possible.
     *
     * @param blackTurn true si c'est le tour des noirs, false pour les blancs.
     * @return Masque binaire des coups valides (bits à 1 = coups possibles).
     */
    public long getValidMovesBitmask(boolean blackTurn) {
        // Détermine les masques du joueur courant et de l'adversaire
        long my = blackTurn ? black : white;
        long opp = blackTurn ? white : black;
        
        // Masque des cases vides (ni noires ni blanches)
        long empty = ~(my | opp);
        long moves = 0L;

        // Masques pour éviter le débordement horizontal (sortie du plateau)
        long notA = 0xFEFEFEFEFEFEFEFEL; // Tous les bits sauf ceux de la colonne A (colonne de gauche)
        long notH = 0x7F7F7F7F7F7F7F7FL; // Tous les bits sauf ceux de la colonne H (colonne de droite)

        // 8 directions de recherche : Est, Ouest, Sud, Nord, et 4 diagonales
        // Pour chaque direction, on applique des masques pour éviter les débordements
        
        // 1. Vers la Droite (Est, +1) : nécessite masque notH (ne pas déborder vers la colonne A)
        long candidates = (my & notH) << 1 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notH) << 1 & opp;
        moves |= (candidates & notH) << 1 & empty;

        // 2. Vers la Gauche (Ouest, -1) : nécessite masque notA
        candidates = (my & notA) >>> 1 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notA) >>> 1 & opp;
        moves |= (candidates & notA) >>> 1 & empty;

        // 3. Vers le Bas (Sud, +8) : pas de problème horizontal, seulement vertical
        candidates = (my << 8) & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates << 8) & opp;
        moves |= (candidates << 8) & empty;

        // 4. Vers le Haut (Nord, -8)
        candidates = (my >>> 8) & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates >>> 8) & opp;
        moves |= (candidates >>> 8) & empty;

        // 5. Diagonale Bas-Droite (Sud-Est, +9) : nécessite notH
        candidates = (my & notH) << 9 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notH) << 9 & opp;
        moves |= (candidates & notH) << 9 & empty;

        // 6. Diagonale Haut-Gauche (Nord-Ouest, -9) : nécessite notA
        candidates = (my & notA) >>> 9 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notA) >>> 9 & opp;
        moves |= (candidates & notA) >>> 9 & empty;

        // 7. Diagonale Bas-Gauche (Sud-Ouest, +7) : nécessite notA
        candidates = (my & notA) << 7 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notA) << 7 & opp;
        moves |= (candidates & notA) << 7 & empty;

        // 8. Diagonale Haut-Droite (Nord-Est, -7) : nécessite notH
        candidates = (my & notH) >>> 7 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notH) >>> 7 & opp;
        moves |= (candidates & notH) >>> 7 & empty;

        return moves;
    }

    /**
     * Applique un coup sur le plateau (modifie l'état actuel).
     * Place un pion et retourne tous les pions adverses capturés.
     *
     * @param x Coordonnée x (ligne) du coup (0-7).
     * @param y Coordonnée y (colonne) du coup (0-7).
     * @param blackTurn true si c'est le tour des noirs, false pour les blancs.
     */
    public void makeMove(int x, int y, boolean blackTurn) {
        // Crée le masque pour la case jouée
        long move = 1L << (x * 8 + y);
        
        // Détermine les masques du joueur courant et de l'adversaire
        long my = blackTurn ? black : white;
        long opp = blackTurn ? white : black;
        long flipped = 0L; // Masque des pions à retourner

        // Directions de recherche (4 directions principales, traitées en positif et négatif)
        long[] dirs = {1, 8, 7, 9}; // Est, Sud, Sud-Ouest, Sud-Est
        
        for (long dir : dirs) {
            // Direction positive (par exemple, vers la droite ou le bas)
            long mask = 0L;
            long runner = move << dir; // "Coureur" qui se déplace dans la direction
            
            // Tant qu'on rencontre des pions adverses, on les marque comme potentiellement retournables
            while ((runner & opp) != 0) {
                mask |= runner;
                runner <<= dir;
            }
            // Si on termine sur un pion allié, tous les pions marqués sont retournables
            if ((runner & my) != 0) flipped |= mask;

            // Direction négative (opposée)
            mask = 0L;
            runner = move >>> dir;
            
            while ((runner & opp) != 0) {
                mask |= runner;
                runner >>>= dir;
            }
            if ((runner & my) != 0) flipped |= mask;
        }

        // Applique les modifications selon la couleur du joueur
        if (blackTurn) {
            black |= (move | flipped);  // Ajoute le nouveau pion et les pions retournés
            white &= ~flipped;          // Retire les pions retournés des blancs
        } else {
            white |= (move | flipped);
            black &= ~flipped;
        }
    }
}
