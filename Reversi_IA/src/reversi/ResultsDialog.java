package reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultsDialog extends JDialog {
    private GameController controller;
    
    public ResultsDialog(JFrame parent, GameController controller, String winner, int blackScore, int whiteScore) {
        super(parent, "Partie Terminée", true);
        this.controller = controller;
        
        setLayout(new BorderLayout());
        setSize(350, 200);
        setLocationRelativeTo(parent);
        
        // Result message
        JLabel resultLabel = new JLabel(winner, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Score display
        JLabel scoreLabel = new JLabel(
            String.format("Noir: %d - Blanc: %d", blackScore, whiteScore), 
            SwingConstants.CENTER
        );
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Rejouer avec les memes config
        JButton playAgainButton = new JButton("Rejouer");
        playAgainButton.setBackground(new Color(70, 130, 180)); // bleu
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        playAgainButton.addActionListener(e -> {
            controller.startNewGame(
                controller.getPlayer1(), 
                controller.getPlayer2()
            );
            dispose();
        });
        // Rejouer avec des nouvelles config
        JButton newGame = new JButton("Nouvelle Partie");
        playAgainButton.setBackground(new Color(70, 130, 180)); // bleu
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        newGame.addActionListener(e -> {
            dispose();
            new PlayerSelectionDialog(parent, controller).setVisible(true);
        });

        buttonPanel.add(playAgainButton);
        buttonPanel.add(newGame);
        
        // Layout
        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(resultLabel);
        contentPanel.add(scoreLabel);
        contentPanel.add(new JLabel()); // Spacer
        contentPanel.add(buttonPanel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
}