package afester.javafx.examples.ttf;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PaletteView extends GridPane {

    public PaletteView() {
    }

    public PaletteView(ColorPalette cp) {
        setPalette(cp);
    }

    public void setPalette(ColorPalette cp) {
        getChildren().clear();

        final int columns = ((int) Math.sqrt(cp.getSize() - 1)) + 1;
        final int rows = (cp.getSize() -1) / columns + 1;
        System.err.printf("%s: %s x %s (%s)\n", cp.getSize(), columns, rows, columns*rows);

        int column = 0;
        int row = 0;
        for (Color c : cp.getColors()) {
            Rectangle rect = new Rectangle(10, 10);
            rect.setFill(c);
            add(rect, column, row);
            column++;
            if (column % columns == 0) {
                column = 0;
                row++;
            }
        }
    }
}
