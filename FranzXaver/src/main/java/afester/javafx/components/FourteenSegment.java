package afester.javafx.components;

import afester.javafx.svg.SvgLoader;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Implements the rendering of a single fourteen segment display.
 * The background of the display is transparent, so that it can be styled
 * from the containing component (usually a SevenSegmentPanel).
 */
public class FourteenSegment extends Group {

    private SVGPath panel = null;
    private SVGPath[] segments = new SVGPath[14];
    private SVGPath segDP = null;

    private Color onColor; //= Color.RED; //  new Color(0x23 / 255.0, 0x2A / 255.0, 0x47 / 255.0, 1.0);
    private Color offColor;// = Color.GREEN; // new Color(0xC9 / 255.0, 0xD3 / 255.0, 0xBB / 255.0, 1.0);
    private char content = ' ';
    private boolean isDp;

    private LongProperty currentMaskProperty = new SimpleLongProperty();
    public LongProperty getCurrentMaskProperty() { return currentMaskProperty; }
    public long getCurrentMask() { return currentMaskProperty.get(); }
    public void setCurrentMask(long value) { currentMaskProperty.set(value); }

    // TODO: static data - required once only!
    private final /* static */ Map<Character, Long> segmentTable = new HashMap<>();

    int idx = 0;
    public FourteenSegment() {

        // load the drawing
        SvgLoader loader = new SvgLoader();
        InputStream svgFile = getClass().getResourceAsStream("14segment.svg");
        Node g = loader.loadSvg(svgFile);
        getChildren().add(g);

        final String[] ids = {"segA", "segB", "segC", "segD", "segE", "segF",
                              "segG1", "segG2", "segH", "segI", "segJ", "segK", "segL", "segM"};
        for (String id : ids) {
            segments[idx] = (SVGPath) g.lookup("#" + id);
            segments[idx].setOnMouseClicked(new EventHandler<MouseEvent>() {
                final int segIdx;  { segIdx = idx; }

                @Override
                public void handle(MouseEvent event) {
                    setSegmentEnabled(segIdx, !isSegmentEnabled(segIdx));
                }
            });

            idx++;
        }
        segDP = (SVGPath) g.lookup("#segDP");
        panel = (SVGPath) g.lookup("#panel");

        // load the character set
        InputStream charsetFile = getClass().getResourceAsStream("charset14.dat");
        Properties x = new Properties();
        try {
            x.load(charsetFile);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        x.forEach((k, v) -> {
            // TODO: This is probably only valid for ISO8859-1.
            char character = Character.toChars(Integer.decode((String) k))[0];
            long segmentMap = Long.parseLong((String) v, 2);
            segmentTable.put(character, segmentMap);
        });

        // initialize default colors (from svg file as far as possible)
        offColor = (Color) segDP.getFill();
        onColor = Color.BLACK;

        // "The only way the addListener knows whether you are registering a change 
        //  listener or an invalidation listener is by looking at the arguments you
        //  specify for the Lambda expression"
        //
        // Note also that the listener is only called when the value actually changes!!!
        currentMaskProperty.addListener( e -> { // (e, oldvalue, newvalue) -> {
            long mask = ((LongProperty) e).getValue();
            applyMask(mask);
        });

        applyMask(0);
        setDP(false);
    }


    private void applyMask(long mask) {
        long bit = 1;
        for (int s = 0;  s < segments.length;  s++) {
            if ((mask & bit) != 0) {
                segments[s].setFill(onColor);
            } else {
                segments[s].setFill(offColor);
            }
            bit <<= 1;
        }
    }

    /**
     * Sets the color to use for enabled segments.
     *
     * @param color The color to use when painting enabled segments.
     */
    public void setOnColor(Color col) {
        onColor = col;

        applyMask(getCurrentMask());
        setDP(this.isDp);
    }


    public Color getOnColor() {
        return onColor;
    }

    /**
     * Sets the color to use for disabled segments.
     *
     * @param color The color to use when painting disabled segments.
     */
    public void setOffColor(Color col) {
        offColor = col;

        applyMask(getCurrentMask());
        setDP(this.isDp);
    }


    public Color getOffColor() {
        return offColor;
    }

    
    public void setPanelColor(Color col) {
        panel.setFill(col);
    }

    
    public Color getPanelColor() {
        return (Color) panel.getFill();
    }


    public void setSegmentEnabled(int idx, boolean state) {
        long segMask = 1 << idx;
        if (state) {
            segments[idx].setFill(onColor);
            setCurrentMask(getCurrentMask() | segMask); 
        } else {
            segments[idx].setFill(offColor);
            setCurrentMask(getCurrentMask() & ~segMask);
        }
    }


    public boolean isSegmentEnabled(int idx) {
        long segMask = 1 << idx;
        return (getCurrentMask() & segMask) != 0; 
    }

    

    /**
     * Enables the necessary digits to show a (hexadecimal) number from 
     * 0 to f (15d).
     * 
     * @param number The number to show (0 to 15)
     */
/*    public void setDigit(int number) {
        this.number = number;
        for (int s = 0;  s < segments.length;  s++) {
            setSegmentEnabled(s, digits[number][s]);
        }
    }
*/

    /**
     * Shows a particular character in this display digit.
     * Currently all characters from code point 32 (space character) to code point
     * 127 are supported.
     * Unsupported characters will throw a Runtime exception.
     
     * @param c The character to display.
     * @throws CharConversionException if the character is not supported.
     */
    public void setChar(char c) throws CharConversionException {
        Long segmentMask = segmentTable.get(c);
        if (segmentMask == null) {
            throw new CharConversionException("Error: invalid character '" + c + "'");
        }

        setCurrentMask(segmentMask);
        content = c;
    }


    /**
     * Turns off all seven segments. The decimal point is not affected by this
     * method - to disable all segments, use clear() and setDP(false).
     */
    public void clear() {
//        for (SVGPath seg : segments) {
//            seg.setFill(offColor);
//        }
        try {
            setChar(' ');
        } catch (CharConversionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Enables or disabled the decimal point for this segment.
     * 
     * @param state <code>true</code> to enable the decimal point, 
     *              <code>false</code> to disable it.
     */
    public void setDP(boolean state) {
        this.isDp = state;
        if (state) {
            segDP.setFill(onColor);    
        } else {
            segDP.setFill(offColor);
        }
    }
/*
    public void setBackgroundColor(Color newPaint) {
        SevenSegment.this.panel.setFill(newPaint);        
    }*/
}
