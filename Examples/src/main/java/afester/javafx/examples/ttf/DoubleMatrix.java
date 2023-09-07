package afester.javafx.examples.ttf;

import java.util.ArrayList;
import java.util.List;


//public class Matrix<T extends Number> {
// NOTE: It is quite difficult to implement a Matrix class with a generic number type in Java.
// See https://github.com/stefanmuenchow/Generic-Arithmetic
//     https://stackoverflow.com/questions/14046209/using-a-generic-class-to-perform-basic-arithmetic-operations

public class DoubleMatrix {

    private final List<Double> data;
    private final int rowCount;
    private final int columnCount; 

    public DoubleMatrix(int rows, int columns) {
        data = new ArrayList<>(rows * columns);
        for (int idx = 0;  idx < rows * columns;  idx++) {
            data.add(0.0);
        }

        rowCount = rows;
        columnCount = columns;
    }


    @Override
    public String toString() {
        // determine column widths
        List<Integer> colWidths = getColumnWidths();

        // width of all columns
        int width = colWidths.stream().mapToInt(Integer::intValue).sum();
        
        // columnCount -1 columns have a "  " appended and there is one addtl. "|" at the start and end of each line
        width += 2*(columnCount - 1) + 2;

//        String header = String.format("/ %0" + (width - 4) +  "d \\\n", 0).replace("0", " "); 
//        String footer = String.format("\\ %0" + (width - 4) + "d /\n", 0).replace("0", " "); 
//        String header = String.format("/%0" + (width - 2) +  "d\\\n", 0).replace("0", " "); 
//        String footer = String.format("\\%0" + (width - 2) + "d/\n", 0).replace("0", " "); 
        String header = String.format(".-%0" + (width - 4) +  "d-.\n", 0).replace("0", " "); 
        String footer = String.format("`-%0" + (width - 4) + "d-.\n", 0).replace("0", " "); 

        StringBuffer result = new StringBuffer();
        int idx = 0;
        result.append(header);
        // result.append("+-         +|\n");
        for (int row = 0;  row < rowCount;  row++) {
            
            result.append('|');
            for (int column = 0;  column < columnCount;  column++) {
                final String format = "%" + colWidths.get(column) + "s"; 
                Double element = data.get(idx++);
                if (column > 0) {
                    result.append("  ");
                }
                result.append(String.format(format, element));
            }
            result.append("|\n");
        }
        result.append(footer);

        return result.toString();
    }


    private List<Integer> getColumnWidths() {
        List<Integer> result = new ArrayList<>(columnCount);
        for (int idx = 0;  idx < columnCount; idx++) {
            result.add(0);
        }

        for (int column = 0;  column < columnCount;  column++) {
            for (int row = 0;  row < rowCount;  row++) {
                int length = Math.max(result.get(column),  ("" + getElement(row, column)).length());
                result.set(column, length);
            }
        }

        return result;
    }


    public Double getElement(int row, int column) {
        return data.get(row * columnCount + column);
    }


    public void setElement(int row, int column, Double value) {
        data.set(row * columnCount + column, value);
    }
    
    
    public DoubleMatrix add(DoubleMatrix other) {
        DoubleMatrix result = new DoubleMatrix(rowCount, columnCount);

        for (int idx = 0; idx < data.size();  idx++) {
            result.data.set(idx, data.get(idx) + other.data.get(idx));
        }

        return result;
    }


    public DoubleMatrix multiply(DoubleMatrix B) {
        if (this.columnCount != B.rowCount) {
            throw new ArithmeticException("Can not multiply two matrices where A.columnCount != B.rowCount");
        }

        DoubleMatrix result = new DoubleMatrix(this.rowCount, B.columnCount);

        for (int aRows  = 0; aRows < this.rowCount;  aRows++) {
            for (int bColumns = 0; bColumns < B.columnCount;  bColumns++) {
                Double cellSum = 0.0;
                for (int idx = 0;  idx < this.columnCount;  idx++) {
                    cellSum += this.getElement(aRows, idx) * B.getElement(idx, bColumns);
                }
                result.setElement(aRows, bColumns, cellSum);
            }
        }
        return result;
    }


    public DoubleMatrix multiply(Double scalar) {
        DoubleMatrix result = new DoubleMatrix(rowCount, columnCount);

        for (int idx = 0; idx < data.size();  idx++) {
            result.data.set(idx, data.get(idx) * scalar);
        }

        return result;
    }


//    public Double getDeterminante() {
//        return 0.0;
//    }
}
