import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class PacManApp extends JFrame {

    private CellModel cellModel;
    private MyBoardModel boardModel;
    private JTable table;
    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel timerLabel = new JLabel("Timer: 0");
    private final JLabel bonusLabel = new JLabel("");
    private Thread pacmanThread;
    private Thread ghostThread;
    private Thread upgradeThread;
    private Thread timerThread;
    private Thread pacmanEatingThread;

    private Hearts hearts;
    private volatile Direction pacmanDirection = Direction.NONE;
    private volatile boolean isGameOver = false;
    private volatile boolean upgradeThreadRunning = false;
    private int pacmanFrame = 0;
    private volatile boolean pacmanOpeningMouth = true;
    private volatile boolean pacmanEating = false;
    private volatile int pacmanSpeed = 500;
    private int timer = 0;
    private volatile boolean timerRunning = false;
    private final Leaderboard leaderboard = new Leaderboard();
    private JScrollPane scrollPane;


    public PacManApp() {
        this.cellModel = new CellModel(15, 15); // board size
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
        table.setDefaultRenderer(Object.class, new CellRenderer(this));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(30);
        }

        scrollPane = new JScrollPane(table);
        table.setTableHeader(null);

        // creating buttons and adding some styles
        JButton newGame = new JButton("New Game");
        newGame.setFont(new Font("Monospace", Font.BOLD, 18));
        newGame.setOpaque(true);
        newGame.setBackground(Color.yellow);
        newGame.setBorderPainted(false);
        JButton highScores = new JButton("High Scores");
        highScores.setFont(new Font("Monospace", Font.PLAIN, 18));
        highScores.setOpaque(true);
        highScores.setBackground(Color.yellow);
        highScores.setBorderPainted(false);
        JButton exit = new JButton("Exit");
        exit.setFont(new Font("Monospace", Font.ITALIC, 18));
        exit.setOpaque(true);
        exit.setBackground(Color.yellow);
        exit.setBorderPainted(false);

        // wrapping buttons into a menu
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(newGame);
        buttons.add(highScores);
        buttons.add(exit);
        buttons.setBackground(Color.pink);
        JScrollPane buttonsScrollPane = new JScrollPane(buttons);

        // creating bottom row game data
        JPanel gameDataFirstRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel gameDataSecondRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel gameDataThirdRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel gameDataFourthRow = new JPanel(new FlowLayout(FlowLayout.CENTER));

        hearts = new Hearts(cellModel.getLives());
        JLabel exitInstruction = new JLabel("Press ctrl+shift+Q to exit  ");
        exitInstruction.setFont(new Font("Monospace", Font.ITALIC, 14));
        scoreLabel.setFont(new Font("Monospace", Font.BOLD, 14));
        bonusLabel.setFont(new Font("Monospace", Font.BOLD, 16));

        gameDataFirstRow.add(exitInstruction);
        gameDataSecondRow.add(scoreLabel);
        gameDataSecondRow.add(hearts);
        gameDataThirdRow.add(timerLabel);
        gameDataFourthRow.add(bonusLabel);
        gameDataFirstRow.setBackground(Color.pink);
        gameDataSecondRow.setBackground(Color.pink);
        gameDataThirdRow.setBackground(Color.pink);
        gameDataFourthRow.setBackground(Color.pink);


        JPanel gameData = new JPanel(new GridLayout(4, 1));
        gameData.add(gameDataFirstRow);
        gameData.add(gameDataSecondRow);
        gameData.add(gameDataThirdRow);
        gameData.add(gameDataFourthRow);
        gameData.setBackground(Color.pink);
        JScrollPane gameDataScrollPanel = new JScrollPane(gameData);


        // finalizing layout of the game
        setFocusable(true);
        setLayout(new BorderLayout());
        add(buttonsScrollPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(gameDataScrollPanel, BorderLayout.SOUTH);

        // part to get window size to adjust properly
        resizeWindow();

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
        exit.addActionListener(_ -> System.exit(0));

        highScores.addActionListener(_ -> showHighScores());

        newGame.addActionListener(_ -> {
            isGameOver = false;

            int rows = getBoardRows("0");
            int columns = getBoardColumns("0");
            cellModel = new CellModel(rows, columns);
            cellModel.setUpgradeListener(upgradeType -> {
                showBonus(getBonusText(upgradeType));
                if (upgradeType == UpgradeTypes.SPEED) {
                    increasePacmanSpeed();
                }
            });

            boardModel = new MyBoardModel(cellModel);
            table.setModel(boardModel);

            resizeWindow();

            scoreLabel.setText("Score: 0");
            pacmanDirection = Direction.NONE;

            if (pacmanThread != null && pacmanThread.isAlive()) {
                pacmanThread.interrupt();
            }
            if (ghostThread != null && ghostThread.isAlive()) {
                ghostThread.interrupt();
            }
            if (upgradeThread != null && upgradeThread.isAlive()) {
                stopUpgradeThread();
            }

            pacmanThread = new Thread(this::movePacmanThread);
            ghostThread = new Thread(this::moveGhostThread);
            startUpgradeThread();
            startTimer();
            startPacmanEatingThread();
            PacManApp.this.requestFocusInWindow();
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

    public void resizeWindow() {
        int cellSize = 30;
        int topMenuHeight = 120;
        int bottomMenuHeight = 120;

        int newWidth = table.getColumnCount() * cellSize;
        int newHeight = table.getRowCount() * cellSize + topMenuHeight + bottomMenuHeight;

        // part to solve the issue when it would go over the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        newWidth = Math.min(newWidth, screenSize.width);
        newHeight = Math.min(newHeight, screenSize.height);

        //preferred size for scrollable
        scrollPane.setPreferredSize(new Dimension(newWidth, newHeight));

        // preferred size for frame
        setPreferredSize(new Dimension(newWidth, newHeight));
        pack();
        Dimension maximumSize = getPreferredSize();
        setMaximumSize(maximumSize);
        setLocationRelativeTo(null);
    }

    private void movePacmanThread() {
        while (!Thread.currentThread().isInterrupted() && !isGameOver) {
            try {
                Thread.sleep(pacmanSpeed);
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
                            //livesLabel.setText("Lives: " + cellModel.getLives());
                            hearts.updateHearts(cellModel.getLives());
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

    private void startPacmanEatingThread() {
        pacmanEating = true;
        pacmanEatingThread = new Thread(() -> {
           while (pacmanEating && !isGameOver) {
               try {
                   Thread.sleep(300);
                   if (pacmanDirection != Direction.NONE) {
                       if (pacmanOpeningMouth) {
                           pacmanFrame++;
                           if (pacmanFrame == 2) {
                               pacmanOpeningMouth = false;
                           }
                       } else {
                           pacmanFrame--;
                           if (pacmanFrame == 0) {
                               pacmanOpeningMouth = true;
                           }
                       }
                   }
                   SwingUtilities.invokeLater(() -> boardModel.fireTableDataChanged());
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               }
           }
        });
        pacmanEatingThread.setDaemon(true);
        pacmanEatingThread.start();
    }

    private void stopPacmanEatingThread() {
        pacmanEating = false;
        if (pacmanEatingThread != null) {
            pacmanEatingThread.interrupt();
        }
    }

    private void startTimer() {
        timerRunning = true;
        timerLabel.setText("Time: 0");
        timerThread = new Thread(() -> {
            while (timerRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (timerRunning) {
                    timer++;
                    SwingUtilities.invokeLater(() -> timerLabel.setText("Time: " + timer));
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void stopTimer() {
        timerRunning = false;
        if (timerThread != null) {
            timerThread.interrupt();
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

    private void startUpgradeThread() {
        upgradeThreadRunning = true;
        upgradeThread = new Thread(() -> {
            java.util.Random randInt = new java.util.Random();
            while (upgradeThreadRunning && !isGameOver) {
                try {
                    Thread.sleep(5000);
                    // create 25% chance for upgrade to appear
                    if (randInt.nextInt(4) == 0) {
                        cellModel.dropUpgrade();
                        SwingUtilities.invokeLater(() -> boardModel.fireTableDataChanged());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        upgradeThread.setDaemon(true);
        upgradeThread.start();
    }

    private void stopUpgradeThread() {
        upgradeThreadRunning = false;
        if (upgradeThread != null) {
            upgradeThread.interrupt();
        }
    }

    public void showBonus(String text) {
        SwingUtilities.invokeLater(() -> {
            bonusLabel.setText(text);
            bonusLabel.revalidate();
            bonusLabel.repaint();
        });
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            SwingUtilities.invokeLater(() -> {
                if (bonusLabel.getText().equals(text)) {
                    bonusLabel.setText("");
                    bonusLabel.revalidate();
                    bonusLabel.repaint();
                }
            });
        }).start();
    }

    private void gameOver() {
        if (isGameOver) return;

        isGameOver = true;
        stopUpgradeThread();
        stopTimer();
        stopPacmanEatingThread();
        pacmanDirection = Direction.NONE;
        hearts.updateHearts(cellModel.getLives());
        JOptionPane.showMessageDialog(this, "Game Over! Press New Game to restart. Your score: " + cellModel.getPoints(), "Game Over", JOptionPane.INFORMATION_MESSAGE);

        String name = JOptionPane.showInputDialog(this,"Enter your name for the leaderboard","Game Over", JOptionPane.PLAIN_MESSAGE);

        if (name != null) {
            leaderboard.addScore(name, cellModel.getPoints());
        }
    }

    private void showHighScores() {
       java.util.List<HighScore> highScores = leaderboard.getScores();
       SwingUtilities.invokeLater(() -> new LeaderBoardWindow(highScores).setVisible(true));
    }

    public void increasePacmanSpeed() {
        pacmanSpeed = 250;
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                pacmanSpeed = 500;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public int getPacmanFrame() {
        return pacmanFrame;
    }

    private String getBonusText(UpgradeTypes upgradeType) {
        return switch (upgradeType) {
            case SPEED -> "Speed Up!";
            case LIFE -> "Extra Life!";
            case IMMORTAL -> "Immortal!";
            case GET_POINTS -> "Bonus Points!";
            case REMOVE_GHOST -> "Ghost Removed!";
        };
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

}
