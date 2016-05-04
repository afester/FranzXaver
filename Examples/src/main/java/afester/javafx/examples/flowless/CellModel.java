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

package afester.javafx.examples.flowless;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A simple model for one cell used in the Flowless example.
 *
 */
public class CellModel {

    private final String text;
    private final String prefix;
    private final Color color;
    private final Color rectColor;
    private final Color bgColor;

    /**
     * Creates a new model for one Cell.
     * 
     * @param prefix The text to display in the beginning of each cell
     * @param text   The text to display after the prefix
     * @param color  The color of the main text
     * @param rectColor The color for the rectangle
     * @param bgColor   The background color of the main text
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
