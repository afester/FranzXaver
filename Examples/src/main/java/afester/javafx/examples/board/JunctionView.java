//package afester.javafx.examples.board;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import afester.javafx.examples.board.model.AirWire;
//import afester.javafx.examples.board.model.Junction;
//import javafx.geometry.Point2D;
//
//public class JunctionView extends AbstractNodeView {
//
//
//    public JunctionView(Junction junction, int x) {
//        super(junction);
//        setFill(null);
//    }
//    
//
//
////    @Override
////    public org.w3c.dom.Node getXML(Document doc) {
////        Element result = doc.createElement("junction");
////        result.setAttribute("x", Double.toString(getCenterX()));
////        result.setAttribute("y", Double.toString(getCenterY()));
////        result.setAttribute("id", Integer.toString(id));
////
////        return result;
////    }
//
//
////    @Override
////    public Node createNode() {
////        throw new RuntimeException("NYI!");
////    }
//
//
//    @Override
//    public String getRepr() {
//        return "Junction";
//    }
//
//    @Override
//    public String toString() {
//        return String.format("Junction[pos=%s/%s]", getCenterX(), getCenterY());  
//    }
//
//
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
//}
