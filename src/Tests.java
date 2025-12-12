import javax.swing.*;
import java.util.List;

/**
 * Test class to run multiple games between two bots and display statistics.
 * Allows selecting bots, number of games, and shows win rates and average times.
 */
public class Tests {

    /**
     * Main entry point for running bot vs bot tests.
     * Opens a dialog to configure and run the tests.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and show the test configuration dialog
            new TestConfigurationDialog().setVisible(true);
        });
    }

    /**
     * Runs a series of games between two bots and collects statistics.
     *
     * @param bot1Type Type of the first bot (black player)
     * @param bot2Type Type of the second bot (white player)
     * @param numGames Number of games to play
     * @return Statistics object containing results
     */
    public static TestStatistics runBotVsBotTests(String bot1Type, String bot2Type, int numGames, JTextArea outputArea) {
        int blackWins = 0;
        int whiteWins = 0;
        int draws = 0;
        long totalTimeMillis = 0;
        
        for (int game = 1; game <= numGames; game++) {
            String message = "Running game " + game + " of " + numGames + "...";
            output(outputArea, message);
            
            long startTime = System.currentTimeMillis();
            
            // Create players
            Player blackPlayer = createBot(Couleurcase.NOIR, bot1Type);
            Player whitePlayer = createBot(Couleurcase.BLANC, bot2Type);
            
            // Simulate the game
            TestResultat result = simulateGameDirect(blackPlayer, whitePlayer);
            
            long endTime = System.currentTimeMillis();
            long gameDuration = endTime - startTime;
            totalTimeMillis += gameDuration;
            
            // Update statistics
            if (result.winner == Couleurcase.NOIR) {
                blackWins++;
            } else if (result.winner == Couleurcase.BLANC) {
                whiteWins++;
            } else {
                draws++;
            }
            
            String resultMessage="Game " + game + " completed in " + gameDuration + "ms. Result: " + 
                             getName(bot1Type) + " (Black) " + result.blackScore + " - " + 
                             result.whiteScore + " " + getName(bot2Type) + " (White)";
            output(outputArea, resultMessage);
        }
        
        return new TestStatistics(blackWins, whiteWins, draws, numGames, totalTimeMillis, bot1Type, bot2Type);
    }

     /**
     * Appends text to the output area in a thread-safe way.
     */
    private static void output(JTextArea outputArea, String text) {
        if (outputArea != null) {
            SwingUtilities.invokeLater(() -> {
                outputArea.append(text + "\n");
                // Auto-scroll to the bottom
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }
    }
    
    /**
     * Direct simulation of a game between two bots.
     */
    private static TestResultat simulateGameDirect(Player blackPlayer, Player whitePlayer) {
        ReversiPlateau board = new ReversiPlateau();
        board.initialisation();
        
        Couleurcase currentTurn = Couleurcase.NOIR;
        int consecutivePasses = 0;
        
        while (true) {
            // Check if game is over
            if (board.GameOver()) {
                break;
            }
            
            List<Move> validMoves = board.getValidMoves(currentTurn);
            
            if (validMoves.isEmpty()) {
                consecutivePasses++;
                currentTurn = currentTurn.oppose();
                
                if (consecutivePasses >= 2) {
                    break; // Both players passed consecutively
                }
                continue;
            } else {
                consecutivePasses = 0;
            }
            
            // Get the bot's move
            Move chosenMove = null;
            if (currentTurn == Couleurcase.NOIR) {
                chosenMove = ((BotPlayer) blackPlayer).getMove(board.copy());
            } else {
                chosenMove = ((BotPlayer) whitePlayer).getMove(board.copy());
            }
            
            // Apply the move
            if (chosenMove != null && board.isMoveValid(chosenMove, currentTurn)) {
                board.placePion(chosenMove, currentTurn);
            }
            
            // Switch turn
            currentTurn = currentTurn.oppose();
        }
        
        // Calculate final scores
        int blackScore = board.getScore(Couleurcase.NOIR);
        int whiteScore = board.getScore(Couleurcase.BLANC);
        
        Couleurcase winner;
        if (blackScore > whiteScore) {
            winner = Couleurcase.NOIR;
        } else if (whiteScore > blackScore) {
            winner = Couleurcase.BLANC;
        } else {
            winner = Couleurcase.VIDE; // Draw
        }
        
        return new TestResultat(winner, blackScore, whiteScore);
    }
    
    /**
     * Creates a bot instance based on the bot type string.
     */
    private static Player createBot(Couleurcase color, String botType) {
        switch (botType) {
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
    
    /**
     * Gets the display name of a bot type.
     */
    public static String getName(String className) {
        switch (className) {
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
            case "Bot Aléatoire": return "Bot Aléatoire";
            case "BFS": return "BFS Bot";
            case "DFS": return "DFS Bot";
            case "Dijkstra": return "Dijkstra Bot";
            case "Greedy BFS Bot": return "Greedy BFS Bot";
            case "A*": return "A* Bot";
            case "AlphaBeta": return "AlphaBeta";
            case "Monte Carlo": return "Monte Carlo";
            case "AlphaBeta Rapide": return "AlphaBeta Rapide";
            case "Dijkstra Rapide": return "Dijkstra Bot Rapide";
            default: return className; // Retourne le nom tel quel si non reconnu
        }
    }
}