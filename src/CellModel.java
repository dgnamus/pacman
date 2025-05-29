public class CellModel {

    private CellTypes[][] board;
    private Pacman pacman;
    private Ghost lucky;

    public CellModel(int height, int width) {
        board = new CellTypes[height][width];
        createBoard();
    }

    private void createBoard() {
        int height = board.length;
        int width = board[0].length;

        // I'm starting with walls
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                board[i][j] = CellTypes.WALL;

        // Carve out corridors in every second row and column
        for (int i = 1; i < height - 1; i += 2) {
            for (int j = 1; j < width - 1; j += 2) {
                board[i][j] = CellTypes.EMPTY;
            }
        }

        // Randomly remove some walls to create passages
        java.util.Random rand = new java.util.Random();
        for (int i = 1; i < height - 2; i += 2) {
            for (int j = 1; j < width - 2; j += 2) {
                if (rand.nextBoolean()) board[i][j + 1] = CellTypes.EMPTY;
                if (rand.nextBoolean()) board[i + 1][j] = CellTypes.EMPTY;
            }
        }

        // defining the middle of the board
        int centerRow = board.length / 2;
        int centerCol = board[0].length / 2;

        // finding a place for Lucky ghost
        int topLeftRow = 2;
        int topLeftCol = 2;

        pacman = new Pacman(centerRow, centerCol);
        lucky = new Ghost(topLeftRow, topLeftCol);

        // Placing Pacman on a empty board
        if (board[centerRow][centerCol] == CellTypes.EMPTY) {
            board[centerRow][centerCol] = CellTypes.PACMAN;
        } else {
            // Find nearby EMPTY cell if center is occupied
            occupied:
            for (int r = centerRow - 1; r <= centerRow + 1; r++) {
                for (int c = centerCol - 1; c <= centerCol + 1; c++) {
                    if (board[r][c] == CellTypes.EMPTY) {
                        board[r][c] = CellTypes.PACMAN;
                        break occupied;
                    }
                }
            }
        }

        // Placing lucky on a empty board
        if (board[topLeftRow][topLeftCol] == CellTypes.EMPTY) {
            board[topLeftRow][topLeftCol] = CellTypes.GHOST;
            lucky.setGhostPosition(topLeftRow, topLeftCol);
        } else {
            // Find nearby EMPTY cell if center is occupied
            occupied:
            for (int r = topLeftRow; r <= topLeftRow + 1; r++) {
                for (int c = topLeftCol; c <= topLeftCol + 1; c++) {
                    if (board[r][c] == CellTypes.EMPTY) {
                        board[r][c] = CellTypes.GHOST;
                        lucky.setGhostPosition(r, c);
                        break occupied;
                    }
                }
            }
        }
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

    public synchronized boolean movePacman(int dRow, int dCol) {
        int currentRow = pacman.getRow();
        int currentCol = pacman.getColumn();
        int newRow = currentRow + dRow;
        int newCol = currentCol + dCol;

        if (newRow < 0 || newCol < 0 || newRow >= board.length || newCol >= board[0].length) return false;
        if (board[newRow][newCol] == CellTypes.WALL) return false;

        // Clear old Pacman position on board
        board[currentRow][currentCol] = CellTypes.EMPTY;

        // Move pacman position
        pacman.move(dRow, dCol, board);

        // Set new Pacman position on board
        board[pacman.getRow()][pacman.getColumn()] = CellTypes.PACMAN;

        return true;
    }

    public synchronized boolean moveGhost(Ghost ghost, int dRow, int dCol) {
        int currentRow = ghost.getRow();
        int currentCol = ghost.getColumn();
        int newRow = currentRow + dRow;
        int newCol = currentCol + dCol;

        if (newRow < 0 || newCol < 0 || newRow >= board.length || newCol >= board[0].length) return false;
        if (board[newRow][newCol] == CellTypes.WALL) return false;

        // Clear old Ghost position on board
        board[currentRow][currentCol] = CellTypes.EMPTY;

        // Move ghost position
        ghost.setGhostPosition(newRow, newCol);

        // Set new Ghost position on board
        board[ghost.getRow()][ghost.getColumn()] = CellTypes.GHOST;

        return true;
    }

    public Ghost getLucky() {
        return lucky;
    }
}
