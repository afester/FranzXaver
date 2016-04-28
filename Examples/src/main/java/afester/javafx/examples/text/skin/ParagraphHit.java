package afester.javafx.examples.text.skin;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public class ParagraphHit {

    private ParagraphNode node;
    private Point2D mousePos;
    private int index = 0;

    public ParagraphHit(int index, ParagraphNode child, Point2D localCoordinates) {
        this.index = index;
        this.node = child;
        this.mousePos = localCoordinates;
    }

    public ParagraphNode getNode() {
        return node;
    }

//    public int getFragmentIndex() {
//        CharacterHit ch = node.hit(mousePos.getX(), mousePos.getY());
//        System.err.printf("HIT: %s\n", ch);
/*        System.err.printf("  CHECK FRAGMENT: %s\n", mousePos);
        for (Node child : node.getChildrenUnmodifiable()) {

            System.err.printf("     FRAGMENT: %s\n", child.getBoundsInParent());
            if (child.getBoundsInParent().contains(mousePos)) {
                // System.err.printf("    %s\n", child);
                child.setStyle("-fx-fill: green");
            }
        }
*/
//        return 0;
//    }

    public Point2D getCellOffset() {
        return mousePos;
    }

    public int getParagraphIndex() {
        return index;
    }
}
