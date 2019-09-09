package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import javafx.geometry.Point2D;
import javafx.scene.Group;


public abstract class AbstractNodeView extends Group implements Interactable {

    protected AbstractNode node;

    public AbstractNodeView(AbstractNode node) {
        // setMouseTransparent(false);
        this.node = node;
    }

    @Override
	public Point2D getPos() {
	    return node.getPosition();
    }
}
