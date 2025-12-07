/**
 * Énumération représentant les états possibles d'une case sur le plateau de Reversi.
 * Chaque case peut être vide, occupée par un pion blanc, ou occupée par un pion noir.
 */
public enum Couleurcase {
    
    /** Case vide (sans pion). */
    VIDE,
    
    /** Case occupée par un pion blanc. */
    BLANC,
    
    /** Case occupée par un pion noir. */
    NOIR;
    
    /**
     * Retourne la couleur opposée à la couleur actuelle.
     * Utile pour déterminer le joueur adverse.
     * 
     * @return La couleur opposée : 
     *         - BLANC → NOIR
     *         - NOIR → BLANC
     *         - VIDE → VIDE (aucune opposition pour une case vide)
     */
    public Couleurcase oppose() {
        if (this == BLANC) {
            return NOIR;
        }
        else if (this == NOIR) {
            return BLANC;
        }
        return VIDE; // Une case vide n'a pas d'opposé
    }
}