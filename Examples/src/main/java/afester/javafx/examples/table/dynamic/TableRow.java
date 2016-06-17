package afester.javafx.examples.table.dynamic;

import java.util.HashMap;
import java.util.Map;

public class TableRow {
    private final int rowId;
    private Map<Integer, String> values = new HashMap<>();

    public TableRow(int i) {
        this.rowId = i;
    }

    public int getRowIdx() {
        return rowId;
    }

    public String getValue(int i) {
        String result = values.get(i);
        if (result == null) {
            result = "Cell(" + i + ", " + rowId + ")";
        }
        return result;
    }

    public void setValue(int i, String value) {
        values.put(i,  value);
    }
}
