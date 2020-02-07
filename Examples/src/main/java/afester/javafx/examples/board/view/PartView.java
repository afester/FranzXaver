package afester.javafx.examples.board.view;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import afester.javafx.examples.board.model.Pin;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.ShapeArc;
import afester.javafx.examples.board.model.ShapeCircle;
import afester.javafx.examples.board.model.ShapeLine;
import afester.javafx.examples.board.model.ShapeRectangle;
import afester.javafx.examples.board.model.Package;
import afester.javafx.examples.board.model.ShapeModel;
import afester.javafx.examples.board.model.ShapeText;
import afester.javafx.svg.SvgLoader;
import afester.javafx.svg.SvgTextBox;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
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
    private final static Logger log = LogManager.getLogger();

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

        part.isSelectedProperty().addListener((obj, oldValue, isSelected) -> {
            if (isSelected) {
                // TODO: This is (most likely (absolutely certainly)) a hack!
                BoardView bv = (BoardView) getParent().getParent();
                final double nonScaledWidth = 1 / bv.getLocalToSceneTransform().getMxx();

                selectionRect.setStroke(Color.RED);
                selectionRect.setStrokeWidth(nonScaledWidth);

//          l1 = new Line(b.getMinX(), b.getMinY(),               b.getMinX()+b.getWidth(), b.getMinY()+b.getHeight());
//          l1.setStroke(Color.BLUE);
//          l1.setStrokeWidth(nonScaledWidth);
//          l2 = new Line(b.getMinX(), b.getMinY()+b.getHeight(), b.getMinX()+b.getWidth(), b.getMinY());
//          l2.setStroke(Color.BLUE);
//          l2.setStrokeWidth(nonScaledWidth);
//          getChildren().addAll(selectionRect); // , l1, l2);

                if (isSvg) {
                    shapeViews.setOpacity(0.4);
                }
            } else {
                selectionRect.setStroke(null);
                shapeViews.setOpacity(1.0);
            }
        });

        render(false);
    }


    public Part getPart() {
        return part;
    }

    @Override
    public void setSelected(BoardView bv, boolean isSelected) {
        part.setSelected(isSelected);
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

    private boolean once = true;
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

        log.debug("Render {} as Svg: {}", () -> this, () -> asSvg);

        // render part as SVG?
        isSvg = false;
        if (asSvg) {
            final String packageName = part.getPackage().getName();
            final String packageSvg = package2svg.get(packageName);
            if (packageSvg != null) {
                // WIP: Use a CAD font - for some reason, can not be loaded through CSS
                // since it does not resolve through the system font names (even though it is installed)
                if (once) {
                    InputStream is = getClass().getResourceAsStream("PCBius.ttf");
                    System.err.println(is);
                    Font.loadFont(is, 12);
                    once = false;
                }

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
            for (ShapeModel ps : pkg.getShapes()) {
                Node s = null;
                switch(ps.getType()) {
                    case SHAPETYPE_CIRCLE :
                        s = createNode((ShapeCircle) ps);
                        break;

                    case SHAPETYPE_RECTANGLE :
                        s = createNode((ShapeRectangle) ps);
                        break;

                    case SHAPETYPE_TEXT :
                        s = createNode((ShapeText) ps);
                        break;

                    case SHAPETYPE_ARC:
                        s = createNode((ShapeArc) ps);
                        break;

                    case SHAPETYPE_LINE:
                        s = createNode((ShapeLine) ps);
                        break;

                    case SHAPETYPE_PAD:
//                        s = createNode((PartPad) ps);
                        break;
                }
                if (s != null) {
                    shapeViews.getChildren().add(s);
                }
            }

            for (Pin ps : part.getPins()) {
                Node s = new PadView(ps, isBottom);
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

    private Node createNode(ShapeLine ps) {
        var p1 = ps.getStart();
        var p2 = ps.getEnd();
        var width = ps.getWidth();

        Shape line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        line.setStrokeWidth(width);
        return line;
    }

    private Node createNode(ShapeArc ps) {
        var center = ps.getCenter();
        var radius = ps.getRadius();
        var startAngle = ps.getStartAngle();
        var angle = ps.getAngle();
        var width = ps.getWidth();

        Arc arc = new Arc(center.getX(), center.getY(), radius, radius, startAngle, angle);
        arc.setType(ArcType.OPEN);
        arc.setFill(null);
        arc.setStrokeWidth(width);
        arc.setStroke(Color.GRAY);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        return arc;
    }

    private Node createNode(ShapeRectangle ps) {
        var p1 = ps.getP1();
        var p2 = ps.getP2();
            
        final double width = Math.abs(p2.getX() - p1.getX());
        final double height = Math.abs(p2.getY() - p1.getY());
        final double x = Math.min(p1.getX(), p2.getX());
        final double y = Math.min(p1.getY(), p2.getY());

        Shape rect = new Rectangle(x, y, width, height);

        return rect;
    }

    private Node createNode(ShapeCircle ps) {
        var center = ps.getCenter();
        var radius = ps.getRadius();
        var width = ps.getWidth();

        Shape circle = new Circle(center.getX(), center.getY(), radius);
        circle.setStrokeWidth(width);

        return circle;
    }

    private Node createNode(ShapeText ps) {
        final var pos = ps.getPos();
        final var size = ps.getSize();
        final var weight = ps.getWeight();
        final var text = ps.getText();

        Text textShape = new Text(pos.getX(), pos.getY(), text);
        textShape.setTextOrigin(VPos.BOTTOM); // .BASELINE);

        // WIP: Use a CAD font - for some reason, can not be loaded through CSS
        // since it does not resolve through the system font names (even though it is
        // installed)
//      InputStream is = getClass().getResourceAsStream("PCBius.ttf");
//      final Font f = Font.loadFont(is, size);
//      textShape.setFont(f);

        // Overwrite explicit settings through inline style (which has
        // the highest precedence):
        textShape.setStyle(String.format("-fx-font-size:%s; -fx-font-weight: %s", size, weight));

        // Replace name and value placeholders
        if (textShape.getText().equals(">NAME")) { // TODO: Eagle specific!!!
            textShape.setText(part.getName());
        }
        if (textShape.getText().equals(">VALUE")) { // TODO: Eagle specific!
            textShape.setText(part.getValue());
        }

        return textShape;
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
