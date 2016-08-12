package afester.javafx.shapes;


/**
 * Defines the style of an arrow.
 * The style consists of the shape, the length/height, the angle at the head
 * and whether the arrow is filled or not.
 */
public class ArrowStyle {

    private ArrowShape shape;
    private double length;
    private double angle;
    private boolean filled;


    public ArrowStyle(ArrowShape shape, double length, double angle, boolean filled) {
        this.shape = shape;
        this.length = length;
        this.angle = angle;
        this.filled = filled;
    }


    /**
     * @return the length of the arrow (the distance from the connection 
     * point to the head)
     */
    public double getLength() {
        return length;
    }

    /**
     * @return The opening angle of the arrow (the inner angle at the head)
     */
    public double getAngle() {
        return angle;
    }

    /**
     * @return The shape of the arrow.
     */
    public ArrowShape getShape() {
        return shape;
    }

    /**
     * @return Whether the arrow shape is filled or not.
     */
    public boolean isFilled() {
        return filled;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(angle);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (filled ? 1231 : 1237);
        temp = Double.doubleToLongBits(length);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((shape == null) ? 0 : shape.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArrowStyle other = (ArrowStyle) obj;
        if (Double.doubleToLongBits(angle) != Double.doubleToLongBits(other.angle))
            return false;
        if (filled != other.filled)
            return false;
        if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length))
            return false;
        if (shape != other.shape)
            return false;
        return true;
    }

    
    @Override
    public String toString() {
        return String.format("ArrowStyle[shape=%s, length=%s, angle=%s, filled=%s]",
                              shape, length, angle, filled);
    }
}
