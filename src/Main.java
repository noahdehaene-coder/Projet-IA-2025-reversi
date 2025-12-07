import javax.swing.SwingUtilities;

/**
 * Classe principale (point d'entrée) de l'application Reversi.
 */
public class Main {

    /**
     * Point d'entrée principal de l'application.
     * Configure et lance le jeu Reversi avec interface graphique.
     *
     * @param args Arguments de la ligne de commande (non utilisés dans cette application).
     */
    public static void main(String[] args) {
        
        // Exécute l'initialisation dans l'Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            
            // 1. Créer le Modèle (la logique du jeu)
            ReversiPlateau model = new ReversiPlateau();
            
            // 2. Créer le Contrôleur (le "cerveau")
            // On lui donne une référence vers le modèle
            GameController controller = new GameController(model);
            
            // 3. Demander au contrôleur d'afficher la Vue (la fenêtre)
            // La vue sera créée et liée au contrôleur et au modèle.
            controller.showGameWindow();

            // 4. Afficher la boîte de dialogue pour sélectionner les joueurs,
            // puis commencer une nouvelle partie avec les joueurs choisis.
            new PlayerSelectionDialog(controller.getView(), controller).setVisible(true);
            
        });
    }
}