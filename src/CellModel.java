import java.util.*;

public class CellModel {

    private final CellTypes[][] board;
    private Pacman pacman;
    private final GhostBox<Ghost> ghostbox = new GhostBox<>();
    private int lives = 3;
    private int points = 0;
    private final List<Upgrade> upgrades = new ArrayList<>();
    private UpgradeListener upgradeListener;
    private volatile boolean immortal = false;
    private final Map<Ghost, CellTypes> ghostCellTypesMap = new HashMap<>();

    public CellModel(int rows, int columns) {
        board = new CellTypes[rows][columns];
        createBoard();
    }

    public void setUpgradeListener(UpgradeListener listener) {
        this.upgradeListener = listener;
    }

    public void dropUpgrade() {
        Random randInt = new Random();
        int height = board.length;
        int width = board[0].length;
        int tries = height * width;
        while (tries-- > 0) {
            int row = randInt.nextInt(height);
            int col = randInt.nextInt(width);
            if (board[row][col] == CellTypes.EMPTY || board[row][col] == CellTypes.FOOD) {
                UpgradeTypes upgradeType = UpgradeTypes.values()[randInt.nextInt(UpgradeTypes.values().length)];
                upgrades.add(new Upgrade(upgradeType, row, col));
                board[row][col] = CellTypes.UPGRADE;
                break;
            }
        }
    }

    private void createBoard() {
        int height = board.length;
        int width = board[0].length;
        Random rand = new Random();

        // defining the middle of the board
        int centerRow = board.length / 2;
        int centerCol = board[0].length / 2;

        // generate maze
        generateMazeWithPrimsAlgorithm(board, rand);
        generateBraids(board, rand);
        closeBoardEdges(board);
        // Create ghost box
        createGhostBox(board, centerRow, centerCol);

        // 4. Place FOOD on all EMPTY cells
        for (int i = 1; i < height - 1; i++)
            for (int j = 1; j < width - 1; j++)
                if (board[i][j] == CellTypes.EMPTY)
                    board[i][j] = CellTypes.FOOD;

        pacman = new Pacman(3, 3);

        // Placing Pacman on an empty board
        board[3][3] = CellTypes.PACMAN;

        //Clearing ghosts from the board as they are doubling otherwise
        ghostbox.getGhosts().clear();

        // Placing ghosts on an empty board
        BasicGhost ghost1 = new BasicGhost(centerRow + 1, centerCol + 1);
        BasicGhost ghost2 = new BasicGhost(centerRow + 1, centerCol - 1);
        BasicGhost ghost3 = new BasicGhost(centerRow - 1, centerCol - 1);
        BasicGhost ghost4 = new BasicGhost(centerRow - 1, centerCol + 1);
        addGhost(ghost1);
        addGhost(ghost2);
        addGhost(ghost3);
        addGhost(ghost4);

    }

    public int getBoardLength() {
        return board.length;
    }

    public int getBoardWidth() {
        return board[0].length;
    }

    public CellTypes getCell(int row, int column) {
        return board[row][column];
    }

    public void setCell(int row, int column, CellTypes type) {
        board[row][column] = type;
    }

    private void applyUpgrade(UpgradeTypes upgradeType) {
        switch (upgradeType) {
            case SPEED:
                // double pacman speed
                if (upgradeListener != null) {
                    upgradeListener.onUpgradePicked(UpgradeTypes.SPEED);
                }
                break;
            case LIFE:
                // add 1 extra life
                if (upgradeListener != null) {
                    upgradeListener.onUpgradePicked(UpgradeTypes.LIFE);
                }
                addLife();
                System.out.println("Add 1 life");
                break;
            case IMMORTAL:
                // become immortal for 10 seconds
                if (upgradeListener != null) {
                    upgradeListener.onUpgradePicked(UpgradeTypes.IMMORTAL);
                }
                System.out.println("You became immortal for 10 seconds");
                if (!immortal) {
                    immortal = true;
                    new Thread(() -> {
                        try {
                            Thread.sleep(20000);
                            immortal = false;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            immortal = false;
                            System.out.println("Back to mortality");
                        }
                    }).start();
                }
                break;
            case GET_POINTS:
                // get random amount of point between 100 and 500
                java.util.Random rand = new java.util.Random();
                int randomPoints = rand.nextInt(401) + 100;
                addPoints(randomPoints);
                if (upgradeListener != null) {
                    upgradeListener.onUpgradePicked(UpgradeTypes.GET_POINTS);
                }
                System.out.println("Get random amount of points");
                break;
            case REMOVE_GHOST:
                // remove 1 ghost from the board
                if (upgradeListener != null) {
                    upgradeListener.onUpgradePicked(UpgradeTypes.REMOVE_GHOST);
                }
                removeGhost();
                System.out.println("Remove 1 ghost");
                break;
        }
    }

