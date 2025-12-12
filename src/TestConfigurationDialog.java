import javax.swing.*;
import java.awt.*;

/**
 * Boîte de dialogue de configuration pour les tests bot contre bot.
 * Permet de sélectionner les types de bots, le nombre de parties, et de lancer les tests.
 */
public class TestConfigurationDialog extends JFrame {
    
    /** Combo box pour sélectionner le premier type de bot (joueur noir) */
    private JComboBox<String> bot1Combo;
    
    /** Combo box pour sélectionner le second type de bot (joueur blanc) */
    private JComboBox<String> bot2Combo;
    
    /** Spinner pour sélectionner le nombre de parties à exécuter */
    private JSpinner numGamesSpinner;
    
    /** Bouton pour lancer l'exécution des tests */
    private JButton runButton;
    
    /** Zone de texte pour afficher la progression des tests */
    private JTextArea outputArea;
    
    /**
     * Constructeur de la boîte de dialogue de configuration.
     * Initialise l'interface utilisateur avec les contrôles de sélection.
     */
    public TestConfigurationDialog() {
        super("Configuration des Tests Bot vs Bot");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(new BorderLayout());
        setSize(700, 450); // Légèrement plus haute pour la zone de sortie
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Titre
        JLabel titleLabel = new JLabel("Configuration des Tests Bot vs Bot", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Panneau de configuration
        JPanel configPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Sélection Bot 1
        configPanel.add(new JLabel("Bot 1 (Noir):", SwingConstants.RIGHT));
        bot1Combo = new JComboBox<>(new String[]{
            "Bot Aléatoire", "BFS", "DFS", "Dijkstra", 
            "Greedy BFS Bot", "A*", "AlphaBeta", "Monte Carlo", 
            "AlphaBeta Rapide", "Dijkstra Rapide"
        });
        bot1Combo.setFocusable(false); 
        configPanel.add(bot1Combo);
        
        // Sélection Bot 2
        configPanel.add(new JLabel("Bot 2 (Blanc):", SwingConstants.RIGHT));
        bot2Combo = new JComboBox<>(new String[]{
            "Bot Aléatoire", "BFS", "DFS", "Dijkstra", 
            "Greedy BFS Bot", "A*", "AlphaBeta", "Monte Carlo", 
            "AlphaBeta Rapide", "Dijkstra Rapide"
        });
        bot2Combo.setSelectedItem("Bot Aléatoire");
        bot2Combo.setFocusable(false);
        configPanel.add(bot2Combo);
        
        // Nombre de parties
        configPanel.add(new JLabel("Nombre de parties:", SwingConstants.RIGHT));
        numGamesSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
        configPanel.add(numGamesSpinner);
        
        // Zone de sortie - zone de texte plus petite en bas
        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Progression"));
        scrollPane.setPreferredSize(new Dimension(380, 100));
        
        // Bouton Exécuter
        runButton = new JButton("Exécuter les Tests");
        runButton.setBackground(new Color(70, 130, 180));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        runButton.addActionListener(e -> runTests());
        
        // Tout mettre ensemble
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
    
    /**
     * Lance les tests avec la configuration sélectionnée.
     * Récupère les paramètres, exécute les tests en arrière-plan et affiche les résultats.
     */
    private void runTests() {
        String bot1Type = (String) bot1Combo.getSelectedItem();
        String bot2Type = (String) bot2Combo.getSelectedItem();
        int numGames = (int) numGamesSpinner.getValue();
        
        // Efface la sortie précédente et affiche le message de démarrage
        outputArea.setText("");
        appendToOutput("Démarrage des tests...\n");
        appendToOutput("=======================\n");
        
        runButton.setEnabled(false);
        
        // Exécute les tests dans un thread séparé pour garder l'interface réactive
        SwingWorker<TestStatistics, Integer> worker = new SwingWorker<>() {
            @Override
            protected TestStatistics doInBackground() throws Exception {
                return Tests.runBotVsBotTests(bot1Type, bot2Type, numGames, outputArea);
            }
            
            @Override
            protected void done() {
                try {
                    TestStatistics stats = get();
                    
                    // Affiche la boîte de dialogue des résultats
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
     * Méthode pour ajouter du texte à la zone de sortie.
     * Assure une mise à jour de la zone de texte.
     *
     * @param text Texte à ajouter à la zone de sortie
     */
    private void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            // Défilement automatique vers le bas
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
}