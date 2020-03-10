package afester.javafx.examples.board.tools;

import afester.javafx.components.ToolbarButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;


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
public class Action {

    public static class Separator extends Action {

        @Override
        public MenuItem getMenuItem() {
            return new SeparatorMenuItem();
        }

        @Override
        public Node[] getToolbarButtons() {
            return new Node[] {new javafx.scene.control.Separator()};
        }
    }

    protected String title;
    private String description;
    protected Image icon;
    private EventHandler<ActionEvent> action;

    // property which enables or disables this action
    private final BooleanProperty isEnabled = new SimpleBooleanProperty(true);
    public BooleanProperty enabledProperty() { return isEnabled; }
    public boolean isEnabled() { return isEnabled.get(); }
    public void setEnabled(boolean flag) { isEnabled.set(flag); }

    private Action() {
    }

    public Action(String title, String description, Image icon, 
                  EventHandler<ActionEvent> action) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.action = action;
    }

    public Action(String title, String description,
                  EventHandler<ActionEvent> action) {
        this(title, description, null, action);
    }

    public Action(String title, EventHandler<ActionEvent> action) {
        this(title, null, null, action);
    }

    public Action(String title, String description, Image icon) {
        this(title, description, icon, null);
    }


    public MenuItem getMenuItem() {
        MenuItem result = new MenuItem(title);
        result.setOnAction(action);
        result.disableProperty().bind(enabledProperty().not());
        return result;
    }

    public Node[] getToolbarButtons() {
        ToolbarButton result = new ToolbarButton(title, icon);
        result.setOnAction(action);
        result.disableProperty().bind(enabledProperty().not());
        return new Node[] {result};
    }

    public static Menu createMenu(String title, Action... actions){
        Menu result = new Menu(title);
        for (Action a : actions) {
            result.getItems().add(a.getMenuItem());
        }

        return result;
    }

    public static ToolBar createToolBar(Action... actions) {
        ToolBar result = new ToolBar();
        for (Action a : actions) {
            result.getItems().addAll(a.getToolbarButtons());
        }

        return result;
    }
}
