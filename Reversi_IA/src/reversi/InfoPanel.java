package reversi;

import javax.swing.*;
import java.awt.FlowLayout;

public class InfoPanel extends JPanel {

    private JLabel turnLabel;
    private JLabel scoreLabel;

    public InfoPanel() {
        setLayout(new FlowLayout());
        
        turnLabel = new JLabel("Tour : Noir");
        scoreLabel = new JLabel("Score : N 2 - B 2");
        
        add(turnLabel);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(scoreLabel);
    }

    public void updateInfo(Couleurcase currentTurn, int blackScore, int whiteScore) {
        turnLabel.setText("Tour : " + (currentTurn == Couleurcase.NOIR ? "Noir" : "Blanc"));
        scoreLabel.setText(String.format("Score : Noir %d - Blanc %d", blackScore, whiteScore));
    }
    
    public void showWinner(String message) {
        turnLabel.setText(message);
    }
}

