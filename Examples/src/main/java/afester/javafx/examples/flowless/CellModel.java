package afester.javafx.examples.flowless;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CellModel {

    private final String text;
    private final String prefix;
    private final Color color;
    private final Color rectColor;
    private final Color bgColor;

    /**
     * 
     * @param prefix
     * @param text
     * @param color
     * @param rectColor
     * @param bgColor
     */
    public CellModel(String prefix, String text, Color color, Color rectColor, Color bgColor) {
        this.prefix = prefix;
        this.text = text;
        this.color = color;
        this.rectColor = rectColor;
        this.bgColor = bgColor;
    }

    public String getText() {
        return text;
    }


    public Color getColor() {
        return color;
    }

    public Paint getRectColor() {
        return rectColor;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public String getPrefix() {
        return prefix;
    }
}
