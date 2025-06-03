import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class PacApp extends JFrame {

    private CellModel cellModel;
    private MyBoardModel boardModel;
    private JTable table;
    private Thread pacmanThread;
    private Thread ghostThread;
    private JLabel scoreLabel = new JLabel("Score: 0");
    private JLabel livesLabel = new JLabel("Lives: 3");
    private volatile Direction pacmanDirection = Direction.NONE;
    private volatile boolean isGameOver = false;
    private final Leaderboard leaderboard = new Leaderboard();



    public PacApp() {
        this.cellModel = new CellModel(100, 100); // board size
        this.boardModel = new MyBoardModel(cellModel);

        generateFrame();
    }

    private int getBoardRows(String rows) {
        String input = JOptionPane.showInputDialog("Enter the number of rows (10-100):", rows);
        try {
            int rowsInt = Integer.parseInt(input);
            if (rowsInt >= 10 && rowsInt <= 100) {
                return rowsInt;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number between 10 and 100.");
                return getBoardRows(rows);
            }
        } catch (NumberFormatException e) {
            return getBoardRows(rows);
        }
    }

    private int getBoardColumns(String columns) {
        String input = JOptionPane.showInputDialog("Enter the number of columns (10-100):", columns);
        try {
            int columnsInt = Integer.parseInt(input);
            if (columnsInt >= 10 && columnsInt <= 100) {
                return columnsInt;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number between 10 and 100.");
                return getBoardColumns(columns);
            }
        } catch (NumberFormatException e) {
            return getBoardColumns(columns);
        }
    }

    public void generateFrame() {
        // creating table
        table = new JTable();
        table.setModel(boardModel);
        table.setRowHeight(30);
        table.setDefaultRenderer(Object.class, new CellRenderer());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(25);
        }

        table.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int newRowHeight = Math.max(10, table.getHeight() / table.getRowCount());
                table.setRowHeight(newRowHeight);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        table.setTableHeader(null);

        // creating buttons
        JButton newGame = new JButton("New Game");
        JButton highScores = new JButton("High Scores");
        JButton exit = new JButton("Exit");

        // wrapping buttons into a menu
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(newGame);
        buttons.add(highScores);
        buttons.add(exit);

        // wrapping game data
        JPanel gameData = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gameData.add(scoreLabel);
        gameData.add(livesLabel);

        // finalizing layour of the game
        setFocusable(true);
        setLayout(new BorderLayout());
        add(buttons, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(gameData, BorderLayout.SOUTH);

        pack();
        //boardModel.fireTableDataChanged();
        setVisible(true);
        setLocationRelativeTo(null);
        requestFocusInWindow();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Exiting with ctrl shift Q key combination
        InputMap input = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap action = getRootPane().getActionMap();
        KeyStroke exitCombo = KeyStroke.getKeyStroke("control shift Q");
        input.put(exitCombo, "exitGame");
        action.put("exitGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameOver();
            }
        });

        // Action listeners for new game,  exit and leaderboard buttons
        exit.addActionListener(e -> System.exit(0));

        highScores.addActionListener(e -> showHighScores());

        newGame.addActionListener(e -> {
            isGameOver = false;

            int rows = getBoardRows("0");
            int columns = getBoardColumns("0");
            cellModel = new CellModel(rows, columns);
            boardModel = new MyBoardModel(cellModel);
            table.setModel(boardModel);


            scoreLabel.setText("Score: 0");
            pacmanDirection = Direction.NONE;

            if (pacmanThread != null && pacmanThread.isAlive()) {
                pacmanThread.interrupt();
            }
            if (ghostThread != null && ghostThread.isAlive()) {
                ghostThread.interrupt();
            }

            pacmanThread = new Thread(this::movePacmanThread);
            ghostThread = new Thread(this::moveGhostThread);
            PacApp.this.requestFocusInWindow();
            pacmanThread.setDaemon(true);
            ghostThread.setDaemon(true);
            pacmanThread.start();
            ghostThread.start();
        });

        // Direction action listeners
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
    }

    private void movePacmanThread() {
        while (!Thread.currentThread().isInterrupted() && !isGameOver) {
            try {
                Thread.sleep(500);
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
                if (cellModel.getLives() == 0) {
                    gameOver();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void moveGhostThread() {
        while (!Thread.currentThread().isInterrupted() && !isGameOver) {
            try {
                Thread.sleep(500);

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

                if (cellModel.getLives() == 0) {
                    gameOver();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void gameOver() {
        if (isGameOver) return;

        isGameOver = true;
        pacmanDirection = Direction.NONE;
        JOptionPane.showMessageDialog(this, "Game Over! Press New Game to restart. Your score: " + cellModel.getPoints(), "Game Over", JOptionPane.INFORMATION_MESSAGE);

        String name = JOptionPane.showInputDialog(this,"Enter your name for the leaderboard","Game Over", JOptionPane.PLAIN_MESSAGE);

        if (name != null) {
            leaderboard.addScore(name, cellModel.getPoints());
        }
    }

    private void showHighScores() {
        java.util.List<HighScore> scores = leaderboard.getScores();
        StringBuilder stringBuilder = new StringBuilder("High Scores:\n");
        for (int i = 0; i < scores.size(); i++) {
            HighScore score = scores.get(i);
            stringBuilder.append(i + 1).append(". ").append(score.getPlayerName()).append(": ").append(score.getHighScore()).append("\n");
        }
        JOptionPane.showMessageDialog(this, stringBuilder.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

}
