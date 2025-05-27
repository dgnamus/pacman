import javax.swing.table.AbstractTableModel;

public class MyBoardModel extends AbstractTableModel {
    private CellModel cellmodel;

    public MyBoardModel(CellModel cellmodel) {
        this.cellmodel = cellmodel;
    }

    @Override
    public int getRowCount() {
        return cellmodel.getSize();
    }

    @Override
    public int getColumnCount() {
        return cellmodel.getSize();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return cellmodel.getCell(rowIndex, columnIndex);
    }
}
