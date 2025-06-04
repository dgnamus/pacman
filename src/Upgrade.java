public class Upgrade {
    private UpgradeTypes upgradeType;
    private int row;
    private int column;

    public Upgrade(UpgradeTypes upgradeType, int row, int column) {
        this.upgradeType = upgradeType;
        this.row = row;
        this.column = column;
    }

    public UpgradeTypes getUpgradeType() {
        return upgradeType;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setUpgradeType(UpgradeTypes upgradeType) {
        this.upgradeType = upgradeType;
    }

//    public void applyUpgrade(Pacman pacman, CellModel cellModel) {
//        switch (upgradeType) {
//            case ADD_LIFE:
//                cellModel.addLife();
//                break;
//            case ADD_SCORE:
//                cellModel.addScore(10);
//                break;
//            case REMOVE_GHOST:
//                cellModel.freezeGhosts();
//                break;
//            case EXTRA_LIFE:
//                cellModel.addLife();
//                break;
//            case GHOST_TO_PACMAN:
//                cellModel.ghostToPacman();
//                break;
//        }
//    }
}
