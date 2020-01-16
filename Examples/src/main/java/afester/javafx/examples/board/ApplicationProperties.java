package afester.javafx.examples.board;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import afester.javafx.examples.board.view.ShapeStyle;
import javafx.scene.paint.Color;

public class ApplicationProperties {

    // the actual storage
    private final Properties p = new Properties();

    private ApplicationProperties() {
        
    }

    public static ApplicationProperties load() {
        ApplicationProperties result = new ApplicationProperties();

        try {
            InputStream is = new FileInputStream("application.xml");
            result.p.loadFromXML(is);

            //result.setBottomTraceColor(result.getColor("bottomTraceColor", Color.RED));
            //result.setBottomTraceWidth(result.getDouble("bottomTraceWidth", 1.0));
        }catch (FileNotFoundException e) {
            // Ignored - no properties loaded, default values taken ...
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void save() {
        try {
            //setColor("bottomTraceColor", getBottomTraceColor());
            //setDouble("bottomTraceWidth", getBottomTraceWidth());

            OutputStream os = new FileOutputStream("application.xml");
            p.storeToXML(os, "BreadBoardEditor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setString(String key, String value) {
        p.setProperty(key, value);
    }

    public String getString(String key, String defaultValue) {
        final var sval = p.getProperty(key);
        if (sval == null) {
            return defaultValue;
        }

        return sval;
    }


    public void setDouble(String key, Double value) {
        p.setProperty(key, value.toString());
    }

    public Double getDouble(String key, Double defaultValue) {
        final var dval = p.getProperty(key);
        if (dval == null) {
            return defaultValue;
        }

        return Double.parseDouble(dval);
    }

    
    public void setColor(String key, Color value) {
        p.setProperty(key, value.toString());
    }

    public Color getColor(String key, Color defaultValue) {
        final var cval = p.getProperty(key);
        if (cval == null) {
            return defaultValue;
        }

        return Color.valueOf(cval);
    }

//    public List<?> getList(String key) {
//        return null;
//    }


//    // The color of the trace on the bottom of the board
//    private final ObjectProperty<Color> bottomTraceColor = new SimpleObjectProperty<Color>(Color.RED);
//    public ObjectProperty<Color> bottomTraceColorProperty() { return bottomTraceColor; }
//    public Color getBottomTraceColor() { return bottomTraceColor.get(); }
//    public void setBottomTraceColor(Color color) { bottomTraceColor.set(color); }
//
//    // The width of the traces (in mm)
//    private final DoubleProperty bottomTraceWidth = new SimpleDoubleProperty(1.0);
//    public DoubleProperty bottomTraceWidthProperty() { return bottomTraceWidth; }
//    public Double getBottomTraceWidth() { return bottomTraceWidth.get(); }
//    public void setBottomTraceWidth(Double newWidth) { bottomTraceWidth.set(newWidth); }

    // The style of the top board view
    private final ShapeStyle topBoardStyle = new ShapeStyle();
    public ShapeStyle getTopBoardStyle() { return topBoardStyle; }

    // The style of a normal trace
    private final ShapeStyle topTraceNormalStyle = new ShapeStyle();
    public ShapeStyle getTopTraceNormalStyle() { return topTraceNormalStyle; }

    // The style of a highlighted normal trace
    private final ShapeStyle topTraceHighlightedStyle = new ShapeStyle();
    public ShapeStyle getTopTraceHighlightedStyle() { return topTraceHighlightedStyle; }
    
    // The style of a selected normal trace
    private final ShapeStyle topTraceSelectedStyle = new ShapeStyle();
    public ShapeStyle getTopTraceSelectedStyle() { return topTraceSelectedStyle; }
}
