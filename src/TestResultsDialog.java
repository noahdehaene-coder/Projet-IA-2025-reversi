import javax.swing.*;
import java.awt.*;

/**
 * Dialog showing test results.
 */
public class TestResultsDialog extends JDialog {
    
    public TestResultsDialog(JFrame parent, TestStatistics stats) {
        super(parent, "Résultats des Tests", true);
        
        setLayout(new BorderLayout());
        setSize(500, 800);
        setLocationRelativeTo(parent);
        
        // Title
        JLabel titleLabel = new JLabel("Résultats des Tests", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Results panel
        JPanel resultsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Bot names
        resultsPanel.add(createResultLabel(Tests.getName(stats.bot1Type) + " (Noir) vs " + 
                                          Tests.getName(stats.bot2Type) + " (Blanc)", 
                                          Font.BOLD, 14));
        resultsPanel.add(new JSeparator());
        
        // Number of games
        resultsPanel.add(createResultLabel("Nombre de parties: " + stats.totalGames, Font.PLAIN, 13));
        
        // Wins
        resultsPanel.add(createResultLabel(Tests.getName(stats.bot1Type) + " victoires: " + stats.blackWins, 
                                          Font.PLAIN, 13));
        resultsPanel.add(createResultLabel(Tests.getName(stats.bot2Type) + " victoires: " + stats.whiteWins, 
                                          Font.PLAIN, 13));
        resultsPanel.add(createResultLabel("Égalités: " + stats.draws, Font.PLAIN, 13));
        
        // Win rates
        double blackWinRate = (stats.blackWins * 100.0) / stats.totalGames;
        double whiteWinRate = (stats.whiteWins * 100.0) / stats.totalGames;
        double drawRate = (stats.draws * 100.0) / stats.totalGames;
        
        resultsPanel.add(createResultLabel(
            String.format("Taux de victoire %s: %.1f%%", Tests.getName(stats.bot1Type), blackWinRate),
            Font.PLAIN, 13));
        resultsPanel.add(createResultLabel(
            String.format("Taux de victoire %s: %.1f%%", Tests.getName(stats.bot2Type), whiteWinRate),
            Font.PLAIN, 13));
        resultsPanel.add(createResultLabel(
            String.format("Taux d'égalité: %.1f%%", drawRate),
            Font.PLAIN, 13));
        
        // Time statistics
        resultsPanel.add(new JSeparator());
        resultsPanel.add(createResultLabel("Temps total: " + formatTime(stats.totalTimeMillis), 
                                          Font.PLAIN, 13));
        
        double avgTime = stats.totalTimeMillis / (double) stats.totalGames;
        resultsPanel.add(createResultLabel(
            String.format("Temps moyen par partie: %.0f ms", avgTime),
            Font.PLAIN, 13));
        
        // Winner
        resultsPanel.add(new JSeparator());
        String overallWinner;
        if (stats.blackWins > stats.whiteWins) {
            overallWinner = Tests.getName(stats.bot1Type) + " (Noir) est le gagnant global!";
        } else if (stats.whiteWins > stats.blackWins) {
            overallWinner = Tests.getName(stats.bot2Type) + " (Blanc) est le gagnant global!";
        } else {
            overallWinner = "Match nul global!";
        }
        
        JLabel winnerLabel = new JLabel(overallWinner, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        if (stats.blackWins > stats.whiteWins) {
            winnerLabel.setForeground(new Color(0, 100, 0)); // Green for winner
        } else if (stats.whiteWins > stats.blackWins) {
            winnerLabel.setForeground(new Color(0, 100, 0));
        }
        resultsPanel.add(winnerLabel);
        
        // Close button
        JButton closeButton = new JButton("Fermer");
        closeButton.setBackground(new Color(70, 130, 180));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(closeButton);
        
        // Assembly
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(resultsPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel);
    }
    
    private JLabel createResultLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        return label;
    }
    
    private String formatTime(long millis) {
        if (millis < 1000) {
            return millis + " ms";
        } else if (millis < 60000) {
            return String.format("%.1f secondes", millis / 1000.0);
        } else {
            long minutes = millis / 60000;
            long seconds = (millis % 60000) / 1000;
            return String.format("%d min %d sec", minutes, seconds);
        }
    }
}