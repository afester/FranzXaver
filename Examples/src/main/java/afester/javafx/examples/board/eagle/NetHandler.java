package afester.javafx.examples.board.eagle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import afester.javafx.examples.board.model.AirWire;
import afester.javafx.examples.board.model.Pad;
import afester.javafx.examples.board.model.Part;

class NetHandler extends SubContentHandler {
    private EagleSchematicHandler mainHandler;
    private SheetHandler sheetHandler;
    private final List<Pad> padList = new ArrayList<>();

    public NetHandler(SheetHandler sh, EagleSchematicHandler mainHandler) {
        this.sheetHandler = sh;
        this.mainHandler = mainHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("pinref")) {
            final String part = attributes.getValue("part");
            final String gate = attributes.getValue("gate");
            final String pin = attributes.getValue("pin");

            Part p = mainHandler.getPart(part);
            if (p == null) {
                System.err.printf("    Ignoring <pinref part=\"%s\" gate=\"%s\" pin=\"%s\"> since part has no package\n", part, gate, pin);
            } else {
                // Map the pin reference for the current part to a Pad name in the current part
                Map<String, String> pinPadMappings = mainHandler.getPinPadMapping(p);
                String padName = pinPadMappings.get(pin + "@" + gate);

                // Here we need the "global" Pad as a graph node, not the pad template from the Package! 
                Pad pad = p.getPad(padName);
                System.err.printf("    %s\n", pad);

                padList.add(pad);
            }
        }        
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("net")) {
            Pad p1 = null;
            for (Pad p2 : padList) {
                if (p1 != null) {
                    // TODO: can we simply add the Pads to the net here and afterwards to a "Reset Net" to create the actual AirWires??
                    sheetHandler.currentNet.addTrace(new AirWire(p1, p2, sheetHandler.currentNet));
                }
                p1 = p2;
            }

            return true;
        }

        return false;
    }
    
}