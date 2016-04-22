package afester.javafx.examples.text.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import afester.javafx.examples.text.model.TwoDimensional.Position;
import afester.javafx.examples.text.model.TwoDimensional.Bias;

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

public class Document<S, PS> {

    private List<Paragraph<S, PS>> paragraphs = new ArrayList<>();
    private final TwoLevelNavigator navigator;

    // The length of the document - TODO: How to update?????
    private final IntegerProperty length = new SimpleIntegerProperty(0);
    public int getLength() { return length.getValue(); }
    public IntegerProperty lengthProperty() { return length; }
    // @Override public int length() { return length.getValue(); }

    
    public Document() {
        navigator = 
                new TwoLevelNavigator(() -> paragraphs.size(),
                                      i -> paragraphs.get(i).length() + (i == paragraphs.size() - 1 ? 0 : 1));
    }


    public void add(Paragraph<S, PS> para) {
        paragraphs.add(para);
        length.setValue(getLength() + para.length());
    }


    public List<Paragraph<S, PS>> getParagraphs() {
        return paragraphs;
    }


    /**
     * @param row The row (paragraph index) of the location
     * @param col The column (character index within the paragraph) of the location
     *
     * @return A Position object which describes the given location.
     */
    public Position position(int row, int col) {
        return navigator.position(row, col);
    }


    /**
     * @param offset  The offset to use
     * @param bias    The direction (Forward/Backward) of the offset
     *
     * @return   The position of the given offset      
     */
    public Position offsetToPosition(int offset, Bias bias) {
        return navigator.offsetToPosition(offset, bias);
    }
}
