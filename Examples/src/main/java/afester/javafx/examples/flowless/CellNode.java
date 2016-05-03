package afester.javafx.examples.flowless;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.fxmisc.flowless.Cell;

public class CellNode implements Cell<CellModel, Node> {

    private Node node;

    private static String toCssColor(Color col) {
        int red = (int)Math.round(col.getRed() * 255.0);
        int green = (int)Math.round(col.getGreen() * 255.0);
        int blue = (int)Math.round(col.getBlue() * 255.0);

        return String.format("#%02x%02x%02x", red, green, blue);
    }

    private Background createBackground(Color col) {
        return new Background(
                new BackgroundFill(col, new CornerRadii(0), new Insets(0)));
    }


    /**
     * 
     * @param contents
     */
    public CellNode(CellModel contents) {

        HBox prefixBox = new HBox();
        Text prefix = new Text(contents.getPrefix());
        prefix.setFont(Font.font("Courier New"));
        prefixBox.getChildren().add(prefix);
        // prefixBox.setBackground(createBackground(contents.getColor()));
        //prefix.setFill(contents.getColor());

        HBox textBox = new HBox();
        Text text = new Text(contents.getText());
        textBox.getChildren().add(text);
        textBox.setBackground(createBackground(contents.getBgColor()));

        Rectangle rect = new Rectangle(14, 14);
        rect.setFill(contents.getRectColor());

        HBox panel = new HBox();
        panel.getChildren().addAll(rect, prefixBox, textBox);
        HBox.setMargin(rect,  new Insets(1, 2, 1, 2));
        
        node = panel;
    }


    @Override
    public Node getNode() {
        return node;
    }
    
    @Override
    public String toString() {
        return node.toString();
    }
}