    public boolean movePacman(int dRow, int dCol) {
        int currentRow = pacman.getRow();
        int currentCol = pacman.getColumn();
        int newRow = currentRow + dRow;
        int newCol = currentCol + dCol;

        if (newRow < 0 || newCol < 0 || newRow >= board.length || newCol >= board[0].length) return false;
        if (board[newRow][newCol] == CellTypes.WALL) return false;

        if (board[newRow][newCol] == CellTypes.GHOST) {
            if (!immortal) {
                removeLife();
                pacman.setPacmanPosition(3, 3);
            }
        }

        if (board[newRow][newCol] == CellTypes.FOOD) {
            addPoints(10);
        }

        if (board[newRow][newCol] == CellTypes.UPGRADE) {
            Upgrade picked = null;
            for (Upgrade upgrade : upgrades) {
                if (upgrade.getRow() == newRow && upgrade.getColumn() == newCol) {
                    picked = upgrade;
                    break;
                }
            }
            if (picked != null) {
                applyUpgrade(picked.getUpgradeType());
            }
            board[newRow][newCol] = CellTypes.EMPTY;
        }

        // Clear old Pacman position on board
        board[currentRow][currentCol] = CellTypes.EMPTY;

        // Move pacman position
        pacman.move(dRow, dCol, board);

        // Set new Pacman position on board
        board[pacman.getRow()][pacman.getColumn()] = CellTypes.PACMAN;

        return true;
    }

