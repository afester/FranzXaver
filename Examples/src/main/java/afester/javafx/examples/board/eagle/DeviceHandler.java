package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import afester.javafx.examples.board.model.Package;

class DeviceHandler extends SubContentHandler {
    public Device currentDevice = null;
    private DeviceSetHandler deviceSetHandler;

    public DeviceHandler(DeviceSetHandler deviceSetHandler) {
        this.deviceSetHandler = deviceSetHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
            if (localName.equals("device")) {
                final String deviceName = attributes.getValue("name");
                final String packageName = attributes.getValue("package");

                // get the package from the current library
                // TODO: improve API
                Library lib = deviceSetHandler.libHandler.currentLibrary;
                Package pkg = lib.getPackage(packageName);

                currentDevice = new Device(deviceName, pkg);
                System.err.printf("      %s\n", currentDevice);
            } else if (localName.equals("connects")) {
                currentHandler = new ConnectHandler(this);
            } else if (localName.equals("package3dinstances")) {
                currentHandler = new IgnoreHandler("package3dinstances");
            } else if (localName.equals("technologies")) {
                currentHandler = new IgnoreHandler("technologies");
            } else {
                System.err.printf("        %s\n", localName);
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("devices")) {
            return true;
        } else if (localName.equals("device" )) {
            deviceSetHandler.addDevice(currentDevice);
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
    
}