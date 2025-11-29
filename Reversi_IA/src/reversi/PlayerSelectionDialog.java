package reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerSelectionDialog extends JDialog {
    private GameController controller;
    private JComboBox<String> blackPlayerCombo;
    private JComboBox<String> whitePlayerCombo;
    
    public PlayerSelectionDialog(JFrame parent, GameController controller) {
        super(parent, "Configuration des Joueurs", true);
        this.controller = controller;
        
        setLayout(new BorderLayout());
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Title
        JLabel titleLabel = new JLabel("Choisir les joueurs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // 20px top/bottom
        
        // Player selection panel
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        selectionPanel.add(new JLabel("Joueur Noir:", SwingConstants.CENTER));
        blackPlayerCombo = new JComboBox<>(new String[]{"Humain", "Bot Aléatoire", "BFS", "DFS", "Dijkstra"});
        blackPlayerCombo.setFocusable(false);
        blackPlayerCombo.setSelectedItem("Humain"); // DEFAULT: Human for black
        selectionPanel.add(blackPlayerCombo);
        
        selectionPanel.add(new JLabel("Joueur Blanc:", SwingConstants.CENTER));
        whitePlayerCombo = new JComboBox<>(new String[]{"Humain", "Bot Aléatoire", "BFS", "DFS", "Dijkstra"});
        whitePlayerCombo.setFocusable(false); // Remove focus outline
        whitePlayerCombo.setSelectedItem("Bot Aléatoire"); // DEFAULT: Bot Aleatoire for white
        selectionPanel.add(whitePlayerCombo);
        
        // Start button
        JButton startButton = new JButton("Commencer la Partie");
        startButton.setFocusPainted(false); 
        startButton.addActionListener(e -> startGame());
        
        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(selectionPanel, BorderLayout.CENTER);
        contentPanel.add(startButton, BorderLayout.SOUTH);
        
        add(contentPanel);
    }
    
    private void startGame() {
        // Create players based on selection
        Player blackPlayer = createPlayer(Couleurcase.NOIR, (String) blackPlayerCombo.getSelectedItem());
        Player whitePlayer = createPlayer(Couleurcase.BLANC, (String) whitePlayerCombo.getSelectedItem());
        
        // Start the game
        controller.startNewGame(blackPlayer, whitePlayer);
        dispose();
    }
    
    private Player createPlayer(Couleurcase color, String playerType) {
        if ("Humain".equals(playerType)) 
            return new HumanPlayer(color);
        else if ("Bot Aléatoire".equals(playerType)) 
            return new RandomBot(color);
        else if ("BFS".equals(playerType))
            return new BFSBot(color);
        else if ("DFS".equals(playerType))
            return new DFSBot(color);
        else
            return new DijkstraBot(color);

    }
}