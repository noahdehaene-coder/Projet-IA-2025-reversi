package reversi;

import javax.swing.*;
import java.awt.*;

/**
 * Boîte de dialogue affichée à la fin d'une partie de Reversi.
 * Montre les résultats (gagnant, scores) et propose des options pour rejouer.
 */
public class ResultsDialog extends JDialog {
    
    /**
     * Constructeur de la boîte de dialogue des résultats.
     *
     * @param parent La fenêtre parente (pour centrage).
     * @param controller Le contrôleur de jeu pour gérer les actions.
     * @param winner Le message de résultat (ex: "Noir gagne!").
     * @param blackScore Score final du joueur noir.
     * @param whiteScore Score final du joueur blanc.
     * @param blackPlayerName Nom du joueur noir (nom de la classe).
     * @param whitePlayerName Nom du joueur blanc (nom de la classe).
     */
    public ResultsDialog(JFrame parent, GameController controller, String winner, 
                         int blackScore, int whiteScore, 
                         String blackPlayerName, String whitePlayerName) {
        super(parent, "Partie Terminée", true); // Modale (bloque la fenêtre parente)

        // Convertit les noms de classe en noms affichables
        String blackName = getName(blackPlayerName);
        String whiteName = getName(whitePlayerName);
        
        setLayout(new BorderLayout());
        setSize(350, 200);
        setLocationRelativeTo(parent); // Centre par rapport à la fenêtre parente
        
        // Étiquette du résultat (gagnant)
        JLabel resultLabel = new JLabel(winner, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Étiquette des scores finaux
        JLabel scoreLabel = new JLabel(
            String.format("%s: %d - %s: %d", blackName, blackScore, whiteName, whiteScore), 
            SwingConstants.CENTER
        );
        
        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Deux boutons côte à côte

        // Bouton "Rejouer" (mêmes joueurs, même configuration)
        JButton playAgainButton = new JButton("Rejouer");
        playAgainButton.setBackground(new Color(70, 130, 180)); // Bleu 
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false); // Enlève le contour de focus
        playAgainButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Marge intérieure
        playAgainButton.addActionListener(e -> {
            // Redémarre une partie avec les mêmes joueurs
            controller.startNewGame(
                controller.getPlayer1(), 
                controller.getPlayer2()
            );
            dispose(); // Ferme la boîte de dialogue
        });
        
        // Bouton "Nouvelle Partie" (choisir de nouveaux joueurs)
        JButton newGameButton = new JButton("Nouvelle Partie");
        newGameButton.setBackground(new Color(70, 130, 180)); // Bleu
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        newGameButton.addActionListener(e -> {
            dispose(); // Ferme cette boîte de dialogue
            // Ouvre la boîte de dialogue de sélection des joueurs
            new PlayerSelectionDialog(parent, controller).setVisible(true);
        });

        // Ajoute les boutons au panneau
        buttonPanel.add(playAgainButton);
        buttonPanel.add(newGameButton);
        
        // Panneau principal de contenu
        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 lignes, 1 colonne
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marges intérieures
        contentPanel.add(resultLabel);
        contentPanel.add(scoreLabel);
        contentPanel.add(new JLabel()); // Espaceur vide
        contentPanel.add(buttonPanel);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Convertit un nom de classe en nom lisible pour l'affichage.
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
