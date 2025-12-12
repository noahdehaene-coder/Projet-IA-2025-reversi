package reversi;

import javax.swing.*;
import java.util.List;

/**
 * Classe de test pour exécuter plusieurs parties entre deux bots et afficher des statistiques.
 * Permet de sélectionner les bots, le nombre de parties, et montre les pourcentages de victoire et le temps moyens.
 */
public class Tests {

    /**
     * Point d'entrée principal pour exécuter les tests bot contre bot.
     * Ouvre une boîte de dialogue pour configurer et exécuter les tests.
     * 
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crée et affiche la boîte de dialogue de configuration des tests
            new TestConfigurationDialog().setVisible(true);
        });
    }

    /**
     * Exécute une série de parties entre deux bots et collecte les statistiques.
     *
     * @param bot1Type Type du premier bot (joueur noir)
     * @param bot2Type Type du second bot (joueur blanc)
     * @param numGames Nombre de parties à jouer
     * @param outputArea Zone de texte pour la sortie
     * @return Objet TestStatistics contenant les résultats
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
            
            // Crée les joueurs
            Player blackPlayer = createBot(Couleurcase.NOIR, bot1Type);
            Player whitePlayer = createBot(Couleurcase.BLANC, bot2Type);
            
            // Simule la partie
            TestResultat result = simulateGameDirect(blackPlayer, whitePlayer);
            
            long endTime = System.currentTimeMillis();
            long gameDuration = endTime - startTime;
            totalTimeMillis += gameDuration;
            
            // Met à jour les statistiques
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
     * Ajoute du texte à la zone de sortie.
     *
     * @param outputArea Zone de texte où afficher le message
     * @param text Texte à afficher
     */
    private static void output(JTextArea outputArea, String text) {
        if (outputArea != null) {
            SwingUtilities.invokeLater(() -> {
                outputArea.append(text + "\n");
                // Auto-scroll vers le bas
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }
    }
    
    /**
     * Simulation directe d'une partie entre deux bots.
     *
     * @param blackPlayer Joueur noir (bot)
     * @param whitePlayer Joueur blanc (bot)
     * @return Objet TestResultat contenant le gagnant et les scores
     */
    private static TestResultat simulateGameDirect(Player blackPlayer, Player whitePlayer) {
        ReversiPlateau board = new ReversiPlateau();
        board.initialisation();
        
        Couleurcase currentTurn = Couleurcase.NOIR;
        int consecutivePasses = 0;
        
        while (true) {
            // Vérifie si la partie est terminée
            if (board.GameOver()) {
                break;
            }
            
            List<Move> validMoves = board.getValidMoves(currentTurn);
            
            if (validMoves.isEmpty()) {
                consecutivePasses++;
                currentTurn = currentTurn.oppose();
                
                if (consecutivePasses >= 2) {
                    break; // Les deux joueurs ont passé consécutivement
                }
                continue;
            } else {
                consecutivePasses = 0;
            }
            
            // Obtient le coup du bot
            Move chosenMove = null;
            if (currentTurn == Couleurcase.NOIR) {
                chosenMove = ((BotPlayer) blackPlayer).getMove(board.copy());
            } else {
                chosenMove = ((BotPlayer) whitePlayer).getMove(board.copy());
            }
            
            // Applique le coup
            if (chosenMove != null && board.isMoveValid(chosenMove, currentTurn)) {
                board.placePion(chosenMove, currentTurn);
            }
            
            // Change de tour
            currentTurn = currentTurn.oppose();
        }
        
        // Calcule les scores finaux
        int blackScore = board.getScore(Couleurcase.NOIR);
        int whiteScore = board.getScore(Couleurcase.BLANC);
        
        Couleurcase winner;
        if (blackScore > whiteScore) {
            winner = Couleurcase.NOIR;
        } else if (whiteScore > blackScore) {
            winner = Couleurcase.BLANC;
        } else {
            winner = Couleurcase.VIDE; // Match nul
        }
        
        return new TestResultat(winner, blackScore, whiteScore);
    }
    
    /**
     * Crée une instance de bot basée sur le type de bot (chaîne).
     *
     * @param color Couleur du bot (NOIR ou BLANC)
     * @param botType Type de bot (chaîne identifiant l'algorithme)
     * @return Instance de Player correspondant au bot demandé
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
     * Obtient le nom d'affichage d'un type de bot.
     *
     * @param className Identifiant ou nom de classe du bot
     * @return Nom d'affichage correspondant.
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
