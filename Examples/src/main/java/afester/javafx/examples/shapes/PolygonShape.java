package afester.javafx.examples.shapes;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class PolygonShape extends Polygon {

    public PolygonShape(List<Point2D> points) {
        ObservableList<Double> polyPoints = getPoints();
        points.forEach(e -> { polyPoints.add(e.getX()); polyPoints.add(e.getY()); } );
    }

}
