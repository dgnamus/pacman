import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CellRenderer extends JLabel implements TableCellRenderer {

    private final ImageIcon wallIcon;
    private final ImageIcon foodIcon;
    private final ImageIcon ghostIcon;
    //private final ImageIcon pacmanOpenIcon;
    private final ImageIcon upgradeIcon;
    private final ImageIcon[] pacFrames = new ImageIcon[3];
    private final PacManApp pacManApp;


    public CellRenderer(PacManApp pacManApp) {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        wallIcon = loadIcon("./resources/wall.png");
        foodIcon = loadIcon("./resources/food.png");
        ghostIcon = loadIcon("./resources/ghost.png");
        //pacmanOpenIcon = loadIcon("./resources/pacman_open.png");
        pacFrames[0] = loadIcon("./resources/pacman1.png");
        pacFrames[1] = loadIcon("./resources/pacman2.png");
        pacFrames[2] = loadIcon("./resources/pacman3.png");
        //pacFrames[3] = loadIcon("./resources/pacman4.png");

        upgradeIcon = loadIcon("./resources/cherry.png");

        this.pacManApp = pacManApp;
    }

    private ImageIcon loadIcon(String path) {
        try {
            //System.out.println(wallIcon);
            return new ImageIcon(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(JTable table, Object type, boolean isSelected, boolean hasFocus, int row, int column) {

        setText("");
        setIcon(null);

        int width = table.getColumnModel().getColumn(column).getWidth();
        int height = table.getRowHeight(row);

        if (type instanceof CellTypes cellType) {
            switch (cellType) {
                case WALL -> {
                    setBackground(Color.blue);
                    if (wallIcon != null) {
                        Image scaledImage = wallIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case FOOD -> {
                    setBackground(Color.black);
                    if (foodIcon != null) {
                        Image scaledImage = foodIcon.getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case PACMAN -> {
                    setBackground(Color.black);
                    int frame = pacManApp.getPacmanFrame();
                    ImageIcon pacmanIcon = pacFrames[frame];
                    if (pacmanIcon != null) {
                        Image scaledImage = pacmanIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case GHOST -> {
                    setBackground(Color.black);
                    setOpaque(true);
                    if (ghostIcon != null) {
                        Image scaledImage = ghostIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case UPGRADE -> {
                    setBackground(Color.black);
                    setOpaque(true);
                    if (upgradeIcon != null) {
                        Image scaledImage = upgradeIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                default -> setBackground(Color.black);
            }
        } else {
            setBackground(Color.RED);
        }

        return this;
    }

}
