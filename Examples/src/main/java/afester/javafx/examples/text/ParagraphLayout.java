package afester.javafx.examples.text;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.VBox;


/**
 * Container for all paragraphs.
 * Essentially a VBox which contains the ParagraphNode nodes as its children.
 * 
 * @param <PS>
 * @param <S>
 */
public class ParagraphLayout<PS, S> extends VBox {

    /**
     * Returns the paragraph node for given coordinates. 
     *
     * @param x
     * @param y
     *
     * @return
     */
    public ParagraphHit hit(double x, double y) {
        System.err.printf("Hit test: %f,%f\n", x, y);

        int idx = 0;
        for (Node child : getChildrenUnmodifiable()) {
            if (child.getBoundsInParent().contains(x, y)) {
                ParagraphNode<PS, S> paragraphNode = (ParagraphNode) child;
                Point2D localCoordinates = paragraphNode.sceneToLocal(x, y);
                return new ParagraphHit(idx, (ParagraphNode) child, localCoordinates);
            }

            idx++;
        }

        return null;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        System.err.println(toString() + ".layoutChildren()");
        getChildrenUnmodifiable().stream().forEach(n -> ((ParagraphNode) n).layoutChildren());
    }
}
