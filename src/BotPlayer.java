/**
 * Classe abstraite représentant un joueur contrôlé par l'ordinateur (bot).
 * Toutes les implémentations de bots doivent hériter de cette classe
 * et fournir leur propre logique de décision de coup.
 */
public abstract class BotPlayer extends Player {
    
    /**
     * Constructeur du bot.
     * Initialise le bot avec une couleur spécifique (NOIR ou BLANC).
     *
     * @param color La couleur des pions du bot.
     */
    public BotPlayer(Couleurcase color) {
        super(color);
    }
    
    /**
     * Méthode abstraite que chaque stratégie de bot doit implémenter.
     * Cette méthode est appelée quand c'est le tour du bot de jouer.
     * Elle doit analyser l'état actuel du jeu et retourner le coup choisi.
     *
     * @param board Une copie du plateau actuel pour analyse. 
     * La copie permet au bot d'évaluer des coups sans modifier l'état réel du jeu.
     * @return Le coup (Move) choisi par le bot, ou null si aucun coup n'est possible.
     */
    public abstract Move getMove(ReversiPlateau board);
}