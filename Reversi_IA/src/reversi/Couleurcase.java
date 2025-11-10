package reversi;

public enum Couleurcase{
    VIDE,
    BLANC,
    NOIR;
    
    public Couleurcase oppose(){
        if (this == BLANC){
            return NOIR;
        }
        else if (this == NOIR){
            return BLANC;
        }
        return VIDE;
    }
}
