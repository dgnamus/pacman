public class Upgrade {
    private final UpgradeTypes upgradeType;
    private final int row;
    private final int column;

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
}
