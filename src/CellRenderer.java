import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CellRenderer extends JLabel implements TableCellRenderer {

    private final ImageIcon wallIcon;
    private final ImageIcon foodIcon;
    private final ImageIcon ghostIcon;
    private final ImageIcon pacmanOpenIcon;

    public CellRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        wallIcon = loadIcon("./resources/wall.png");
        foodIcon = loadIcon("./resources/food.png");
        ghostIcon = loadIcon("./resources/ghost.png");
        pacmanOpenIcon = loadIcon("./resources/pacman_open.png");
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
                    setBackground(Color.BLACK);
                    if (wallIcon != null) {
                        Image scaledImage = wallIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case EMPTY -> {
                    setBackground(Color.LIGHT_GRAY);
                }
                case FOOD -> {
                    setBackground(Color.LIGHT_GRAY);
                    if (foodIcon != null) {
                        Image scaledImage = foodIcon.getImage().getScaledInstance(5, 5, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case PACMAN -> {
                    setBackground(Color.LIGHT_GRAY);
                    if (pacmanOpenIcon != null) {
                        Image scaledImage = pacmanOpenIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case GHOST -> {
                    setBackground(Color.LIGHT_GRAY);
                    if (ghostIcon != null) {
                        Image scaledImage = ghostIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                default -> setBackground(Color.PINK);
            }
        } else {
            setBackground(Color.RED);
        }

        return this;
    }

}
