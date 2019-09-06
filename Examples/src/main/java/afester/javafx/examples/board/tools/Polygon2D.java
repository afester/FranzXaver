package afester.javafx.examples.board.tools;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;


/**
 * An improved version of the Polygon shape which uses a list of Point2D objects rather than an Array of Double
 * to define the corners of the Polygon.
 */
public class Polygon2D extends Polygon {

    public final void setPoints(List<Point2D> boardDims) {
        final Double[] polygonCoords = new Double [boardDims.size() * 2];
        int idx = 0;
        for (Point2D coord : boardDims) {
            polygonCoords[idx] = coord.getX();
            polygonCoords[idx+1] = coord.getY();
            idx += 2;
        }
        getPoints().setAll(polygonCoords);        
    }
}

