package afester.javafx.shapes;

public class ArrowStyle {

    private ArrowShape shape;
    private double length;
    private double angle;


    public ArrowStyle(ArrowShape shape, double length, double angle) {
        this.shape = shape;
        this.length = length;
        this.angle = angle;
    }


    public double getLength() {
        return length;
    }


    public double getAngle() {
        return angle;
    }


    public ArrowShape getShape() {
        return shape;
    }

    
    @Override
    public String toString() {
        return "ArrowStyle[shape=" + shape + ", length=" + length + ", angle=" + angle + "]";
    }


}
