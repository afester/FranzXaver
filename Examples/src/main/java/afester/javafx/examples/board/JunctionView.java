package afester.javafx.examples.board;

import afester.javafx.examples.board.model.Junction;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class JunctionView extends AbstractNodeView {

    public JunctionView(Junction junction) {
        super(junction);

        Shape shape = new Circle(0, 0, 0.5); // drill*2);
        shape.setFill(null);
        shape.setStroke(null);

        getChildren().addAll(shape);

        junction.colorProperty().addListener((obj, oldColor, newColor) -> shape.setFill(newColor));

        this.setLayoutX(getPos().getX());
        this.setLayoutY(getPos().getY());
        junction.positionProperty().addListener((obj, oldPos, newPos) -> {
            this.setLayoutX(newPos.getX());
            this.setLayoutY(newPos.getY());
        });
    }


    @Override
    public String getRepr() {
        return "Junction";
    }

    @Override
    public String toString() {
        return String.format("JunctionView[pos=%s]", node.getPosition());  
    }

//
//    public List<AirWire> getAirwires() {
//        List<AirWire> result = traceStarts.stream()
//                                          .filter(trace -> trace instanceof AirWire)
//                                          .map(trace -> (AirWire) trace)
//                                          .collect(Collectors.toList());
//        result.addAll(traceEnds.stream()
//                               .filter(trace -> trace instanceof AirWire)
//                               .map(trace -> (AirWire) trace)
//                               .collect(Collectors.toList()));
//        return result;
//    }
//
//
//    final private static double MIN_DIST = 0.1;
//
//    /** 
//     * @param other The other junction to validate
//     * @return <code>true</code> if this junction is at the same position as 
//     *         another one, <code>false</code> otherwise.
//     */
//    public boolean samePositionAs(Junction j2) {
//        if (getPos().distance(j2.getPos()) < MIN_DIST) {
//            return true;
//        }
//        return false;
//    }
//
//
//
//    @Override
//    public Point2D getPos() {
//        // TODO Auto-generated method stub
//        return new Point2D(0, 0);
//    }

    @Override
    public void setSelected(boolean isSelected) {
        // TODO Auto-generated method stub
        
    }


    public Junction getJunction() {
        return (Junction) node;
    }
}
