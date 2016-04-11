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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;



/**
 *  An analog meter component.
 */
public class Meter extends Control {

    /* The current value */
    private DoubleProperty value = new SimpleDoubleProperty();

    public Double getValue() {
        return value.get();
    }

    public void setValue(Double value) {
        this.value.set(value);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    
    /* The current unit text  */
    private StringProperty unitText = new SimpleStringProperty();

    public String getUnitText() {
        return unitText.get();
    }

    public void setUnitText(String value) {
        this.unitText.set(value);
    }

    public StringProperty unitTextProperty() {
        return unitText;
    }

   
    
    public Meter() {
    }


    @Override
    protected javafx.scene.control.Skin<?> createDefaultSkin() {
        return new MeterSkin(this);
    }
}
