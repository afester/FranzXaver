package afester.javafx.shapes;

import java.util.Arrays;
import java.util.List;

public enum LineDash {
    SOLID(1.0), 
    DOTTED(2.0, 2.0), 
    DASHED(10.0, 10.0), 
    DASHDOTTED(10.0, 2.0, 2.0, 2.0);

    private List<Double> dashElements;

    
    private LineDash(Double... elements) {
        dashElements = Arrays.asList(elements);
    }

    public List<Double> getDashArray() {
        return dashElements;
    }
}
