package afester.javafx.examples.text.model;

/**
 * A single segment of text with a specific style.
 * This is the smallest building block of a rich text document.
 */
public class StyledText<S> implements CharSequence {

    // The text of this text fragment
    private final String text;

    // The style which is applied to this text fragment
    private S style;

    /**
     * Creates a new text segment with a specified style.
     *
     * @param text  The text for the segment
     * @param style The style for the segment
     */
    public StyledText(String text, S style) {
        this.text = text;
        this.style = style;
    }


    /**
     * @return The length of the text in this segment.
     */
    public int length()  {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

/*************************/
    // TODO: This conflicts with CharSequence.toString()!
    @Override
    public String toString() {
        return String.format("TextFragment[style=%s, text=\"%s\"]", style, text);
    }
    /**
     * @return The text of this text fragment.
     */
    public String getText() {
        return text;
    }
/*************************/

    /**
     * @param start The start index of the text to return.
     * @param end   The end index of the text to return. 
     * @return A text segment which contains the text of this segment
     *         from a start index up to an end index
     */
    @Override
    public StyledText<S> subSequence(int start, int end) {
        return new StyledText<S>(text.substring(start, end), style);
    }

    /**
     * @param start The start index of the text to return.
     * @return A text segment which contains the text of this segment
     *         from a start index up to the end of the text.
     */
    public StyledText<S> subSequence(int start) {
        return new StyledText<S>(text.substring(start), style);
    }

    /**
     * @param str The text to append to this text segment.
     * @return A new text segment with the text from this segment
     *         appended by another text
     */
    public StyledText<S> concat(CharSequence str) {
        return new StyledText<S>(text + ((StyledText<S>) str).getText(), style);
    }


    /**
     * @return The style associated with this text fragment.
     */
    public S getStyle() {
        return style;
    }

}
