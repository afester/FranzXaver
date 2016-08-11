package afester.javafx.shapes;

import javafx.scene.Parent;
import javafx.scene.shape.MoveTo;

public abstract class ArrowLine extends Parent implements EditableShape {
    protected Arrow start = new Arrow();
    protected Arrow end = new Arrow();
    protected MoveTo moveTo = new MoveTo();

    protected double startX, startY, endX, endY;

    protected ArrowStyle startStyle = new ArrowStyle(ArrowShape.NONE, 0, 0);
    protected ArrowStyle endStyle = new ArrowStyle(ArrowShape.NONE, 0, 0);

}
