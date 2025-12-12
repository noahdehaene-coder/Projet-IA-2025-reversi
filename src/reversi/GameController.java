package reversi;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Contrôleur principal du jeu Reversi.
 * Gère la logique du jeu, coordonne les interactions entre le modèle (plateau)
 * et la vue (interface graphique), et contrôle le déroulement des tours.
 */
public class GameController {

    /** Modèle du plateau de jeu. */
    private ReversiPlateau model;
    
    /** Vue (interface graphique) du jeu. */
    private GameFrame view;
    
    /** Joueur 1 (Noir). */
    private Player player1;
    
    /** Joueur 2 (Blanc). */
    private Player player2;
    
    /** Couleur du joueur dont c'est actuellement le tour. */
    private Couleurcase currentTurn;

    /**
     * Retourne la vue associée à ce contrôleur.
     *
     * @return L'instance de GameFrame.
     */
    public GameFrame getView() {
        return view;
    }

    /**
     * Constructeur du contrôleur de jeu.
     *
     * @param model Le modèle de plateau de jeu.
     */
    public GameController(ReversiPlateau model) {
        this.model = model;
    }
    
    /**
     * Crée et affiche la fenêtre de jeu.
     * Appelée par la classe Main pour initialiser l'interface graphique.
     */
    public void showGameWindow() {
        this.view = new GameFrame(this);
        this.view.getBoardPanel().setBoardModel(model); // Lie le panneau du plateau au modèle
        this.view.setVisible(true);
    }

    /**
     * Démarre une nouvelle partie avec les joueurs spécifiés.
     *
     * @param p1 Joueur 1 (Noir).
     * @param p2 Joueur 2 (Blanc).
     */
    public void startNewGame(Player p1, Player p2) {
        this.player1 = p1; // Noir
        this.player2 = p2; // Blanc
        this.model.initialisation(); // Réinitialise le plateau
        this.currentTurn = Couleurcase.NOIR; // Les noirs commencent
        
        updateView();
        checkNextTurn(); // Démarre le premier tour
    }

    /**
     * Gère le clic d'un joueur humain sur le plateau.
     * Vérifie la validité du coup et l'applique si possible.
     *
     * @param x Coordonnée x (ligne) du clic.
     * @param y Coordonnée y (colonne) du clic.
     */
    public void handleHumanMove(int x, int y) {
        // Vérifie si c'est bien le tour d'un joueur humain
        if (currentTurn == Couleurcase.NOIR && player1 instanceof HumanPlayer ||
            currentTurn == Couleurcase.BLANC && player2 instanceof HumanPlayer) {
            
            Move move = new Move(x, y);
            
            // 1. Valide le coup
            if (model.isMoveValid(move, currentTurn)) {
                // 2. Applique le coup sur le modèle
                model.placePion(move, currentTurn);

                // Efface les indications de coups possibles
                view.getBoardPanel().setAvailableMoves(new ArrayList<>());

                // Redessine immédiatement le plateau pour montrer le coup du joueur humain
                view.getBoardPanel().paintImmediately(0, 0, view.getBoardPanel().getWidth(), view.getBoardPanel().getHeight());
                
                // 3. Passe au tour suivant
                switchTurn();
            } else {
                // Coup invalide
            }
        }
    }

    /**
     * Passe au joueur suivant et gère la logique de tour (humain ou bot).
     * Gère également les cas où un joueur doit passer son tour.
     */
    private void switchTurn() {
        currentTurn = currentTurn.oppose(); // Change de joueur
        updateView();
        
        // Vérifie si la partie est terminée
        if (model.GameOver()) {
            endGame();
            return;
        }

        // Si le nouveau joueur n'a pas de coup valide, il passe son tour
        if (model.getValidMoves(currentTurn).isEmpty()) {
            currentTurn = currentTurn.oppose(); // Revient au joueur précédent
            updateView(); // Informe que le joueur passe son tour
            
            // Si l'autre joueur n'a pas non plus de coup valide, la partie est terminée
            if (model.getValidMoves(currentTurn).isEmpty()) {
                endGame();
                return;
            }
        }
        
        // Vérifie qui doit jouer ensuite (humain ou bot)
        checkNextTurn();
    }
    
