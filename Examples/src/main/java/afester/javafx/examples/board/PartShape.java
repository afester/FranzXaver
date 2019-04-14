package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javafx.scene.shape.Shape;

public interface PartShape {

    public abstract Shape createNode();

    // TODO: Probably not a good idea to depend on an XML Node/Document here
    public abstract Node getXML(Document doc);
}
