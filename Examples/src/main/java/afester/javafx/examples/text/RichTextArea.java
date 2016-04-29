package afester.javafx.examples.text;

import afester.javafx.examples.text.model.Document;
import afester.javafx.examples.text.model.Paragraph;
import afester.javafx.examples.text.model.StyledText;
import afester.javafx.examples.text.model.TwoDimensional.Position;
import afester.javafx.examples.text.skin.RichTextAreaBehaviour;
import afester.javafx.examples.text.skin.RichTextAreaSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.IndexRange;
import static afester.javafx.examples.text.model.TwoDimensional.Bias.*;


public class RichTextArea extends Control {

    private RichTextAreaSkin skin;
    private Document document;

    /* ****************** *
     *  Properties
     * ****************** */

    // The length of the text
    private final IntegerProperty length = new SimpleIntegerProperty();
    public final int getLength() { return length.getValue(); }
    public final IntegerProperty lengthProperty() { return length; }

    // The current paragraph index
    private final IntegerProperty currentParagraph = new SimpleIntegerProperty();
    public final int getCurrentParagraph() { return currentParagraph.getValue(); }
    public final IntegerProperty currentParagraphProperty() { return currentParagraph; }

    // The current column
    private final IntegerProperty caretColumn = new SimpleIntegerProperty();
    public final int getCaretColumn() { return caretColumn.getValue(); }
    public final IntegerProperty caretColumnProperty() { return caretColumn; }

    // The current caret position (absolute index of the character)
    private final IntegerProperty caretPosition = new SimpleIntegerProperty();
    public final int getCaretPosition() { return caretPosition.getValue(); }
    public final IntegerProperty caretPositionProperty() { return caretPosition; }

    // selection anchor
    private final IntegerProperty anchor = new SimpleIntegerProperty();
    public final int getAnchor() { return anchor.getValue(); }
    public final IntegerProperty anchorProperty() { return anchor; }

    // The current selection
    // private final Var<IndexRange> internalSelection = Var.newSimpleVar(EMPTY_RANGE);
    private final SimpleObjectProperty<IndexRange> selection = new SimpleObjectProperty<>(new IndexRange(0, 0));
    public final IndexRange getSelection() { return selection.getValue(); }
    public final ObservableValue<IndexRange> selectionProperty() { return selection; }


    /**
     * 
     */
    public RichTextArea() {

        caretPositionProperty().addListener(e -> {
            Position p = document.offsetToPosition(caretPosition.getValue(), Forward);
            currentParagraph.setValue(p.getMajor());
            caretColumn.setValue(p.getMinor());
        });
    }


    private static int clamp(int min, int val, int max) {
        return val < min ? min
             : val > max ? max
             : val;
    }

    /**
     * Moves the caret to the given position in the text
     * and clears any selection.
     * In RichTextFX, implemented as default method on NavigationActions
     */
    public void moveTo(int pos) {
        selectRange(pos, pos);
    }

    /**
     * Positions the anchor and caretPosition explicitly,
     * effectively creating a selection.
     * 
     * @param anchor    The start index of the selection.
     * @param caretPosition The end index of the selection (the current caret position)
     */
    public void selectRange(int anchor, int caretPosition) {
        System.err.printf("RichTextArea.selectRange: %d - %d\n", anchor, caretPosition);
        
 //       try(Guard g = suspend(
//                this.caretPosition, currentParagraph,
//                caretColumn, this.anchor,
//                selection, selectedText)) {
            this.caretPosition.setValue(clamp(0, caretPosition, getLength()));
            this.anchor.setValue(clamp(0, anchor, getLength()));
            this.selection.setValue(IndexRange.normalize(getAnchor(), getCaretPosition()));
//        }
 
    }


    @Override
    protected javafx.scene.control.Skin<?> createDefaultSkin() {
        // See http://tomasmikula.github.io/blog/2014/06/11/separation-of-view-and-controller-in-javafx-controls.html
        RichTextAreaSkin skin = new RichTextAreaSkin(this);
        RichTextAreaBehaviour behaviour = new RichTextAreaBehaviour(this, skin);
        return skin;
    }


    public void setDocument(Document doc) {
        this.document = doc;
        length.bind(document.lengthProperty()); // ??????
        if (skin != null) {
            skin.getTopLevelNode().refreshDocument();
        }
    }


    public Document getDocument() {
        return document;
    }


    public Position position(int row, int col) {
        return document.position(row, col);
    }

    /**
     * Replaces the selection with the given replacement String. If there is
     * no selection, then the replacement text is simply inserted at the current
     * caret position. If there was a selection, then the selection is cleared
     * and the given replacement text is inserted.
     */
    public void replaceSelection(String replacement) {
        replaceText(getSelection(), replacement);
    }

    public void replaceText(IndexRange range, String text) {
        replaceText(range.getStart(), range.getEnd(), text);
    }

    public void replaceText(int start, int end, String text) {
        
        
        Document doc = new Document();
        Paragraph para = new Paragraph<String, String>("p");
        para.add(new StyledText(text, ""));
        doc.add(para);

        document.replace(start, end, doc);
    }

    public void replace(int start, int end, Document replacement) {
        document.replace(start, end, replacement);
    }
}
