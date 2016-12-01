package afester.javafx.examples.docbook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.SegmentOps;

import javafx.scene.Node;


/**
 * A custom object which contains a file path to an image file.
 * When rendered in the rich text editor, the image is loaded from the
 * specified file.
 */
public abstract class CustomObject<S> {

    /**
     * 
     * @param styleCodec
     * @return
     */
    public static <S> Codec<CustomObject<S>> codec(Codec<S> styleCodec) {
        return new Codec<CustomObject<S>>() {

            @Override
            public String getName() {
                return "LinkedImage<" + styleCodec.getName() + ">";
            }

            @Override
            public void encode(DataOutputStream os, CustomObject<S> seg) throws IOException {
                seg.encode(os);
//                // external path rep should use forward slashes only
//                String externalPath = i.imagePath.replace("\\", "/");
//                Codec.STRING_CODEC.encode(os, externalPath);
//                styleCodec.encode(os, i.style);
            }

            @Override
            public CustomObject<S> decode(DataInputStream is) throws IOException {
                // Sanitize path - make sure that forward slashes only are used
                String imagePath = Codec.STRING_CODEC.decode(is);
                imagePath = imagePath.replace("\\",  "/");
                S style = styleCodec.decode(is);
                return new LinkedImage<>(imagePath, style);
            }

        };
    }


    public static class CustomObjectOps<S> implements SegmentOps<CustomObject<S>, S> {

        private final CustomObject<S> emptySeg = new CustomObject<S>(null) {

            @Override
            public CustomObject<S> setStyle(S style) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Node createNode() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void encode(DataOutputStream os) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return "EmptyCustomObject";
            }};

        public CustomObjectOps(S defaultStyle) {
        }

        @Override
        public int length(CustomObject<S> seg) {
            return seg == emptySeg ? 0 : 1;
        }

        @Override
        public char charAt(CustomObject<S> seg, int index) {
            return seg == emptySeg ? '\0' : '\ufffc';
        }

        @Override
        public String getText(CustomObject<S> seg) {
            return seg == emptySeg ? "" : "\ufffc";
        }

        @Override
        public CustomObject<S> subSequence(CustomObject<S> seg, int start, int end) {
            if (start < 0) {
                throw new IllegalArgumentException("Start cannot be negative. Start = " + start);
            }
            if (end > length(seg)) {
                throw new IllegalArgumentException("End cannot be greater than segment's length");
            }
            return start == 0 && end == 1
                    ? seg
                    : emptySeg;
        }

        @Override
        public CustomObject<S> subSequence(CustomObject<S> seg, int start) {
            if (start < 0) {
                throw new IllegalArgumentException("Start cannot be negative. Start = " + start);
            }
            return start == 0
                    ? seg
                    : emptySeg;
        }

        @Override
        public S getStyle(CustomObject<S> seg) {
            return seg.getStyle();
        }

        @Override
        public CustomObject<S> setStyle(CustomObject<S> seg, S style) {
            return seg == emptySeg ? emptySeg : seg.setStyle(style);
        }

        @Override
        public Optional<CustomObject<S>> join(CustomObject<S> currentSeg, CustomObject<S> nextSeg) {
            return Optional.empty();
        }


        @Override
        public CustomObject<S> createEmpty() {
            return emptySeg;
        }
    }

    
    
    
    
    private final S style;

    /**
     * Creates a new linked image object.
     *
     * @param style The text style to apply to the corresponding segment.
     */
    public CustomObject(S style) {
        this.style = style;
    }

    public S getStyle() {
        return style;
    }

    public abstract String toString();

    public abstract CustomObject<S> setStyle(S style);

    public abstract Node createNode();

    public abstract void encode(DataOutputStream os) throws IOException;
}
