package reversi;

import javax.swing.*;
import java.awt.BorderLayout;

public class GameFrame extends JFrame{
    
    private BoardPanel boardPanel;
    private InfoPanel infoPanel;
    
    public GameFrame(GameController controller){
        setTitle("Reversi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.NORTH);
        
        boardPanel = new BoardPanel(controller);
        add(boardPanel, BorderLayout.CENTER);
        
        //ajouter une option pour sélectionner les joueurs/bots
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
    
    public InfoPanel getInfoPanel(){
        return infoPanel;
    }
}
