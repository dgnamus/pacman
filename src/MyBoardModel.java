import javax.swing.table.AbstractTableModel;

public class MyBoardModel extends AbstractTableModel {
    private CellModel cellmodel;

    public MyBoardModel(CellModel cellmodel) {
        this.cellmodel = cellmodel;
    }

    @Override
    public int getRowCount() {
        return cellmodel.getBoardLength();
    }

    @Override
    public int getColumnCount() {
        return cellmodel.getBoardWidth();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return cellmodel.getCell(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        cellmodel.setCell(rowIndex, columnIndex, (CellTypes) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

}
