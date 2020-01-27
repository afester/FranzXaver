package afester.javafx.examples.board;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import afester.javafx.examples.board.view.ShapeStyle;
import afester.javafx.shapes.LineDash;
import javafx.scene.paint.Color;

public class ApplicationProperties {

    // the actual storage
    private final Properties p = new Properties();

    private final Map<StyleSelector, ShapeStyle> styleMap = new HashMap<>();

    private ApplicationProperties() {
        styleMap.put(StyleSelector.TOPBOARD, new ShapeStyle());
        styleMap.put(StyleSelector.TOPPAD,   new ShapeStyle());

        styleMap.put(StyleSelector.TOPTRACE_NORMAL,      new ShapeStyle());
        styleMap.put(StyleSelector.TOPTRACE_HIGHLIGHTED, new ShapeStyle());
        styleMap.put(StyleSelector.TOPTRACE_SELECTED,    new ShapeStyle());

        styleMap.put(StyleSelector.TOPAIRWIRE_NORMAL,      new ShapeStyle());
        styleMap.put(StyleSelector.TOPAIRWIRE_HIGHLIGHTED, new ShapeStyle());
        styleMap.put(StyleSelector.TOPAIRWIRE_SELECTED,    new ShapeStyle());

        styleMap.put(StyleSelector.TOPBRIDGE_NORMAL,       new ShapeStyle());
        styleMap.put(StyleSelector.TOPBRIDGE_HIGHLIGHTED,  new ShapeStyle());
        styleMap.put(StyleSelector.TOPBRIDGE_SELECTED,     new ShapeStyle());

        styleMap.put(StyleSelector.BOTTOMBOARD,            new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMPAD,              new ShapeStyle());

        styleMap.put(StyleSelector.BOTTOMTRACE_NORMAL,     new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMTRACE_HIGHLIGHTED,new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMTRACE_SELECTED,   new ShapeStyle());

        styleMap.put(StyleSelector.BOTTOMAIRWIRE_NORMAL,      new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMAIRWIRE_HIGHLIGHTED, new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMAIRWIRE_SELECTED,    new ShapeStyle());

        styleMap.put(StyleSelector.BOTTOMBRIDGE_NORMAL,       new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMBRIDGE_HIGHLIGHTED,  new ShapeStyle());
        styleMap.put(StyleSelector.BOTTOMBRIDGE_SELECTED,     new ShapeStyle());
    }

    public static ApplicationProperties load() {
        ApplicationProperties result = new ApplicationProperties();

        try {
            InputStream is = new FileInputStream("application.xml");
            result.p.loadFromXML(is);

            result.styleMap.forEach( (key, value) -> {
                var propBaseName = key.name();
                var color = result.getColor(propBaseName + ".color", Color.BLACK);
                var width = result.getDouble(propBaseName + ".width", 0.5);
                var opacity = result.getDouble(propBaseName + ".opacity", 1.0);
                var lineStyle = LineDash.SOLID;
                try {
                    lineStyle = LineDash.valueOf(
                            result.getString(propBaseName + ".linestyle", LineDash.SOLID.name()));
                }
                catch(IllegalArgumentException iae) {
                }

                value.setColor(color);
                value.setWidth(width);
                value.setOpacity(opacity);
                value.setLineStyle(lineStyle);
            });

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
            
            styleMap.forEach((key, value) -> {
                var propBaseName = key.name();
                setColor(propBaseName + ".color", value.getColor());
                setDouble(propBaseName + ".width", value.getWidth());
                setDouble(propBaseName + ".opacity", value.getOpacity());
                setString(propBaseName + ".linestyle", value.getLineStyle().name());
            });

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

    public ShapeStyle getStyle(StyleSelector key) {
        return styleMap.get(key);
    }


    public Map<StyleSelector, ShapeStyle> getStyles() {
        return styleMap;
    }
}
