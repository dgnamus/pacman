import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

class LeaderBoardWindowModel extends AbstractListModel<String> {
    private final Vector<String> entries;

    public LeaderBoardWindowModel(List<HighScore> scores) {
        entries = new Vector<>();
        for (int i = 0; i < scores.size(); i++) {
            HighScore score = scores.get(i);
            entries.add((i + 1) + ". " + score.getPlayerName() + ": " + score.getHighScore());
        }
    }

    @Override
    public int getSize() {
        return entries.size();
    }

    @Override
    public String getElementAt(int index) {
        return entries.get(index);
    }
}

public class LeaderBoardWindow extends JFrame {

    public LeaderBoardWindow(List<HighScore> scores) {
        setTitle("High Scores");

        JList<String> jList = new JList<>();
        jList.setFont(new Font("Monospaced", Font.ITALIC, 14));
        jList.setBackground(Color.ORANGE);
        jList.setModel(new LeaderBoardWindowModel(scores));

        JScrollPane scrollPane = new JScrollPane(jList);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        closeButton.setBackground(Color.cyan);
        closeButton.addActionListener(_ -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.blue);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.PAGE_END);

        setSize(350, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
