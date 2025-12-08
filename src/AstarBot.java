import java.util.*;


public class AstarBot extends BotPlayer {
    
    /**
     * Constructeur du bot A*.
     *
     * @param color Couleur des pions du bot (Blanc ou Noir).
     */
    public AstarBot(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode principale pour obtenir le meilleur coup calculé par le bot.
     * Utilise l'algorithme A* pour évaluer les coups possibles.
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
        
        // Utilise la recherche A* pour trouver le meilleur coup avec une profondeur de 4
        return aStarSearch(board, validMoves, 4);
    }
    
    /**
     * Implémente la recherche A* pour évaluer les coups.
     * f(n) = g(n) + h(n) où :
     * - g(n) est le bénéfice réel/coût jusqu'à présent
     * - h(n) est l'estimation heuristique du coût jusqu'au but
     *
     * @param currentBoard Le plateau actuel.
     * @param validMoves Liste des coups valides à évaluer.
     * @param maxDepth Profondeur maximale de recherche pour l'estimation heuristique.
     * @return Le meilleur coup selon l'évaluation A*.
     */
    private Move aStarSearch(ReversiPlateau currentBoard, List<Move> validMoves, int maxDepth) {
        Move bestMove = null;
        double bestFScore = Double.NEGATIVE_INFINITY; // Initialise avec la plus petite valeur
        
        // Évalue chaque premier coup possible
        for (Move firstMove : validMoves) {
            // Calcule le score g (bénéfice réel immédiat)
            double gScore = calculateActualBenefit(currentBoard, firstMove);
            
            // Calcule le score h (estimation heuristique du potentiel futur)
            double hScore = calculateHeuristicEstimate(currentBoard, firstMove, maxDepth);
            
            // Évaluation A* : f(n) = g(n) + h(n)
            double fScore = gScore + hScore;
            
            // Met à jour le meilleur coup si ce score est supérieur
            if (fScore > bestFScore) {
                bestFScore = fScore;
                bestMove = firstMove;
            }
        }
        
        // Retourne le meilleur coup trouvé, ou le premier coup si aucun n'a été sélectionné
        return bestMove != null ? bestMove : validMoves.get(0);
    }
    
    /**
     * Calcule le bénéfice réel (score g) d'un coup.
     * Représente l'avantage immédiat et connu.
     *
     * @param board Le plateau actuel.
     * @param move Le coup à évaluer.
     * @return Le score g (bénéfice réel).
     */
    private double calculateActualBenefit(ReversiPlateau board, Move move) {
        double gScore = 0.0;
        
        // Simule le coup sur une copie du plateau
        ReversiPlateau newBoard = board.copy();
        newBoard.placePion(move, this.color);
        
        // 1. Gain immédiat de pions (le bénéfice le plus important)
        int piecesGained = calculatePiecesGained(board, newBoard);
        gScore += piecesGained * 2.5;
        
        // 2. Bénéfice de stabilité positionnelle
        gScore += getStabilityBenefit(newBoard, move) * 1.8;
        
        // 3. Changement réel de mobilité
        int mobilityChange = calculateMobilityChange(board, newBoard);
        gScore += mobilityChange * 1.2;
        
        // 4. Capture d'un coin (énorme bénéfice)
        if (isCorner(move.x, move.y)) {
            gScore += 15.0;
        }
        
        return gScore;
    }
    
    /**
     * Calcule l'estimation heuristique (score h) du potentiel futur.
     * Estime à quel point ce coup est prometteur pour le succès à long terme.
     *
     * @param board Le plateau actuel.
     * @param move Le coup à évaluer.
     * @param maxDepth Profondeur maximale pour l'anticipation.
     * @return Le score h (estimation heuristique).
     */
    private double calculateHeuristicEstimate(ReversiPlateau board, Move move, int maxDepth) {
        // Simule le coup
        ReversiPlateau newBoard = board.copy();
        newBoard.placePion(move, this.color);
        
        // Utilise une combinaison de facteurs stratégiques pour l'heuristique
        double hScore = 0.0;
        
        // 1. Potentiel de contrôle des coins
        hScore += evaluateCornerPotential(newBoard) * 2.0;
        
        // 2. Potentiel de contrôle des bords
        hScore += evaluateEdgePotential(newBoard) * 1.2;
        
        // 3. Potentiel d'avantage de mobilité
        hScore += evaluateMobilityPotential(newBoard) * 1.0;
        
        // 4. Potentiel de stabilité des pions
        hScore += evaluateStabilityPotential(newBoard) * 1.5;
        
        // 5. Anticipation de quelques coups avec évaluation simplifiée
        if (maxDepth > 0) {
            hScore += lookAheadPotential(newBoard, maxDepth - 1) * 0.8;
        }
        
        return hScore;
    }
    
