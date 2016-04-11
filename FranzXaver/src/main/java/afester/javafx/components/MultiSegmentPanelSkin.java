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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.CharConversionException;


public class MultiSegmentPanelSkin extends SkinBase<MultiSegmentPanel> {

    private MultiSegment[] digits;

    // TODO: why do we need to duplicate the properties here and synchronize
    // them with the control's properties through bind()?
    // => obviously, there is no need to do this - the "LabelSkinBase" class,
    // for instance, also simply registers a listener on the Control's properties.
    // there is one detail, though: the bind() method synchronizes the 
    // properties *initially*, while the listener does not get called automatically
    // at the beginning!
    //private SimpleObjectProperty<Color> onColor = new SimpleObjectProperty<Color>();
    //private SimpleObjectProperty<Color> offColor = new SimpleObjectProperty<Color>();
    //private StringProperty text = new SimpleStringProperty();

    /**
     * Creates a new skin for a MultiSegmentPanel.
     *
     * @param control The MultiSegmentPanel control which uses this skin.
     */
    public MultiSegmentPanelSkin(MultiSegmentPanel control) {
        // NOTE: SkinBase only has a Control parameter!  
        // ... , new SevenSegmentPanelBehavior(control)));
        super(control);

        initialize(control.getNumberOfDigits(), control.getSegmentType());

        // set current ON color from control and setup listener for future changes
        setOnColor(control.getOnColor());
        control.onColorProperty().addListener((obs, oldValue, newValue) -> setOnColor(newValue));

        // set current OFF color from control and setup listener for future changes
        setOffColor(control.getOffColor());
        control.offColorProperty().addListener((obs, old, newValue) -> setOffColor(newValue));

        // set current PANEL color from control and setup listener for future changes
        setPanelColor(control.getPanelColor());
        control.panelColorProperty().addListener((obs, old, newValue) -> setPanelColor(newValue));

        // set current text from control and setup listener for future changes
        setText(control.getText());
        control.textProperty().addListener((obs, old, newValue) -> setText(newValue));
    }


    private int idx = 0;

    private void initialize(int numberOfDigits, String displayType) {
        final HBox displayGroup = new HBox();
        displayGroup.setId("segments");

        digits = new MultiSegment[numberOfDigits];
        for (idx = 0;  idx < numberOfDigits;  idx++) {
            //digits[i] = new SevenSegment();
            digits[idx] = new MultiSegment(displayType);
            digits[idx].setId("seg" + idx);

            digits[idx].getCurrentMaskProperty().addListener(new InvalidationListener() {

                private MultiSegment source = digits[idx];

                @Override
                public void invalidated(Observable observable) {
                    EventHandler<DigitChangeEvent> handler =
                            MultiSegmentPanelSkin.this.getSkinnable().getOnDigitChanged();
                    if (handler != null) {
                        handler.handle(new DigitChangeEvent(source));
                    }
                }

            });

            displayGroup.getChildren().add(digits[idx]);
        }
        displayGroup.setMaxSize(displayGroup.getMaxWidth(), displayGroup.getPrefHeight());

        getChildren().clear();

        // !!!! Nodes are added to the Control's children through this getChildren() 
        // method inherited from SkinBase!
        getChildren().add(displayGroup); 
    }


    public void setSegmentMap(int idx, long segmentMap) {
        digits[idx].setCurrentMask(segmentMap);
    }


    private void setOnColor(Color col) {
        for (MultiSegment s : digits) {
            s.setOnColor(col);
        }
    }

    private void setOffColor(Color col) {
        for (MultiSegment s : digits) {
            s.setOffColor(col);
        }
    }

    private void setPanelColor(Color col) {
        for (MultiSegment s : digits) {
            s.setPanelColor(col);
        }
    }


    private void setText(String text)  {
        try {

            // set the text and take care of the decimal point
            int didx = 0;
            for (int i = 0;  i < text.length() && didx < digits.length; i++) {
                char theCharacter = text.charAt(i);
                if (theCharacter == '.') {
                    if (didx > 0) {
                        // DP is located in previous digit
                        digits[didx - 1].setDp(true);
                    } else {
                        digits[didx].setDp(true);
                        didx++;
                    }
                } else {
                    digits[didx].setChar(theCharacter);
                    digits[didx].setDp(false);
                    didx++;
                }
            }
    
            // clear remaining digits
            while (didx < digits.length) {
                digits[didx].setChar(' ');
                digits[didx].setDp(false);
                didx++;
            }

        } catch (CharConversionException e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    protected double computeMaxWidth(double height, double topInset,
            double rightInset, double bottomInset, double leftInset) {
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset,
            double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
}
