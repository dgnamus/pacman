import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class PacApp extends JFrame {

    private final CellModel cellModel;
    private final MyBoardModel boardModel;
    private Thread pacmanThread;
    private Thread ghostThread;
    private JLabel scoreLabel = new JLabel("Score: 0");
    private JLabel livesLabel = new JLabel("Lives: 3");
    private volatile Direction pacmanDirection = Direction.NONE;

    public PacApp() {
        this.cellModel = new CellModel(30, 30); // board size
        this.boardModel = new MyBoardModel(cellModel);

        generateFrame();
    }

    public void generateFrame() {

        JTable table = new JTable();
        table.setModel(boardModel);
        table.setRowHeight(20);
        table.setDefaultRenderer(Object.class, new CellRenderer());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        }

        table.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int newRowHeight = Math.max(10, table.getHeight() / table.getRowCount());
                table.setRowHeight(newRowHeight);
            }
        });

        JButton newGame = new JButton("New Game");
        JButton highScores = new JButton("High Scores");
        JButton exit = new JButton("Exit");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(newGame);
        buttons.add(highScores);
        buttons.add(exit);

        JPanel gameData = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gameData.add(scoreLabel);
        gameData.add(livesLabel);

        newGame.addActionListener(e -> {
            cellModel.reset();
            boardModel.fireTableDataChanged();
            scoreLabel.setText("Score: 0");
            pacmanDirection = Direction.NONE;

            if (pacmanThread != null && pacmanThread.isAlive()) {
                pacmanThread.interrupt();
            }
            if (ghostThread != null && ghostThread.isAlive()) {
                ghostThread.interrupt();
            }

            pacmanThread = new Thread(() -> movePacmanThread());
            ghostThread = new Thread(() -> moveGhostThread());
            PacApp.this.requestFocusInWindow();
            pacmanThread.setDaemon(true);
            ghostThread.setDaemon(true);
            pacmanThread.start();
            ghostThread.start();
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_W -> pacmanDirection = Direction.UP;
                    case java.awt.event.KeyEvent.VK_S -> pacmanDirection = Direction.DOWN;
                    case java.awt.event.KeyEvent.VK_A -> pacmanDirection = Direction.LEFT;
                    case java.awt.event.KeyEvent.VK_D -> pacmanDirection = Direction.RIGHT;
                }
            }
        });

        setFocusable(true);

        setLayout(new BorderLayout());
        add(buttons, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(gameData, BorderLayout.SOUTH);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        requestFocusInWindow();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void movePacmanThread() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(500); // move every 500ms
                if (pacmanDirection != Direction.NONE) {
                    boolean moved = false;
                    switch (pacmanDirection) {
                        case UP -> moved = cellModel.movePacman(-1, 0);
                        case DOWN -> moved = cellModel.movePacman(1, 0);
                        case LEFT -> moved = cellModel.movePacman(0, -1);
                        case RIGHT -> moved = cellModel.movePacman(0, 1);
                    }
                    if (moved) {
                        SwingUtilities.invokeLater(() -> {
                            boardModel.fireTableDataChanged();
                            scoreLabel.setText("Score: " + cellModel.getPoints());
                            livesLabel.setText("Lives: " + cellModel.getLives());
                        });
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void moveGhostThread() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(500); // move every 500ms

                boolean ghostMoved = false;

                int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                java.util.List<int[]> shuffledDirs = new java.util.ArrayList<>(Arrays.asList(directions));
                java.util.Collections.shuffle(shuffledDirs);

                for (Ghost ghost : cellModel.getGhosts()) {
                    if (ghost.move(cellModel)) {
                        ghostMoved = true;
                    }
                }

                if (ghostMoved) {
                    SwingUtilities.invokeLater(boardModel::fireTableDataChanged);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

}
