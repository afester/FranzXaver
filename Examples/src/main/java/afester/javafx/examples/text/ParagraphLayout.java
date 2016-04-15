package afester.javafx.examples.text;

import javafx.scene.Node;
import javafx.scene.layout.VBox;


public class ParagraphLayout extends VBox {

    /**
     * Returns the paragraph node for given screen coordinates. 
     *
     * @param x
     * @param y
     *
     * @return
     */
    public ParagraphNode hit(double x, double y) {
        System.err.printf("Hit test: %f,%f\n", x, y);

        for (Node child : getChildrenUnmodifiable()) {
            if (child.getBoundsInParent().contains(x, y)) {
                return (ParagraphNode) child;
            }
        }

        return null;
    }

}
