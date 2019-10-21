package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.AbstractNode;
import javafx.scene.Group;


public abstract class AbstractPadView extends Group {

    protected AbstractNode node;

    public AbstractPadView(AbstractNode node) {
        this.node = node;
    }
}
