package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class DeviceSetHandler extends SubContentHandler {

    private DeviceSet currentDeviceSet = null;
    public LibraryHandler libHandler;

    public DeviceSetHandler(LibraryHandler libraryHandler) {
        this.libHandler = libraryHandler;
    }

    public void addDevice(Device device) {
        currentDeviceSet.addDevice(device);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
    
            if (localName.equals("deviceset")) {
                final String name = attributes.getValue("name");

                boolean isUserValue = false;
                final String uservalue = attributes.getValue("uservalue");
                if (uservalue != null && uservalue.equals("yes")) {
                    isUserValue = true;
                }
                currentDeviceSet = new DeviceSet(name, isUserValue);
                System.err.printf("    %s\n", currentDeviceSet);
            } else if (localName.equals("devices")) {
                currentHandler = new DeviceHandler(this);
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("devicesets")) {
            return true;
        } else if (localName.equals("deviceset" )) {
            libHandler.addDeviceset(currentDeviceSet);
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
}