package afester.javafx.examples.board.tools;

import java.util.ArrayList;
import java.util.List;

import afester.javafx.components.ToolbarToggleButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * There are essentially three kinds of Actions:
 * 
 * * Normal actions - when the menu item or toolbar button is pressed,
 *                    the action listener for the action is called.
 * " Toggle actions - when the menu item or toolbar button is pressed,
 *                    the menu item / toolbar changes its state between true and
 *                    false and the action listener for the action is called
 *                    to promote the new state.
 *                    TODO: It should also be possible to bind to the "current 
 *                    state" of the action to update dependent values
 * * Toggle action group - when the menu item or toolbar button is pressed,
 *                    it is selected and all other actions of the same group 
 *                    become deselected. The ActionGroup's listener
 *                    is called to determine the new Action which is now active.                     
 */
public class ActionToggle extends Action {

    // property which sets this toggle action as selected or unselected
    private final BooleanProperty isSelected = new SimpleBooleanProperty(true);
    public BooleanProperty selectedProperty() { return isSelected; }
    public boolean isSelected() { return isSelected.get(); }
    public void setSelected(boolean flag) { isSelected.set(flag); }

    
    private final List<CheckMenuItem> menuItems = new ArrayList<>();
    private final List<ToolbarToggleButton> toolbarButtons = new ArrayList<>();

    public ActionToggle(String title, String description, Image icon, 
                  EventHandler<ActionEvent> action) {
        super(title, description, icon, action);
        
        isSelected.addListener((obj, oldValue, newValue) -> {
            menuItems.forEach(m -> m.setSelected(newValue));
            toolbarButtons.forEach(t -> t.setSelected(newValue));
        });
    }

    public ActionToggle(String title, String description, Image icon) {
        this(title, description, icon, null);
    }

    public ActionToggle(String title, String description,
                        EventHandler<ActionEvent> action) {
        this(title, description, null, action);
    }

    public ActionToggle(String title, EventHandler<ActionEvent> action) {
        this(title, null, null, action);
    }

    @Override
    public MenuItem getMenuItem() {
        CheckMenuItem result = new CheckMenuItem(title);
        result.setGraphic(new ImageView(icon));
        menuItems.add(result);

        result.disableProperty().bind(enabledProperty().not());

        result.setSelected(isSelected());
        result.selectedProperty().addListener((obj, oldValue, newValue) -> {
            this.setSelected(newValue);
        });

        return result;
    }

    @Override
    public Node[] getToolbarButtons() {
        ToolbarToggleButton result = new ToolbarToggleButton(title, icon);
        toolbarButtons.add(result);

        result.disableProperty().bind(enabledProperty().not());

        result.setSelected(isSelected());
        result.selectedProperty().addListener((obj, oldValue, newValue) -> {
            this.setSelected(newValue);
        });

        return new Node[] {result};
    }
}
