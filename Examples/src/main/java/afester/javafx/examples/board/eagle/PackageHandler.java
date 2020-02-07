package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import afester.javafx.examples.board.model.Package;
import afester.javafx.examples.board.model.ShapeArc;
import afester.javafx.examples.board.model.ShapeCircle;
import afester.javafx.examples.board.model.ShapeLine;
import afester.javafx.examples.board.model.ShapePad;
import afester.javafx.examples.board.model.ShapeRectangle;
import afester.javafx.examples.board.model.ShapeModel;
import afester.javafx.examples.board.model.ShapeText;
import afester.javafx.shapes.ArcFactory;
import afester.javafx.shapes.ArcParameters;
import javafx.geometry.Point2D;
import javafx.scene.text.FontWeight;

class PackageHandler extends SubContentHandler {

    private Package result;
    private ShapeText currentText = null;
    private LibraryHandler libHandler;

    public PackageHandler(LibraryHandler libraryHandler) {
        this.libHandler = libraryHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("package")) {
            final String packageName = attributes.getValue("name");
            System.err.printf("    <package name=\"%s\"\n", packageName);

            result = new Package(libHandler.currentLibrary.getId() + "$" + packageName, packageName);
        } else if (localName.equals("pad") ){
            final String padName = attributes.getValue("name");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            // padNode.getAttribute("drill");
            // padNode.getAttribute("shape");
            // padNode.getAttribute("diameter");

            final ShapePad pad = new ShapePad(padName, padPos);
            System.err.printf("      %s\n", pad);
            result.addPad(pad);
        } else if (localName.equals("smd") ){
            final String padName = attributes.getValue("name");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            //Double padDx = Double.parseDouble(smdPadNode.getAttribute("dx"));
            //Double padDy = -Double.parseDouble(smdPadNode.getAttribute("dy"));

            final ShapePad pad = new ShapePad(padName, padPos); 
            System.err.printf("      %s\n", pad);
            result.addPad(pad);
        } else if (localName.equals("wire") ){
            final Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                           -Double.parseDouble(attributes.getValue("y1")));
            final Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                           -Double.parseDouble(attributes.getValue("y2")));

            final Double width = Double.parseDouble(attributes.getValue("width"));
            // final String layer = attributes.getValue("layer");

            // if the "curve" attribute is defined, an arc is rendered instead of the line
            final String curveAttr = attributes.getValue("curve");
            
            ShapeModel wireShape;
            if (curveAttr != null && !curveAttr.isEmpty()) {
                final double alpha = Double.parseDouble(curveAttr);

                // NOTE: -alpha is required due to the transformation of the y coordinate!
                ArcParameters ap = ArcFactory.arcFromPointsAndAngle(p1,  p2, -alpha);
                wireShape = new ShapeArc(ap.getCenter(), ap.getRadius(), ap.getStartAngle(),
                                        ap.getLength(), width);
            } else {
                wireShape = new ShapeLine(p1, p2, width);
            }
            System.err.printf("      %s\n", wireShape);
            result.addShape(wireShape);
            
        } else if (localName.equals("rectangle")) {
            final Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                           -Double.parseDouble(attributes.getValue("y1")));
            final Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                           -Double.parseDouble(attributes.getValue("y2")));
            // final String layer = attributes.getValue("layer");

            ShapeModel rectangle = new ShapeRectangle(p1, p2);
            System.err.printf("      %s\n", rectangle);
            result.addShape(rectangle);
        } else if (localName.equals("circle")) {
            final Point2D center = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            final Double radius = Double.parseDouble(attributes.getValue("radius"));
            final Double width = Double.parseDouble(attributes.getValue("width"));

            ShapeModel circle = new ShapeCircle(center, radius, width);
            System.err.printf("      %s\n", circle);
            result.addShape(circle);
        } else if (localName.equals("text")) {
            final Point2D textPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                                -Double.parseDouble(attributes.getValue("y")));
            final Double size = Double.parseDouble(attributes.getValue("size"));    // Text height in mm
            Double ratio = 1.0;                                                    // Text weight (Normal, bold, ...)
            try {
                final String value = attributes.getValue("ratio");
                if (value != null) {
                    ratio = Double.parseDouble(value);
                }
            } catch(NumberFormatException nfe) {
                // intentionally left blank - default value is 1.0
            }

            // final String layer = attributes.getValue("layer");

            FontWeight fontWeight = FontWeight.NORMAL;
            if (ratio > 5) {
                fontWeight = FontWeight.BOLD;
            } 
            if (ratio > 10) {
                fontWeight = FontWeight.EXTRA_BOLD;
            }

            // convert text height in mm to points - actually size / 0.3514598035 should be correct,
            // but the texts then end up much too large
            Double fontSize = size / 0.3514598035 / 2;

            currentText = new ShapeText(textPos, fontSize, fontWeight);
            result.addShape(currentText);
        } else if (localName.equals("description")) {
            // ignored
        } else {
            System.err.println("      " + localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("packages")) {
            return true;
        } else if (localName.equals("package")) {
            System.err.printf("    %s\n", result);
            libHandler.addPackage(result);
        } else if (localName.equals("text")) {
            System.err.println("      " + currentText);
            currentText = null;
        }
        return false;
    }


    @Override
    public void characters(char[] ch, int start, int length) {
        if (currentText != null) {
            currentText.append(new String(ch, start, length));
        }
    }

}