package afester.javafx.examples.board.view;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import afester.javafx.examples.board.model.Pin;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.Interactable;
import afester.javafx.examples.board.SelectionShape;
import afester.javafx.examples.board.model.Package;
import afester.javafx.examples.board.model.PartShape;
import afester.javafx.svg.SvgLoader;
import afester.javafx.svg.SvgTextBox;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 * This class is the JavaFX node which visualizes a specific Part.
 * The Part is either visualized through its package shapes or through an SVG image.
 * 
 * Node hierarchy:
 * PartView  pickOnBounds=true
 *   +-shapeViews       mouseTransparent=true
 *   |   +-Line
 *   |   +-Rectangle
 *   |   +-Text
 *   |   +-...
 *   +-padViews         mouseTransparent=true
 *   +-crossLine1
 *   +-crossLine2
 *   +-selectionRect    mouseTransparent=true
 */
public class PartView extends Group implements Interactable {

    private Part part;  // reference to model

    private Rectangle selectionRect;
    private Rotate rot = new Rotate();
    private Group padViews;
    private Group shapeViews;
    private boolean isBottom;
    private boolean isSvg = false;  // flag to determine if the node is rendered as SVG image or not
    private Point2D originalPos = new Point2D(0, 0);

    public PartView(Part part, boolean isBottom) {
        this.part = part;
        this.isBottom = isBottom;

        // Any reason why this seems to not have worked earlier?????
        setPickOnBounds(true);

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

        render(false);
    }


    public Part getPart() {
        return part;
    }

    @Override
    public void setSelected(BoardView bv, boolean isSelected) {
        if (isSelected) {
            // TODO: This is (probably) a hack!
            final double nonScaledWidth = 1 / bv.getLocalToSceneTransform().getMxx();

            selectionRect.setStroke(Color.RED);
            selectionRect.setStrokeWidth(nonScaledWidth);

//            l1 = new Line(b.getMinX(), b.getMinY(),               b.getMinX()+b.getWidth(), b.getMinY()+b.getHeight());
//            l1.setStroke(Color.BLUE);
//            l1.setStrokeWidth(nonScaledWidth);
//            l2 = new Line(b.getMinX(), b.getMinY()+b.getHeight(), b.getMinX()+b.getWidth(), b.getMinY());
//            l2.setStroke(Color.BLUE);
//            l2.setStrokeWidth(nonScaledWidth);
//            getChildren().addAll(selectionRect); // , l1, l2);

            if (isSvg) {
                shapeViews.setOpacity(0.4);
            }
            
         } else {
             selectionRect.setStroke(null);
             shapeViews.setOpacity(1.0);
         }
    }
    
    
    
    @SuppressWarnings("serial")
    private final Map<String, String> package2svg = new HashMap<>() {{
        // Resistors
        put("0204/5",   "0204_5.svg");
        put("0204/7",   "0204_7.svg");
        put("0617/17",  "0617_17.svg");
        put("RTRIM64Y", "RTRIM64Y.svg");
        put("RTRIM64Z", "RTRIM64Z.svg");
        put("PTH",      "PTH.svg");

        // Capacitors
        put("C025-025X050", "C025-025X050.svg");
        put("C050-025X075", "C050-025X075.svg");
        put("E2-5",         "E2-5.svg");
        put("E5-8,5",       "E5-8,5.svg");
        put("E55-30H",      "E55-30H.svg");

        // Diodes
        put("DO41Z10",       "DO41Z10.svg");
        put("DO41-10",       "DO41Z10.svg");
        put("ZDIO-5",        "ZDIO-5.svg");

        // Transistors
        put("TO92-EBC",      "TO92.svg");

        // clamps
        
        put("691121710004",  "691121710004.svg");
        put("691216510002",  "691216510002.svg");
        put("691216510003",  "691216510003.svg");
        put("AK500/4",       "AK500_4.svg");    // HACK!!!!!
        put("AK500/9",       "AK500_9.svg");    // HACK!!!!!

        // Relais
        put("HR91C-12H",     "HR91C-12H.svg");

        // Headers
        put("MA10-2",                "MA10-2.svg");

        // ICs
        put("DC-DC_CONVERTER_78XX",  "DC-DC_CONVERTER_78XX.svg");
        put("DIL06",         "DIL6.svg");
        put("DIL08",         "DIL8.svg");
        put("PDIP8",         "PDIP8.svg");
        put("DIL14",         "DIL14.svg");
    }};


