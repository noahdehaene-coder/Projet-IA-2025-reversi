package reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel{
    
    private GameController controller;
    private ReversiPlateau boardModel;
    
    public static final int CELL_SIZE = 80;
    
    public BoardPanel(GameController controller){
        this.controller = controller;
        setPreferredSize(new Dimension(CELL_SIZE * ReversiPlateau.taille, CELL_SIZE * ReversiPlateau.taille));
        setBackground(new Color(0, 100, 0));
        
        addMouseListener(new MouseAdapter() {
            
            public void mousePressed(MouseEvent e) {
                
                int x = e.getY() / CELL_SIZE; // Inversé car x=ligne, y=colonne
                int y = e.getX() / CELL_SIZE;
                
                
                controller.handleHumanMove(x, y);
            }
        });
    }
    
    public void setBoardModel(ReversiPlateau boardModel){
        this.boardModel = boardModel;
    }
    
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (boardModel == null) return;
        
        g.setColor(Color.BLACK);
        for (int i = 0; i <= ReversiPlateau.taille; i++){
            g.drawLine(i*CELL_SIZE, 0, i*CELL_SIZE, getHeight());
            
            g.drawLine(0, i*CELL_SIZE, getWidth(), i*CELL_SIZE);
        }
        
        for (int i = 0; i<ReversiPlateau.taille;i++){
            for (int j = 0; j<ReversiPlateau.taille; j++){
                Couleurcase disc = boardModel.getEtat(i, j);
                if (disc == Couleurcase.NOIR){
                    g.setColor(Color.BLACK);
                    g.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                } else if (disc == Couleurcase.BLANC) {
                    g.setColor(Color.WHITE);
                    g.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }
            }
        }
    }
}