    public boolean moveGhost(Ghost ghost, int dRow, int dCol) {
        int currentRow = ghost.getRow();
        int currentCol = ghost.getColumn();
        int newRow = currentRow + dRow;
        int newCol = currentCol + dCol;

        if (newRow < 0 || newCol < 0 || newRow >= board.length || newCol >= board[0].length) return false;
        if (board[newRow][newCol] == CellTypes.WALL || board[newRow][newCol] == CellTypes.GHOST) return false;

        // Restore the cell the ghost is leaving to what it was before the ghost stood there
        CellTypes oldCell = ghostCellTypesMap.getOrDefault(ghost, CellTypes.EMPTY);
        board[currentRow][currentCol] = oldCell;

        // Remember what is in the cell the ghost is moving to
        ghostCellTypesMap.put(ghost, board[newRow][newCol]);

        // Move ghost position
        ghost.setGhostPosition(newRow, newCol);

        // Set new Ghost position on board
        board[ghost.getRow()][ghost.getColumn()] = CellTypes.GHOST;

        if (ghost.getRow() == pacman.getRow() && ghost.getColumn() == pacman.getColumn() && !immortal) {
            removeLife();
            pacman.setPacmanPosition(3, 3);
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[0].length; c++) {
                    if (board[r][c] == CellTypes.PACMAN) {
                        board[r][c] = CellTypes.EMPTY;
                    }
                }
            }
            board[3][3] = CellTypes.PACMAN;
        }

        return true;
    }

    public void addGhost(Ghost ghost) {
        ghostbox.addGhost(ghost);
        ghostCellTypesMap.put(ghost, board[ghost.getRow()][ghost.getColumn()]);
        board[ghost.getRow()][ghost.getColumn()] = CellTypes.GHOST;
    }

    public void removeGhost() {
        List<Ghost> ghosts = ghostbox.getGhosts();
        if (ghosts.isEmpty()) return;
        java.util.Random rand = new java.util.Random();
        int randomGhostIndex = rand.nextInt(ghosts.size());
        Ghost ghostToRemove = ghosts.get(randomGhostIndex);
        ghosts.remove(ghostToRemove);
        board[ghostToRemove.getRow()][ghostToRemove.getColumn()] = CellTypes.EMPTY;
    }

    public List<Ghost> getGhosts() {
        return ghostbox.getGhosts();
    }

    public int getPoints() {
        return points;
    }

    private void addPoints(int points) {
        this.points += points;
    }

    public int getLives() {
        return lives;
    }

    public void removeLife() {
        this.lives -= 1;
    }

    public void addLife() {
        if (this.lives < 3) {
            this.lives += 1;
        }
    }

    private void generateBraids(CellTypes[][] board, Random rand) {
        int height = board.length;
        int width = board[0].length;
        int[] dRows = {0, 0, -1, 1};
        int[] dCols = {-1, 1, 0, 0};

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (board[i][j] != CellTypes.EMPTY)
                    continue;
                // Count how many empty neighbors (if only 1, it's a dead end)
                int emptyNeighbors = 0;
                int wallDir = -1;
                for (int dir = 0; dir < 4; dir++) {
                    int ni = i + dRows[dir];
                    int nj = j + dCols[dir];
                    if (board[ni][nj] == CellTypes.EMPTY)
                        emptyNeighbors++;
                    else
                        wallDir = dir; // Last direction with wall
                }
                if (emptyNeighbors == 1) {
                    rand.nextDouble();// Remove a wall to make a loop
                    // (preferably not the direction we came from, but for simplicity, pick the last wall)
                    int ni = i + dRows[wallDir];
                    int nj = j + dCols[wallDir];
                    if (board[ni][nj] == CellTypes.WALL) {
                        board[ni][nj] = CellTypes.EMPTY;
                    }
                }
            }
        }
    }

    private void generateMazeWithPrimsAlgorithm(CellTypes[][] board, Random rand) {
        int height = board.length;
        int width = board[0].length;

        // Fill entire board with WALL
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                board[i][j] = CellTypes.WALL;

        // Choose a random starting cell with odd indices
        int startRow = 1 + 2 * rand.nextInt((height - 1) / 2);
        int startCol = 1 + 2 * rand.nextInt((width - 1) / 2);
        board[startRow][startCol] = CellTypes.EMPTY;

        // Frontier cells: Each element is {wallRow, wallCol, dir}
        // dir is 0:left, 1:right, 2:up, 3:down (direction from wall to next cell)
        java.util.List<int[]> frontier = new java.util.ArrayList<>();
        int[] dRows = {0, 0, -1, 1};
        int[] dCols = {-1, 1, 0, 0};
        for (int dir = 0; dir < 4; dir++) {
            int nRow = startRow + dRows[dir];
            int nCol = startCol + dCols[dir];
            int nRow2 = startRow + dRows[dir] * 2;
            int nCol2 = startCol + dCols[dir] * 2;
            if (nRow2 > 0 && nRow2 < height - 1 && nCol2 > 0 && nCol2 < width - 1) {
                frontier.add(new int[]{nRow, nCol, dir});
            }
        }

        while (!frontier.isEmpty()) {
            int fIdx = rand.nextInt(frontier.size());
            int[] f = frontier.remove(fIdx);
            int wallRow = f[0], wallCol = f[1], dir = f[2];
            int cellRow = wallRow + dRows[dir];
            int cellCol = wallCol + dCols[dir];

            if (cellRow > 0 && cellRow < height - 1 && cellCol > 0 && cellCol < width - 1
                    && board[cellRow][cellCol] == CellTypes.WALL) {
                // Carve through the wall and into the new cell
                board[wallRow][wallCol] = CellTypes.EMPTY;
                board[cellRow][cellCol] = CellTypes.EMPTY;

                // Add new frontier walls for the new cell
                for (int ndir = 0; ndir < 4; ndir++) {
                    int nnRow = cellRow + dRows[ndir];
                    int nnCol = cellCol + dCols[ndir];
                    int nnRow2 = cellRow + dRows[ndir] * 2;
                    int nnCol2 = cellCol + dCols[ndir] * 2;
                    if (nnRow2 > 0 && nnRow2 < height - 1 && nnCol2 > 0 && nnCol2 < width - 1
                            && board[nnRow2][nnCol2] == CellTypes.WALL) {
                        frontier.add(new int[]{nnRow, nnCol, ndir});
                    }
                }
            }
        }
    }

    private void closeBoardEdges(CellTypes[][] board) {
        int height = board.length;
        int width = board[0].length;
        // Top and bottom rows
        for (int j = 0; j < width; j++) {
            board[0][j] = CellTypes.WALL;
            board[height - 1][j] = CellTypes.WALL;
        }
        // Left and right columns
        for (int i = 0; i < height; i++) {
            board[i][0] = CellTypes.WALL;
            board[i][width - 1] = CellTypes.WALL;
        }
    }

    private void createGhostBox(CellTypes[][] board, int centerRow, int centerCol) {
        for (int i = -2; i < 2; i++) {
            for (int j = -2; j < 2; j++) {
                int r = centerRow + i;
                int c = centerCol + j;
                if (r > 0 && r < board.length - 1 && c > 0 && c < board[0].length - 1) {
                    board[r][c] = CellTypes.EMPTY;
                }
            }
        }
    }

}
