package afester.javafx.examples.text;

public class TextFragment<S> {

    // The text of this text fragment
    private String text;

    // The style which is applied to this text fragment
    private S style;

    public TextFragment(String text, S style) {
        this.text = text;
        this.style = style;
    }

    public TextFragment(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return text;
    }


    public String getText() {
        return text;
    }


    public S getStyle() {
        return style;
    }
}
