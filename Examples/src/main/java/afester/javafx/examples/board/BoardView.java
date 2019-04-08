package afester.javafx.examples.board;

import java.util.Iterator;
import java.util.function.BiConsumer;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class DoubleVal {
    public double val;
}



public class BoardView extends Pane {

    private Board board;
    private Color boardColor = // Color.rgb(0x92, 0x92, 0x49);
                                // Color.rgb(0xcc,  0x82,  0x11, 1.0);
                                Color.rgb(0xff, 0xec, 0x80, 1.0);
    private Color padColor   = // Color.rgb(0xB2, 0x99, 0x70);
                                // Color.rgb(0xf8, 0xe7, 0xbe, 1.0);
                                //Color.rgb(0xfe, 0xcc, 0x00, 1.0);
                                Color.rgb(0xb8, 0x73, 0x33, 1.0);   // COPPER

    private final Point2D padOffset = new Point2D(2.5, 2.0);

    private Group partsGroup;
    private Group wireGroup;
    private Group dimensionGroup;

    private Interactor interactor = new MouseInteractor(this); 

    public BoardView() {
    }

    public static <T> void pointIterator(Iterable<T> iterable, BiConsumer<T, T> consumer) {
        Iterator<T> it = iterable.iterator();
        while(it.hasNext()) {
            T first = it.next();
            if(!it.hasNext()) return;
            T second = it.next();
            consumer.accept(first, second);
        }
    }


    public static void lineIterator(Iterable<Double> iterable, BiConsumer<Point2D, Point2D> consumer) {
        Iterator<Double> it = iterable.iterator();

        if(!it.hasNext()) return;
        Double firstX = it.next();
        if(!it.hasNext()) return;
        Double firstY = it.next();

        Double x1 = firstX;
        Double y1 = firstY;
        while(it.hasNext()) {
            Double x2 = it.next();
            if(!it.hasNext()) return;
            Double y2 = it.next();

            consumer.accept(new Point2D(x1, y1), new Point2D(x2, y2));
            x1 = x2;
            y1 = y2;
        }

        // close the polygon
        consumer.accept(new Point2D(x1, y1), new Point2D(firstX, firstY));
    }


    public void setBoard(Board board) {
        getChildren().clear();

        // TODO: store the board dimensions in the board file
        Double[] boardDims = new Double[]{
                0.0, 0.0,
                55.0, 0.0,
                55.0, 31.0,
                90.0, 31.0,
                90.0, 68.0,
                100.0, 81.0,
                100.0, 132.0,
                0.0, 132.0};
 
        this.board = board;
        
        System.err.println("\nCreating board background ...");

        Polygon boardShape = new Polygon();
        boardShape.setFill(boardColor);
        boardShape.setStroke(Color.BLACK);
        boardShape.setStrokeWidth(0.5);
        boardShape.getPoints().addAll(boardDims);

//        Rectangle background = new Rectangle(board.getWidth(), board.getHeight(), boardColor);
//        background.setStroke(Color.BLACK);
        getChildren().add(boardShape);

        pointIterator(boardShape.getPoints(), (xpos, ypos) -> {
            System.err.printf("%s/%s\n", xpos, ypos); 

            Circle c = new Circle(xpos, ypos, 0.5);
            c.setFill(null);
            c.setStroke(Color.RED);
            c.setStrokeWidth(0.3);
            getChildren().add(c);
        });


        Bounds b = boardShape.getBoundsInParent();
//        System.err.println(b);
//        Rectangle r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
//        r.setStroke(Color.GREEN);
//        r.setFill(null);
//        getChildren().add(r);

        Group padsGroup = new Group();
        for (double ypos = padOffset.getY();  ypos < b.getHeight();  ypos += 2.54 ) {
            for (double xpos = padOffset.getX();  xpos < b.getWidth();  xpos += 2.54) {

                // pad (backside of board)
//                Circle c = new Circle(xpos, ypos, 0.7);
//                c.setFill(Color.WHITE);
//                c.setStroke(padColor);
//                c.setStrokeWidth(0.6);
//                getChildren().add(c);

                // hole (frontside of board)
                Circle c = new Circle(xpos, ypos, 0.4);
                c.setFill(Color.WHITE);
                c.setStroke(null);
                padsGroup.getChildren().add(c);
            }
        }

        Polygon clipShape = new Polygon();
        clipShape.getPoints().addAll(boardDims);

        padsGroup.setClip(clipShape);
        getChildren().add(padsGroup);

        partsGroup = new Group();
        getChildren().add(partsGroup);

        wireGroup = new Group();
        getChildren().add(wireGroup);

        dimensionGroup = new Group();
        getChildren().add(dimensionGroup);

        // add the board dimensions
        System.err.println("Adding Board dimensions ...");
        updateBoardDimensions(boardShape);

        // Add all devices
        System.err.println("Adding Parts ...");
        board.getParts().forEach((k, g) -> {
            System.err.println("  " + g);
            partsGroup.getChildren().add(g);
        });

        System.err.println("Adding Nets ...");
        board.getNets().forEach(v -> {
            System.err.println("  " + v);
            wireGroup.getChildren().add(v);
        });
        
        setOnMousePressed(e -> { 
            if (interactor != null) {
                interactor.mousePressed(e);
            }
        });

        setOnMouseDragged(e -> {
            if (interactor != null) {
                interactor.mouseDragged(e);
            }
         });
    }


    private void updateBoardDimensions(Polygon boardShape) {
        // add the board dimensions
        Font dimFont = Font.font("Courier", 2.0);
        final Point2D unitVec = new Point2D(1.0, 0.0);
        lineIterator(boardShape.getPoints(), (p1, p2) -> {
            System.err.printf("%s/%s\n", p1, p2);
            
            Point2D vecDir = p2.subtract(p1);                               // direction vector
            Point2D vecNorm = new Point2D(vecDir.getY(), -vecDir.getX());   // norm vector
            vecNorm = vecNorm.normalize();                                  // normalized norm vector ...
            vecNorm = vecNorm.multiply(3.0);                                // ... of length 3.0

            Point2D p1_1 = p1.add(vecNorm);                                 // line parallel to existing line, outside the shape
            Point2D p2_1 = p2.add(vecNorm);

            Line l = new Line(p1_1.getX(), p1_1.getY(), p2_1.getX(), p2_1.getY());
            l.setStroke(Color.BLUE);
            l.setStrokeWidth(0.5);
            dimensionGroup.getChildren().add(l);

            Point2D midpoint = p2_1.midpoint(p1_1);
//            Circle c = new Circle(midpoint.getX(), midpoint.getY(), 0.5);
//            c.setFill(null);
//            c.setStroke(Color.RED);
//            c.setStrokeWidth(0.3);
//            getChildren().add(c);

            Double angle = unitVec.angle(vecDir);
//            if (angle > 180) {
//                angle -= 180;
//            }
            Double length = p1.distance(p2);
            System.err.printf("%s/%s => %s\n", Point2D.ZERO, vecDir, angle);
            Text value = new Text(midpoint.getX(), midpoint.getY(), String.format("%.1f mm", length));
            // value.setRotationAxis(new Point3D(midpoint.getX(), midpoint.getY(), 0.0));
            value.setTextOrigin(VPos.BASELINE);
            value.setRotate(angle);
            value.setFont(dimFont);
            dimensionGroup.getChildren().add(value);
        });
    }

    public Board getBoard() {
        return board;
    }

	public Point2D getPadOffset() {
		return padOffset;
	}
}
