package afester.javafx.examples.board;

import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GridText extends Text {

    public GridText(String text, int column, int row, Color color) {
        super(text);
        GridPane.setConstraints(this, column, row);
        setTextOrigin(VPos.TOP);
        setFont(Font.font("Arial", 4));
        setFill(color);
    }

    public GridText(String text, int column, int row) {
        this(text, column, row, Color.BLACK);
    }
}
