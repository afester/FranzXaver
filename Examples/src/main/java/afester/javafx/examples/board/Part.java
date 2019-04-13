package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

public class Part extends Group implements Interactable {

    private String partName;
    private Map<String, Pad> pads = new HashMap<>();
    private List<PartShape> shapes = new ArrayList<>();

    private Rectangle selectionRect;
    

    public Part(String partName) {
        this.partName = partName;
        this.setMouseTransparent(false);
        //this.setPickOnBounds(true);
    }

    public void addPad(Pad pin, String pinId) {
        pads.put(pinId, pin);
    }

    public Pad getPad(String pinId) {
        return pads.get(pinId);
    }

    /**
     * @return The name / reference of this part.
     */
    public String getName() {
        return partName;
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

        if (getTransforms().isEmpty()) {
            getTransforms().add(new Rotate(0, 0, 0));    
        }
        Rotate rot2 = (Rotate) getTransforms().get(0);// .add(rot);
        
        double rotation = rot2.getAngle();
        rotation += 90;
        if (rotation >= 360) {
            rotation = 0;
        }
        rot2.setAngle(rotation);

        // adjust the traces which are connected the pads of this device
        reconnectTraces();
    }

    
    private void reconnectTraces() {
        pads.forEach( (k, v) -> {
            Point2D p = this.localToParent(v.getCenterX(), v.getCenterY());
            v.moveTraces2(p.getX(), p.getY());
        });
    }

//    private Line l1;
//    private Line l2;

    @Override
    public void setSelected(boolean isSelected) {
        getChildren().remove(selectionRect);
//        getChildren().remove(l1);
//        getChildren().remove(l2);
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

    @Override
    public String toString() {
        return String.format("Part[partName=%s %s]", partName, getBoundsInLocal());
    }

    
    /**
     * Creates the Part as a JavaFX node
     */
    public void createNode() {

        for (PartShape ps : shapes) {
            Shape s = ps.createNode();
            getChildren().add(s);
        }

        for (Junction ps : pads.values()) {
            Shape s = ps.createNode();
            getChildren().add(s);
        }

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

    public void addShape(PartShape shape) {
        shapes.add(shape);
    }

    public List<PartShape> getShapes() {
        return shapes;
    }

    public Collection<Pad> getPads() {
        return pads.values();
    }

    @Override
    public void leftMouseAction(MouseEvent e, BoardView bv) {
      Interactable currentSelection = bv.getSelectedObject();
      if (currentSelection != this) {
          if (currentSelection != null) {
              currentSelection.setSelected(false);
          }
          setSelected(true);
          bv.setSelectedObject(this);
      }
    }
    
    @Override
    public void rightMouseAction(MouseEvent e) {
    	rotatePart();
    }

    private Point2D snapToGrid(double x, double y, BoardView bv, Point2D offset) {                                                      
        // final double grid = 2.54;                                                                      
        final double grid = 1.27;       // for now, we also allow positions between pads - this is        
                                        // required to properly position the Eagle parts ...              

        double xpos = offset.getX() + x;                                                                        
        double ypos = offset.getY() + y;                                                                        

        xpos = (int) ( (xpos - bv.getPadOffset().getX()) / grid);                                         
        ypos = (int) ( (ypos - bv.getPadOffset().getY()) / grid);                                         

        xpos = xpos * grid + bv.getPadOffset().getX();                                                    
        ypos = ypos * grid + bv.getPadOffset().getY();                                                    

        return new Point2D(xpos, ypos);                                                                   
    }

    @Override
    public void mouseDragged(MouseEvent e, BoardView bv, Point2D offset) {
        // System.err.println("MOVE: " + currentSelection);

        // Snap to center of part
        // (this is also what the Eagle board editor does)
        Point2D snapPos = snapToGrid(e.getX(), e.getY(), bv, offset);
        move(snapPos);
    }
}
