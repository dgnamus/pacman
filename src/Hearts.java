import javax.swing.*;
import java.awt.*;

public class Hearts extends JPanel {
    private final ImageIcon heartImage = new ImageIcon("./resources/heart.png");
    private int lives;

    public Hearts(int lives) {
        this.lives = lives;
        setOpaque(false);
        updateHearts(lives);
    }

    public void updateHearts(int lives) {
        this.lives = lives;
        removeAll();
        for (int i = 0; i < lives; i++) {
            JLabel heartLabel = new JLabel();
            heartLabel.setIcon(new ImageIcon(heartImage.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH)));
            add(heartLabel);
        }
        revalidate();
        repaint();
    }
}
