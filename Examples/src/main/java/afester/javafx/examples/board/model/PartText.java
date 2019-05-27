package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PartText implements PartShape {

    
    private Double x;
    private Double y;
    private Double size;
    private String text;

    public PartText(Double x, Double y, String text, Double size) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.size = size;
    }

    @Override
    public Shape createNode() {
        Text textShape = new Text(x, y, text);
        Font theFont = Font.font("Courier", size);	// TODO: The rendered text is messed up if the size is too small!
        textShape.setFont(theFont);
        textShape.setFill(Color.GRAY);
        textShape.setTextOrigin(VPos.TOP);

        return textShape;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("text");
        result.setAttribute("x", x.toString());
        result.setAttribute("y", y.toString());
        result.setAttribute("size", size.toString());
        result.setTextContent(text);

        return result;
    }

}
