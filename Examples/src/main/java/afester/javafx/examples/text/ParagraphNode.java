package afester.javafx.examples.text;

import afester.javafx.examples.text.internal.TextFlowExt;
import afester.javafx.examples.text.model.Paragraph;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.PathElement;



public class ParagraphNode<PS, S> extends TextFlowExt {

    private Paragraph<PS, S> theParagraph;

    // the current position of the caret in this paragraph
    private final IntegerProperty caretPosition = new SimpleIntegerProperty();
    public IntegerProperty caretPositionProperty() { return caretPosition; }
    public void setCaretPosition(int pos) { caretPosition.setValue(pos); }
    // private final Val<Integer> clampedCaretPosition;

    // The index of this paragraph within its container
    private final IntegerProperty index = new SimpleIntegerProperty();
    public IntegerProperty indexProperty() { return index; }
    public void setIndex(int index) { this.index.setValue(index); }
    public int getIndex() { return index.getValue(); }

    // flag to indicate whether the caret is currently visible in this paragraph
    private final BooleanProperty caretVisible = new SimpleBooleanProperty();
    public BooleanProperty caretVisibleProperty() { return caretVisible; }

    private final Caret caret = new Caret();
    //private final Path selectionShape = new Path();


    public ParagraphNode(Paragraph<PS, S> para) {
        theParagraph = para;

        // Note: there seems to be a bug with unbind().
        // See https://bugs.openjdk.java.net/browse/JDK-8130458
        caret.showingProperty().bind(caretVisible);

        // bind paragraph insets to caret so that the caret position 
        // takes care of the paragraph insets
        insetsProperty().addListener((obs, oldValue, newValue) -> { 
            caret.layoutXProperty().setValue(newValue.getLeft());
            caret.layoutYProperty().setValue(newValue.getTop());
        });

        getChildren().add(caret);
    }

    public Paragraph<PS, S> getParagraph() {
        return theParagraph;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        System.err.println("ParagraphNode.layoutChildren");
        updateCaretShape();
        //updateSelectionShape();
        //updateBackgroundShapes();
    }


    private void updateCaretShape() {
        PathElement[] shape = getCaretShape(caretPosition.getValue(), true);
        caret.setShape(shape); // caretShape.getElements().setAll(shape);
    }
   
}
