package afester.javafx.examples.board;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseInteractor implements Interactor {

    private double offsetX = 0;
    private double offsetY = 0;
    private Part currentSelection = null;
    private BoardView bv;

    public MouseInteractor(BoardView boardView) {
    	bv = boardView;
	}

	@Override
    public void mousePressed(MouseEvent e) {

        if (!e.isControlDown()) {
            // System.err.println("BOARD:" + e);
            
            // determine the clicked object
            Part dev = null;
            EventTarget target = e.getTarget();
            System.err.println("TARGET:" + target);

            if (target instanceof SelectionShape) { // todo: this is a hack. It should at least be "instanceof Part"
                dev = (Part) ((SelectionShape) target).getParent();

            } else if (target instanceof AirWire) {
                AirWire aw = (AirWire) target;
                Net net = (Net) aw.getParent(); // TODO: provide an explicit access path
                Point2D clickPoint = new Point2D(e.getX(), e.getY());

                // determine which part of the AirWire to keep 
                double d1 = clickPoint.distance(aw.getStart());
                double d2 = clickPoint.distance(aw.getEnd());
                System.err.printf("CLICKED: %s -> %s (%s, %s)\n", clickPoint, aw, d1, d2);

                Junction newJunction = new Junction(clickPoint);

                if (d1 < d2) {
                    // keep To
                    Junction nearest = aw.getFrom();
                    System.err.println("NEARBY: " + nearest);

                    // connect existing wire to new junction
                    nearest.traceStarts.remove(aw);
                    aw.setFrom(newJunction);

//***************************************
                    Junction edge = new Junction(nearest.getPos().getX(), newJunction.getYpos());
                    System.err.println("EDGE1: " + edge);

                    // create new Trace: nearest - edge - newJunction
                    Trace t = new Trace(nearest, edge);
                    net.addTrace(t);            // TODO: Here, one operation should be sufficient
                    nearest.traceStarts.add(t); // to add the new trace ... (the second line should not be required)
                    edge.traceEnds.add(t);

                    Trace t2 = new Trace(edge, newJunction);
                    net.addTrace(t2);
                    edge.traceStarts.add(t2);
                    newJunction.traceEnds.add(t2);
                    
                    net.addJunction(edge);
                    net.addJunction(newJunction);
//***************************************
                } else {
                    // keep From
                    Junction nearest = aw.getTo();
                    System.err.println("NEARBY: " +  nearest);

                    // connect existing wire to new junction
                    nearest.traceEnds.remove(aw);
                    aw.setTo(newJunction);

//***************************************                        
                    Junction edge = new Junction(newJunction.getXpos(), nearest.getPos().getY());
                    System.err.println("EDGE2: " + edge);

                    // create new Trace: newJunction - edge - nearest
                    Trace t = new Trace(newJunction, edge);
                    net.addTrace(t);                    // TODO: Here, one operation should be sufficient
                    newJunction.traceStarts.add(t);
                    edge.traceEnds.add(t);

                    Trace t2 = new Trace(edge, nearest);
                    net.addTrace(t2);;
                    edge.traceStarts.add(t2);
                    nearest.traceEnds.add(t2);           // to add the new trace ... (the second line should not be required)

                    net.addJunction(edge);
                    net.addJunction(newJunction);

//***************************************
                }

                return;
            }

            if (e.getButton() == MouseButton.PRIMARY) {

                // a new object has been selected
                if (dev != currentSelection) {
                    if (currentSelection != null) {
                        currentSelection.setSelected(false);
                    }
                    if (dev != null) {
                        dev.setSelected(true);
                    }

                    currentSelection = dev;
                    System.err.println("SELECTED:" + currentSelection);
                }

                if (currentSelection != null) {
                    offsetX = currentSelection.getLayoutX() - e.getX();
                    offsetY = currentSelection.getLayoutY() - e.getY();
                }

                // store current mouse position
//                mx = e.getX();
//                my = e.getY();
            } else if (e.getButton() == MouseButton.SECONDARY) {
                System.err.println("ROTATE: " + dev);
                dev.rotatePart();
            }
        }
    }
    
    public void mouseDragged(MouseEvent e) {
        if (!e.isControlDown() && e.isPrimaryButtonDown() && currentSelection != null) {
            // System.err.println("MOVE: " + currentSelection);

            // Snap to center of part
            // (this is also what the Eagle board editor does)

            Point2D snapPos = snapToGrid(e.getX(), e.getY());
            currentSelection.move(snapPos);
        }

    }
    

    private Point2D snapToGrid(double x, double y) {
        // final double grid = 2.54;
        final double grid = 1.27;       // for now, we also allow positions between pads - this is 
                                        // required to properly position the Eagle parts ...

        double xpos = offsetX + x;
        double ypos = offsetY + y;

        xpos = (int) ( (xpos - bv.getPadOffset().getX()) / grid);
        ypos = (int) ( (ypos - bv.getPadOffset().getY()) / grid);

        xpos = xpos * grid + bv.getPadOffset().getX();
        ypos = ypos * grid + bv.getPadOffset().getY();

        return new Point2D(xpos, ypos);
    }
}
