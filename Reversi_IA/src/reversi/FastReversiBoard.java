package reversi;

public class FastReversiBoard {
    public long black;
    public long white;

    public FastReversiBoard(long black, long white) {
        this.black = black;
        this.white = white;
    }

    // Convertit le plateau lent (ReversiPlateau) en plateau rapide (bits)
    public FastReversiBoard(ReversiPlateau p) {
        black = 0L;
        white = 0L;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (p.getEtat(i, j) == Couleurcase.NOIR) black |= (1L << (i * 8 + j));
                else if (p.getEtat(i, j) == Couleurcase.BLANC) white |= (1L << (i * 8 + j));
            }
        }
    }

    public FastReversiBoard copy() {
        return new FastReversiBoard(black, white);
    }

    // Récupère les coups possibles sous forme de bits
    public long getValidMovesBitmask(boolean blackTurn) {
        long my = blackTurn ? black : white;
        long opp = blackTurn ? white : black;
        long empty = ~(my | opp);
        long moves = 0L;

        // Masques pour éviter de sortir du plateau (bord Gauche et bord Droit)
        long notA = 0xFEFEFEFEFEFEFEFEL; // Tout sauf colonne A
        long notH = 0x7F7F7F7F7F7F7F7FL; // Tout sauf colonne H

        // 8 directions : Est, Ouest, Sud, Nord, et diagonales
        // On doit appliquer les masques AVANT de décaler
        
        // Vers la Droite (+1) : nécessite masque notH (on ne déborde pas vers A)
        long candidates = (my & notH) << 1 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notH) << 1 & opp;
        moves |= (candidates & notH) << 1 & empty;

        // Vers la Gauche (-1) : nécessite masque notA
        candidates = (my & notA) >>> 1 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notA) >>> 1 & opp;
        moves |= (candidates & notA) >>> 1 & empty;

        // Vers le Bas (+8) : pas de souci horizontal
        candidates = (my << 8) & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates << 8) & opp;
        moves |= (candidates << 8) & empty;

        // Vers le Haut (-8)
        candidates = (my >>> 8) & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates >>> 8) & opp;
        moves |= (candidates >>> 8) & empty;

        // Diagonale Bas-Droite (+9) : nécessite notH
        candidates = (my & notH) << 9 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notH) << 9 & opp;
        moves |= (candidates & notH) << 9 & empty;

        // Diagonale Haut-Gauche (-9) : nécessite notA
        candidates = (my & notA) >>> 9 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notA) >>> 9 & opp;
        moves |= (candidates & notA) >>> 9 & empty;

        // Diagonale Bas-Gauche (+7) : nécessite notA
        candidates = (my & notA) << 7 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notA) << 7 & opp;
        moves |= (candidates & notA) << 7 & empty;

        // Diagonale Haut-Droite (-7) : nécessite notH
        candidates = (my & notH) >>> 7 & opp;
        for (int i = 0; i < 6; i++) candidates |= (candidates & notH) >>> 7 & opp;
        moves |= (candidates & notH) >>> 7 & empty;

        return moves;
    }

    // Applique un coup (modifie l'état actuel)
    public void makeMove(int x, int y, boolean blackTurn) {
        long move = 1L << (x * 8 + y);
        long my = blackTurn ? black : white;
        long opp = blackTurn ? white : black;
        long flipped = 0L;

        long[] dirs = {1, 8, 7, 9};
        for (long dir : dirs) {
            // Direction positive
            long mask = 0L;
            long runner = move << dir;
            while ((runner & opp) != 0) {
                mask |= runner;
                runner <<= dir;
            }
            if ((runner & my) != 0) flipped |= mask;

            // Direction négative
            mask = 0L;
            runner = move >>> dir;
            while ((runner & opp) != 0) {
                mask |= runner;
                runner >>>= dir;
            }
            if ((runner & my) != 0) flipped |= mask;
        }

        if (blackTurn) {
            black |= (move | flipped);
            white &= ~flipped;
        } else {
            white |= (move | flipped);
            black &= ~flipped;
        }
    }
}