    /**
     * Calcule combien de pions ce coup retournerait.
     *
     * @param originalBoard Le plateau original.
     * @param newBoard Le plateau après le coup.
     * @return Le nombre de pions gagnés (retournés).
     */
    private int calculatePiecesGained(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalScore = originalBoard.getScore(this.color);
        int newScore = newBoard.getScore(this.color);
        // Soustrait 1 car le nouveau pion placé est compté dans le score
        return newScore - originalScore - 1;
    }
    
    /**
     * Évalue le bénéfice de stabilité de cette position.
     *
     * @param board Le plateau après le coup.
     * @param move Le coup évalué.
     * @return Un score de stabilité.
     */
    private double getStabilityBenefit(ReversiPlateau board, Move move) {
        double stability = 0.0;
        int x = move.x, y = move.y;
        
        // Les coins sont complètement stables
        if (isCorner(x, y)) {
            stability += 10.0;
        }
        
        // Les bords sont relativement stables
        if (x == 0 || x == 7 || y == 0 || y == 7) {
            stability += 4.0;
        }
        
        // Compte les voisins alliés pour une stabilité supplémentaire
        int friendlyNeighbors = countFriendlyNeighbors(board, x, y);
        stability += friendlyNeighbors * 0.5;
        
        return stability;
    }
    
    /**
     * Calcule le changement de mobilité (nos coups vs coups adverses).
     *
     * @param originalBoard Le plateau original.
     * @param newBoard Le plateau après le coup.
     * @return La différence nette de mobilité.
     */
    private int calculateMobilityChange(ReversiPlateau originalBoard, ReversiPlateau newBoard) {
        int originalMyMoves = originalBoard.getValidMoves(this.color).size();
        int originalOpponentMoves = originalBoard.getValidMoves(this.color.oppose()).size();
        int newMyMoves = newBoard.getValidMoves(this.color).size();
        int newOpponentMoves = newBoard.getValidMoves(this.color.oppose()).size();
        
        int myMobilityChange = newMyMoves - originalMyMoves;
        int opponentMobilityChange = newOpponentMoves - originalOpponentMoves;
        
        return myMobilityChange - opponentMobilityChange;
    }
    
    /**
     * Vérifie si une position est un coin.
     *
     * @param x Coordonnée x (ligne).
     * @param y Coordonnée y (colonne).
     * @return true si la position est un coin, false sinon.
     */
    private boolean isCorner(int x, int y) {
        return (x == 0 && y == 0) || (x == 0 && y == 7) || 
               (x == 7 && y == 0) || (x == 7 && y == 7);
    }
    
