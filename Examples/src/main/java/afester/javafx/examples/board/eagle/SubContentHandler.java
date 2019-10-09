package afester.javafx.examples.board.eagle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class SubContentHandler {
    protected SubContentHandler currentHandler = null;

    public abstract void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;
 
    public abstract boolean endElement(String uri, String localName, String qName) throws SAXException;

    public void characters(char[] ch, int start, int length) {
        if (currentHandler != null) {
            currentHandler.characters(ch,  start, length);
        }
    }
}