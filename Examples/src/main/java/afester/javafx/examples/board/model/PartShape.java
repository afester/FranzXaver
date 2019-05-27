package afester.javafx.examples.board.model;

import org.w3c.dom.Document;

import javafx.scene.Node;

public interface PartShape {

    public abstract Node createNode();

    // TODO: Probably not a good idea to depend on an XML Node/Document here
    public abstract org.w3c.dom.Node getXML(Document doc);
}
