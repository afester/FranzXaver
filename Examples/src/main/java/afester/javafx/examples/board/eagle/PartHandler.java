package afester.javafx.examples.board.eagle;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import afester.javafx.examples.board.model.Package;
import afester.javafx.examples.board.model.Pad;
import afester.javafx.examples.board.model.Part;

class PartHandler extends SubContentHandler {

    private EagleSchematicHandler mainHandler;

    public PartHandler(EagleSchematicHandler eagleSchematicHandler) {
        this.mainHandler = eagleSchematicHandler;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("part")) {

            String partName = atts.getValue("name");        // unique id (reference designator)
            String partValue = atts.getValue("value");      // might be null - then we can use "deviceset" as value

            String partLibrary = atts.getValue("library");
            String partDeviceSet = atts.getValue("deviceset");
            String partDevice = atts.getValue("device");

            System.err.printf("  <part name=\"%s\" library=\"%s\" deviceset=\"%s\" device=\"%s\" value=\"%s\">\n", partName, partLibrary, partDeviceSet, partDevice, partValue);

            // get reference to package
            Library lib = mainHandler.getLibrary(partLibrary);
            DeviceSet deviceSet = lib.getDeviceSet(partDeviceSet);
            Device dev = deviceSet.getDevice(partDevice);

            // Set the part value from the device set if none was specified with the part
            if (partValue == null) {
                if (!deviceSet.isUserValue()) {
                    partValue = partDeviceSet;
                } else {
                    partValue = "";
                }
            }

            // NOTE: Device connects the package and the part by the Pin/Pad mapping!
            // In other words, we need the pin/pad mapping to later reference the proper pad from the net list!

            // System.err.println("    " + dev);

            Package partPackage = dev.getPackage();
            if (partPackage != null)  {
                Map<String, String> pinPadMapping = dev.getPinPadMapping();
                System.err.printf("    %s\n", partPackage);
                System.err.printf("    %s\n", pinPadMapping);
                Part part = new Part(partName, partValue, partPackage);
                partPackage.getPads().forEach(pad -> part.addPad(new Pad(part, pad)));
                mainHandler.addPart(part, pinPadMapping);
            } else {
                System.err.printf("    Ignored %s since it does not have a package\n", partName);
            }
        }
    }


    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("parts")) {
            return true;
        }

        return false;
    }

}