package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import afester.javafx.examples.board.model.Net;
import javafx.geometry.Point2D;

class SheetHandler extends SubContentHandler {
    private EagleSchematicHandler mainHandler;
    public Net currentNet;

    public SheetHandler(EagleSchematicHandler eagleSchematicHandler) {
        this.mainHandler = eagleSchematicHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
            if (localName.equals("instance")) {
                final String partName = attributes.getValue("part");        // unique id (reference designator)
                final Point2D pos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                                -Double.parseDouble(attributes.getValue("y")));
    
                System.err.printf("  <instance part=\"%s\" pos=%s>\n", partName, pos);
                mainHandler.setPosition(partName, pos);
            } else if (localName.equals("bus")) {
               
            } else if (localName.equals("net")) {
                final String netName = attributes.getValue("name");
                System.err.printf("  <net name=\"%s\">\n", netName);
                currentNet = new Net(netName);
                mainHandler.addNet(currentNet);
                currentHandler = new NetHandler(this, mainHandler);
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("sheets")) {
            return true;
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
}