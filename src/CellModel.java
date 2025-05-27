public class CellModel {

    private CellTypes[][] board;

    public CellModel(int height, int width) {
        board = new CellTypes[height][width];
        createBoard();
    }

    private void createBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == 0 || j == 0 || i == board.length - 1 || j == board.length - 1) {
                    board[i][j] = CellTypes.WALL;
                } else {
                    board[i][j] = CellTypes.EMPTY;
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
}
