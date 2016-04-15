package afester.javafx.examples.text;

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


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (TextFragment<S> frag : fragments) {
            result.append(frag);
        }

        return result.toString();
    }

    public List<TextFragment<S>> getFragments() {
        return fragments;
    }

    public PS getStyle() {
        return paragraphStyle;
    }

}
