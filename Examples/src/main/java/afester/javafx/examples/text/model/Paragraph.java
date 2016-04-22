package afester.javafx.examples.text.model;

import java.util.ArrayList;
import java.util.List;

public class Paragraph<S, PS> {

    private List<TextFragment<S>> fragments = new ArrayList<>();
    private PS paragraphStyle;


    public Paragraph(PS style) {
        this.paragraphStyle = style;
    }


    public void add(TextFragment<S> textFragment) {
        fragments.add(textFragment);
    }


    public List<TextFragment<S>> getFragments() {
        return fragments;
    }


    public PS getStyle() {
        return paragraphStyle;
    }


    // TODO: Why is length only calculated once????
    private int length = -1;
    
    /**
     * @return The length of the text in this paragraph, calculated 
     *         as the sum of the lengths of all text fragments.
     */
    public int length() {
        if (length == -1) {
            length = fragments.stream().mapToInt(TextFragment::length).sum();
        }

        return length;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (TextFragment<S> frag : fragments) {
            result.append(frag);
        }

        return result.toString();
    }
}
