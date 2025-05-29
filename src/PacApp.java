import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PacApp extends JFrame {

    private final CellModel cellModel;
    private final MyBoardModel boardModel;

    private volatile Direction pacmanDirection = Direction.NONE;

    public PacApp() {
        this.cellModel = new CellModel(30, 30); // board size
        this.boardModel = new MyBoardModel(cellModel);
        generateFrame();
        movePacmanThread();
        moveGhostThread();
    }

    private void movePacmanThread() {
        Thread movementThread = new Thread(() -> {
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
                            SwingUtilities.invokeLater(() -> boardModel.fireTableDataChanged());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        movementThread.setDaemon(true);
        movementThread.start();
    }

    private void moveGhostThread() {
        Thread ghostThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500); // move every 500ms

                    boolean ghostMoved = false;

                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    java.util.List<int[]> shuffledDirs = new java.util.ArrayList<>(Arrays.asList(directions));
                    java.util.Collections.shuffle(shuffledDirs);

                    for (int[] dir : shuffledDirs) {
                        if (cellModel.moveGhost(cellModel.getLucky(), dir[0], dir[1])) {
                            ghostMoved = true;
                            break; // move only once per cycle
                        }
                    }

                    if (ghostMoved) {
                        SwingUtilities.invokeLater(boardModel::fireTableDataChanged);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        ghostThread.setDaemon(true);
        ghostThread.start();
    }

    public void generateFrame() {

        JTable table = new JTable();
        table.setModel(boardModel);
        table.setRowHeight(30);
        table.setDefaultRenderer(Object.class, new CellRenderer());

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(30);
        }

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
        requestFocusInWindow();

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

}
