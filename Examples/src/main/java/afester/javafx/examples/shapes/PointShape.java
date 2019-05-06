package afester.javafx.examples.shapes;

import afester.javafx.shapes.Circle;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class PointShape extends Group {

    public PointShape(double x, double y, Color col) {
        
        Circle c = new Circle(x, y, 3);
        c.setFill(col);
        c.setStroke(null);
        
        Text t = new Text(x, y, String.format("%d/%d",  (int) x, (int) y));
        getChildren().addAll(c, t);
    }
    
}