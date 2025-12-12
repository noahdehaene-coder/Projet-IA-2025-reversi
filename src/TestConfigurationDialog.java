import javax.swing.*;
import java.awt.*;

/**
 * Dialog for configuring bot vs bot tests.
 */
public class TestConfigurationDialog extends JFrame {
    
    private JComboBox<String> bot1Combo;
    private JComboBox<String> bot2Combo;
    private JSpinner numGamesSpinner;
    private JButton runButton;
    private JTextArea outputArea;
    
    public TestConfigurationDialog() {
        super("Configuration des Tests Bot vs Bot");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(new BorderLayout());
        setSize(700, 450); // Slightly taller for output area
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Title
        JLabel titleLabel = new JLabel("Configuration des Tests Bot vs Bot", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Configuration panel - back to original 3x2 layout
        JPanel configPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Bot 1 selection
        configPanel.add(new JLabel("Bot 1 (Noir):", SwingConstants.RIGHT));
        bot1Combo = new JComboBox<>(new String[]{
            "Bot Aléatoire", "BFS", "DFS", "Dijkstra", 
            "Greedy BFS Bot", "A*", "AlphaBeta", "Monte Carlo", 
            "AlphaBeta Rapide", "Dijkstra Rapide"
        });
        bot1Combo.setFocusable(false); 
        configPanel.add(bot1Combo);
        
        // Bot 2 selection
        configPanel.add(new JLabel("Bot 2 (Blanc):", SwingConstants.RIGHT));
        bot2Combo = new JComboBox<>(new String[]{
            "Bot Aléatoire", "BFS", "DFS", "Dijkstra", 
            "Greedy BFS Bot", "A*", "AlphaBeta", "Monte Carlo", 
            "AlphaBeta Rapide", "Dijkstra Rapide"
        });
        bot2Combo.setSelectedItem("Bot Aléatoire");
        bot2Combo.setFocusable(false);
        configPanel.add(bot2Combo);
        
        // Number of games
        configPanel.add(new JLabel("Nombre de parties:", SwingConstants.RIGHT));
        numGamesSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
        configPanel.add(numGamesSpinner);
        
        // Output area - smaller text area at bottom
        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Progression"));
        scrollPane.setPreferredSize(new Dimension(380, 100));
        
        // Run button - keep original size
        runButton = new JButton("Exécuter les Tests");
        runButton.setBackground(new Color(70, 130, 180));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        runButton.addActionListener(e -> runTests());
        
        // Assembly
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(configPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel);
    }
    
    private void runTests() {
        String bot1Type = (String) bot1Combo.getSelectedItem();
        String bot2Type = (String) bot2Combo.getSelectedItem();
        int numGames = (int) numGamesSpinner.getValue();
        
        // Clear previous output and show starting message
        outputArea.setText("");
        appendToOutput("Démarrage des tests...\n");
        appendToOutput("=======================\n");
        
        runButton.setEnabled(false);
        
        // Run tests in a separate thread to keep UI responsive
        SwingWorker<TestStatistics, Integer> worker = new SwingWorker<>() {
            @Override
            protected TestStatistics doInBackground() throws Exception {
                return Tests.runBotVsBotTests(bot1Type, bot2Type, numGames, outputArea);
            }
            
            @Override
            protected void done() {
                try {
                    TestStatistics stats = get();
                    
                    // Show results dialog
                    new TestResultsDialog(TestConfigurationDialog.this, stats).setVisible(true);
                    
                    runButton.setEnabled(true);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    appendToOutput("\nERREUR: " + e.getMessage());
                    JOptionPane.showMessageDialog(TestConfigurationDialog.this,
                        "Erreur lors de l'exécution des tests: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    
                    runButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Helper method to append text to output area
     */
    private void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            // Auto-scroll to the bottom
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
}