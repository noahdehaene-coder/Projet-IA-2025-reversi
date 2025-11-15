package reversi;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class GameController {

    private ReversiPlateau model;
    private GameFrame view;
    
    private Player player1; // Noir
    private Player player2; // Blanc
    private Couleurcase currentTurn;

    public GameFrame getView() {
        return view;
    }

    public GameController(ReversiPlateau model) {
        this.model = model;
    }
    
    // Appelée par Main pour créer la fenêtre
    public void showGameWindow() {
        this.view = new GameFrame(this);
        this.view.getBoardPanel().setBoardModel(model); // Lie le panel au modèle
        this.view.setVisible(true);
    }

    public void startNewGame(Player p1, Player p2) {
        this.player1 = p1; // Noir
        this.player2 = p2; // Blanc
        this.model.initialisation();
        this.currentTurn = Couleurcase.NOIR;
        
        updateView();
        checkNextTurn(); // Démarre le premier tour
    }

    /**
     * Gère le clic d'un joueur humain sur le plateau.
     */
    public void handleHumanMove(int x, int y) {
        // Vérifier si c'est bien le tour d'un humain
        if (currentTurn == Couleurcase.NOIR && player1 instanceof HumanPlayer ||
            currentTurn == Couleurcase.BLANC && player2 instanceof HumanPlayer) {
            
            Move move = new Move(x, y);
            
            // 1. Valider le coup
            if (model.isMoveValid(move, currentTurn)) {
                // 2. Appliquer le coup
                model.placePion(move, currentTurn);

                view.getBoardPanel().setAvailableMoves(new ArrayList<>()); //clear hints

                view.getBoardPanel().paintImmediately(0, 0, view.getBoardPanel().getWidth(), view.getBoardPanel().getHeight());
                //show humanplayer's move immediately without waiting for bot response
                
                // 3. Passer au tour suivant
                switchTurn();
            } else {
                // Afficher un message d'erreur (coup invalide)
            }
        }
    }

    /**
     * Passe au joueur suivant et gère la logique de tour (humain ou bot).
     */
    private void switchTurn() {
        currentTurn = currentTurn.oppose();
        updateView();
        
        if (model.GameOver()) {
            endGame();
            return;
        }

        // Si le nouveau joueur n'a pas de coup, on repasse à l'autre
        if (model.getValidMoves(currentTurn).isEmpty()) {
            currentTurn = currentTurn.oppose();
            updateView(); // Informer que le joueur passe son tour
            
            if (model.getValidMoves(currentTurn).isEmpty()) {
                endGame(); // Si l'autre n'a pas de coup non plus, c'est la fin
                return;
            }
        }
        
        checkNextTurn();
    }
    
    /**
     * Vérifie si le joueur actuel est un bot et lui demande de jouer.
     */
    private void checkNextTurn() {
        Player currentPlayer = (currentTurn == Couleurcase.NOIR) ? player1 : player2;
        
        if (currentPlayer instanceof BotPlayer) {
            // Demander au bot de jouer (peut nécessiter un SwingWorker pour les IA longues)
            Move botMove = ((BotPlayer) currentPlayer).getMove(model.copy()); // Donne une copie pour éviter la triche
            
            if (botMove != null && model.isMoveValid(botMove, currentTurn)) {
                model.placePion(botMove, currentTurn);
                switchTurn();
            }
        }
        // Si c'est un HumanPlayer, on ne fait rien, on attend handleHumanMove()
    }

    private void updateView() {
        view.getInfoPanel().updateInfo(
            currentTurn, 
            model.getScore(Couleurcase.NOIR), 
            model.getScore(Couleurcase.BLANC)
        );

        // Update available moves for human players
        if ((currentTurn == Couleurcase.NOIR && player1 instanceof HumanPlayer) ||
            (currentTurn == Couleurcase.BLANC && player2 instanceof HumanPlayer)) {
            List<Move> validMoves = model.getValidMoves(currentTurn);
            view.getBoardPanel().setAvailableMoves(validMoves);
        } else {
            // Clear hints during bot turns
            view.getBoardPanel().setAvailableMoves(new ArrayList<>());
        }
        view.getBoardPanel().repaint(); // Redessine le plateau
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    
      private void endGame() {
        // Calculate winner and scores
        int blackScore = model.getScore(Couleurcase.NOIR);
        int whiteScore = model.getScore(Couleurcase.BLANC);
        
        String winnerMessage;
        if (blackScore > whiteScore) {
            winnerMessage = "Noir gagne!";
        } else if (whiteScore > blackScore) {
            winnerMessage = "Blanc gagne!";
        } else {
            winnerMessage = "Égalité!";
        }
        
        // Show results dialog
        SwingUtilities.invokeLater(() -> {
            new ResultsDialog(view, this, winnerMessage, blackScore, whiteScore).setVisible(true);
        });
    }
}

