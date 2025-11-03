package reversi_test;


public class case_disponible{
	public enum Couleurcase {
		DISP,
	    BLANC,
	    NOIR,
	    VIDE;
	}
	
	Couleurcase tableau [] [];
	int longueur;
	int largeur;
	
	public case_disponible () {
		this(8,8);
	}
	
	public case_disponible (int longueur, int largeur) {
		this.longueur=longueur;
		this.largeur=largeur;
		tableau = new Couleurcase[largeur][longueur];
		for (int i=0; i<longueur;i++)
			for (int sub_i=0; sub_i<longueur;sub_i++)
				tableau [i][sub_i]=Couleurcase.VIDE;
	}
	
	public case_disponible (Couleurcase [] [] tab) {
		tableau=tab;
		longueur=tab[0].length;
		largeur=tab.length;
	}
	
	public int getLargeur() {
		return largeur;
	}
	
	public int getLongueur() {
		return longueur;
	}
	
	public Couleurcase[][] getTableau(){
		return tableau;
	}
	
	public Couleurcase oppose (Couleurcase c) {
		if (c.equals(Couleurcase.BLANC))
			return Couleurcase.NOIR;
		else if (c.equals(Couleurcase.NOIR))
			return Couleurcase.BLANC;
		else
			return Couleurcase.VIDE;
	}
	
	public boolean dans_tableau_bounds (int x, int y) {
		if (x < 0 || x >= tableau.length || y < 0 || y >= tableau[0].length) 
			return false;
		return true;
	}
	
	public boolean placeDisponible (int x, int y, Couleurcase c) {
		
		if (c==Couleurcase.VIDE)
			return false;
		if (!(tableau[x][y].equals(Couleurcase.VIDE)))
			return false;
		
		int[][] voisins = { {-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1} };
		
		for (int[] v : voisins) {
			if (dans_tableau_bounds(x+v[0],y+v[1]))
				if (oppose(c).equals(tableau [x+v[0]][y+v[1]]))
					for (int i=x+v[0], j=y+v[1]; dans_tableau_bounds(i,j); i+=v[0], j+=v[1])
						if (c.equals(tableau [i][j]))
							return true;
		}
		return false;
	}
	
	public static Couleurcase[][] toutesLesPLaceDisponibles (Couleurcase joueur, Couleurcase[][] tab) {
		case_disponible temp = new case_disponible(tab);
		for (int x=0; x<temp.getLargeur(); x++)
			for (int y=0; y<temp.getLongueur(); y++)
				if (temp.placeDisponible(x,y,joueur))
					tab[x][y]=Couleurcase.DISP;
		return tab;
	}
}