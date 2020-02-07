package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public interface ShapeModel {

    public abstract Node getXML(Document doc);
    
    public abstract ShapeType getType();
}
