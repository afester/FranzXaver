package afester.javafx.examples.text.model;

/**
 * A single fragment of text with a specific style.
 * This is the smallest building block of a rich text document.
 *
 * @param <S>
 */
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


    /**
     * @return The text of this text fragment.
     */
    public String getText() {
        return text;
    }


    /**
     * @return The style associated with this text fragment.
     */
    public S getStyle() {
        return style;
    }


    /**
     * @return The length of the text of this fragment.
     */
    public int length()  {
        return text.length();
    }

    @Override
    public String toString() {
        return text;
    }
}
