package afester.javafx.examples.board;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import afester.javafx.examples.board.model.Pad;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.PartShape;
import afester.javafx.svg.SvgLoader;
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
    private boolean isBottom;
    private boolean isSvg = false;

    
    public PartView(Part part, boolean isBottom) {
        this.part = part;
        this.isBottom = isBottom;

        this.setMouseTransparent(false);
        //this.setPickOnBounds(true);

        rot.setAngle(part.getRotation());
        getTransforms().add(rot);
        part.rotationProperty().addListener((obj, oldValue, newValue) -> {
            rot.setAngle(newValue.doubleValue());
        });

        setLayoutX(part.getPosition().getX());
        setLayoutY(part.getPosition().getY());
        part.positionProperty().addListener((obj, oldValue, newValue) -> {
            setLayoutX(newValue.getX());
            setLayoutY(newValue.getY());
        });

        createNode();
    }

    
    /**
     * Rotates the part clockwise at 90 degrees.
     */
    public void rotatePart() {
        part.rotateClockwise();
    }

    public Part getPart() {
        return part;
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
    
    
    
    @SuppressWarnings("serial")
    private final Map<String, String> package2svg = new HashMap<>() {{
        // Resistors
        put("0204/5",        "0204_5.svg");
        put("0204/7",        "0204_7.svg");

        // Capacitors
        put("C025-025X050",  "C025-025X050.svg");
        put("C050-025X075",  "C050-025X075.svg");
        put("E5-8,5",        "E5-8,5.svg");
        put("E55-30H",       "E55-30H.svg");

        // Diodes
        put("DO41Z10",       "DO41Z10.svg");
        put("DO41-10",       "DO41Z10.svg");

        // clamps
        put("691121710004",  "691121710004.svg");
        put("691216510002",  "691216510002.svg");
        put("691216510003",  "691216510003.svg");
        put("AK500/9",       "AK500_9.svg");    // HACK!!!!!

        // Relais
        put("HR91C-12H",     "HR91C-12H.svg");

        // Headers
        put("MA10-2",                "MA10-2.svg");

        // ICs
        put("DC-DC_CONVERTER_78XX",  "DC-DC_CONVERTER_78XX.svg");
        put("DIL06",         "DIL6.svg");
        put("DIL08",         "DIL8.svg");
        put("PDIP8",         "DIL8.svg");
        put("DIL14",         "DIL14.svg");
    }};


    /**
     * Creates the Part as a JavaFX node
     */
    private void createNode() {
        getChildren().clear();
        shapeViews = new Group();
        padViews = new Group();

        System.err.printf("Render %s as Svg: %s\n", this, isSvg);

        // render part as SVG?
        boolean svgAvailable = false;
        if (isSvg) {
            final String packageName = part.getPackageRef();
            final String packageSvg = package2svg.get(packageName);
            if (packageSvg != null) {
                InputStream svgFile = getClass().getResourceAsStream(packageSvg);
                SvgLoader loader = new SvgLoader();
                Group svgImage = loader.loadSvg(svgFile);
                shapeViews.getChildren().add(svgImage);
                svgAvailable = true;
            }
        }

        if (!svgAvailable) {
            for (PartShape ps : part.getShapes()) {
                Node s = ps.createNode();
                shapeViews.getChildren().add(s);
            }

            for (Pad ps : part.getPads()) {
                Node s = null;
                if (isBottom) {
                    s = new PadViewBottom(ps);
                } else {
                    s = new PadViewTop(ps);
                }
                padViews.getChildren().add(s);
            }
        }

        getChildren().add(shapeViews);
        getChildren().add(padViews);

        // Create a marker for the mid point
        if (!svgAvailable) {
            Line l1 = new Line(-0.5, 0.0, 0.5, 0.0);
            l1.setStroke(Color.RED);
            l1.setStrokeWidth(0.2);
            Line l2 = new Line(0.0, -0.5, 0.0, 0.5);
            l2.setStroke(Color.RED);
            l2.setStrokeWidth(0.2);
            getChildren().addAll(l1, l2);
        }

        // Finally add a shape which can be used to select the device
        // TODO: This is a Hack
        SelectionShape selectShape = new SelectionShape(getBoundsInLocal());
        getChildren().add(selectShape);
    }

    public void renderSVG(Boolean newValue) {
        this.isSvg = newValue;
        createNode();
    }

    public Collection<Pad> getPads() {
        return part.getPads(); // .values();
    }

	@Override
	public Point2D getPos() {
		return new Point2D(getLayoutX(), getLayoutY());
	}

    public String getName() {
        return part.getName();
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
