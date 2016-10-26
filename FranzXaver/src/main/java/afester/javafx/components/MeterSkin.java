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

import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MeterSkin extends SkinBase<Meter> {
    private SVGPath pointer = null;
    private Text unitText = null;

    private double width = 0;
    private double height = 0;

    /**
     * Creates a new skin for an analog meter.
     *
     * @param control The Meter control which uses this skin.
     */
    public MeterSkin(Meter control) {
        super(control);

        SvgLoader loader = new SvgLoader();
        InputStream svgFile = getClass().getResourceAsStream("meter.svg");
        Node iv = loader.loadSvg(svgFile);
        pointer = (SVGPath) iv.lookup("#pointer");

        // get the pivot point - isn't there any easier way??? 
        Pattern pattern = 
                Pattern.compile("[Mm] (\\d+\\.\\d+),(\\d+\\.\\d+) (\\d+\\.\\d+),(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(pointer.getContent());
        matcher.find();
        width = Double.parseDouble(matcher.group(3));
        height = Double.parseDouble(matcher.group(4));

        unitText = (Text) iv.lookup("#unitText");

        setValue(control.getValue());
        control.valueProperty().addListener((obs, oldValue, newValue) -> {
            setValue(newValue.doubleValue());
        });

        unitText.setText(control.getUnitText());
        control.unitTextProperty().addListener((obs, oldValue, newValue) -> {
            unitText.setText(newValue);
        });

        getChildren().add(iv);
    }
    

    private void setValue(double doubleValue) {
        double angle = doubleValue * 87.9;
        Rotate rotation = new Rotate(angle, width, height);

        pointer.getTransforms().clear();
        pointer.getTransforms().add(rotation);
    }
}
