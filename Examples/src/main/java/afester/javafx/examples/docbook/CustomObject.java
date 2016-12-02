package afester.javafx.examples.docbook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
                String typeId = seg.getClass().getName();
                System.err.println(typeId);
                Codec.STRING_CODEC.encode(os, typeId);
                seg.encode(os);
            }

            @Override
            public CustomObject<S> decode(DataInputStream is) throws IOException {
                String typeId = Codec.STRING_CODEC.decode(is);
                System.err.println("TYPE ID:" + typeId);
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends CustomObject<S>> clazz = (Class<? extends CustomObject<S>>) Class.forName(typeId);

                    CustomObject<S> object = clazz.newInstance();
                    object.decode(is);
                    return object;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
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
            }

            @Override
            protected void decode(DataInputStream is) {
                throw new UnsupportedOperationException();
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
     * Default constructor for deserialization.
     */
    protected CustomObject() {
        this.style = null;
    }

    /**
     * Creates a new linked image object.
     *
     * @param style The text style to apply to the corresponding segment.
     */
    protected CustomObject(S style) {
        this.style = style;
    }

    public S getStyle() {
        return style;
    }

    public abstract String toString();

    public abstract CustomObject<S> setStyle(S style);

    public abstract Node createNode();

    public abstract void encode(DataOutputStream os) throws IOException;
    
    protected abstract void decode(DataInputStream is) throws IOException;
}
