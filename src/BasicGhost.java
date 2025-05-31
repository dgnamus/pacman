public class BasicGhost implements Ghost {
    private int row;
    private int column;

    public BasicGhost(int startRow, int startColumn) {
        this.row = startRow;
        this.column = startColumn;
    }

    @Override
    public void setGhostPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean move(CellModel cellModel) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        java.util.List<int[]> shuffledDirs = new java.util.ArrayList<>(java.util.Arrays.asList(directions));
        java.util.Collections.shuffle(shuffledDirs);

        for (int[] dir : shuffledDirs) {
            if (cellModel.moveGhost(this, dir[0], dir[1])) {
                return true;
            }
        }
        return false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
