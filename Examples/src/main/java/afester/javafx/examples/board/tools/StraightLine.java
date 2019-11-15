package afester.javafx.examples.board.tools;

import javafx.geometry.Point2D;

public class StraightLine {

    private Point2D start;
    private Point2D end;

    public StraightLine(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }


    /**
     * @param other Another StraightLine.
     * @return The intersection point between this StraightLIne and the other 
     *         StraightLine. If the lines are parallel, the x and/or y 
     *         coordinates will have NaN or Infinite values. 
     */
    public Point2D intersection(StraightLine other) {
        // https://www.matheplanet.com/default3.html?call=viewtopic.php?topic=11311

        final double x1 = start.getX();
        final double y1 = start.getY();
        final double x2 = end.getX();
        final double y2 = end.getY();
        final double x3 = other.start.getX();
        final double y3 = other.start.getY();
        final double x4 = other.end.getX();
        final double y4 = other.end.getY();

        final double a = x3 - x4;
        final double b = x1 - x2;
        final double w = x1 - x3;
        final double c = y3 - y4;
        final double d = y1 - y2;
        final double z = y1 - y3;

        final double divisor = d*a - c*b;
        final double Xs = b * ( c*w - a*z) / divisor + x1;
        final double Ys = d * ( c*w - a*z) / divisor + y1;

//      Point2D dir1 = getDirection();
//      Point2D dir2 = other.getDirection();
//
//      double t2 = (dir1.getY() / (dir2.getY() * dir1.getY() * dir1.getX() - dir2.getX()) ) * start.getY() - end.getY() + dir1.getY() * (end.getX() -start.getX());
//
//      Point2D result = start.add(dir1.multiply(t2));

        return new Point2D(Xs, Ys);
    }

    /**
     * @return A direction vector for this straight line.
     */
    public Point2D getDirectionVector() {
        return end.subtract(start);
    }

    /**
     * @return A position vector for this straight line.
     */
    public Point2D getPositionVector() {
        return getStart();  // Any point on the line is a position vector.
    }

    /**
     * @return A norm vector for this line.
     */
    public Point2D getNormVector() {
        final Point2D direction = getDirectionVector();
        return new Point2D(direction.getY(), -direction.getX());
    }

    /**
     * @param point A point which shall be part of the result line.
     * @return A StraightLine which is orthogonal to this StraightLine.
     */
    public StraightLine getOrthogonalLine(Point2D point) {
        final Point2D p1 = point;
        final Point2D p2 = point.add(getNormVector());
        return new StraightLine(p1,  p2);
    }

    /**
     * @param point The point for which to calculate the foot point on this straight line.
     * @return The foot point for the given point on this line (the closest point on the line).  
     */
    public Point2D getFootpoint(Point2D point) {
        final StraightLine ortho = getOrthogonalLine(point);
        return intersection(ortho);
    }

    /**
     * @return A StraightLine which is parallel to this line but goes through the given point. 
     */
//    public StraightLine getParallelLine(Point2D p) {
//        
//    }

    /**
     * @return A StraightLine which is parallel to this line but has the given distance. 
     */
//    public StraightLine getParallelLine(double distance) {
//        
//    }

    /**
     * @param point The point for which to calculate the distance.
     * @return The (shortest) distance to the given point. 
     */
    public double getDistance(Point2D point) {
        final Point2D footPoint = getFootpoint(point);
        return footPoint.distance(point);
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }
    
    
    @Override
    public String toString() {
        return String.format("StraightLine[start=%s, end=%s]", getStart(), getEnd());
    }
}
