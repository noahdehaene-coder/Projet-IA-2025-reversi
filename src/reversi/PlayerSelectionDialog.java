package reversi;

import javax.swing.*;
import java.awt.*;

/**
 * Boîte de dialogue permettant de sélectionner le type de chaque joueur
 * (humain ou différents types de bots) avant de commencer une partie.
 */
public class PlayerSelectionDialog extends JDialog {
    
    /** Contrôleur de jeu pour démarrer la nouvelle partie. */
    private GameController controller;
    
    /** Référence à la fenêtre principale (le plateau) pour pouvoir la fermer. */
    private JFrame parentFrame;
    
    /** Liste déroulante pour sélectionner le joueur noir. */
    private JComboBox<String> blackPlayerCombo;
    
    /** Liste déroulante pour sélectionner le joueur blanc. */
    private JComboBox<String> whitePlayerCombo;
    
    /**
     * Constructeur de la boîte de dialogue de sélection des joueurs.
     * Si l'utilisateur choisit "Simuler plusieurs parties", cette fenêtre
     * et la fenêtre parente (le plateau principal) seront fermées et remplacées
     * par la fenêtre de configuration des tests bot contre bot.
     *
     * @param parent La fenêtre parente (le plateau de jeu) qui sera fermée si l'utilisateur
     *               choisit de lancer des tests au lieu de jouer une partie.
     * @param controller Le contrôleur de jeu pour démarrer la partie.
     */
    public PlayerSelectionDialog(JFrame parent, GameController controller) {
        super(parent, "Configuration des Joueurs", true); // Modale (bloque la fenêtre parente)
        this.controller = controller;
        this.parentFrame = parent; // On sauvegarde la référence du plateau
        
        setLayout(new BorderLayout());
        setSize(300, 320); 
        setLocationRelativeTo(parent); // Centre par rapport à la fenêtre parente
        setResizable(false); // Empêche le redimensionnement
        
        // Titre
        JLabel titleLabel = new JLabel("Choisir les joueurs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Marge de 20px en haut
        
        // Panneau de sélection des joueurs
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Grille 2x2 avec espacement
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marges intérieures
        
        // Configuration pour le joueur noir
        selectionPanel.add(new JLabel("Joueur Noir:", SwingConstants.CENTER));
        blackPlayerCombo = new JComboBox<>(new String[]{
            "Humain", "Bot Aléatoire", "BFS", "DFS", "Dijkstra", 
            "Greedy BFS Bot", "A*", "AlphaBeta", "Monte Carlo", 
            "AlphaBeta Rapide", "Dijkstra Rapide"
        });
        blackPlayerCombo.setFocusable(false); // Désactive le focus visuel
        blackPlayerCombo.setSelectedItem("Humain"); // Valeur par défaut
        selectionPanel.add(blackPlayerCombo);
        
        // Configuration pour le joueur blanc
        selectionPanel.add(new JLabel("Joueur Blanc:", SwingConstants.CENTER));
        whitePlayerCombo = new JComboBox<>(new String[]{
            "Humain", "Bot Aléatoire", "BFS", "DFS", "Dijkstra", 
            "Greedy BFS Bot", "A*", "AlphaBeta", "Monte Carlo", 
            "AlphaBeta Rapide", "Dijkstra Rapide"
        });
        whitePlayerCombo.setFocusable(false); // Désactive le focus visuel
        whitePlayerCombo.setSelectedItem("Bot Aléatoire"); // Valeur par défaut
        selectionPanel.add(whitePlayerCombo);
        
        
        // Bouton Démarrer la partie
        JButton startButton = new JButton("Commencer la Partie");
        startButton.setBackground(new Color(70, 130, 180)); // Bleu
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.addActionListener(e -> startGame());

        // Bouton Simuler plusieurs parties
        JButton simulationButton = new JButton("Simuler plusieurs parties");
        simulationButton.setBackground(new Color(60, 179, 113)); // Vert
        simulationButton.setForeground(Color.WHITE);
        simulationButton.setFocusPainted(false);
        simulationButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        simulationButton.addActionListener(e -> {
            new TestConfigurationDialog().setVisible(true);
            
            dispose();
            
            if (parentFrame != null) {
                parentFrame.dispose();
            }
        });

        // Panneau conteneur pour les boutons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonsPanel.add(startButton);
        buttonsPanel.add(simulationButton);
        
        // Assemblage du contenu
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(selectionPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(contentPanel);
    }
    
    /**
     * Démarre une nouvelle partie avec les joueurs sélectionnés.
     */
    private void startGame() {
        Player blackPlayer = createPlayer(Couleurcase.NOIR, (String) blackPlayerCombo.getSelectedItem());
        Player whitePlayer = createPlayer(Couleurcase.BLANC, (String) whitePlayerCombo.getSelectedItem());
        
        controller.startNewGame(blackPlayer, whitePlayer);
        dispose();
    }
    
    /**
     * Crée une instance de joueur selon le type sélectionné.
     *
     * @param color La couleur du joueur à créer (NOIR ou BLANC).
     * @param playerType Le type de joueur sélectionné (chaîne descriptive).
     * @return Une instance de Player correspondante.
     */
    private Player createPlayer(Couleurcase color, String playerType) {
        switch (playerType) {
            case "Humain": return new HumanPlayer(color);
            case "Bot Aléatoire": return new RandomBot(color);
            case "BFS": return new BFSBot(color);
            case "DFS": return new DFSBot(color);
            case "Dijkstra": return new DijkstraBot(color);
            case "Greedy BFS Bot": return new GreedyBFSBot(color);
            case "A*": return new AstarBot(color);
            case "AlphaBeta": return new AlphaBetaBot(color, 8);
            case "Monte Carlo": return new MonteCarloBot(color);
            case "AlphaBeta Rapide": return new AlphaBetaBotRapide(color, 8);
            case "Dijkstra Rapide": return new DijkstraBotRapide(color);
            default: return new RandomBot(color);
        }
    }
}
