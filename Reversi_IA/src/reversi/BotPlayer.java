package reversi;

public abstract class BotPlayer extends Player {
    public BotPlayer(Couleurcase color) {
        super(color);
    }
    
    /**
     * La méthode que chaque stratégie de Bot doit implémenter.
     * @param board Une copie du plateau actuel.
     * @return Le coup (Move) choisi par le bot.
     */
    public abstract Move getMove(ReversiPlateau board);
}

