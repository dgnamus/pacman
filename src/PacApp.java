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
    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel livesLabel = new JLabel("Lives: 3");
    private volatile Direction pacmanDirection = Direction.NONE;
    private volatile boolean isGameOver = false;


    public PacApp() {
        int rows = getBoardRows("0");
        int columns = getBoardColumns("0");
        this.cellModel = new CellModel(rows, columns); // board size
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

        JTable table = new JTable();
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

            pacmanThread = new Thread(this::movePacmanThread);
            ghostThread = new Thread(this::moveGhostThread);
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
        //boardModel.fireTableDataChanged();
        setVisible(true);
        setLocationRelativeTo(null);
        requestFocusInWindow();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        isGameOver = true;
        pacmanDirection = Direction.NONE;
        JOptionPane.showMessageDialog(this, "Game Over! Press New Game to restart. Your score: " + cellModel.getPoints(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

}
