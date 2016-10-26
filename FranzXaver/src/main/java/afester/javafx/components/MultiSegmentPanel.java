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

import com.sun.javafx.css.converters.ColorConverter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * A multi segment display panel which is capable of displaying 
 * numbers and text, depending on the actual multi segment display type.
 */
public class MultiSegmentPanel extends Control {

    private String format;
    private int numberOfDigits;
    private String segmentType;


    /* The current text */
    private StringProperty text = new SimpleStringProperty("");

    public String getText() {
        return text.get();
    }
    
    public void setText(String text) {
        this.text.set(text);
    }
    
    public StringProperty textProperty() {
        return text;
    }


    /* Color which defines an enabled segment */
    private StyleableObjectProperty<Color> onColor = 
            new SimpleStyleableObjectProperty<Color>(StyleableProperties.ON_COLOR, 
                                                     this, "onColor", Color.YELLOW);

    public Color getOnColor() {
        return onColor.get();
    }

    public void setOnColor(Color col) {
        onColor.set(col);
    }
    
    public StyleableObjectProperty<Color> onColorProperty() {
        return onColor;
    }


    /* Color which defines a disabled segment */
    private StyleableObjectProperty<Color> offColor = 
            new SimpleStyleableObjectProperty<Color>(StyleableProperties.OFF_COLOR, 
                                                     this, "offColor", 
                                      new Color(0xC9 / 255.0, 0xD3 / 255.0, 0xBB / 255.0, 1.0));

    public Color getOffColor() {
        return offColor.get();
    }

    public void setOffColor(Color col) {
        offColor.set(col);
    }
    
    public StyleableObjectProperty<Color> offColorProperty() {
        return offColor;
    }


    /* Color which defines the segment background */
    private StyleableObjectProperty<Color> panelColor = 
            new SimpleStyleableObjectProperty<Color>(StyleableProperties.PANEL_COLOR, 
                                                     this, "panelColor", Color.BLACK);

    public Color getPanelColor() {
        return panelColor.get();
    }
    
    public void setPanelColor(Color col) {
        panelColor.set(col);
    }
    
    public StyleableObjectProperty<Color> panelColorProperty() {
        return panelColor;
    }


    /* Event handler handling for digit changes */
    private ObjectProperty<EventHandler<DigitChangeEvent>> digitChangedProperty = 
            new SimpleObjectProperty<>();

    public void setOnDigitChanged(EventHandler<DigitChangeEvent> handler) {
        onDigitChangedProperty().set(handler);
    }

    public final EventHandler<DigitChangeEvent> getOnDigitChanged() {
        return onDigitChangedProperty().get();
    }

    public final ObjectProperty<EventHandler<DigitChangeEvent>> onDigitChangedProperty() {
        return digitChangedProperty;
    }

    
    /**
     * Creates a new seven segment display panel.
     * Initially, all segments of the display are turned off.
     * Then, the display contents can be set with the setText() and setValue() 
     * methods.
     *
     * @param numberOfDigits The number of digits for the panel.
     */
    public MultiSegmentPanel(String segmentType, int numberOfDigits) {
        this(segmentType, numberOfDigits, 0);
    }


    /**
     * Creates a new seven segment display panel.
     * Initially, all segments of the display are turned off.
     * Then, the display contents can be set with the setText() and setValue() 
     * methods.
     *
     * @param numberOfDigits The overall number of digits
     * @param decimalCount The number of decimal places
     */
    public MultiSegmentPanel(String segmentType, int numberOfDigits, int decimalCount) {
        this.getStyleClass().add("seven-segment-panel");
        this.numberOfDigits = numberOfDigits;
        this.segmentType = segmentType;

        // create the format specifier for floating point conversion
        format = String.format("%%%d.%df", numberOfDigits + 1, decimalCount);
    }


    //    public void setSegmentMap(int i, long segmentMap) {
    //        ((SevenSegmentPanelSkin) getSkin()).setSegmentMap(i, segmentMap);  
    //         // TODO: Is there a better approach??
    //    }


    /**
     * Sets a floating point number to display in the display panel.
     * The number is auto-formatted to fit the setup of the display regarding
     * the number of digits and the number of decimal places.
     *
     * @param value The floating point number to display.
     */
    public void setValue(double value) {
        // Important: use dot as decimal point!
        String text = String.format(Locale.US, format, value);
        setText(text);
    }


    // Skinning and style sheet support

    @Override
    protected javafx.scene.control.Skin<?> createDefaultSkin() {
        // The skin is the actual content of the control.
        // In the most simple case, the skin is made up of one top level node
        // which is connected to the control here.

        MultiSegmentPanelSkin skin =  new MultiSegmentPanelSkin(this);
        return skin;
    }

    int getNumberOfDigits() {
        return numberOfDigits;
    }
    

    String getSegmentType() {
        return segmentType;
    }

    // This private static class defines all CSS properties which are supported by this control
    private static class StyleableProperties {

        
        private static final CssMetaData<MultiSegmentPanel, Color> ON_COLOR =
            new CssMetaData<MultiSegmentPanel, Color>("-fx-on-color", 
                                                      ColorConverter.getInstance(), 
                                                      Color.ORANGE) {
                // TODO: HOLD ON - xxxConverter classes are part of com.sun!!!!

                @Override
                public boolean isSettable(MultiSegmentPanel styleable) {
                    boolean result = styleable.onColor == null || !styleable.onColor.isBound();
                    return result;
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(MultiSegmentPanel styleable) {
                    return styleable.onColorProperty();
                }
        };

        private static final CssMetaData<MultiSegmentPanel, Color> OFF_COLOR =
            new CssMetaData<MultiSegmentPanel, Color>("-fx-off-color", 
                                                      ColorConverter.getInstance(), 
                                                      Color.BLUE) {
                // TODO: HOLD ON - xxxConverter classes are part of com.sun!!!!

                @Override
                public boolean isSettable(MultiSegmentPanel styleable) {
                    boolean result = styleable.offColor == null || !styleable.onColor.isBound();
                    return result;
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(MultiSegmentPanel styleable) {
                    return styleable.offColorProperty();
                }
        };

        private static final CssMetaData<MultiSegmentPanel, Color> PANEL_COLOR =
            new CssMetaData<MultiSegmentPanel, Color>("-fx-panel-color", 
                                                      ColorConverter.getInstance(), 
                                                      Color.BLUE) {
                // TODO: HOLD ON - xxxConverter classes are part of com.sun!!!!

                @Override
                public boolean isSettable(MultiSegmentPanel styleable) {
                    boolean result =    styleable.panelColor == null 
                                     || !styleable.panelColor.isBound();
                    return result;
                }

                @Override
                public StyleableProperty<Color> getStyleableProperty(MultiSegmentPanel styleable) {
                    return styleable.panelColorProperty();
                }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> PROPERTIES;

        static {
            // get list of default CSS properties from this control's parent class (Control)
            final List<CssMetaData<? extends Styleable, ?>> styleables = 
                    new ArrayList<>(Control.getClassCssMetaData());

            // add the additional properties from this class
            Collections.addAll(styleables, ON_COLOR, OFF_COLOR, PANEL_COLOR);

            PROPERTIES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> result = StyleableProperties.PROPERTIES;
        return result;
    }
    
}
