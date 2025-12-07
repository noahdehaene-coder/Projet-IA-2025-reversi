import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList; 

/**
 * Panneau graphique représentant le plateau de jeu Reversi.
 * Gère l'affichage du plateau, les pièces et les indications de coups possibles.
 */
public class BoardPanel extends JPanel {
    
    /** Contrôleur de jeu pour gérer les interactions. */
    private GameController controller;
    
    /** Modèle du plateau contenant l'état actuel du jeu. */
    private ReversiPlateau boardModel;
    
    /** Taille en pixels d'une cellule du plateau. */
    public static final int CELL_SIZE = 80;
    
    /** Liste des coups actuellement disponibles (pour affichage des indications). */
    private List<Move> availableMoves = new ArrayList<>();
    
    /**
     * Constructeur du panneau du plateau.
     *
     * @param controller Le contrôleur de jeu qui gère la logique.
     */
    public BoardPanel(GameController controller) {
        this.controller = controller;
        
        // Définit la taille préférée du panneau (8x8 cellules de CELL_SIZE pixels)
        setPreferredSize(new Dimension(CELL_SIZE * ReversiPlateau.taille, CELL_SIZE * ReversiPlateau.taille));
        
        // Définit la couleur de fond (vert foncé pour simuler un plateau de jeu)
        setBackground(new Color(0, 100, 0));
        
        // Ajoute un écouteur de souris pour détecter les clics sur le plateau
        addMouseListener(new MouseAdapter() {
            
            /**
             * Gère les événements de clic de souris sur le plateau.
             * Convertit les coordonnées pixels en coordonnées de case (ligne, colonne).
             *
             * @param e L'événement de souris contenant les coordonnées du clic.
             */
            public void mousePressed(MouseEvent e) {
                // Conversion des coordonnées : 
                // e.getY() donne la ligne (x) et e.getX() donne la colonne (y).
                // Inversion car dans le modèle, x = ligne, y = colonne
                int x = e.getY() / CELL_SIZE; // Ligne (0-7)
                int y = e.getX() / CELL_SIZE; // Colonne (0-7)
                
                // Transmet le coup au contrôleur
                controller.handleHumanMove(x, y);
            }
        });
    }
    
    /**
     * Définit le modèle de plateau à afficher.
     *
     * @param boardModel Le modèle ReversiPlateau contenant l'état du jeu.
     */
    public void setBoardModel(ReversiPlateau boardModel) {
        this.boardModel = boardModel;
    }
    
    /**
     * Définit la liste des coups disponibles pour l'affichage des indications.
     * Ces coups sont affichés sous forme de cercles gris semi-transparents.
     *
     * @param moves La liste des coups valides pour le joueur actuel.
     */
    public void setAvailableMoves(List<Move> moves) {
        this.availableMoves = moves;
        repaint(); // Force le redessin pour afficher les nouvelles indications
    }
    
    /**
     * Méthode de dessin principale du composant.
     * Dessine la grille, les indications de coups et les pièces.
     *
     * @param g L'objet Graphics utilisé pour le dessin.
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Ne rien dessiner si le modèle n'est pas défini
        if (boardModel == null) return;
        
        // Dessine la grille du plateau (lignes noires)
        g.setColor(Color.BLACK);
        for (int i = 0; i <= ReversiPlateau.taille; i++) {
            // Lignes verticales
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, getHeight());
            
            // Lignes horizontales
            g.drawLine(0, i * CELL_SIZE, getWidth(), i * CELL_SIZE);
        }

        // Dessine les indications des coups disponibles
        g.setColor(new Color(171, 171, 171, 120)); // Gris semi-transparent
        for (Move move : availableMoves) {
            // Dessine un cercle gris à la position du coup possible
            g.fillOval(move.y * CELL_SIZE + 25, move.x * CELL_SIZE + 25, 30, 30);
        }
        
        // Dessine les pièces sur le plateau
        for (int i = 0; i < ReversiPlateau.taille; i++) {
            for (int j = 0; j < ReversiPlateau.taille; j++) {
                Couleurcase disc = boardModel.getEtat(i, j);
                
                if (disc == Couleurcase.NOIR) {
                    // Dessine une pièce noire
                    g.setColor(Color.BLACK);
                    g.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                } else if (disc == Couleurcase.BLANC) {
                    // Dessine une pièce blanche
                    g.setColor(Color.WHITE);
                    g.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }
                // Les cases vides ne sont pas dessinées
            }
        }
    }
}