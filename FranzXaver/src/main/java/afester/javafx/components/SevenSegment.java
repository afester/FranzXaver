package afester.javafx.components;

import afester.javafx.svg.SvgLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Implements the rendering of a single seven segment display.
 * The background of the display is transparent, so that it can be styled
 * from the containing component (usually a SevenSegmentPanel).
 */
public class SevenSegment extends Group {

    private SVGPath panel = null;
    private SVGPath[] segments = new SVGPath[7];
    private SVGPath segDP = null;

    private Color onColor = Color.RED; //  new Color(0x23 / 255.0, 0x2A / 255.0, 0x47 / 255.0, 1.0);
    private Color offColor = Color.GREEN; // new Color(0xC9 / 255.0, 0xD3 / 255.0, 0xBB / 255.0, 1.0);
    private Color bgColor = Color.VIOLET;
    private int number;
    private boolean isDp;

    private final static boolean[][] digits = new boolean[][] {
        //     a
        //    __
        //  f| g|  b
        //    --
        //  e|__|  c
        //     d
        //
        // a     b      c      d      e      f      g
        {true,  true,  true,  true,  true,  true,  false}, // 0
        {false, true,  true,  false, false, false, false}, // 1
        {true,  true,  false, true,  true,  false, true }, // 2
        {true,  true,  true,  true,  false, false, true }, // 3
        {false, true,  true,  false, false, true,  true }, // 4
        {true,  false, true,  true,  false, true,  true }, // 5
        {true,  false, true,  true,  true,  true,  true }, // 6
        {true,  true,  true,  false, false, false, false}, // 7
        {true,  true,  true,  true,  true,  true,  true }, // 8
        {true,  true,  true,  true,  false, true,  true }, // 9

        {true,  true,  true,  false, true,  true,  true }, // a
        {false, false, true,  true,  true,  true,  true }, // b
        {true,  false, false, true,  true,  true,  false}, // c
        {false, true,  true,  true,  true,  false, true }, // d
        {true,  false, false, true,  true,  true,  true }, // e
        {true,  false, false, false, true,  true,  true }, // f

        {false, false, false, false, true,  false, true }, // r
        {false, false, true,  true,  true,  false, false}, // v
        {false, false, false, false, true,  true,  false}, // l
        {false, false, true,  true,  true,  false, false}, // o
        {false, false, false, false, false, false, false}, // ' '
        {false, true,  true,  false, true,  true,  true }, // H
        {false, false, false, true,  true,  true,  false}, // L
        {true,  true,  false, false, true,  true,  true }, // P
        {false, true,  false, false, true,  true,  true }, // µ
        {false, false, true,  false, true,  false, true }  // n
    };


    public SevenSegment() {

        // load the drawing
        SvgLoader loader = new SvgLoader();
        Node g = loader.loadSvg("7segment.svg");
        getChildren().add(g);

        // get references to all required graphical elements
        segments[0] = (SVGPath) g.lookup("#segA");
        segments[1] = (SVGPath) g.lookup("#segB");
        segments[2] = (SVGPath) g.lookup("#segC");
        segments[3] = (SVGPath) g.lookup("#segD");
        segments[4] = (SVGPath) g.lookup("#segE");
        segments[5] = (SVGPath) g.lookup("#segF");
        segments[6] = (SVGPath) g.lookup("#segG");
        segDP = (SVGPath) g.lookup("#segDP");
        panel = (SVGPath) g.lookup("#panel");

        // initialize default colors
        panel.setFill(new Color(1.0, 1.0, 1.0, 0.0));
                                // NOTE/TODO: Need a fill color here, otherwise
                                // the bounds of the panel shape are not considered
                                // when layouting! i.e. setFill(null) will cause
                                // the panel shape to not be rendered at all
        clear();
        setDP(false);
    }



    public Color getOnColor() {
        return onColor;
    }

    /**
     * Sets the color to use for enabled segments.
     *
     * @param color The color to use when painting enabled segments.
     */
    public void setOnColor(Color col) {
        onColor = col;
        setDigit(this.number);
        setDP(this.isDp);
    }

    public Color getOffColor() {
        return offColor;
    }

    /**
     * Sets the color to use for disabled segments.
     *
     * @param color The color to use when painting disabled segments.
     */
    public void setOffColor(Color col) {
        offColor = col;
        setDigit(this.number);
        setDP(this.isDp);
    }

    /**
     * Enables the necessary digits to show a (hexadecimal) number from 
     * 0 to f (15d).
     * 
     * @param number The number to show (0 to 15)
     */
    public void setDigit(int number) {
        this.number = number;
        int s = 0;
        for (SVGPath seg : segments) {
            if (digits[number][s]) {
                seg.setFill(onColor);    
            } else {
                seg.setFill(offColor);
            }
            s++;
        }
    }


    /**
     * Shows a particular character in the display.
     * Currently, the display supports numbers from 0 to 9 and the characters
     * <code>abcdef</code> (shown in either capital or non-capital depending on the 
     * actual character - e.g. 'a' is always displayed as "A", independant whether
     * it is passed as 'a' or 'A').
     * Additionally, the characters <code>HlLnoOPrvµ</code> and the space character
     * are supported. The capital 'O' is shown identical to the number 0,
     * and the rendering of the 'v' looks very bad.
     *
     * @param c The character to display.
     * @throws RuntimeException if the character is not supported. Some
     *         characters (such as M or Z) can not be displayed with seven segments
     *         in a useful way. Use an 14 segment display instead. 
     */
    public void setChar(char c) {
        int hexVal = Character.digit(c, 16);
        if (hexVal >= 0) {
            setDigit(hexVal);
        } else {
            switch(c) {
                // TODO: Use Map
                case 'O' : setDigit(0);   break;
                case 'r' : setDigit(16);  break;
                case 'v' : setDigit(17);  break;
                case 'l' : setDigit(18);  break;
                case 'o' : setDigit(19);  break;
                case ' ' : setDigit(20);  break;
                case 'H' : setDigit(21);  break;
                case 'L' : setDigit(22);  break;
                case 'P' : setDigit(23);  break;
                case 'µ' : setDigit(24);  break;

                default:
                    throw new RuntimeException("Error: invalid character '" + c + "'");
            }
        }
    }


    /**
     * Turns off all seven segments. The decimal point is not affected by this
     * method - to disable all segments, use clear() and setDP(false).
     */
    public void clear() {
        for (SVGPath seg : segments) {
            seg.setFill(offColor);
        }
        this.number = 20; // space ; TODO!
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
