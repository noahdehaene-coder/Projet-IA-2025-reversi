package reversi;

import javax.swing.*;

import java.awt.*;

public class InfoPanel extends JPanel {

    private JLabel turnLabel;
    private JLabel scoreLabel;

    public InfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        turnLabel = new JLabel("Tour :    Noir");
        scoreLabel = new JLabel("Score :    N 2 - B 2");

        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
        add(turnLabel);
        add(scoreLabel);
    }

    public void updateInfo(Couleurcase currentTurn, int blackScore, int whiteScore, String blackPlayerName, String whitePlayerName) {
        String blackName = getName(blackPlayerName);
        String whiteName = getName(whitePlayerName);
        String currentPlayerName = (currentTurn == Couleurcase.NOIR) ? blackName : whiteName;
        
        turnLabel.setText("Tour :    " + (currentTurn == Couleurcase.NOIR ? "Noir" : "Blanc") + " (" + currentPlayerName + ")");
        scoreLabel.setText(String.format("Score :    %s: %d  -  %s: %d", blackName, blackScore, whiteName, whiteScore));
    }
    
    public void showWinner(String message) {
        turnLabel.setText(message);
    }

    private String getName(String className) {
    switch (className) {
        case "HumanPlayer": return "Humain";
        case "RandomBot": return "Bot Aléatoire";
        case "BFSBot": return "BFS Bot";
        case "DFSBot" : return "DFS Bot";
        case "DijkstraBot" : return "Dijkstra Bot";
        default: return className;
    }
}
}

