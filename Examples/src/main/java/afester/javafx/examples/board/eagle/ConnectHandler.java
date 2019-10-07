package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class ConnectHandler extends SubContentHandler {

    private DeviceHandler deviceHandler;

    public ConnectHandler(DeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("connect")) {
            final String gate = attributes.getValue("gate");
            final String pin = attributes.getValue("pin");
            final String pad = attributes.getValue("pad");

            final String pinId = pin + "@" + gate;
            System.err.printf("        %s => %s\n", pinId, pad);
            deviceHandler.currentDevice.addPinPadMapping(pinId, pad);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("connects")) {
            return true;
        }

        return false;
    }
  
}