import java.util.List;

public class CellModel {

    private CellTypes[][] board;
    private Pacman pacman;
    private GhostBox<Ghost> ghostbox = new GhostBox<>();
    private int lives = 3;
    private int points = 0;

    public CellModel(int rows, int columns) {
        board = new CellTypes[rows][columns];
        createBoard();
    }

    private void createBoard() {
        int height = board.length;
        int width = board[0].length;

        // I'm starting with walls
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                board[i][j] = CellTypes.WALL;

        // Every second row, all columns
        for (int i = 1; i < height - 1; i += 2) {
            for (int j = 1; j < width - 1; j++) {
                board[i][j] = CellTypes.FOOD;
            }
        }

        // Every second column, all rows
        for (int j = 1; j < width - 1; j += 2) {
            for (int i = 1; i < height - 1; i++) {
                board[i][j] = CellTypes.FOOD;
            }
        }

        // defining the middle of the board
        int centerRow = board.length / 2;
        int centerCol = board[0].length / 2;

        pacman = new Pacman(3, 3);

        // Placing Pacman on a empty board
        board[3][3] = CellTypes.PACMAN;

        //Clearing ghosts from the board as they are doubling otherwise
        ghostbox.getGhosts().clear();

        // Placing ghosts on a empty board
        BasicGhost ghost1 = new BasicGhost(centerRow+1, centerCol-1);
        BasicGhost ghost2 = new BasicGhost(centerRow-1, centerCol+1);
        BasicGhost ghost3 = new BasicGhost(centerRow-1, centerCol-1);
        BasicGhost ghost4 = new BasicGhost(centerRow+1, centerCol+1);
        addGhost(ghost1);
        addGhost(ghost2);
        addGhost(ghost3);
        addGhost(ghost4);

    }

    public CellTypes[][] getBoard() {
        return board;
    }

    public int getSize() {
        return board.length;
    }

    public CellTypes getCell(int row, int column) {
        return board[row][column];
    }

    public void setCell(int row, int column, CellTypes type) {
        board[row][column] = type;
    }

    public void reset() {
        createBoard();
    }

    public boolean movePacman(int dRow, int dCol) {
        int currentRow = pacman.getRow();
        int currentCol = pacman.getColumn();
        int newRow = currentRow + dRow;
        int newCol = currentCol + dCol;

        if (newRow < 0 || newCol < 0 || newRow >= board.length || newCol >= board[0].length) return false;
        if (board[newRow][newCol] == CellTypes.WALL) return false;

        if (board[newRow][newCol] == CellTypes.GHOST) {
            removeLife();
        }

        if (board[newRow][newCol] == CellTypes.FOOD) {
            addPoints(10);
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

        if (board[newRow][newCol] == CellTypes.PACMAN) {
            removeLife();
        }

        // Clear old Ghost position on board
        board[currentRow][currentCol] = CellTypes.EMPTY;

        // Move ghost position
        ghost.setGhostPosition(newRow, newCol);

        // Set new Ghost position on board
        board[ghost.getRow()][ghost.getColumn()] = CellTypes.GHOST;

        return true;
    }

    public void addGhost(Ghost ghost) {
        ghostbox.addGhost(ghost);
        board[ghost.getRow()][ghost.getColumn()] = CellTypes.GHOST;
    }

    public List<Ghost> getGhosts() {
        return ghostbox.getGhosts();
    }

    public GhostBox<Ghost> getGhostbox() {
        return ghostbox;
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
}
