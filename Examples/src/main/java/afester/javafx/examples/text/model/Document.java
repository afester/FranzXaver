package afester.javafx.examples.text.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import afester.javafx.examples.text.model.TwoDimensional.Position;
import afester.javafx.examples.text.model.TwoDimensional.Bias;


public class Document<S, PS> {

    private ObservableList<Paragraph<S, PS>> paragraphs = FXCollections.observableArrayList();
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


    public void replaceText(int start, int end, String text) {
    }


    public void replace(int start, int end, Document replacement) {
        Position startPosition = navigator.offsetToPosition(start, Bias.Forward);
        Position endPosition = startPosition.offsetBy(end - start, Bias.Forward);
        int firstParIdx = startPosition.getMajor();
        int firstParFrom = startPosition.getMinor();
        int lastParIdx = endPosition.getMajor();
        int lastParTo = endPosition.getMinor();

        // Get the first paragraph which is affected by the text replacement.
        // This paragraph contains all segments up to the one where the start position
        // of the replacement is contained.
        Paragraph<S, PS> firstPar = paragraphs.get(firstParIdx).trim(firstParFrom);;

        // Get the last paragraph which is affected by the text replacement.
        // This paragraph contains all segments from the end position of the
        // replacement up to the end of the paragraph.
        Paragraph<S, PS> lastPar = paragraphs.get(lastParIdx).subSequence(lastParTo);

        List<Paragraph<S, PS>> replacementPars = replacement.getParagraphs();

        // Join the first paragrah, the replacement paragraphs and the last paragraph
        // so that they make up a list of new paragraphs to be set in the document.
        List<Paragraph<S, PS>> newPars = join(firstPar, replacementPars, lastPar);

        setAll(firstParIdx, lastParIdx + 1, newPars);
    }


    private void setAll(int startIdx, int endIdx, Collection<Paragraph<S, PS>> pars) {
        if(startIdx > 0 || endIdx < paragraphs.size()) {
            paragraphs.subList(startIdx, endIdx).clear(); // note that paragraphs remains non-empty at all times
            paragraphs.addAll(startIdx, pars);
        } else {
            paragraphs.setAll(pars);
        }
    }


    private List<Paragraph<S, PS>> join(Paragraph<S, PS> first, List<Paragraph<S, PS>> middle, Paragraph<S, PS> last) {

        int m = middle.size();
        if (m == 0) {       // nothing to insert.
            return Arrays.asList(first.concat(last));
        } else if (m == 1) { // insert one paragraph.
            Paragraph<S, PS> par = middle.get(0);
            Paragraph<S, PS> result = first.concat(par).concat(last);
            return Arrays.asList(result);
        } else {             // insert multiple paragraphs.
            List<Paragraph<S, PS>> res = new ArrayList<>(middle.size());
            res.add(first.concat(middle.get(0)));
            res.addAll(middle.subList(1, m - 1));
            res.add(middle.get(m - 1).concat(last));
            return res;
        }
    }
    

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("Document[\n");

        for (Paragraph<S, PS> p : getParagraphs()) {
            result.append("  " + p + "\n");
            for (TextFragment<S> t : p.getFragments()) {
                result.append("    " + t + "\n");
            }
        }

        result.append("]");

        return result.toString();
    }
}
