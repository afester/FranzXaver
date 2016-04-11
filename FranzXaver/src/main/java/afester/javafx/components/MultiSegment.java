/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * Implements the rendering of a single multi segment display.
 */
public class MultiSegment extends Group {

    private SVGPath panel = null;
    private SVGPath[] segments;
    private SVGPath segDp = null;

    private Color onColor;
    private Color offColor;
    private boolean isDp;
    private boolean isReadOnly = true;  // TODO: Not used in MultiSegmentPanel yet.

    private LongProperty currentMaskProperty = new SimpleLongProperty();

    public LongProperty getCurrentMaskProperty() {
        return currentMaskProperty;
    }

    public long getCurrentMask() {
        return currentMaskProperty.get();
    }

    public void setCurrentMask(long value) {
        currentMaskProperty.set(value);
    }


    // The segment table which maps all supported codepoints to a segments mask 
    private final Map<Character, Long> segmentTable = new HashMap<>();

    private int idx = 0;

    /**
     * Creates a multi segment digit.
     * 
     * @param displayType The definition file to use. Each multi segment digit is specified
     *                    by an .svg file which defines the rendering of the segments
     *                    and a .chr file which defines the character set. The .svg and
     *                    .chr extensions are automatically added to the given displayType.
     */
    public MultiSegment(String displayType) {
        // load the drawing
        SvgLoader loader = new SvgLoader();
        InputStream svgFile = getClass().getResourceAsStream(displayType + ".svg");
        Node digitNode = loader.loadSvg(svgFile);
        getChildren().add(digitNode);

        // load the character set
        InputStream charsetFile = getClass().getResourceAsStream(displayType + ".chr");
        Properties charsetProperties = new Properties();
        try {
            charsetProperties.load(charsetFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String segList = charsetProperties.getProperty("segments");
        final String[] ids  = segList.split(",");
        charsetProperties.forEach((key, value) -> {
            if (!key.equals("segments")) {
                // TODO: This is probably only valid for ISO8859-1.
                char character = Character.toChars(Integer.decode((String) key))[0];
                long segmentMap = Long.parseLong((String) value, 2);
                segmentTable.put(character, segmentMap);
            }
        });

        // identify the segments
        segments = new SVGPath[ids.length];
        for (String id : ids) {
            segments[idx] = (SVGPath) digitNode.lookup("#" + id);

            if (!isReadOnly) {
                // make the segments editable.
                segments[idx].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    final int segIdx = idx;
    
                    @Override
                    public void handle(MouseEvent event) {
                        setSegmentEnabled(segIdx, !isSegmentEnabled(segIdx));
                    }
                });
            }

            idx++;
        }
        segDp = (SVGPath) digitNode.lookup("#segDP");
        panel = (SVGPath) digitNode.lookup("#panel");

        // initialize default colors (from svg file as far as possible)
        offColor = (Color) segDp.getFill();
        onColor = Color.BLACK;

        // "The only way the addListener knows whether you are registering a change 
        //  listener or an invalidation listener is by looking at the arguments you
        //  specify for the Lambda expression"
        //
        // Note also that the listener is only called when the value actually changes!!!
        currentMaskProperty.addListener( (obs, oldValue, newValue) -> {
            applyMask(newValue.longValue());
        });

        applyMask(0);
        setDp(false);
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
     * @param col The color to use when painting enabled segments.
     */
    public void setOnColor(Color col) {
        onColor = col;

        applyMask(getCurrentMask());
        setDp(this.isDp);
    }

    /**
     * @return The color used for enabled segments. 
     */
    public Color getOnColor() {
        return onColor;
    }

    /**
     * Sets the color to use for disabled segments.
     *
     * @param col The color to use when painting disabled segments.
     */
    public void setOffColor(Color col) {
        offColor = col;

        applyMask(getCurrentMask());
        setDp(this.isDp);
    }

    /**
     * @return The current color for disabled segments.
     */
    public Color getOffColor() {
        return offColor;
    }

    /**
     * Sets the panel (background) color of the segment.
     *
     * @param col The color to use for the background of the segment.
     */
    public void setPanelColor(Color col) {
        panel.setFill(col);
    }

    /**
     * @return The current color used for the background of the segment.
     */
    public Color getPanelColor() {
        return (Color) panel.getFill();
    }


    /**
     * Enables or disables a particular segment.
     * This is useful to implement a character set editor.
     *
     * @param idx  The index of the segment, in the order defined
     *             in the segments property of the charset definition file 
     * @param state <code>true</code> to enable the specified segment, 
     *              <code>false</code> to disable it.
     */
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


    /**
     * Checks if the given segment is currently on or off.
     * 
     * @param idx  The index of the segment, in the order defined
     *             in the segments property of the charset definition file 
     * @return <code>true</code> if the specified segment is on, 
     *         <code>false</code> otherwise.
     */
    public boolean isSegmentEnabled(int idx) {
        long segMask = 1 << idx;
        return (getCurrentMask() & segMask) != 0; 
    }


    /**
     * Shows a particular character in this display digit.
     * The mapping from the character code point to the mask of enabled segments
     * is defined in the .chr file which was passed to the constructor.
     * Unsupported characters will throw a CharConversionException exception.
     *
     * @param theChar The character to display.
     * @throws CharConversionException if the character is not supported.
     */
    public void setChar(char theChar) throws CharConversionException {
        Long segmentMask = segmentTable.get(theChar);
        if (segmentMask == null) {
            String message = 
                    String.format("Error: invalid character '%c' (%d)", theChar, (int) theChar); 
            throw new CharConversionException(message);
        }

        setCurrentMask(segmentMask);
    }


    /**
     * Turns off all segments, besides the decimal point. 
     * The decimal point is not affected by this
     * method - to disable all segments, use clear() and setDP(false).
     */
    public void clear() {
        setCurrentMask(0);
    }


    /**
     * Enables or disabled the decimal point for this segment.
     * 
     * @param state <code>true</code> to enable the decimal point, 
     *              <code>false</code> to disable it.
     */
    public void setDp(boolean state) {
        this.isDp = state;
        if (state) {
            segDp.setFill(onColor);    
        } else {
            segDp.setFill(offColor);
        }
    }
}
