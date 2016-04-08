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
