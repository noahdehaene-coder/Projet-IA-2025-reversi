package reversi;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau d'information affiché en haut de la fenêtre de jeu.
 * Montre le tour actuel et les scores des joueurs.
 */
public class InfoPanel extends JPanel {

    /** Étiquette affichant quel joueur doit jouer. */
    private JLabel turnLabel;
    
    /** Étiquette affichant les scores actuels. */
    private JLabel scoreLabel;

    /**
     * Constructeur du panneau d'information.
     * Initialise les composants et les dispose verticalement.
     */
    public InfoPanel() {
        // Utilise un BoxLayout vertical pour empiler les étiquettes
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Initialise les étiquettes avec des valeurs par défaut
        turnLabel = new JLabel("Tour :    Noir");
        scoreLabel = new JLabel("Score :    N 2 - B 2");

        // Centre les étiquettes horizontalement
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
        // Ajoute les étiquettes au panneau
        add(turnLabel);
        add(scoreLabel);
    }

    /**
     * Met à jour les informations affichées avec l'état actuel du jeu.
     *
     * @param currentTurn Le joueur dont c'est le tour (NOIR ou BLANC).
     * @param blackScore Le score du joueur noir.
     * @param whiteScore Le score du joueur blanc.
     * @param blackPlayerName Le nom du joueur noir (nom de la classe).
     * @param whitePlayerName Le nom du joueur blanc (nom de la classe).
     */
    public void updateInfo(Couleurcase currentTurn, int blackScore, int whiteScore, String blackPlayerName, String whitePlayerName) {
        // Convertit les noms de classe en noms affichables
        String blackName = getName(blackPlayerName);
        String whiteName = getName(whitePlayerName);
        
        // Détermine le nom du joueur dont c'est le tour
        String currentPlayerName = (currentTurn == Couleurcase.NOIR) ? blackName : whiteName;
        
        // Met à jour le texte des étiquettes
        turnLabel.setText("Tour :    " + (currentTurn == Couleurcase.NOIR ? "Noir" : "Blanc") + " (" + currentPlayerName + ")");
        scoreLabel.setText(String.format("Score :    %s: %d  -  %s: %d", blackName, blackScore, whiteName, whiteScore));
    }
    
    /**
     * Affiche un message de fin de partie (gagnant).
     *
     * @param message Le message à afficher (ex: "Noir gagne!").
     */
    public void showWinner(String message) {
        turnLabel.setText(message);
    }

    /**
     * Convertit un nom de classe en nom lisible pour l'affichage.
     * Utilisé pour afficher des noms compréhensibles plutôt que des noms de classe.
     *
     * @param className Le nom de la classe du joueur.
     * @return Le nom affichable correspondant.
     */
    private String getName(String className) {
        switch (className) {
            case "HumanPlayer": return "Humain";
            case "RandomBot": return "Bot Aléatoire";
            case "BFSBot": return "BFS Bot";
            case "DFSBot" : return "DFS Bot";
            case "DijkstraBot" : return "Dijkstra Bot";
            case "GreedyBFSBot" : return "Greedy BFS Bot";
            case "AstarBot" : return "A* Bot";
            case "AlphaBetaBot": return "AlphaBeta";
            case "MonteCarloBot": return "Monte Carlo";
            case "AlphaBetaBotRapide": return "AlphaBeta Rapide";
            case "DijkstraBotRapide" : return "Dijkstra Bot Rapide";
            default: return className; // Retourne le nom tel quel si non reconnu
        }
    }
}
