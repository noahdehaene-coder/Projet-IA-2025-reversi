import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Fenêtre principale de l'application Reversi.
 * Contient l'interface graphique complète du jeu, organisée en deux parties :
 * - InfoPanel : en haut, affiche les informations du jeu (scores, tour actuel)
 * - BoardPanel : au centre, affiche le plateau de jeu interactif
 */
public class GameFrame extends JFrame {
    
    /** Panneau affichant le plateau de jeu et gérant les interactions. */
    private BoardPanel boardPanel;
    
    /** Panneau affichant les informations du jeu (scores, tour actuel, etc.). */
    private InfoPanel infoPanel;
    
    /**
     * Constructeur de la fenêtre principale du jeu.
     * Initialise les composants graphiques et les arrange dans la fenêtre.
     *
     * @param controller Le contrôleur de jeu qui gère la logique.
     */
    public GameFrame(GameController controller) {
        setTitle("Reversi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ferme l'application quand la fenêtre est fermée
        setLayout(new BorderLayout()); // Utilise un BorderLayout pour organiser les composants
        
        // Crée et ajoute le panneau d'informations en haut de la fenêtre
        infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.NORTH);
        
        // Crée et ajoute le panneau du plateau au centre de la fenêtre
        boardPanel = new BoardPanel(controller);
        add(boardPanel, BorderLayout.CENTER);
        
        // Ajuste la taille de la fenêtre pour contenir tous les composants
        pack();
        
        // Centre la fenêtre sur l'écran
        setLocationRelativeTo(null);
        
        // Empêche le redimensionnement de la fenêtre pour une expérience cohérente
        setResizable(false);
    }
    
    /**
     * Retourne le panneau du plateau de jeu.
     *
     * @return L'instance de BoardPanel.
     */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
    
    /**
     * Retourne le panneau d'informations.
     *
     * @return L'instance de InfoPanel.
     */
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
}