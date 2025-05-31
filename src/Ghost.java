public interface Ghost {
    int getRow();
    int getColumn();
    void setGhostPosition(int row, int column);
    boolean move(CellModel cellModel);
}
