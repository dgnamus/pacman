public class Pacman {

    private int row;
    private int column;

    public Pacman(int startRow, int startColumn) {
        this.row = startRow;
        this.column = startColumn;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void move(int dRow, int dCol, CellTypes[][] board) {
        int newRow = row + dRow;
        int newCol = column + dCol;

        if (newRow < 0 || newCol < 0 || newRow >= board.length || newCol >= board[0].length) return;
        if (board[newRow][newCol] == CellTypes.WALL) return;

        board[row][column] = CellTypes.EMPTY; // clear old position
        row = newRow;
        column = newCol;
        board[row][column] = CellTypes.PACMAN; // set new position
    }
}

