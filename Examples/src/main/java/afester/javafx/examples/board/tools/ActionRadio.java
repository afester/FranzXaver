package afester.javafx.examples.board.tools;

import java.util.ArrayList;
import java.util.List;

import afester.javafx.components.ToolbarToggleButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;


public class ActionRadio<T> extends Action {

    // property which specifies the currently selected choice.
    private final ObjectProperty<T> selectedChoice = new SimpleObjectProperty<>();
    public ObjectProperty<T> selectedChoiceProperty() { return selectedChoice; }
    public T getSelectedChoice() { return selectedChoice.get(); }
    public void setSelectedChoice(T value) { selectedChoice.set(value); }

//    public ActionRadio(String title, String description, Image icon, 
//                  EventHandler<ActionEvent> action) {
//        super(title, description, icon, action);
//        
////        isSelected.addListener((obj, oldValue, newValue) -> {
////            menuItems.forEach(m -> m.setSelected(newValue));
////            toolbarButtons.forEach(t -> t.setSelected(newValue));
////        });
//    }

    private final ActionChoice<T>[] choices;

    @SafeVarargs
    public ActionRadio(EventHandler<ActionEvent> action, 
                       ActionChoice<T>... actionChoice) {
        super("", "", null, action);
        choices = actionChoice;
    }


//    @Override
//    public MenuItem getMenuItem() {
//        CheckMenuItem result = new CheckMenuItem(title);
//        result.setGraphic(new ImageView(icon));
//        menuItems.add(result);
//
//        result.disableProperty().bind(enabledProperty().not());
//
//        result.setSelected(isSelected());
//        result.selectedProperty().addListener((obj, oldValue, newValue) -> {
//            this.setSelected(newValue);
//        });
//
//        return result;
//    }

    @Override
    public Node[] getToolbarButtons() {
        List<Node> result = new ArrayList<>();
        ToggleGroup toggleGroup = new ToggleGroup();
        for (var choice : choices) {
            ToolbarToggleButton button = choice.getToolbarButton();
            button.setToggleGroup(toggleGroup);
            result.add(button);

//            toolbarButtons.add(result);
//    
//            result.disableProperty().bind(enabledProperty().not());
//            result.setSelected(isSelected());
            button.selectedProperty().addListener((obj, oldValue, newValue) -> {
                this.setSelectedChoice(choice.getValue());
            });
        }

        return result.toArray(new Node[0]);
    }
}
