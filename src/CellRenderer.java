import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CellRenderer extends JLabel implements TableCellRenderer {

    private final ImageIcon wallIcon;

    public CellRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        wallIcon = loadIcon("./resources/wall.png");
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
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setText("");
        setIcon(null);

        if (value instanceof CellTypes cellType) {
            switch (cellType) {
                case WALL -> {
                    setBackground(Color.BLACK);
                    if (wallIcon != null) {
                        int width = table.getColumnModel().getColumn(column).getWidth();
                        int height = table.getRowHeight(row);
                        Image scaledImage = wallIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(scaledImage));
                    }
                }
                case EMPTY -> {
                    setBackground(Color.WHITE);
                }
                case PACMAN -> {
                    setBackground(Color.YELLOW);
                    setText("P");
                }
                case GHOST -> {
                    setBackground(Color.GRAY);
                    setText("G");
                }
                default -> setBackground(Color.PINK);
            }
        } else {
            setBackground(Color.GRAY);
        }

        return this;
    }

}
