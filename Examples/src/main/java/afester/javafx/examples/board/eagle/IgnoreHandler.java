package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class IgnoreHandler extends SubContentHandler {

    private String myElement;
    
    public IgnoreHandler(String localName) {
        myElement = localName;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(myElement)) {
            return true;
        }

        return false;
    }
    

    @Override
    public String toString() {
        return String.format("IgnoreHandler[endElement=\"%s\"]", myElement);
    }
    
}