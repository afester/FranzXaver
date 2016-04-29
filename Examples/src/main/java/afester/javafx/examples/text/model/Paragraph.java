package afester.javafx.examples.text.model;

import static afester.javafx.examples.text.model.TwoDimensional.Bias.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import afester.javafx.examples.text.model.TwoDimensional.Position;

/**
 * 
 */
public final class Paragraph<S, PS> {

    private final List<StyledText<S>> segments;
    private final TwoLevelNavigator navigator;
    private PS paragraphStyle;


    public Paragraph(PS style) {
        this(new ArrayList<>(), style);
    }


    Paragraph(List<StyledText<S>> segments, PS paragraphStyle) {
        assert !segments.isEmpty();
        this.segments = segments;
        this.paragraphStyle = paragraphStyle;
        navigator = new TwoLevelNavigator(segments::size,
                i -> segments.get(i).length());
    }

    /**
     * @return A read only view on the list of segments in this paragraph.
     */
    public List<StyledText<S>> getFragments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * @return The style of this paragraph.
     */
    public PS getStyle() {
        return paragraphStyle;
    }

    public void add(StyledText<S> textFragment) {
        segments.add(textFragment);
    }

    // TODO: Why is length only calculated once????
    private int length = -1;
    
    /**
     * @return The length of the text in this paragraph, calculated 
     *         as the sum of the lengths of all text fragments.
     */
    public int length() {
        if (length == -1) {
            length = segments.stream().mapToInt(StyledText::length).sum();
        }

        return length;
    }

    

    public Paragraph<S, PS> concat(Paragraph<S, PS> p) {
        if(length() == 0) {
            return p;
        }

        if(p.length() == 0) {
            return this;
        }

        StyledText<S> left = segments.get(segments.size() - 1);
        StyledText<S> right = p.segments.get(0);
        if (Objects.equals(left.getStyle(), right.getStyle())) {
            StyledText<S> segment = left.concat(right);
            List<StyledText<S>> segs = new ArrayList<>(segments.size() + p.segments.size() - 1);
            segs.addAll(segments.subList(0, segments.size() - 1));
            segs.add(segment);
            segs.addAll(p.segments.subList(1, p.segments.size()));
            return new Paragraph<>(segs, paragraphStyle);
        } else {
            List<StyledText<S>> segs = new ArrayList<>(segments.size() + p.segments.size());
            segs.addAll(segments);
            segs.addAll(p.segments);
            return new Paragraph<>(segs, paragraphStyle);
        }
    }


    /**
     * @return The text in this paragraph as a single, unstyled text string
     */
    public String getText() {
        StringBuilder result = new StringBuilder();
        for (StyledText<S> frag : segments) {
            result.append(frag);
        }

        return result.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Paragraph[style=%s]", paragraphStyle);
    }


    public String dump() {
        StringBuffer result = new StringBuffer(toString());

        for (StyledText tf : this.segments) {
            result.append("\n");
            result.append("   ");
            result.append(tf.toString());
        }
        result.append("\n");
        return result.toString();
    }


    /**
     * Returns a new Paragraph with the given number of characters from the beginning
     * of this paragraph.
     *
     * @param length The character length of the resulting paragraph.
     * @return A new Paragraph with the first <code>length</code> number of characters
     *         and the same segment structure.
     */
    public Paragraph<S, PS> trim(int length) {
        if (length >= length()) {
            return this;
        }

        Position pos = navigator.offsetToPosition(length, Backward);
        int segIdx = pos.getMajor();
        List<StyledText<S>> segs = new ArrayList<>(segIdx + 1);
        segs.addAll(segments.subList(0, segIdx));
        segs.add(segments.get(segIdx).subSequence(0, pos.getMinor()));
        return new Paragraph<>(segs, paragraphStyle);
    }


    /**
     * Returns a new Paragraph with the given number of characters from the end
     * of this paragraph.
     *
     * @param start The start index from where to return characters.
     * @return A new Paragraph with the characters starting at the given start index.
     */
    public Paragraph<S, PS> subSequence(int start) {
        if (start < 0) {
            throw new IllegalArgumentException("start must not be negative (was: " + start + ")");
        } else if (start == 0) {
            return this;
        } else if (start <= length()) {
            Position pos = navigator.offsetToPosition(start, Forward);
            int segIdx = pos.getMajor();
            List<StyledText<S>> segs = new ArrayList<>(segments.size() - segIdx);
            segs.add(segments.get(segIdx).subSequence(pos.getMinor()));
            segs.addAll(segments.subList(segIdx + 1, segments.size()));
            return new Paragraph<>(segs, paragraphStyle);
        } else {
            throw new IndexOutOfBoundsException(start + " not in [0, " + length() + "]");
        }
    }
}