    /**
     * Vérifie si le joueur actuel est un bot et lui demande de jouer.
     * Si c'est un humain, attend l'interaction utilisateur.
     */
    private void checkNextTurn() {
        Player currentPlayer = (currentTurn == Couleurcase.NOIR) ? player1 : player2;
        
        // Si le joueur actuel est un bot
        if (!(currentPlayer instanceof HumanPlayer)) {
            // Utilise SwingUtilities.invokeLater pour exécuter dans le thread EDT (Event Dispatch Thread)
            SwingUtilities.invokeLater(() -> {
                // Demande au bot de calculer son coup (donne une copie pour éviter la modification directe)
                Move botMove = ((BotPlayer) currentPlayer).getMove(model.copy());
                
                // Applique le coup s'il est valide
                if (botMove != null && model.isMoveValid(botMove, currentTurn)) {
                    model.placePion(botMove, currentTurn);
                    switchTurn(); // Passe au tour suivant
                } 
                // Si botMove est null, le bot passe son tour (géré dans switchTurn)
            });
        }
        // Si c'est un joueur humain, on ne fait rien, on attend handleHumanMove()
    }

    /**
     * Met à jour la vue avec l'état actuel du jeu.
     * Affiche les scores, le joueur courant, et les indications de coups possibles.
     */
    private void updateView() {
        // Récupère les noms des joueurs
        String blackName = (player1 != null) ? player1.getName() : "Noir";
        String whiteName = (player2 != null) ? player2.getName() : "Blanc";

        // Met à jour le panneau d'information
        view.getInfoPanel().updateInfo(
            currentTurn, 
            model.getScore(Couleurcase.NOIR), 
            model.getScore(Couleurcase.BLANC),
            blackName,
            whiteName
        );

        // Met à jour les indications de coups possibles pour les joueurs humains
        if ((currentTurn == Couleurcase.NOIR && player1 instanceof HumanPlayer) ||
            (currentTurn == Couleurcase.BLANC && player2 instanceof HumanPlayer)) {
            List<Move> validMoves = model.getValidMoves(currentTurn);
            view.getBoardPanel().setAvailableMoves(validMoves);
        } else {
            // Efface les indications pendant les tours des bots
            view.getBoardPanel().setAvailableMoves(new ArrayList<>());
        }
        
        view.getBoardPanel().repaint(); // Redessine le plateau
    }

    /**
     * Retourne le joueur 1 (Noir).
     *
     * @return L'instance de Player correspondant au joueur 1.
     */
    public Player getPlayer1() { return player1; }
    
    /**
     * Retourne le joueur 2 (Blanc).
     *
     * @return L'instance de Player correspondant au joueur 2.
     */
    public Player getPlayer2() { return player2; }
    
    /**
     * Termine la partie et affiche les résultats.
     * Calcule le gagnant et affiche une boîte de dialogue avec les scores.
     */
    private void endGame() {
        // Calcule les scores finaux
        int blackScore = model.getScore(Couleurcase.NOIR);
        int whiteScore = model.getScore(Couleurcase.BLANC);

        String blackName = (player1 != null) ? player1.getName() : "Noir";
        String whiteName = (player2 != null) ? player2.getName() : "Blanc";
        
        // Détermine le message de résultat
        String winnerMessage;
        if (blackScore > whiteScore) {
            winnerMessage = getName(blackName) + " gagne!";
        } else if (whiteScore > blackScore) {
            winnerMessage = getName(whiteName) + " gagne!";
        } else {
            winnerMessage = "Égalité!";
        }
        
        // Affiche la boîte de dialogue des résultats dans le thread EDT
        SwingUtilities.invokeLater(() -> {
            new ResultsDialog(view, this, winnerMessage, blackScore, whiteScore, blackName, whiteName).setVisible(true);
        });
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
