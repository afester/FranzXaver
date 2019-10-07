package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import afester.javafx.examples.board.model.Package;

class LibraryHandler extends SubContentHandler {

    private EagleSchematicHandler mainHandler;
    public Library currentLibrary = null;

    public LibraryHandler(EagleSchematicHandler eagleSchematicHandler) {
        mainHandler = eagleSchematicHandler;
    }


    public void addDeviceset(DeviceSet ds) {
        currentLibrary.addDeviceSet(ds);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, atts);
        } else {

            if (localName.equals("library")) {
                final String libraryName = atts.getValue("name");
                currentLibrary = new Library(libraryName);
                System.err.printf("  %s\n", currentLibrary);    
            } else if (localName.equals("packages")) {
                currentHandler = new PackageHandler(this);
            } else if (localName.equals("symbols") || localName.equals("packages3d")) {
                currentHandler = new IgnoreHandler(localName);
            } else if (localName.equals("devicesets")) {
                currentHandler = new DeviceSetHandler(this);
            }
        }
    }
    

    public void addPackage(Package result) {
        currentLibrary.addPackage(result);
    }


    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("libraries")) {
            return true;
        } else if (localName.equals("library")) {
            System.err.printf("  Loaded %s\n", currentLibrary);
            mainHandler.addLibrary(currentLibrary);
        }

        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }

}