    /**
     * Creates the Part as a JavaFX node
     */
    public void render(Boolean asSvg) {
        getChildren().clear();
        shapeViews = new Group();
        shapeViews.setId("partShapes");
        shapeViews.setMouseTransparent(true);
        padViews = new Group();
        padViews.setId("partPads");
        padViews.setMouseTransparent(true);

        System.err.printf("Render %s as Svg: %s\n", this, asSvg);

        // render part as SVG?
        isSvg = false;
        if (asSvg) {
            final String packageName = part.getPackage().getName();
            final String packageSvg = package2svg.get(packageName);
            if (packageSvg != null) {
                InputStream svgFile = getClass().getResourceAsStream(packageSvg);
                SvgLoader loader = new SvgLoader();
                Group svgImage = loader.loadSvg(svgFile);

                // update the part Value
                final SvgTextBox partValueText = (SvgTextBox) svgImage.lookup("#partValue");
                if (partValueText != null) {
                    Text text = partValueText.getTextSpan(0);
                    text.setText(part.getValue());
                }
                // update the part Name
                final SvgTextBox partNameText = (SvgTextBox) svgImage.lookup("#partName");
                if (partNameText != null) {
                    Text text = partNameText.getTextSpan(0);
                    text.setText(part.getName());
                }

                shapeViews.getChildren().add(svgImage);
                isSvg = true;
            }
        }

        if (!isSvg) {

            Package pkg = part.getPackage();
            for (PartShape ps : pkg.getShapes()) {
                Node s = ps.createNode();

                // Replace name and value placeholders
                if (s instanceof Text) {    // TODO: Hack
                    Text t = (Text) s;
                    if (t.getText().equals(">NAME")) {  // TODO: Eagle specific!!!
                        t.setText(part.getName());
                    } if (t.getText().equals(">VALUE")) {   // TODO: Eagle specific!
                        t.setText(part.getValue());
                    }
                }

                shapeViews.getChildren().add(s);
            }

            for (Pin ps : part.getPins()) {
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
        if (!isSvg) {
            Line l1 = new Line(-0.5, 0.0, 0.5, 0.0);
            l1.getStyleClass().add("centerCross");

            Line l2 = new Line(0.0, -0.5, 0.0, 0.5);
            l2.getStyleClass().add("centerCross");

            getChildren().addAll(l1, l2);
        }

        // Finally add a shape which can be used to visualize the selection state
        selectionRect = new SelectionShape(getBoundsInLocal());
        getChildren().add(selectionRect);
    }


    @Override
    public String getTypeSelector() {
        // for SVG rendering, we must disable CSS styling of the Part
        if (isSvg) {
            return "SvgPart";
        }

        return super.getTypeSelector();
    }

    public Collection<Pin> getPads() {
        return part.getPins();
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
        return String.format("Part: %s (%s) - %s", part.getName(), part.getValue(), part.getPackage().getName()); 
    }


    @Override
    public void startDrag() {
        originalPos = getPos();
    }

    @Override
    public void drag(BoardView bv, Point2D delta) {
        Point2D newPos = originalPos.add(delta);
        newPos = bv.snapToGrid(newPos);
        part.setPosition(newPos);
    }

    @Override
    public String toString() {
        return String.format("PartView[partName=%s %s]", part.getName(), getBoundsInLocal());
    }


}
