public class Ghost {
    private int row;
    private int column;

    public Ghost(int startRow, int startColumn) {
        this.row = startRow;
        this.column = startColumn;
    }

    public void setGhostPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
