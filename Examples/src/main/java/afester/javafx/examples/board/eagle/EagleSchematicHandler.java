package afester.javafx.examples.board.eagle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.Part;
import javafx.geometry.Point2D;

class EagleSchematicHandler extends DefaultHandler {
    private final Map<String, Part> parts = new HashMap<>();
    private final List<Net> nets = new ArrayList<>();
    private final Map<Part, Map<String, String>> pinPadMappings = new HashMap<>();
    private final Map<String, Library> libraries = new HashMap<>();
    double minY = 0;

    private SubContentHandler currentHandler = null;

    public List<Net> getNets() {
        return nets; 
    }

    public Collection<Part> getParts() {
        return parts.values();
    }

    public void addPart(Part part, Map<String, String> pinPadMapping) {
        pinPadMappings.put(part, pinPadMapping);
        parts.put(part.getName(), part);
    }

    public void addNet(Net net) {
        nets.add(net);
    }

    public Library getLibrary(String name) {
        return libraries.get(name);
    }

    public void addLibrary(Library lib) {
        libraries.put(lib.getName(), lib);
    }

    public Part getPart(String partName) {
        return parts.get(partName);
    }

    public Map<String, String> getPinPadMapping(Part part) {
        return pinPadMappings.get(part);
    }

    @Override
    public void startDocument() throws SAXException {
        System.err.println("Start document");
    }

    @Override
    public void endDocument() throws SAXException {
        System.err.println("End document");
        System.err.printf("Loaded %s package libraries.\n", libraries.size());

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, atts);
        } else {
            if (localName.equals("libraries")) {
                currentHandler = new LibraryHandler(this);
            } else if (localName.equals("parts")) {
                currentHandler = new PartHandler(this);
            } else if (localName.equals("sheets")) {
                currentHandler = new SheetHandler(this);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }
    }
    

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentHandler != null) {
            currentHandler.characters(ch, start, length);
        }
    }

    public void setPosition(String partName, Point2D pos) {

        minY = Math.min(pos.getY(), minY);

        Part part = getPart(partName);
        if (part != null) {
            part.setPosition(pos);
        } else {
            System.err.printf("WARNING: %s not found in sheet\n", partName);
        }
    }
}