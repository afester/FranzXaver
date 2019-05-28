package afester.javafx.examples.board;

import java.util.Collection;

import afester.javafx.examples.board.model.Pad;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.PartShape;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 * This class is the JavaFX node which visualizes a specific Part.
 */
public class PartView extends Group implements Interactable {

    private Part part;  // reference to model
    private Rectangle selectionRect;
    private Rotate rot = new Rotate();
    private Group padViews = new Group();
    private Group shapeViews = new Group();
    
    public PartView(Part part) {
        this.part = part;

        this.setMouseTransparent(false);
        //this.setPickOnBounds(true);
        rot.setAngle(part.getRotation());
        setLayoutX(part.getPosition().getX());
        setLayoutY(part.getPosition().getY());
        getTransforms().add(rot);

        createNode();
    }

    /**
     * Moves the part to a new location on the board.
     *
     * @param x The new x coordinate of the device origin.
     * @param y The new y coordinate of the device origin.
     */
    public void move(Point2D pos) {

//        System.err.printf("MOVE: %s/%s\n", x, y);
        
//        for (Pad p : pads.values()) {
//            System.err.printf("   %s\n", p);
//        }

        // Set the new location of the device
        setLayoutX(pos.getX());
        setLayoutY(pos.getY());

        // adjust the traces which are connected the pads of this device
        reconnectTraces();
    }

    
    /**
     * Rotates the part clockwise at 90 degrees.
     */
    public void rotatePart() {

        // set the new rotation of the device
        //setRotate(rotation);    // this rotates around the center of the Part Group! (BoundsInLocal rect)
        // To be able to set the origin (0, 0) we need to use a rotate transform:

        double rotation = rot.getAngle();
        rotation += 90;
        if (rotation >= 360) {
            rotation = 0;
        }
        rot.setAngle(rotation);

        // adjust the traces which are connected the pads of this device
        reconnectTraces();
    }


    void reconnectTraces() {
        part.getPads().forEach(pad -> { // );pads.forEach( (k, v) -> {
            pad.traceStarts.forEach(wire -> {
                System.err.printf("START: %s -> %s\n", wire, getPos());
                wire.setStart(getPos());
            });
            pad.traceEnds.forEach(wire -> {
                System.err.printf("END: %s -> %s\n", wire, getPos());    
                wire.setEnd(getPos());
            });

//
//            //Point2D p = this.localToParent(pad.getPos()); //.getCenterX(), pad.getCenterY());
//            //pad.setConnectPosition(p);
//
//            getChildrenUnmodifiable().forEach(node -> {
//                if (node instanceof PadView) {
//                    PadView pv = (PadView) node; 
//                    Point2D p = this.getParent().localToParent(this.localToParent(pv.localToParent(pv.getPos()))); //.getCenterX(), pad.getCenterY());
//                    System.err.println("PART POS: " + this.getPos());
//                    Object x = this.getParent();
//                    pv.moveTraces2(this.getPos().getX(), this.getPos().getY()); // p.getX(), p.getY());                    
//                }
//            });

//            Point2D p = this.localToParent(pad.getCenterX(), pad.getCenterY());
//            pad.moveTraces2(p.getX(), p.getY());
        });
    }

    @Override
    public void setSelected(boolean isSelected) {
        getChildren().remove(selectionRect);
        if (isSelected) {
            // Calculate bounds of the selected element in the content's coordinates
            Bounds b = getBoundsInLocal();

            final double nonScaledWidth = 1 / getParent().getParent().getScaleX();      // TODO: This is (probably) a hack!

            // create the visualization for selection
            selectionRect = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
            selectionRect.setFill(null);
            selectionRect.setStroke(Color.RED);
            selectionRect.setStrokeWidth(nonScaledWidth);
            selectionRect.getStrokeDashArray().addAll(1.0, 1.0);

//            l1 = new Line(b.getMinX(), b.getMinY(),               b.getMinX()+b.getWidth(), b.getMinY()+b.getHeight());
//            l1.setStroke(Color.BLUE);
//            l1.setStrokeWidth(nonScaledWidth);
//            l2 = new Line(b.getMinX(), b.getMinY()+b.getHeight(), b.getMinX()+b.getWidth(), b.getMinY());
//            l2.setStroke(Color.BLUE);
//            l2.setStrokeWidth(nonScaledWidth);

            getChildren().addAll(selectionRect); // , l1, l2);
         }
    }
    
    /**
     * Creates the Part as a JavaFX node
     */
    private void createNode() {

        for (PartShape ps : part.getShapes()) {
            Node s = ps.createNode();
            shapeViews.getChildren().add(s);
        }

        for (Pad ps : part.getPads()) {
            Node s = new PadView(ps); // ps.createNode();
            padViews.getChildren().add(s);
        }

        getChildren().add(padViews);
        getChildren().add(shapeViews);

        // Create a marker for the mid point
        //Circle c = new Circle(0, 0, 0.5);
        //c.setFill(null);

        Line l1 = new Line(-0.5, 0.0, 0.5, 0.0);
        l1.setStroke(Color.RED);
        l1.setStrokeWidth(0.2);
        Line l2 = new Line(0.0, -0.5, 0.0, 0.5);
        l2.setStroke(Color.RED);
        l2.setStrokeWidth(0.2);

        getChildren().addAll(l1, l2);

        // Finally add a shape which can be used to select the device
        // TODO: This is a Hack
        SelectionShape selectShape = new SelectionShape(getBoundsInLocal());
        getChildren().add(selectShape);
    }


    public Collection<Pad> getPads() {
        return part.getPads(); // .values();
    }

	@Override
	public Point2D getPos() {
		return new Point2D(getLayoutX(), getLayoutY());
	}

    @Override
    public String getRepr() {
        return String.format("Part: %s (%s) - %s", part.getName(), part.getValue(), part.getPackageRef()); 
    }


    @Override
    public String toString() {
        return String.format("PartView[partName=%s %s]", part.getName(), getBoundsInLocal());
    }
}