    /**
     * Compte les pions alliés voisins d'une position.
     *
     * @param board Le plateau.
     * @param x Coordonnée x de la position.
     * @param y Coordonnée y de la position.
     * @return Le nombre de voisins alliés.
     */
    private int countFriendlyNeighbors(ReversiPlateau board, int x, int y) {
        int count = 0;
        // Les 8 directions possibles
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};
        
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            // Vérifie les limites et si la case contient un pion allié
            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && 
                board.getEtat(nx, ny) == this.color) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Évalue le potentiel de contrôle des coins.
     *
     * @param board Le plateau à évaluer.
     * @return Un score de potentiel de contrôle des coins.
     */
    private double evaluateCornerPotential(ReversiPlateau board) {
        double potential = 0.0;
        int[][] corners = {{0,0}, {0,7}, {7,0}, {7,7}};
        
        for (int[] corner : corners) {
            Couleurcase state = board.getEtat(corner[0], corner[1]);
            if (state == this.color) {
                potential += 5.0; // Nous contrôlons ce coin
            } else if (state == Couleurcase.VIDE) {
                // Vérifie si nous avons accès à ce coin
                if (board.getValidMoves(this.color).stream()
                    .anyMatch(m -> m.x == corner[0] && m.y == corner[1])) {
                    potential += 3.0; // Nous pouvons prendre ce coin
                }
            }
        }
        
        return potential;
    }
    
    /**
     * Évalue le potentiel de contrôle des bords.
     *
     * @param board Le plateau à évaluer.
     * @return Un score de potentiel de contrôle des bords.
     */
    private double evaluateEdgePotential(ReversiPlateau board) {
        double potential = 0.0;
        
        // Compte nos pions sur les bords vs pions adverses
        for (int i = 0; i < 8; i++) {
            // Bord supérieur
            if (board.getEtat(0, i) == this.color) potential += 1.0;
            else if (board.getEtat(0, i) == this.color.oppose()) potential -= 1.0;
            
            // Bord inférieur
            if (board.getEtat(7, i) == this.color) potential += 1.0;
            else if (board.getEtat(7, i) == this.color.oppose()) potential -= 1.0;
            
            // Bord gauche
            if (board.getEtat(i, 0) == this.color) potential += 1.0;
            else if (board.getEtat(i, 0) == this.color.oppose()) potential -= 1.0;
            
            // Bord droit
            if (board.getEtat(i, 7) == this.color) potential += 1.0;
            else if (board.getEtat(i, 7) == this.color.oppose()) potential -= 1.0;
        }
        
        return potential;
    }
    
    /**
     * Évalue le potentiel d'avantage de mobilité.
     *
     * @param board Le plateau à évaluer.
     * @return Un score de potentiel de mobilité.
     */
    private double evaluateMobilityPotential(ReversiPlateau board) {
        int myMobility = board.getValidMoves(this.color).size();
        int opponentMobility = board.getValidMoves(this.color.oppose()).size();
        return (myMobility - opponentMobility) * 0.5;
    }
    
    /**
     * Évalue le potentiel de stabilité des pions.
     *
     * @param board Le plateau à évaluer.
     * @return Un score de potentiel de stabilité.
     */
    private double evaluateStabilityPotential(ReversiPlateau board) {
        double stability = 0.0;
        
        // Évaluation simple de la stabilité basée sur les connexions aux coins
        // Les pions connectés aux coins sont plus stables
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getEtat(i, j) == this.color) {
                    // Vérifie si ce pion est connecté à un coin
                    if (isConnectedToCorner(board, i, j)) {
                        stability += 0.3;
                    }
                }
            }
        }
        
        return stability;
    }
    
    /**
     * Vérifie si un pion est connecté à un coin (directement ou via des pions alliés).
     *
     * @param board Le plateau.
     * @param x Coordonnée x du pion.
     * @param y Coordonnée y du pion.
     * @return true si connecté à un coin, false sinon.
     */
    private boolean isConnectedToCorner(ReversiPlateau board, int x, int y) {
        // Implémentation simple, vérifie si sur la même ligne/colonne qu'un coin que nous contrôlons
        int[][] corners = {{0,0}, {0,7}, {7,0}, {7,7}};
        
        for (int[] corner : corners) {
            if (board.getEtat(corner[0], corner[1]) == this.color) {
                if (x == corner[0] || y == corner[1]) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Anticipation simplifiée pour l'évaluation du potentiel.
     *
     * @param board Le plateau actuel.
     * @param depth Profondeur restante d'anticipation.
     * @return Un score estimé du potentiel futur.
     */
    private double lookAheadPotential(ReversiPlateau board, int depth) {
        // Condition d'arrêt : profondeur nulle ou partie terminée
        if (depth <= 0 || board.GameOver()) {
            return evaluateBoardSimple(board);
        }
        
        List<Move> moves = board.getValidMoves(this.color);
        if (moves.isEmpty()) {
            return evaluateBoardSimple(board);
        }
        
        double bestPotential = Double.NEGATIVE_INFINITY;
        
        // Examine un échantillon de coups pour estimer le potentiel
        for (int i = 0; i < Math.min(3, moves.size()); i++) { // Échantillonne 3 coups
            Move move = moves.get(i);
            ReversiPlateau newBoard = board.copy();
            newBoard.placePion(move, this.color);
            
            // Évalue récursivement avec un facteur d'atténuation
            double potential = evaluateBoardSimple(newBoard) + lookAheadPotential(newBoard, depth - 1) * 0.7;
            if (potential > bestPotential) {
                bestPotential = potential;
            }
        }
        
        return bestPotential;
    }
    
    /**
     * Évaluation simple du plateau pour l'anticipation.
     *
     * @param board Le plateau à évaluer.
     * @return Un score basé sur la différence de pions.
     */
    private double evaluateBoardSimple(ReversiPlateau board) {
        int myScore = board.getScore(this.color);
        int opponentScore = board.getScore(this.color.oppose());
        return (myScore - opponentScore) * 0.1;
    }
}
