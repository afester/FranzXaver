package afester.javafx.shapes;

import javafx.scene.Parent;

public abstract class ArrowLine extends Parent implements EditableShape {
    protected Arrow start = new Arrow();
    protected Arrow end = new Arrow();

    protected double startX, startY, endX, endY;

    protected ArrowStyle startStyle = null; // new ArrowStyle(ArrowShape.NONE, 0, 0, false);
    protected ArrowStyle endStyle = null; // new ArrowStyle(ArrowShape.NONE, 0, 0, false);

    public void setStartArrow(ArrowStyle style) {
        this.startStyle = style;
        update();
    }


    public void setEndArrow(ArrowStyle style) {
        this.endStyle = style;
        update();
    }

    abstract protected void update();
    
    abstract public void setStrokeWidth(Double newWidth);
    
    abstract public void setEndX(double x);

    abstract public void setEndY(double y);

    abstract public void setStartX(double x);

    abstract public void setStartY(double y);
}

