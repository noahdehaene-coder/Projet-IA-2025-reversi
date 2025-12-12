package reversi;

import java.util.*;

/**
 * Classe représentant un bot utilisant l'algorithme Greedy Best-First Search.
 * Cet algorithme est "greedy" (glouton) : il choisit toujours l'option la plus prometteuse
 * selon une fonction heuristique, sans explorer profondément l'arbre des coups.
 * Rapide mais pas forcément optimal à long terme.
 */
public class GreedyBFSBot extends BotPlayer {
    
    /**
     * Constructeur du bot Greedy BFS.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public GreedyBFSBot(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise une recherche greedy basée sur une heuristique immédiate.
     *
     * @param board L'état actuel du plateau de jeu.
     * @return Le meilleur coup trouvé, ou null si aucun coup n'est possible (passe le tour).
     */
    @Override
    public Move getMove(ReversiPlateau board) {
        // Récupère tous les coups valides pour le joueur actuel
        List<Move> validMoves = board.getValidMoves(this.color);
        
        // Si aucun coup valide, passe le tour
        if (validMoves.isEmpty()) {
            return null;
        }
        
        // Si un seul coup possible, le retourne immédiatement
        if (validMoves.size() == 1) {
            return validMoves.get(0);
        }
        
        // Utilise la recherche greedy (Greedy BFS) pour trouver le meilleur coup
        return greedyBFSSearch(board, validMoves);
    }
    
    /**
     * Implémente l'algorithme Greedy Best-First Search.
     * Toujours développe le noeud le plus prometteur en premier
     * selon une fonction heuristique, sans regarder plus loin.
     *
     * @param currentBoard Le plateau de jeu actuel.
     * @param validMoves Liste des coups valides pour le tour actuel.
     * @return Le meilleur coup selon l'heuristique greedy.
     */
    private Move greedyBFSSearch(ReversiPlateau currentBoard, List<Move> validMoves) {
        // File de priorité pour toujours obtenir le coup avec le meilleur score heuristique
        PriorityQueue<ScoredMove> moveQueue = new PriorityQueue<>(
            // Tri décroissant : le plus grand score en premier
            (a, b) -> Double.compare(b.score, a.score)
        );
        
        // Évalue chaque coup avec l'heuristique greedy
        for (Move move : validMoves) {
            double score = greedyHeuristic(currentBoard, move);
            moveQueue.add(new ScoredMove(move, score));
        }
        
        // Retourne le coup avec le score heuristique le plus élevé
        return moveQueue.poll().move;
    }
    
    /**
     * Fonction heuristique greedy qui évalue un coup basé sur :
     * 1. Gain immédiat de pions
     * 2. Qualité de la position
     * 3. Potentiel de réduction de la mobilité de l'adversaire
     *
     * @param board Le plateau actuel.
     * @param move Le coup à évaluer.
     * @return Un score heuristique (plus élevé = meilleur).
     */
    private double greedyHeuristic(ReversiPlateau board, Move move) {
        double score = 0.0;
        
        // Simule le coup pour voir les conséquences immédiates
        ReversiPlateau simulatedBoard = board.copy();
        simulatedBoard.placePion(move, this.color);
        
        // 1. Gain immédiat de pions (le plus important pour l'approche greedy)
        int piecesGained = calculatePiecesGained(board, simulatedBoard);
        score += piecesGained * 3.0;
        
        // 2. Valeur positionnelle, greedy pour les coins et les bords
        score += getGreedyPositionValue(move.x, move.y) * 2.5;
        
        // 3. Réduction de la mobilité de l'adversaire, greedy pour limiter l'adversaire
        int opponentMobilityReduction = calculateOpponentMobilityReduction(board, simulatedBoard);
        score += opponentMobilityReduction * 1.5;
        
        // 4. Détection de victoire immédiate, l'option la plus greedy
        if (simulatedBoard.GameOver()) {
            int finalScore = simulatedBoard.getScore(this.color) - simulatedBoard.getScore(this.color.oppose());
            if (finalScore > 0) {
                score += 1000; // Énorme bonus pour une victoire immédiate
            }
        }
        
        return score;
    }
    
    /**
     * Calcule combien de pions ce coup retournerait.
     *
     * @param originalBoard Le plateau original (avant le coup).
     * @param newBoard Le plateau après le coup.
     * @return Le nombre de pions gagnés (retournés).
     */
    private int calculatePiecesGained(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalScore = originalBoard.getScore(this.color);
        int newScore = newBoard.getScore(this.color);
        // Soustrait 1 car le pion placé est compté dans le score
        return newScore - originalScore - 1;
    }
    
    /**
     * Évaluation greedy de la valeur positionnelle.
     * Préfère fortement les coins et les bords, évite les positions dangereuses.
     *
     * @param x Coordonnée x (ligne) de la position.
     * @param y Coordonnée y (colonne) de la position.
     * @return Un score de valeur positionnelle.
     */
    private double getGreedyPositionValue(int x, int y) {
        // Les coins sont extrêmement précieux dans l'approche greedy
        if ((x == 0 && y == 0) || (x == 0 && y == 7) || 
            (x == 7 && y == 0) || (x == 7 && y == 7)) {
            return 20.0; // Très haute valeur pour les coins
        }
        // Les bords sont aussi très précieux
        else if (x == 0 || x == 7 || y == 0 || y == 7) {
            return 8.0; // Haute valeur pour les bords
        }
        // Contrôle du centre
        else if (x >= 2 && x <= 5 && y >= 2 && y <= 5) {
            return 3.0;
        }
        // Positions dangereuses près des coins
        else if ((x == 1 || x == 6) && (y == 1 || y == 6)) {
            return -10.0; // Évite fortement ces positions
        }
        else {
            return 1.0; // Valeur par défaut pour les autres cases
        }
    }
    
    /**
     * Calcule à quel point ce coup réduit la mobilité de l'adversaire.
     *
     * @param originalBoard Le plateau original.
     * @param newBoard Le plateau après le coup.
     * @return La réduction du nombre de coups possibles de l'adversaire.
     */
    private int calculateOpponentMobilityReduction(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalOpponentMoves = originalBoard.getValidMoves(this.color.oppose()).size();
        int newOpponentMoves = newBoard.getValidMoves(this.color.oppose()).size();
        return originalOpponentMoves - newOpponentMoves; // Positif = bon (réduction)
    }
    
    /**
     * Classe interne pour stocker les coups avec leurs scores.
     * Utilisée dans la file de priorité.
     */
    private static class ScoredMove {
        /** Le coup. */
        Move move;
        
        /** Le score heuristique associé. */
        double score;
        
        /**
         * Constructeur pour créer un coup avec score.
         *
         * @param move Le coup.
         * @param score Le score heuristique.
         */
        ScoredMove(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }
}
