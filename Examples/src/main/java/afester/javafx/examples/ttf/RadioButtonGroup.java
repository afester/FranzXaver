package afester.javafx.examples.ttf;

import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class RadioButtonGroup<E> extends VBox {

    private ToggleGroup buttonGroup;

    @SuppressWarnings({ "serial", "unchecked" })
    public RadioButtonGroup(RadioButtonValues[] values) {
        buttonGroup = new ToggleGroup();
        setSpacing(5);

        Arrays.stream(values).forEach(e -> {
            RadioButton rb = new RadioButton(e.getLabel());
            rb.setToggleGroup(buttonGroup);
            rb.setSelected(true);
            getChildren().add(rb);

            // TODO: any better approach for this bi-directional binding?
            rb.setOnAction(a -> {
                setSelectedValue((E) e);
            });

            selectedValue.addListener(l -> {
               if (getSelectedValue() == e) {
                   rb.setSelected(true);
                   onAction.get().handle(new ActionEvent() {});
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
    
    

    /*
     * The "selected value" property
     */
    public final void setSelectedValue(E value) {
        selectedValue.setValue(value);
    }
    public final E getSelectedValue() {
        return selectedValue.getValue();
    }
    public final ObjectProperty<E> selectedValueProperty() {
        return selectedValue;
    }
    private ObjectProperty<E> selectedValue = new SimpleObjectProperty<>();

    /*
     * The "onAction" property
     */
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
    public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
    public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };
}
