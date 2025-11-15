package reversi;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            
            // 1. Créer le Modèle (la logique du jeu)
            ReversiPlateau model = new ReversiPlateau();
            
            // 2. Créer le Contrôleur (le "cerveau")
            // On lui donne une référence vers le modèle
            GameController controller = new GameController(model);
            
            // 3. Demander au contrôleur d'afficher la Vue (la fenêtre)
            // La vue sera créée et liée au contrôleur et au modèle.
            controller.showGameWindow();

            // 4. Joueur choisi contre qui jouer, puis commence une partie
            new PlayerSelectionDialog(controller.getView(), controller).setVisible(true);
            
        });
    }
}
