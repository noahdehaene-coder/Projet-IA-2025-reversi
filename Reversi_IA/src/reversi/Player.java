package reversi;

public abstract class Player {
    protected Couleurcase color;

    public Player(Couleurcase color) {
        this.color = color;
    }

    public Couleurcase getColor() {
        return color;
    }
    
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
