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

package afester.javafx.examples.layouts;

import javafx.scene.paint.Color;

public class RectParams {
    private double width;
    private double height;
    private Color color;

    RectParams(double width, double height, Color col) {
        this.width = width;
        this.height = height;
        this.color = col;
    }

    double getWidth() {
        return width;
    }
    
    double getHeight() {
        return height;
    }

    Color getColor() {
        return color;
    }

}
