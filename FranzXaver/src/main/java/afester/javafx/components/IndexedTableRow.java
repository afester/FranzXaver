package afester.javafx.components;

import java.util.HashMap;
import java.util.Map;


/**
 * A table row which is referenced by an index.
 *
 * @param <S> The data type of each cell.
 */
class IndexedTableRow<S> {
    private int id;
    private Map<Integer, S> rowData = new HashMap<>();
    
    IndexedTableRow(int id) {
        this.id = id;
    }

    public int getRowNumber() {
        return id;
    }


    /**
     * @param colId The column index of the cell.
     * @return The value of the cell at the given column.
     */
    public S getValue(Integer colId) {
        return rowData.get(colId);
    }

    /**
     * Sets the value at the given column.
     * 
     * @param column The column index of the cell.
     * @param value  The value to set for the cell.
     */
    public void setValue(int column, S value) {
        rowData.put(column , value);
    }


    @Override
    public String toString() {
        return String.format("TableRow[%s %s]",  id, rowData);
    }
}