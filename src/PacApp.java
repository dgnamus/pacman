import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PacApp extends JFrame {

    public PacApp() {
        generateFrame();
    }

    public void generateFrame() {

        JTable table = new JTable();
        table.setModel(new MyBoardModel(new CellModel(10,15)));
        table.setRowHeight(30);

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);

        pack();

        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
