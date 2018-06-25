package afester.javafx.examples.ttf;

import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class RadioButtonGroup extends VBox {

    private ToggleGroup buttonGroup;

    public RadioButtonGroup(RadioButtonValues[] values) {
        buttonGroup = new ToggleGroup();
        setSpacing(5);

        Arrays.stream(values).forEach(e -> {
            RadioButton rb = new RadioButton(e.getLabel());
            rb.setToggleGroup(buttonGroup);
            rb.setSelected(true);
            getChildren().add(rb);

            // TODO: any better approach for this bi-directional binding?
            rb.setOnAction(a -> setSelectedValue(e));
            selectedValue.addListener(l -> {
               if (getSelectedValue() == e) {
                   rb.setSelected(true);
               }
            });
        });

//        for (RadioButtonValues eff : values) {
//            System.err.println(eff.name());
//            System.err.println("   " + eff.toString());
//            
//            RadioButton rb = new RadioButton(eff.getLabel());
//            rb.setUserData(eff);
//            rb.setToggleGroup(buttonGroup);
//            rb.setSelected(true);
//            getChildren().add(rb);
//
//            rb.setOnAction(e -> setSelectedValue((RadioButtonValues) ((RadioButton) e.getSource()).getUserData()));
//        }
    }
    
    
    private ObjectProperty<RadioButtonValues> selectedValue = new SimpleObjectProperty<>();

    public final void setSelectedValue(RadioButtonValues value) {
        selectedValue.setValue(value);
    }
    public final RadioButtonValues getSelectedValue() {
        return selectedValue.getValue();
    }
    public final ObjectProperty<RadioButtonValues> selectedValueProperty() {
        return selectedValue;
    }
}
